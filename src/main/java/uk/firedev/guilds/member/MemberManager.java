package uk.firedev.guilds.member;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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

    public @NotNull List<Member> getAllMembers() {
        return List.copyOf(loadedMembers.values());
    }
    
}
