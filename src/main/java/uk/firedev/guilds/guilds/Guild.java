package uk.firedev.guilds.guilds;

import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.guilds.claims.Claim;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Guild {

    private final @NotNull UUID uuid;
    private final @NotNull String name;
    private final @NotNull UUID owner;
    private final @NotNull List<Claim> claims = new ArrayList<>();

    private Guild(@NotNull String name, @NotNull UUID owner, @NotNull UUID uuid) {
        this.name = name;
        this.owner = owner;
        this.uuid = uuid;
        GuildManager.getInstance().loadedGuilds.put(uuid, this);
    }

    public static @Nullable Guild create(@NotNull String name, @NotNull Player owner) {
        if (GuildManager.getInstance().getByOwner(owner.getUniqueId()) != null) {
            owner.sendPlainMessage("You already own a guild.");
            return null;
        }
        if (GuildManager.getInstance().getByName(name) != null) {
            owner.sendPlainMessage("A guild with this name already exists.");
            return null;
        }
        owner.sendPlainMessage("Guild " + name + " has been created.");
        return new Guild(name, owner.getUniqueId(), UUID.randomUUID());
    }

    public static Guild load(@NotNull ResultSet set) throws SQLException {
        return new Guild(
            set.getString("name"),
            UUID.fromString(set.getString("owner")),
            UUID.fromString(set.getString("id"))
        );
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull UUID getOwner() {
        return owner;
    }

    public @NotNull List<Claim> getClaims() {
        return claims;
    }

}
