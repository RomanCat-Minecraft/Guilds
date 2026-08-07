package uk.firedev.guilds.guild.rank.ranks;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.guild.rank.RankConfig;
import uk.firedev.guilds.guild.rank.RankType;
import uk.firedev.guilds.guild.rank.permissions.RankPermission;
import uk.firedev.guilds.utils.LoadingUtil;

import java.util.List;

public class OfficerRank extends Rank {

    public OfficerRank(@NotNull Guild guild) {
        super(guild);
    }

    @NotNull
    @Override
    public String getDefaultDisplay() {
        return "Officer";
    }

    @NotNull
    @Override
    public List<RankPermission> getDefaultPermissions() {
        return List.of(
            RankPermission.MANAGE_NAME,
            RankPermission.MANAGE_HOME,
            RankPermission.MANAGE_OPEN,
            RankPermission.LAND_CLAIM,
            RankPermission.LAND_UNCLAIM,
            RankPermission.MANAGE_RANKS,
            RankPermission.MANAGE_BOARD
        );
    }

    @Nullable
    @Override
    public ConfigurationSection getConfig() {
        return RankConfig.get().getConfig().getConfigurationSection("officer");
    }

    @NotNull
    @Override
    public List<RankPermission> getPermissions() {
        return LoadingUtil.mergeLists(
            guild.getTreasurerRank().getPermissions(),
            getDefaultPermissions()
        );
    }

    @NotNull
    @Override
    public RankType getType() {
        return RankType.OFFICER;
    }

}
