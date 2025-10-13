package uk.firedev.guilds.member;

import net.kyori.adventure.audience.Audience;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MemberManager {

    private static final MemberManager instance = new MemberManager();

    protected final Map<UUID, Member> loadedMembers = new HashMap<>();

    private MemberManager() {}

    public static MemberManager getInstance() {
        return instance;
    }

    public @NotNull Member getMember(@NotNull UUID uuid) {
        Member member = loadedMembers.get(uuid);
        // TODO load from db.
        if (member == null) {
            Member newMember = new Member(uuid);
            loadedMembers.put(uuid, newMember);
            return newMember;
        }
        return member;
    }

    public @NotNull Member getMember(@NotNull OfflinePlayer player) {
        return getMember(player.getUniqueId());
    }

    public @Nullable Member getMemberByAudience(@NotNull Audience audience) {
        if (!(audience instanceof Player player)) {
            return null;
        }
        return getMember(player.getUniqueId());
    }

    public @NotNull List<Member> getAllMembers() {
        return List.copyOf(loadedMembers.values());
    }
    
}
