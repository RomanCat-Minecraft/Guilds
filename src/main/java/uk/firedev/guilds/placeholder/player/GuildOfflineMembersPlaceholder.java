package uk.firedev.guilds.placeholder.player;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.chatchannels.libs.daisylib.placeholders.IPlaceholder;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.member.MemberManager;

public class GuildOfflineMembersPlaceholder implements IPlaceholder {

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("guild_offline_members");
    }

    @Override
    public @Nullable String parse(@Nullable OfflinePlayer player, @NonNull String identifier) {
        if (player == null) {
            return null;
        }
        Guild guild = MemberManager.get().getMemberGuild(player);
        return guild == null ? "No Guild" : String.valueOf(guild.getMembersRaw().size() - guild.getOnlineMembers().size());
    }

}
