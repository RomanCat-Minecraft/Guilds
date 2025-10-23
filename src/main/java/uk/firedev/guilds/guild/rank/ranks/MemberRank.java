package uk.firedev.guilds.guild.rank.ranks;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.guild.rank.RankConfig;
import uk.firedev.guilds.guild.rank.permissions.RankPermission;

import java.util.List;

public class MemberRank extends Rank {

    public MemberRank(@NotNull Guild guild) {
        super(guild);
    }

    @NotNull
    @Override
    public String getDefaultDisplay() {
        return "Member";
    }

    @NotNull
    @Override
    public List<RankPermission> getDefaultPermissions() {
        return List.of(
            RankPermission.BANK_VIEW
        );
    }

    @Nullable
    @Override
    public ConfigurationSection getConfig() {
        return RankConfig.getInstance().getConfig().getConfigurationSection("member");
    }

    @NotNull
    @Override
    public List<RankPermission> getPermissions() {
        return List.of();
    }

}
