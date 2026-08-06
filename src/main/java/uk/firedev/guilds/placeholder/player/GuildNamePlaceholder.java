package uk.firedev.guilds.placeholder.player;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.placeholders.IPlaceholder;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.member.MemberManager;

public class GuildNamePlaceholder implements IPlaceholder {

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("guild_name");
    }

    @Override
    public @Nullable String parse(@Nullable OfflinePlayer player, @NonNull String identifier) {
        if (player == null) {
            return null;
        }
        Guild guild = MemberManager.getInstance().getMemberGuild(player);
        return guild == null ? "No Guild" : guild.getName();
    }

}
