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

public class RecruiterRank extends Rank {

    public RecruiterRank(@NotNull Guild guild) {
        super(guild);
    }

    @NotNull
    @Override
    public String getDefaultDisplay() {
        return "Recruiter";
    }

    @NotNull
    @Override
    public List<RankPermission> getDefaultPermissions() {
        return List.of(
            RankPermission.MEMBER_INVITE
        );
    }

    @Nullable
    @Override
    public ConfigurationSection getConfig() {
        return RankConfig.get().getConfig().getConfigurationSection("recruiter");
    }

    @NotNull
    @Override
    public List<RankPermission> getPermissions() {
        return LoadingUtil.mergeLists(
            guild.getMemberRank().getPermissions(),
            getDefaultPermissions()
        );
    }

    @NotNull
    @Override
    public RankType getType() {
        return RankType.RECRUITER;
    }

}
