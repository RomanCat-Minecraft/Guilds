package uk.firedev.guilds.guild.rank;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.guilds.guild.Guild;

import java.util.List;

public abstract class Rank {

    protected final @NotNull Guild guild;

    protected Rank(@NotNull Guild guild) {
        this.guild = guild;
    }

    public abstract @NotNull String getDefaultDisplay();

    public abstract @NotNull List<RankPermission> getDefaultPermissions();

    public abstract @Nullable ConfigurationSection getConfig();

    public boolean hasPermission(@NotNull RankPermission permission) {
        return getPermissions().contains(permission);
    }

    public abstract @NotNull String getDisplay();

    public abstract @NotNull List<RankPermission> getPermissions();

}
