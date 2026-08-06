package uk.firedev.guilds.guild;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.config.CurseFilter;
import uk.firedev.guilds.config.MessageConfig;
import uk.firedev.guilds.guild.rank.permissions.RankPermission;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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

    public void load() {
        ChatChannelRegistry.getInstance().register(GuildChat.getInstance());
        Bukkit.getPluginManager().registerEvents(new GuildListener(), Guilds.getInstance());
    }

    public void reload() {
        GuildChat.getInstance().reload();
    }

    public void unload() {}

    public @Nullable Guild getByUuid(@NotNull UUID uuid) {
        return loadedGuilds.get(uuid);
    }

    public @Nullable Guild getByMember(@NotNull UUID uuid) {
        Member member = MemberManager.getInstance().getMember(uuid);
        return member.getGuild();
    }

    public @Nullable Guild getByOwner(@NotNull UUID owner) {
        for (Guild guild : loadedGuilds.values()) {
            if (guild.getOwner().getUuid().equals(owner)) {
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
        if (getByMember(owner.getUniqueId()) != null) {
            MessageConfig.getInstance().getCreateInGuildMessage().send(owner);
            return;
        }
        if (getByName(name) != null) {
            MessageConfig.getInstance().getCreateExistsMessage().send(owner);
            return;
        }
        if (CurseFilter.getInstance().containsCurses(name)) {
            MessageConfig.getInstance().getFilterContainsCursesMessage().send(owner);
            return;
        }
        Guild guild = new Guild(name, owner.getUniqueId(), UUID.randomUUID());
        loadedGuilds.put(guild.getId(), guild);
        MemberManager.getInstance().getMember(owner.getUniqueId()).setGuild(guild);
        MessageConfig.getInstance().getCreateSuccessMessage(guild).send(owner);
    }

    public void disbandGuild(@Nullable Guild guild, @NotNull Player player) {
        if (guild == null) {
            MessageConfig.getInstance().getDisbandInvalidMessage().send(player);
            return;
        }
        if (!guild.hasPermission(player, RankPermission.DISBAND)) {
            MessageConfig.getInstance().getGuildNoPermissionMessage(guild).send(player);
            return;
        }
        guild.broadcast(MessageConfig.getInstance().getDisbandSuccessMessage(guild));
        loadedGuilds.remove(guild.getId());
        player.updateCommands();
    }

    public void disbandGuild(@NotNull String name, @NotNull Player player) {
        Guild guild = getByName(name);
        disbandGuild(guild, player);
    }

    public void disbandGuild(@NotNull UUID uuid, @NotNull Player player) {
        Guild guild = getByUuid(uuid);
        disbandGuild(guild, player);
    }

    public List<Guild> getAllGuilds() {
        return List.copyOf(loadedGuilds.values());
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
            Guilds.getInstance().getLogging().warn("Attempted to load a guild with an invalid UUID.", exception);
            return Optional.empty();
        } catch (SQLException exception) {
            Guilds.getInstance().getLogging().warn("Failed to load guild.", exception);
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
