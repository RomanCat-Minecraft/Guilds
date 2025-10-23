package uk.firedev.guilds.guild.rank.ranks;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.guild.rank.RankConfig;
import uk.firedev.guilds.guild.rank.permissions.RankPermission;
import uk.firedev.guilds.utils.LoadingUtil;

import java.util.List;

public class TreasurerRank extends Rank {

    public TreasurerRank(@NotNull Guild guild) {
        super(guild);
    }

    @NotNull
    @Override
    public String getDefaultDisplay() {
        return "Treasurer";
    }

    @NotNull
    @Override
    public List<RankPermission> getDefaultPermissions() {
        return List.of(
            RankPermission.BANK_DEPOSIT,
            RankPermission.BANK_WITHDRAW,
            RankPermission.BANK_VIEW
        );
    }

    @Nullable
    @Override
    public ConfigurationSection getConfig() {
        return RankConfig.getInstance().getConfig().getConfigurationSection("treasurer");
    }

    @NotNull
    @Override
    public List<RankPermission> getPermissions() {
        return LoadingUtil.mergeLists(
            guild.getRecruiterRank().getPermissions(),
            getDefaultPermissions()
        );
    }

}
