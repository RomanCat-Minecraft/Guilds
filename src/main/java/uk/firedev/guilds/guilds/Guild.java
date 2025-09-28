package uk.firedev.guilds.guilds;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.guilds.claims.Claim;
import uk.firedev.messagelib.message.ComponentMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uk.firedev.guilds.claims.Claim.claim;

public class Guild {

    private final @NotNull UUID uuid;
    private final @NotNull List<Claim> claims = new ArrayList<>();

    private @NotNull String name;
    private @NotNull UUID owner;

    protected Guild(@NotNull String name, @NotNull UUID owner, @NotNull UUID uuid) {
        this.name = name;
        this.owner = owner;
        this.uuid = uuid;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull UUID getId() {
        return uuid;
    }

    public @NotNull UUID getOwner() {
        return owner;
    }

    public @NotNull List<Claim> getClaims() {
        return claims;
    }

    // Management

    public void setOwner(@NotNull OfflinePlayer newOwner, @NotNull Player player) {
        if (!player.getUniqueId().equals(newOwner.getUniqueId())) {
            player.sendPlainMessage("Only the owner can set a new owner.");
            return;
        }
        owner = newOwner.getUniqueId();
        player.sendPlainMessage("Transferred ownership of " + name + " to " + newOwner.getName());
        getOnlineMembers().forEach(member -> member.sendPlainMessage(newOwner.getName() + " is the new owner of " + name + "!"));
    }

    public void claimChunk(@NotNull Chunk chunk, @NotNull Player player) {
        Claim claim = claim(chunk);
        Guild owner = claim.getOwner();
        if (owner != null) {
            player.sendPlainMessage("This chunk is claimed by " + owner.getName());
        } else {
            claim.setOwner(this);
            player.sendPlainMessage("Successfully claimed this chunk for your guild.");
        }
    }

    public void unclaimChunk(@NotNull Chunk chunk, @NotNull Player player) {
        Claim claim = claim(chunk);
        Guild owner = claim.getOwner();
        if (owner == null) {
            player.sendPlainMessage("This chunk is not claimed.");
            return;
        }
        if (owner.equals(this)) {
            claim.removeOwner();
            player.sendPlainMessage("Successfully unclaimed this chunk.");
        } else {
            player.sendPlainMessage("This chunk is claimed by " + owner.getName());
        }
    }

    // Utility

    public List<Player> getOnlineMembers() {
        // TODO Not currently implemented.
        return List.of();
    }

    // Loading

    public static Guild load(@NotNull ResultSet set) throws SQLException {
        return new Guild(
            set.getString("name"),
            UUID.fromString(set.getString("owner")),
            UUID.fromString(set.getString("id"))
        );
    }

    // Saving

    public void save() {
        // TODO nerd shit with databases
    }

}
