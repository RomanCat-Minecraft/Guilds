package uk.firedev.guilds.placeholder.global;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.placeholders.IPlaceholder;
import uk.firedev.guilds.guild.GuildManager;

public class TotalGuildsPlaceholder implements IPlaceholder {

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("total_guilds");
    }

    @Override
    public @Nullable String parse(@Nullable OfflinePlayer player, @NonNull String identifier) {
        return String.valueOf(GuildManager.getInstance().getAllGuilds().size());
    }

}
