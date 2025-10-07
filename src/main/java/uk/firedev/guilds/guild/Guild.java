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
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.claim.Claim;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static uk.firedev.guilds.claim.Claim.claim;

public class Guild {

    private final @NotNull UUID uuid;
    private final @NotNull List<Claim> claims = new ArrayList<>();

    // TODO map to guild rank?
    private final @NotNull List<Member> members = new ArrayList<>();

    private @NotNull String name;
    private @NotNull Member owner;
    private @Nullable Location home;
    private double balance;
    private boolean isPublic = false;

    protected Guild(@NotNull String name, @NotNull UUID owner, @NotNull UUID uuid) {
        this.name = name;
        this.owner = MemberManager.getInstance().getMember(owner);
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
            player.sendPlainMessage("Only the owner can set a new owner.");
            return;
        }
        if (newName.equals(name)) {
            player.sendPlainMessage("The Guild is already called " + newName);
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
            player.sendPlainMessage("Only the owner can set a new owner.");
            return;
        }
        if (player.getUniqueId().equals(newOwner.getUniqueId())) {
            player.sendPlainMessage("You are already the owner.");
            return;
        }
        owner = MemberManager.getInstance().getMember(newOwner);
        // Tell the old owner
        player.sendPlainMessage("Transferred ownership of " + name + " to " + newOwner.getName());
        // Tell every member of the guild (including the new owner)
        broadcast(newOwner.getName() + " is now owner of " + name + "!");
        updateCommandRequirements();
        CommandAPI.updateRequirements(player);
    }

    public void claimChunk(@NotNull Player player) {
        Location location = player.getLocation();
        Claim claim = claim(location.getChunk());
        Guild owner = claim.getOwner();
        if (owner != null) {
            player.sendPlainMessage("This chunk is claimed by " + owner.getName());
        } else {
            claim.setOwner(this);
            claims.add(claim);
            player.sendPlainMessage("Successfully claimed this chunk for your guild.");
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
            player.sendPlainMessage("This chunk is not claimed.");
            return;
        }
        if (!owner.equals(this)) {
            player.sendPlainMessage("This chunk is claimed by " + owner.getName());
            return;
        }
        if (home != null && chunk.equals(home.getChunk())) {
            player.sendPlainMessage("This chunk contains the Guild home. Cannot unclaim!");
            return;
        }
        claim.removeOwner();
        claims.remove(claim);
        player.sendPlainMessage("Successfully unclaimed this chunk.");
        updateCommandRequirements();
    }

    public void setHome(@NotNull Player player) {
        if (!player.getUniqueId().equals(owner.getUuid())) {
            player.sendPlainMessage("Only the owner can set a new home.");
            return;
        }
        Location location = player.getLocation();
        Claim claim = claim(location.getChunk());
        if (!this.equals(claim.getOwner())) {
            player.sendPlainMessage("This land doesn't belong to your guild!");
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
            player.sendPlainMessage("You do not have enough money to deposit!");
        } else {
            balance += amount.doubleValue();
            broadcastOnline(player.getName() + " deposited " + amount.toPlainString() + " into the Guild bank.");
        }
    }

    public void withdraw(@NotNull Player player, @NotNull BigDecimal amount) {
        if (balance < amount.doubleValue()) {
            player.sendPlainMessage("The Guild does not have that much in the bank!");
            return;
        }

        EconomyResponse response = Guilds.INSTANCE.getEconomy().deposit("Guilds", player.getUniqueId(), amount);
        if (!response.transactionSuccess()) {
            player.sendPlainMessage("Failed to withdraw money from the Guild bank. Please try again.");
            Loggers.warn(Guilds.INSTANCE.getComponentLogger(), "Failed to withdraw money from Guild " + getName());
            Loggers.warn(Guilds.INSTANCE.getComponentLogger(), response.errorMessage);
        } else {
            balance -= amount.doubleValue();
            broadcastOnline(player.getName() + " withdrew " + amount.toPlainString() + " from the Guild bank.");
        }
    }

    public void invite(@NotNull Player player, @NotNull Player invited) {
        if (!player.getUniqueId().equals(owner.getUuid())) {
            player.sendPlainMessage("Only the owner can invite a new member.");
            return;
        }
        if (player.equals(invited)) {
            player.sendPlainMessage("You cannot invite yourself!");
            return;
        }
        Member member = MemberManager.getInstance().getMember(invited);
        if (members.contains(member)) {
            player.sendPlainMessage("That player is already a member of this guild!");
            return;
        }
        member.invite(player, this);
    }

    public void setPublic(@NotNull Player player, boolean isPublic) {
        if (!player.getUniqueId().equals(owner.getUuid())) {
            player.sendPlainMessage("Only the owner can change the publicity.");
            return;
        }
        if (this.isPublic == isPublic) {
            if (isPublic) {
                player.sendPlainMessage("The guild is already public.");
            } else {
                player.sendPlainMessage("The guild is already private.");
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

    // Utility

    /**
     * @return A list of online members as Player objects.
     */
    public List<Player> getOnlineMembers(boolean includeOwner) {
        List<Member> memberList = new ArrayList<>(members);
        if (includeOwner) {
            memberList.add(owner);
        }
        return memberList.stream()
            .map(Member::getPlayer)
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * @return A list of all members as OfflinePlayer objects.
     */
    public List<OfflinePlayer> getMembers(boolean includeOwner) {
        List<Member> memberList = new ArrayList<>(members);
        if (includeOwner) {
            memberList.add(owner);
        }
        return memberList.stream()
            .map(Member::getOfflinePlayer)
            .toList();
    }

    /**
     * @return An unmodifiable list of all members as Member objects.
     */
    public List<Member> getMembersRaw(boolean includeOwner) {
        List<Member> memberList = new ArrayList<>(members);
        if (includeOwner) {
            memberList.add(owner);
        }
        return List.copyOf(memberList);
    }

    /**
     * @param uuid The {@link UUID} of the player to check.
     * @return Whether a player is a member of this Guild.
     */
    public boolean isMember(@NotNull UUID uuid) {
        return members.stream().anyMatch(member -> member.getUuid().equals(uuid));
    }

    /**
     * @param player The {@link OfflinePlayer} instance of the player to check.
     * @return Whether a player is part of this Guild.
     */
    public boolean isMember(@NotNull OfflinePlayer player) {
        return isMember(player.getUniqueId());
    }

    /**
     * @param uuid The {@link UUID} of the player to check.
     * @return Whether a player is a member or the owner of this Guild.
     */
    public boolean isMemberOrOwner(@NotNull UUID uuid) {
        return uuid.equals(owner.getUuid()) || isMember(uuid);
    }

    /**
     * @param player The {@link OfflinePlayer} of the player to check.
     * @return Whether a player is a member or the owner of this Guild.
     */
    public boolean isMemberOrOwner(@NotNull OfflinePlayer player) {
        return isMemberOrOwner(player.getUniqueId());
    }

    public void addMember(@NotNull Member member) {
        if (members.contains(member)) {
            return;
        }
        members.add(member);
        member.setGuild(this);
        broadcastOnline(member.getUsername() + " has joined the guild!");
        updateCommandRequirements();
    }

    public void removeMember(@NotNull Member member) {
        if (!members.contains(member)) {
            return;
        }
        members.remove(member);
        member.setGuild(null);
        member.sendMessage("You have left the guild.");
        broadcastOnline(member.getUsername() + " has left the guild!");
        member.updateCommandRequirements();
        updateCommandRequirements();
    }

    public ComponentSingleMessage getMessagePrefix() {
        return ComponentMessage.componentMessage("<gray>[<yellow>" + getName() + "<gray>] ");
    }

    /**
     * Updates command requirements for every Guild member.
     */
    public void updateCommandRequirements() {
        getOnlineMembers(true).forEach(CommandAPI::updateRequirements);
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
        getMembersRaw(true).forEach(member -> member.sendMessage(finalMessage));
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
        message.prepend(getMessagePrefix()).send(getOnlineMembers(true));
    }

    public void leave(@NotNull Player player) {
        leave(MemberManager.getInstance().getMember(player));
    }

    public void leave(@NotNull Member member) {
        if (member.equals(owner)) {
            member.sendMessage("The owner cannot leave the guild!");
            return;
        }
        if (!members.contains(member)) {
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
