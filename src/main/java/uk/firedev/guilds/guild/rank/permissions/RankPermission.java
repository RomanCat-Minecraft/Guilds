package uk.firedev.guilds.guild.rank.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.function.Predicate;

public enum RankPermission implements Predicate<CommandSender> {
    GUILD_DISBAND,
    GUILD_TRANSFER,
    GUILD_RENAME,
    GUILD_SETHOME,
    GUILD_SETPUBLIC,
    GUILD_RANKS,
    LAND_CLAIM,
    LAND_UNCLAIM,
    BANK_WITHDRAW,
    BANK_DEPOSIT,
    BANK_VIEW,
    MEMBER_INVITE,
    MEMBER_RANK;

    RankPermission() {
    }

    public boolean hasPermission(@NotNull Member member) {
        Rank rank = member.getGuildRank();
        if (rank == null) {
            return false;
        }
        return rank.hasPermission(this);
    }

    @Override
    public boolean test(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        Member member = MemberManager.getInstance().getMember(player);
        return hasPermission(member);
    }

}
