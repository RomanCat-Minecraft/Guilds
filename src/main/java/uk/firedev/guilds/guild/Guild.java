package uk.firedev.guilds.guild;

import net.milkbowl.vault2.economy.EconomyResponse;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.command.CooldownHelper;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.utils.DatabaseUtils;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.claim.Claim;
import uk.firedev.guilds.config.MainConfig;
import uk.firedev.guilds.config.MessageConfig;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.guild.rank.RankType;
import uk.firedev.guilds.guild.rank.permissions.RankPermission;
import uk.firedev.guilds.guild.rank.ranks.MemberRank;
import uk.firedev.guilds.guild.rank.ranks.OfficerRank;
import uk.firedev.guilds.guild.rank.ranks.OwnerRank;
import uk.firedev.guilds.guild.rank.ranks.RecruiterRank;
import uk.firedev.guilds.guild.rank.ranks.TreasurerRank;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;
import uk.firedev.guilds.utils.EconomyHelper;
import uk.firedev.guilds.utils.LoadingUtil;
import uk.firedev.guilds.utils.TeleportWarmup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static uk.firedev.guilds.claim.Claim.claim;

public class Guild {

    private final CooldownHelper visitConfirmation = CooldownHelper.create();

    private final @NotNull UUID uuid;

    // TODO need claim limits.
    private final @NotNull List<Claim> claims = new ArrayList<>();

    private final @NotNull Map<Member, Rank> members = new HashMap<>();

    private final @NotNull OwnerRank ownerRank;
    private final @NotNull OfficerRank officerRank;
    private final @NotNull TreasurerRank treasurerRank;
    private final @NotNull RecruiterRank recruiterRank;
    private final @NotNull MemberRank memberRank;

    private @NotNull String name;
    private @NotNull Member owner;
    private @Nullable Location home;
    private double balance;
    private double tax;
    private boolean open = false;
    private String board = null;
    private boolean allowVisits = false;
    private double visitCost = 0.0D;

    protected Guild(@NotNull String name, @NotNull UUID owner, @NotNull UUID uuid) {
        this.ownerRank = new OwnerRank(this);
        this.officerRank = new OfficerRank(this);
        this.treasurerRank = new TreasurerRank(this);
        this.recruiterRank = new RecruiterRank(this);
        this.memberRank = new MemberRank(this);

        this.name = name;
        this.owner = MemberManager.getInstance().getMember(owner);
        this.members.put(this.owner, ownerRank);
        this.uuid = uuid;
    }

    public static Guild load(@NotNull ResultSet set) throws SQLException {
        Guild guild = new Guild(
            set.getString("name"),
            UUID.fromString(set.getString("owner")),
            UUID.fromString(set.getString("id"))
        );
        guild.board = set.getString("board");
        guild.allowVisits = set.getBoolean("allowVisits");
        guild.visitCost = set.getDouble("visitCost");
        Location home = DatabaseUtils.parseLocation(set.getString("home"));
        LoadingUtil.doOrWarn(
            home,
            obj -> guild.home = obj,
            "Guild " + guild.name + " has an invalid home location. Not setting it."
        );
        return guild;
    }

    // Getters

    public @NotNull String getName() {
        return name;
    }

    public @Nullable String getBoard() {
        return board;
    }

    public @NotNull UUID getId() {
        return uuid;
    }

    public @NotNull Member getOwner() {
        return owner;
    }

    public @Nullable Location getHome() {
        return home;
    }

    public boolean getAllowVisits() {
        return allowVisits;
    }

    public double getBalance() {
        return balance;
    }

    public double getTax() {
        return tax;
    }

    public @NotNull List<Claim> getClaims() {
        return claims;
    }

    public boolean isOpen() {
        return open;
    }

    // Management

    public void setName(@NotNull String newName, @NotNull Player player) {
        if (!hasPermission(player, RankPermission.MANAGE_NAME)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        if (newName.equals(name)) {
            MessageConfig.getInstance().getRenameAlreadyNamedMessage(this).send(player);
            return;
        }
        // TODO filtering possibly mayhaps
        String oldName = name;
        name = newName;
        broadcast(
            MessageConfig.getInstance().getRenameRenamedMessage(this, oldName)
        );
        updateCommandRequirements();
    }

    public void setOwner(@NotNull OfflinePlayer newOwner, @NotNull Player player) {
        if (!hasPermission(player, RankPermission.TRANSFER)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        if (player.getUniqueId().equals(newOwner.getUniqueId())) {
            MessageConfig.getInstance().getTransferSelfMessage(this).send(player);
            return;
        }
        Member member = getMember(newOwner);
        if (member == null) {
            MessageConfig.getInstance().getNotMemberMessage(this).send(player);
            return;
        }
        members.put(owner, officerRank);
        owner = member;
        members.put(member, ownerRank);
        broadcast(
            MessageConfig.getInstance().getTransferSuccessMessage(this, member.getUsername())
        );
        updateCommandRequirements();
        player.updateCommands();
    }

    public void claimChunk(@NotNull Player player) {
        Location location = player.getLocation();
        Claim claim = claim(location.getChunk());
        Guild owner = claim.getOwner();
        if (owner != null) {
            MessageConfig.getInstance().getClaimAlreadyClaimedMessage(this, owner).send(player);
        } else {
            claim.setOwner(this);
            claims.add(claim);
            MessageConfig.getInstance().getClaimSuccessMessage(this).send(player);
            // The first claim should set the guild home.
            if (claims.size() == 1) {
                this.home = location;
                broadcast(MessageConfig.getInstance().getHomeSetMessage(this, location));
            }
            updateCommandRequirements();
        }
    }

    public void unclaimChunk(@NotNull Chunk chunk, @NotNull Player player) {
        Claim claim = claim(chunk);
        Guild owner = claim.getOwner();
        if (owner == null) {
            MessageConfig.getInstance().getUnclaimNotClaimedMessage(this).send(player);
            return;
        }
        if (!owner.equals(this)) {
            MessageConfig.getInstance().getClaimAlreadyClaimedMessage(this, owner).send(player);
            return;
        }
        if (home != null && chunk.equals(home.getChunk())) {
            MessageConfig.getInstance().getUnclaimContainsHomeMessage(this).send(player);
            return;
        }
        claim.removeOwner();
        claims.remove(claim);
        MessageConfig.getInstance().getUnclaimSuccessMessage(this).send(player);
        updateCommandRequirements();
    }

    public void setHome(@NotNull Player player) {
        if (!hasPermission(player, RankPermission.MANAGE_HOME)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        Location location = player.getLocation();
        Claim claim = claim(location.getChunk());
        if (!this.equals(claim.getOwner())) {
            MessageConfig.getInstance().getHomeInvalidLandMessage(this).send(player);
            return;
        }
        home = location;
        broadcast(MessageConfig.getInstance().getHomeSetMessage(this, home));
        updateCommandRequirements();
    }

    public void deposit(@NotNull Player player, double amount) {
        if (!hasPermission(player, RankPermission.BANK_DEPOSIT)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        EconomyResponse response = EconomyHelper.withdraw(player, amount);
        if (!response.transactionSuccess()) {
            MessageConfig.getInstance().getBankDepositNotEnoughMessage(this).send(player);
        } else {
            balance += amount;
            broadcastOnline(
                MessageConfig.getInstance().getBankDepositSuccessMessage(this, player.getName(), amount)
            );
        }
    }

    public void withdraw(@NotNull Player player, double amount) {
        if (!hasPermission(player, RankPermission.BANK_WITHDRAW)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        if (balance < amount) {
            MessageConfig.getInstance().getBankWithdrawNotEnoughMessage(this).send(player);
            return;
        }

        EconomyResponse response = EconomyHelper.deposit(player, amount);
        if (!response.transactionSuccess()) {
            MessageConfig.getInstance().getBankWithdrawFailedMessage(this).send(player);
            Loggers.warn(Guilds.getInstance().getComponentLogger(), "Failed to withdraw money from Guild " + getName());
            Loggers.warn(Guilds.getInstance().getComponentLogger(), response.errorMessage);
        } else {
            balance -= amount;
            broadcastOnline(MessageConfig.getInstance().getBankWithdrawSuccessMessage(this, player.getName(), amount));
        }
    }

    public void invite(@NotNull Player player, @NotNull Player invited) {
        if (!hasPermission(player, RankPermission.MEMBER_INVITE)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        if (player.equals(invited)) {
            MessageConfig.getInstance().getInviteSelfMessage().send(player);
            return;
        }
        Member member = MemberManager.getInstance().getMember(invited);
        if (members.containsKey(member)) {
            MessageConfig.getInstance().getInviteAlreadyMemberMessage().send(player);
            return;
        }
        member.invite(player, this);
    }

    public void setOpen(@NotNull Player player, boolean open) {
        if (!hasPermission(player, RankPermission.MANAGE_OPEN)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        if (this.open == open) {
            if (open) {
                MessageConfig.getInstance().getOpenAlreadyOpenMessage(this).send(player);
            } else {
                MessageConfig.getInstance().getOpenAlreadyClosedMessage(this).send(player);
            }
            return;
        }
        this.open = open;
        if (open) {
            broadcastOnline(MessageConfig.getInstance().getOpenSetOpenMessage(this));
        } else {
            broadcastOnline(MessageConfig.getInstance().getOpenSetClosedMessage(this));
        }
    }

    public void setRank(@NotNull Player player, @NotNull Member member, @NotNull RankType rankType) {
        if (!hasPermission(player, RankPermission.MANAGE_RANKS)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        if (rankType.equals(RankType.OWNER)) {
            // Transfer if owner.
            setOwner(member.getOfflinePlayer(), player);
            return;
        }
        Rank rank = rankType.getRankInstance(this);
        if (player.getUniqueId().equals(member.getUuid())) {
            MessageConfig.getInstance().getRankSelfMessage(this).send(player);
            return;
        }
        if (!isMember(member)) {
            MessageConfig.getInstance().getNotMemberMessage(this).send(player);
            return;
        }
        setMemberRank(member, rank);
        member.sendMessage(MessageConfig.getInstance().getRankSetChangedTargetMessage(this, rank));
        MessageConfig.getInstance().getRankSetChangedSenderMessage(this, member.getUsername(), rank).send(player);
    }

    public void setTax(@NotNull Player player, double tax) {
        if (!hasPermission(player, RankPermission.MANAGE_TAX)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        if (tax < 0) {
            MessageConfig.getInstance().getTaxTooLowMessage(this).send(player);
            return;
        }
        double max = MainConfig.getInstance().getMaxGuildTax();
        if (tax > max) {
            MessageConfig.getInstance().getTaxTooHighMessage(this, max).send(player);
            return;
        }
        this.tax = tax;
        broadcast(MessageConfig.getInstance().getTaxSetMessage(this, player.getName(), tax));
    }

    public void setBoard(@NotNull Player player, @NotNull String board) {
        if (!hasPermission(player, RankPermission.MANAGE_BOARD)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        // TODO filtering possibly mayhaps
        this.board = board;
        broadcastOnline(
            MessageConfig.getInstance().getBoardMessage(this, board)
        );
        updateCommandRequirements();
    }

    public void setAllowVisits(@NotNull Player player, boolean allowVisits) {
        if (!hasPermission(player, RankPermission.MANAGE_VISIT)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        this.allowVisits = allowVisits;
        if (allowVisits) {
            MessageConfig.getInstance().getVisitAllowedMessage(this).send(player);
        } else {
            MessageConfig.getInstance().getVisitDisallowedMessage(this).send(player);
        }
        updateCommandRequirements();
    }

    public void setVisitCost(@NotNull Player player, double amount) {
        if (!hasPermission(player, RankPermission.MANAGE_VISIT)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        if (amount < 0) {
            MessageConfig.getInstance().getVisitCostTooLowMessage(this).send(player);
            return;
        }
        double max = MainConfig.getInstance().getMaxVisitCost();
        if (visitCost > max) {
            MessageConfig.getInstance().getVisitCostTooHighMessage(this, max).send(player);
            return;
        }
        this.visitCost = amount;
        MessageConfig.getInstance().getVisitSetCostMessage(this, amount).send(player);
        updateCommandRequirements();
    }

    // Utility

    /**
     * @return A list of online members as Player objects.
     */
    public List<Player> getOnlineMembers() {
        return members.keySet().stream()
            .map(Member::getPlayer)
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * @return A list of all members as OfflinePlayer objects.
     */
    public List<OfflinePlayer> getMembers() {
        return members.keySet().stream()
            .map(Member::getOfflinePlayer)
            .toList();
    }

    /**
     * @return An unmodifiable list of all members as Member objects.
     */
    public Map<Member, Rank> getMembersRaw() {
        return Map.copyOf(members);
    }

    /**
     * @param member The {@link Member} of the player to check.
     * @return Whether a member is the owner of this Guild.
     */
    public boolean isOwner(@NotNull Member member) {
        Rank rank = getMemberRank(member);
        return rank != null && rank.getType().equals(RankType.OWNER);
    }

    /**
     * @param uuid The {@link UUID} of the player to check.
     * @return Whether a member is the owner of this Guild.
     */
    public boolean isOwner(@NotNull UUID uuid) {
        return isMember(MemberManager.getInstance().getMember(uuid));
    }

    /**
     * @param member The {@link Member} of the player to check.
     * @return Whether a member is in this Guild.
     */
    public boolean isMember(@NotNull Member member) {
        return members.containsKey(member);
    }

    /**
     * @param uuid The {@link UUID} of the player to check.
     * @return Whether a player is a member of this Guild.
     */
    public boolean isMember(@NotNull UUID uuid) {
        return isMember(MemberManager.getInstance().getMember(uuid));
    }

    /**
     * @param player The {@link OfflinePlayer} instance of the player to check.
     * @return Whether a player is part of this Guild.
     */
    public boolean isMember(@NotNull OfflinePlayer player) {
        return isMember(MemberManager.getInstance().getMember(player));
    }

    /**
     * @param uuid The {@link UUID} of the member to get.
     * @return The member, or null if the member is not in this guild.
     */
    public @Nullable Member getMember(@NotNull UUID uuid) {
        Member member = MemberManager.getInstance().getMember(uuid);
        return isMember(member) ? member : null;
    }

    /**
     * @param player The {@link OfflinePlayer} instance of the player to check.
     * @return The member, or null if the member is not in this guild.
     */
    public @Nullable Member getMember(@NotNull OfflinePlayer player) {
        return getMember(player.getUniqueId());
    }

    public void addMember(@NotNull Member member) {
        if (isMember(member)) {
            return;
        }
        members.put(member, memberRank);
        member.setGuild(this);
        broadcastOnline(MessageConfig.getInstance().getJoinedGuildMessage(this, member.getUsername()));
        updateCommandRequirements();
    }

    public void removeMember(@NotNull Member member) {
        if (!isMember(member)) {
            return;
        }
        broadcastOnline(MessageConfig.getInstance().getLeaveSuccessMessage(this, member.getUsername()));
        members.remove(member);
        member.setGuild(null);
        member.updateCommandRequirements();
        updateCommandRequirements();
    }

    /**
     * Fetches the RankType of the member's guild rank.
     * @param member The member to check.
     * @return The RankType of the member's guild rank, or null if the member is not in this Guild.
     */
    public @Nullable RankType getMemberRankType(@NotNull Member member) {
        Rank rank = getMemberRank(member);
        return rank == null ? null : rank.getType();
    }

    /**
     * Fetches the member's guild rank.
     * @param member The member to check.
     * @return The member's guild rank, or null if the member is not in this Guild.
     */
    public @Nullable Rank getMemberRank(@NotNull Member member) {
        if (member.equals(owner)) {
            return ownerRank;
        }
        return members.get(member);
    }

    public void setMemberRankType(@NotNull Member member, @NotNull RankType rankType) {
        setMemberRank(member, rankType.getRankInstance(this));
    }

    public void setMemberRank(@NotNull Member member, @NotNull Rank rank) {
        if (!isMember(member)) {
            return;
        }
        members.put(member, rank);
        updateCommandRequirements();
    }

    /**
     * Updates command requirements for every Guild member.
     */
    public void updateCommandRequirements() {
        getOnlineMembers().forEach(Player::updateCommands);
    }

    // Members

    /**
     * Sends a {@link ComponentMessage} to all members.
     * @param message The message to send.
     */
    public void broadcast(@NotNull ComponentMessage message) {
        getMembersRaw().keySet().forEach(member -> member.sendMessage(message));
    }

    /**
     * Sends a {@link ComponentMessage} to online members.
     * @param message The message to send.
     */
    public void broadcastOnline(@NotNull ComponentMessage message) {
        getMembersRaw().keySet().forEach(member -> member.sendOnlineMessage(message));
    }

    public void leave(@NotNull Player player) {
        leave(MemberManager.getInstance().getMember(player));
    }

    public void leave(@NotNull Member member) {
        if (member.equals(owner)) {
            member.sendOnlineMessage(MessageConfig.getInstance().getLeaveIsOwnerMessage(this));
            return;
        }
        if (!isMember(member)) {
            member.sendOnlineMessage(MessageConfig.getInstance().getLeaveNotMemberMessage(this));
            return;
        }
        removeMember(member);
    }

    public boolean hasPermission(@NotNull Player player, @NotNull RankPermission permission) {
        return hasPermission(MemberManager.getInstance().getMember(player), permission);
    }

    public boolean hasPermission(@NotNull Member member, @NotNull RankPermission permission) {
        Rank rank = members.get(member);
        if (rank == null) {
            return false;
        }
        return rank.hasPermission(permission);
    }

    public void attemptVisit(@NotNull Player player) {
        // Members bypass everything.
        if (isMember(player)) {
            if (home != null) {
                TeleportWarmup.teleportWarmup(player, home).start();
            } else {
                MessageConfig.getInstance().getHomeNoHomeMessage(this).send(player);
            }
            return;
        }
        if (!allowVisits) {
            MessageConfig.getInstance().getVisitClosedMessage(this).send(player);
            return;
        }
        if (home == null) {
            MessageConfig.getInstance().getHomeNoHomeMessage(this).send(player);
            return;
        }
        double cost = visitCost;
        if (cost > 0D) {
            if (!visitConfirmation.hasCooldown(player.getUniqueId())) {
                MessageConfig.getInstance().getVisitCostConfirmationMessage(this, cost).send(player);
                visitConfirmation.applyCooldown(player.getUniqueId(), Duration.ofSeconds(5));
                return;
            }
            EconomyResponse response = EconomyHelper.withdraw(player, cost);
            if (!response.transactionSuccess()) {
                MessageConfig.getInstance().getVisitNotEnoughMoneyMessage(this, cost).send(player);
                return;
            }
            // The visit cost goes directly to the Guild.
            balance += cost;
        }
        MessageConfig.getInstance().getHomeTeleportingMessage(this).send(player);
        TeleportWarmup.teleportWarmup(player, home).start();
    }

    // Ranks

    public void renameRank(@NotNull Player player, @NotNull RankType rankType, @NotNull String name) {
        if (!hasPermission(player, RankPermission.MANAGE_RANKS)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(this).send(player);
            return;
        }
        Rank rank = rankType.getRankInstance(this);
        String oldName = rank.getDisplay();
        rank.setDisplay(name);
        ComponentMessage message = MessageConfig.getInstance().getRankRenamedMessage(this, oldName, name);
        if (!isMember(player)) {
            message.send(player);
        }
        broadcast(message);
    }

    public @NotNull OwnerRank getOwnerRank() {
        return ownerRank;
    }

    public @NotNull OfficerRank getOfficerRank() {
        return officerRank;
    }

    public @NotNull TreasurerRank getTreasurerRank() {
        return treasurerRank;
    }

    public @NotNull RecruiterRank getRecruiterRank() {
        return recruiterRank;
    }

    public @NotNull MemberRank getMemberRank() {
        return memberRank;
    }

    // Saving

    public void save() {
        // TODO nerd shit with databases
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Guild guild)) {
            return false;
        }
        return guild.getId().equals(this.getId());
    }

}
