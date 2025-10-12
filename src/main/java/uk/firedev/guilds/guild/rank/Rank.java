package uk.firedev.guilds.guild.rank;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Rank {
    OWNER("Owner"),
    OFFICER("Officer"),
    TREASURER("Treasurer"),
    RECRUITER("Recruiter"),
    MEMBER("Member");

    private final String defDisplay;

    Rank(@NotNull String defDisplay) {
        this.defDisplay = defDisplay;
    }

    public @Nullable ConfigurationSection getConfig() {
        return RankConfig.getInstance().getConfig().getConfigurationSection(toString().toLowerCase());
    }

    public @NotNull String getDisplay() {
        ConfigurationSection config = getConfig();
        return config == null ? defDisplay : config.getString("display", defDisplay);
    }

}
