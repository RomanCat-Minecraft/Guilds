package uk.firedev.guilds.guild.rank;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.guilds.guild.Guild;

import java.util.List;

public abstract class Rank {

    protected final @NotNull Guild guild;

    private @NotNull String display;

    protected Rank(@NotNull Guild guild) {
        this.guild = guild;
        this.display = getDefaultDisplay();
    }

    public abstract @NotNull String getDefaultDisplay();

    public abstract @NotNull List<RankPermission> getDefaultPermissions();

    public abstract @Nullable ConfigurationSection getConfig();

    public boolean hasPermission(@NotNull RankPermission permission) {
        return getPermissions().contains(permission);
    }

    public abstract @NotNull List<RankPermission> getPermissions();

    public boolean usingDefaultDisplay() {
        return getDisplay().equals(getDefaultDisplay());
    }

    public @NotNull String getDisplay() {
        return this.display;
    }

    public void setDisplay(@NotNull String display) {
        this.display = display;
    }

}
