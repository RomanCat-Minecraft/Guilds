package uk.firedev.guilds.guild;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.daisylib.utils.DatabaseUtils;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.claim.Claim;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;
import uk.firedev.guilds.utils.LoadingUtil;
import uk.firedev.guilds.utils.MessageUtil;
import uk.firedev.messagelib.message.ComponentMessage;
import uk.firedev.messagelib.message.ComponentSingleMessage;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static uk.firedev.guilds.claim.Claim.claim;

public class Guild {

    private final @NotNull UUID uuid;
    private final @NotNull List<Claim> claims = new ArrayList<>();

    private final @NotNull Map<Member, Rank> members = new HashMap<>();

    private @NotNull String name;
    private @NotNull Member owner;
    private @Nullable Location home;
    private double balance;
    private boolean isPublic = false;

    protected Guild(@NotNull String name, @NotNull UUID owner, @NotNull UUID uuid) {
        this.name = name;
        this.owner = MemberManager.getInstance().getMember(owner);
        this.members.put(this.owner, Rank.OWNER);
        this.uuid = uuid;
    }

    public static Guild load(@NotNull ResultSet set) throws SQLException {
        Guild guild = new Guild(
            set.getString("name"),
            UUID.fromString(set.getString("owner")),
            UUID.fromString(set.getString("id"))
        );
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

    public @NotNull UUID getId() {
        return uuid;
    }

    public @NotNull Member getOwner() {
        return owner;
    }

    public @Nullable Location getHome() {
        return home;
    }

    public double getBalance() {
        return balance;
    }

    public @NotNull List<Claim> getClaims() {
        return claims;
    }

    public boolean isPublic() {
        return isPublic;
    }

    // Management

    public void rename(@NotNull String newName, @NotNull Player player) {
        if (!player.getUniqueId().equals(owner.getUuid())) {
            sendMessage(player, "Only the owner can rename the guild.");
            return;
        }
        if (newName.equals(name)) {
            sendMessage(player, "The Guild is already named " + newName);
            return;
        }
        // TODO filtering possibly mayhaps
        String oldName = name;
        name = newName;
        broadcast("Guild " + oldName + " has been renamed to " + newName);
        updateCommandRequirements();
    }

    public void transfer(@NotNull OfflinePlayer newOwner, @NotNull Player player) {
        if (!player.getUniqueId().equals(owner.getUuid())) {
            sendMessage(player, "Only the owner can set a new owner.");
            return;
        }
        if (player.getUniqueId().equals(newOwner.getUniqueId())) {
            sendMessage(player, "You are already the owner.");
            return;
        }
        members.remove(owner);
        owner = MemberManager.getInstance().getMember(newOwner);
        members.put(owner, Rank.OWNER);
        // Tell the old owner
        sendMessage(player, "Transferred ownership of " + name + " to " + newOwner.getName());
        // Tell every member of the guild (including the new owner)
        broadcast(newOwner.getName() + " is now owner of this guild!");
        updateCommandRequirements();
        CommandAPI.updateRequirements(player);
    }

    public void claimChunk(@NotNull Player player) {
        Location location = player.getLocation();
        Claim claim = claim(location.getChunk());
        Guild owner = claim.getOwner();
        if (owner != null) {
            sendMessage(player, "This chunk is claimed by " + owner.getName());
        } else {
            claim.setOwner(this);
            claims.add(claim);
            sendMessage(player, "Successfully claimed this chunk for your guild.");
            // The first claim should set the guild home.
            if (claims.size() == 1) {
                this.home = location;
                broadcast("The guild home has been set to " + MessageUtil.prepareLocation(location) + "!");
            }
            updateCommandRequirements();
        }
    }

    public void unclaimChunk(@NotNull Chunk chunk, @NotNull Player player) {
        Claim claim = claim(chunk);
        Guild owner = claim.getOwner();
        if (owner == null) {
            sendMessage(player, "This chunk is not claimed.");
            return;
        }
        if (!owner.equals(this)) {
            sendMessage(player, "This chunk is claimed by " + owner.getName());
            return;
        }
        if (home != null && chunk.equals(home.getChunk())) {
            sendMessage(player, "This chunk contains the Guild home. Cannot unclaim!");
            return;
        }
        claim.removeOwner();
        claims.remove(claim);
        sendMessage(player, "Successfully unclaimed this chunk.");
        updateCommandRequirements();
    }

    public void setHome(@NotNull Player player) {
        if (!player.getUniqueId().equals(owner.getUuid())) {
            sendMessage(player, "Only the owner can set a new home.");
            return;
        }
        Location location = player.getLocation();
        Claim claim = claim(location.getChunk());
        if (!this.equals(claim.getOwner())) {
            sendMessage(player, "This land doesn't belong to your guild!");
            return;
        }
        home = location;
        broadcast("The guild home has been set to " + MessageUtil.prepareLocation(home) + "!");
        updateCommandRequirements();
    }

    public void deposit(@NotNull Player player, @NotNull BigDecimal amount) {
        Economy economy = Guilds.INSTANCE.getEconomy();
        EconomyResponse response = economy.withdraw("Guilds", player.getUniqueId(), amount);
        if (!response.transactionSuccess()) {
            sendMessage(player, "You do not have enough money to deposit!");
        } else {
            balance += amount.doubleValue();
            broadcastOnline(player.getName() + " deposited " + amount.toPlainString() + " into the Guild bank.");
        }
    }

    public void withdraw(@NotNull Player player, @NotNull BigDecimal amount) {
        if (balance < amount.doubleValue()) {
            sendMessage(player, "The Guild does not have that much in the bank!");
            return;
        }

        EconomyResponse response = Guilds.INSTANCE.getEconomy().deposit("Guilds", player.getUniqueId(), amount);
        if (!response.transactionSuccess()) {
            sendMessage(player, "Failed to withdraw money from the Guild bank. Please try again.");
            Loggers.warn(Guilds.INSTANCE.getComponentLogger(), "Failed to withdraw money from Guild " + getName());
            Loggers.warn(Guilds.INSTANCE.getComponentLogger(), response.errorMessage);
        } else {
            balance -= amount.doubleValue();
            broadcastOnline(player.getName() + " withdrew " + amount.toPlainString() + " from the Guild bank.");
        }
    }

    public void invite(@NotNull Player player, @NotNull Player invited) {
        if (!player.getUniqueId().equals(owner.getUuid())) {
            sendMessage(player, "Only the owner can invite a new member.");
            return;
        }
        if (player.equals(invited)) {
            sendMessage(player, "You cannot invite yourself!");
            return;
        }
        Member member = MemberManager.getInstance().getMember(invited);
        if (members.containsKey(member)) {
            sendMessage(player, "That player is already a member of this guild!");
            return;
        }
        member.invite(player, this);
    }

    public void setPublic(@NotNull Player player, boolean isPublic) {
        if (!player.getUniqueId().equals(owner.getUuid())) {
            sendMessage(player, "Only the owner can change the publicity.");
            return;
        }
        if (this.isPublic == isPublic) {
            if (isPublic) {
                sendMessage(player, "The guild is already public.");
            } else {
                sendMessage(player, "The guild is already private.");
            }
            return;
        }
        this.isPublic = isPublic;
        if (isPublic) {
            broadcastOnline("The guild is now open to the public.");
        } else {
            broadcastOnline("The guild is now closed to the public.");
        }
    }

    public void setRank(@NotNull Player player, @NotNull Member member, @NotNull Rank rank) {
        if (rank.equals(Rank.OWNER)) {
            sendMessage(player, "A new owner can be set with the transfer command.");
            return;
        }
        if (player.getUniqueId().equals(member.getUuid())) {
            sendMessage(player, "You cannot change your own rank!");
            return;
        }
        if (!owner.getUuid().equals(player.getUniqueId())) {
            sendMessage(player, "Only the owner can assign ranks!");
            return;
        }
        if (!isMember(member)) {
            sendMessage(player, "Member is not a player.");
            return;
        }
        setMemberRank(member, rank);
        sendMessage(member, "Your rank has been changed to " + rank.getDisplay());
        sendMessage(player, "Set " + member.getUsername() + "'s rank to " + rank.getDisplay());
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

    public void addMember(@NotNull Member member) {
        if (isMember(member)) {
            return;
        }
        members.put(member, Rank.MEMBER);
        member.setGuild(this);
        broadcastOnline(member.getUsername() + " has joined the guild!");
        updateCommandRequirements();
    }

    public void removeMember(@NotNull Member member) {
        if (!isMember(member)) {
            return;
        }
        broadcastOnline(member.getUsername() + " has left the guild!");
        members.remove(member);
        member.setGuild(null);
        member.updateCommandRequirements();
        updateCommandRequirements();
    }

    /**
     * Fetches the member's guild rank.
     * @param member The member to check.
     * @return The member's guild rank, or null if the member is not in this Guild.
     */
    public @Nullable Rank getMemberRank(@NotNull Member member) {
        return members.get(member);
    }

    public void setMemberRank(@NotNull Member member, @NotNull Rank rank) {
        if (!isMember(member)) {
            return;
        }
        members.put(member, rank);
    }

    public ComponentSingleMessage getMessagePrefix() {
        return ComponentMessage.componentMessage("<gray>[<yellow>" + getName() + "<gray>] ");
    }

    /**
     * Updates command requirements for every Guild member.
     */
    public void updateCommandRequirements() {
        getOnlineMembers().forEach(CommandAPI::updateRequirements);
    }

    // Members

    /**
     * Sends a plaintext message to all members.
     * @param message The message to send.
     */
    public void broadcast(@NotNull String message) {
        Component component = Component.text(message);
        broadcast(ComponentMessage.componentMessage(component));
    }

    /**
     * Sends a {@link ComponentMessage} to all members.
     * @param message The message to send.
     */
    public void broadcast(@NotNull ComponentMessage message) {
        final ComponentMessage finalMessage = message.prepend(getMessagePrefix());
        getMembersRaw().keySet().forEach(member -> member.sendMessage(finalMessage));
    }

    /**
     * Sends a plaintext message to online members.
     * @param message The message to send.
     */
    public void broadcastOnline(@NotNull String message) {
        Component component = Component.text(message);
        broadcastOnline(ComponentMessage.componentMessage(component));
    }

    /**
     * Sends a {@link ComponentMessage} to online members.
     * @param message The message to send.
     */
    public void broadcastOnline(@NotNull ComponentMessage message) {
        message.prepend(getMessagePrefix()).send(getOnlineMembers());
    }

    /**
     * Sends a plaintext message to the provided player. The player does not have to be in this guild.
     * @param message The message to send.
     */
    public void sendMessage(@NotNull Player player, @NotNull String message) {
        sendMessage(MemberManager.getInstance().getMember(player), message);
    }

    /**
     * Sends a {@link ComponentMessage} to the provided player. The player does not have to be in this guild.
     * @param message The message to send.
     */
    public void sendMessage(@NotNull Player player, @NotNull ComponentMessage message) {
        sendMessage(MemberManager.getInstance().getMember(player), message);
    }

    /**
     * Sends a plaintext message to the provided member. The member does not have to be in this guild.
     * @param message The message to send.
     */
    public void sendMessage(@NotNull Member member, @NotNull String message) {
        Component component = Component.text(message);
        sendMessage(member, ComponentMessage.componentMessage(component));
    }

    /**
     * Sends a {@link ComponentMessage} to the provided member. The member does not have to be in this guild.
     * @param message The message to send.
     */
    public void sendMessage(@NotNull Member member, @NotNull ComponentMessage message) {
        member.sendMessage(message.prepend(getMessagePrefix()));
    }

    public void leave(@NotNull Player player) {
        leave(MemberManager.getInstance().getMember(player));
    }

    public void leave(@NotNull Member member) {
        if (member.equals(owner)) {
            member.sendMessage("The owner cannot leave the guild!");
            return;
        }
        if (!isMember(member)) {
            member.sendMessage("You are not in this guild.");
            return;
        }
        removeMember(member);
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
