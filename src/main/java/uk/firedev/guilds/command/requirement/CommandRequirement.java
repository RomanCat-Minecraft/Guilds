package uk.firedev.guilds.command.requirement;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.function.Predicate;

public class CommandRequirement {

    public static Predicate<CommandSender> getPlayer(@NotNull Predicate<Player> playerPredicate) {
        return sender -> {
            if (!(sender instanceof Player player)) {
                return false;
            }
            return playerPredicate.test(player);
        };
    }

    public static Predicate<CommandSender> getMember(@NotNull Predicate<Member> memberPredicate) {
        return sender -> {
            Member member = MemberManager.getInstance().getMemberByAudience(sender);
            if (member == null) {
                return false;
            }
            return memberPredicate.test(member);
        };
    }

    public static Predicate<CommandSender> requireNotInGuild() {
        return sender -> {
            if (!(sender instanceof Player player)) {
                return false;
            }
            Member member = MemberManager.getInstance().getMember(player);
            return member.getGuild() == null && member.getGuildRank() == null;
        };
    }

    public static Predicate<CommandSender> requireInGuild() {
        return sender -> {
            if (!(sender instanceof Player player)) {
                return false;
            }
            Member member = MemberManager.getInstance().getMember(player);
            return member.getGuild() != null && member.getGuildRank() != null;
        };
    }

}
