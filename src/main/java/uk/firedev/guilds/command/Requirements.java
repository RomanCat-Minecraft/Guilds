package uk.firedev.guilds.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.function.Predicate;

public class Requirements {

    public static Predicate<CommandSender> requireNotInGuild() {
        return sender -> {
            if (!(sender instanceof Player player)) {
                return false;
            }
            Member member = MemberManager.getInstance().getMember(player);
            return member.getGuild() == null;
        };
    }

    public static Predicate<CommandSender> requireInGuild() {
        return sender -> {
            if (!(sender instanceof Player player)) {
                return false;
            }
            Member member = MemberManager.getInstance().getMember(player);
            return member.getGuild() != null;
        };
    }

}
