package uk.firedev.guilds.member;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.registry.ChatChannelRegistry;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildChat;
import uk.firedev.guilds.guild.GuildListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MemberManager {

    private static final MemberManager instance = new MemberManager();

    protected final ConcurrentHashMap<UUID, Member> loadedMembers = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, UUID> nameIndex = new ConcurrentHashMap<>();

    private MemberManager() {}

    public static MemberManager get() {
        return instance;
    }

    public void load() {}

    public void reload() {}

    public void unload() {
        saveAll();
    }

    public @NotNull Member getMember(@NotNull UUID uuid) {
        Member member = loadedMembers.get(uuid);
        // TODO load from db.
        if (member == null) {
            Member newMember = new Member(uuid);
            loadedMembers.put(uuid, newMember);
            nameIndex.put(newMember.getUsername(), uuid);
            return newMember;
        }
        return member;
    }

    public @NotNull Member getMember(@NotNull OfflinePlayer player) {
        return getMember(player.getUniqueId());
    }

    public @Nullable Member getMember(@NotNull Audience audience) {
        if (!(audience instanceof Player player)) {
            return null;
        }
        return getMember(player.getUniqueId());
    }

    public @Nullable Member getMember(@NotNull String name) {
        UUID uuid = nameIndex.get(name);
        return uuid == null ? null : loadedMembers.get(uuid);
    }

    public @Nullable Guild getMemberGuild(@NotNull UUID uuid) {
        return getMember(uuid).getGuild();
    }

    public @Nullable Guild getMemberGuild(@NotNull OfflinePlayer player) {
        return getMember(player).getGuild();
    }

    public @Nullable Guild getMemberGuild(@NotNull Audience audience) {
        Member member = getMember(audience);
        return member == null ? null : member.getGuild();
    }

    public @Nullable Guild getMemberGuild(@NotNull String name) {
        Member member = getMember(name);
        return member == null ? null : member.getGuild();
    }

    public @NotNull List<Member> getAllMembers() {
        return List.copyOf(loadedMembers.values());
    }

    public void loadAll() {
        // TODO implement loading.
    }

    /**
     * Attempts to load a member from the provided {@link ResultSet}.
     * @param set The {@link ResultSet} to load.
     * @return An optional containing a member, or empty if it failed to load.
     */
    public Optional<Member> load(@NotNull ResultSet set) {
        return Optional.empty(); // TODO implement loading.
    }

    public void saveAllAsync() {
        List<Member> clone = List.copyOf(loadedMembers.values());
        Bukkit.getScheduler().runTaskAsynchronously(
            Guilds.get(),
            () -> clone.forEach(this::save)
        );
    }

    public void saveAll() {
        loadedMembers.values().forEach(this::save);
    }

    public void save(@NotNull Member member) {
        // TODO implement saving.
    }
    
}
