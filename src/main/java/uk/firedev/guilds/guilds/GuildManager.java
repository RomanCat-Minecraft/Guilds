package uk.firedev.guilds.guilds;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.daisylib.utils.DatabaseUtils;
import uk.firedev.guilds.Guilds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class GuildManager {

    private static final GuildManager instance = new GuildManager();

    protected final Map<UUID, Guild> loadedGuilds = new HashMap<>();

    private GuildManager() {}

    public static GuildManager getInstance() {
        return instance;
    }

    public @Nullable Guild getByUuid(@NotNull UUID uuid) {
        return loadedGuilds.get(uuid);
    }

    public @Nullable Guild getByOwner(@NotNull UUID owner) {
        for (Guild guild : loadedGuilds.values()) {
            if (guild.getOwner().equals(owner)) {
                return guild;
            }
        }
        return null;
    }

    public @Nullable Guild getByName(@NotNull String name) {
        for (Guild guild : loadedGuilds.values()) {
            if (guild.getName().equalsIgnoreCase(name)) {
                return guild;
            }
        }
        return null;
    }

    public void createGuild(@NotNull String name, @NotNull Player owner) {
        if (getByOwner(owner.getUniqueId()) != null) {
            owner.sendPlainMessage("You already own a guild.");
            return;
        }
        if (getByName(name) != null) {
            owner.sendPlainMessage("A guild with this name already exists.");
            return;
        }
        Guild guild = new Guild(name, owner.getUniqueId(), UUID.randomUUID());
        loadedGuilds.put(guild.getId(), guild);
        owner.sendPlainMessage("Guild " + name + " has been created.");
    }

    public void deleteGuild(@Nullable Guild guild, @NotNull Player player) {
        if (guild == null) {
            player.sendPlainMessage("That guild does not exist.");
            return;
        }
        if (!guild.getOwner().equals(player.getUniqueId())) {
            player.sendPlainMessage("Only the guild owner can delete the guild.");
            return;
        }
        loadedGuilds.remove(guild.getId());
        player.sendPlainMessage("Guild " + guild.getName() + " has been deleted.");
    }

    public void deleteGuild(@NotNull String name, @NotNull Player player) {
        Guild guild = getByName(name);
        deleteGuild(guild, player);
    }

    public void deleteGuild(@NotNull UUID uuid, @NotNull Player player) {
        Guild guild = getByUuid(uuid);
        deleteGuild(guild, player);
    }

    public Collection<Guild> getAllGuilds() {
        return loadedGuilds.values();
    }

    /**
     * Attempts to load a guild from the provided {@link ResultSet}.
     * @param set The {@link ResultSet} to load.
     * @return An optional containing a guild, or empty if it failed to load.
     */
    public Optional<Guild> load(@NotNull ResultSet set) {
        try {
            Guild guild = Guild.load(set);
            loadedGuilds.put(guild.getId(), guild);
            return Optional.of(guild);
        } catch (IllegalArgumentException exception) {
            Loggers.warn(Guilds.INSTANCE.getComponentLogger(), "Attempted to load a guild with an invalid UUID.", exception);
            return Optional.empty();
        } catch (SQLException exception) {
            Loggers.warn(Guilds.INSTANCE.getComponentLogger(), "Failed to load guild.", exception);
            return Optional.empty();
        }
    }

    public void saveAll() {
        // TODO implement saving.
    }

    public void save(@NotNull Guild guild) {
        // TODO implement saving.
    }

}
