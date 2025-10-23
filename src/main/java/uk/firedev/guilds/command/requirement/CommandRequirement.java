package uk.firedev.guilds.command.requirement;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.function.Predicate;

public class CommandRequirement {

    public static Predicate<CommandSourceStack> getPlayer(@NotNull Predicate<Player> playerPredicate) {
        return sender -> {
            if (!(sender.getSender() instanceof Player player)) {
                return false;
            }
            return playerPredicate.test(player);
        };
    }

    public static Predicate<CommandSourceStack> getMember(@NotNull Predicate<Member> memberPredicate) {
        return sender -> {
            Member member = MemberManager.getInstance().getMemberByAudience(sender.getSender());
            if (member == null) {
                return false;
            }
            return memberPredicate.test(member);
        };
    }

    public static Predicate<CommandSourceStack> requireNotInGuild() {
        return sender -> {
            if (!(sender.getSender() instanceof Player player)) {
                return false;
            }
            Member member = MemberManager.getInstance().getMember(player);
            return member.getGuild() == null && member.getGuildRank() == null;
        };
    }

    public static Predicate<CommandSourceStack> requireInGuild() {
        return sender -> {
            if (!(sender.getSender() instanceof Player player)) {
                return false;
            }
            Member member = MemberManager.getInstance().getMember(player);
            return member.getGuild() != null && member.getGuildRank() != null;
        };
    }

    public static @Nullable Player requirePlayer(CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return null;
        }
        return player;
    }

}
