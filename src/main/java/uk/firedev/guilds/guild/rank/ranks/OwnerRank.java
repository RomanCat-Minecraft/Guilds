package uk.firedev.guilds.guild.rank.ranks;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.guild.rank.RankConfig;
import uk.firedev.guilds.guild.rank.RankPermission;

import java.util.Arrays;
import java.util.List;

public class OwnerRank extends Rank {

    public OwnerRank(@NotNull Guild guild) {
        super(guild);
    }

    @Override
    public @NotNull String getDefaultDisplay() {
        return "Owner";
    }

    @NotNull
    @Override
    public List<RankPermission> getDefaultPermissions() {
        return List.of();
    }

    @Override
    public @Nullable ConfigurationSection getConfig() {
        return RankConfig.getInstance().getConfig().getConfigurationSection("owner");
    }

    @Override
    public boolean hasPermission(@NotNull RankPermission permission) {
        return true;
    }

    @NotNull
    @Override
    public String getDisplay() {
        return getDefaultDisplay();
    }

    @NotNull
    @Override
    public List<RankPermission> getPermissions() {
        return Arrays.asList(RankPermission.values());
    }

}
