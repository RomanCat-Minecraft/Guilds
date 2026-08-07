package uk.firedev.guilds.member;

import net.kyori.adventure.audience.Audience;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.guilds.guild.Guild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MemberManager {

    private static final MemberManager instance = new MemberManager();

    protected final Map<UUID, Member> loadedMembers = new HashMap<>();
    protected final Map<String, UUID> nameIndex = new HashMap<>();

    private MemberManager() {}

    public static MemberManager get() {
        return instance;
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
    
}
