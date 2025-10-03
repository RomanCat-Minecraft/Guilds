package uk.firedev.guilds.member;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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
        return member == null ? new Member(uuid) : member;
    }

    public @NotNull Member getMember(@NotNull OfflinePlayer player) {
        return getMember(player.getUniqueId());
    }
    
}
