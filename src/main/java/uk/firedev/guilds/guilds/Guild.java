package uk.firedev.guilds.guilds;

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
import uk.firedev.guilds.claims.Claim;
import uk.firedev.guilds.utils.LoadingUtil;
import uk.firedev.guilds.utils.MessageUtil;
import uk.firedev.messagelib.message.ComponentMessage;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static uk.firedev.guilds.claims.Claim.claim;

public class Guild {

    private final @NotNull UUID uuid;
    private final @NotNull List<Claim> claims = new ArrayList<>();

    // TODO map to guild rank?
    private final @NotNull List<UUID> members = new ArrayList<>();

    private @NotNull String name;
    private @NotNull UUID owner;
    private @Nullable Location home;
    private double balance;

    protected Guild(@NotNull String name, @NotNull UUID owner, @NotNull UUID uuid) {
        this.name = name;
        this.owner = owner;
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

    public @NotNull UUID getOwner() {
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

    // Management

    public void rename(@NotNull String newName, @NotNull Player player) {
        if (!player.getUniqueId().equals(owner)) {
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
        if (!player.getUniqueId().equals(owner)) {
            player.sendPlainMessage("Only the owner can set a new owner.");
            return;
        }
        if (player.getUniqueId().equals(newOwner.getUniqueId())) {
            player.sendPlainMessage("You are already the owner.");
            return;
        }
        owner = newOwner.getUniqueId();
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
        if (!player.getUniqueId().equals(owner)) {
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
            broadcast(player.getName() + " deposited " + amount.toPlainString() + " into the Guild bank.");
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
            broadcast(player.getName() + " withdrew " + amount.toPlainString() + " from the Guild bank.");
        }
    }

    // Utility

    /**
     * @return A list of online members as Player objects.
     */
    public List<Player> getOnlineMembers() {
        // TODO pass list of UUIDs when that's ready
        return members.stream()
            .map(PlayerHelper::getOfflinePlayer)
            .filter(Objects::nonNull)
            .map(OfflinePlayer::getPlayer)
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * @return A list of all members as OfflinePlayer objects.
     */
    public List<OfflinePlayer> getMembers() {
        // TODO pass list of UUIDs when that's ready
        return members.stream()
            .map(PlayerHelper::getOfflinePlayer)
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * @return An unmodifiable list of all members as UUID objects.
     */
    public List<UUID> getMembersRaw() {
        // TODO pass list of UUIDs when that's ready
        return List.copyOf(members);
    }

    /**
     * @param uuid The {@link UUID} of the player to check.
     * @return Whether a player is a member of this Guild.
     */
    public boolean isMember(@NotNull UUID uuid) {
        return members.stream().anyMatch(member -> member.equals(uuid));
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
        return uuid.equals(owner) || isMember(uuid);
    }

    /**
     * @param player The {@link OfflinePlayer} of the player to check.
     * @return Whether a player is a member or the owner of this Guild.
     */
    public boolean isMemberOrOwner(@NotNull OfflinePlayer player) {
        return isMemberOrOwner(player.getUniqueId());
    }

    // Members

    /**
     * Sends a plaintext message to all online members.
     * @param message The message to send.
     */
    public void broadcast(@NotNull String message) {
        Component component = Component.text(message);
        broadcast(ComponentMessage.componentMessage(component));
    }

    public void broadcast(@NotNull ComponentMessage message) {
        List<OfflinePlayer> members = getMembers();
        for (OfflinePlayer member : members) {
            Player online = member.getPlayer();
            if (online == null) {
                // TODO mail.
            } else {
                message.send(online);
            }
        }
    }

    /**
     * Updates command requirements for every Guild member.
     */
    public void updateCommandRequirements() {
        getOnlineMembers().forEach(CommandAPI::updateRequirements);
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
