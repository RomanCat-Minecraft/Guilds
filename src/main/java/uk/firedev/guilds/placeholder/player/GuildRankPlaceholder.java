package uk.firedev.guilds.placeholder.player;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.chatchannels.libs.daisylib.placeholders.IPlaceholder;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.member.MemberManager;

public class GuildRankPlaceholder implements IPlaceholder {

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("guild_rank");
    }

    @Override
    public @Nullable String parse(@Nullable OfflinePlayer player, @NonNull String identifier) {
        if (player == null) {
            return null;
        }
        Rank rank = MemberManager.get().getMember(player.getUniqueId()).getGuildRank();
        return rank == null ? "No Rank" : rank.getDisplay();
    }

}
