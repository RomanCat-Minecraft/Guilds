package uk.firedev.guilds.guild.rank.permissions;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.function.Predicate;

public enum RankPermission implements Predicate<CommandSourceStack> {
    DISBAND,
    TRANSFER,
    MANAGE_NAME,
    MANAGE_HOME,
    MANAGE_OPEN,
    MANAGE_RANKS,
    MANAGE_TAX,
    MANAGE_BOARD,
    MANAGE_VISIT,
    LAND_CLAIM,
    LAND_UNCLAIM,
    BANK_WITHDRAW,
    BANK_DEPOSIT,
    BANK_VIEW,
    MEMBER_INVITE;

    RankPermission() {}

    public boolean hasPermission(@NotNull Member member) {
        Rank rank = member.getGuildRank();
        if (rank == null) {
            return false;
        }
        return rank.hasPermission(this);
    }

    @Override
    public boolean test(CommandSourceStack sender) {
        if (!(sender.getSender() instanceof Player player)) {
            return false;
        }
        Member member = MemberManager.getInstance().getMember(player);
        return hasPermission(member);
    }

}
