package uk.firedev.guilds.command.argument;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.ArgumentBuilder;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.CustomArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.function.Predicate;

public class MemberArgument {

    public static Argument<Member> get() {
        return getWithPredicateSuggestions(member -> true);
    }

    public static Argument<Member> getWithPredicateSuggestions(@NotNull Predicate<Member> predicate) {
        return new CustomArgument<>(new StringArgument("member"), info -> {
            Member targetMember = MemberManager.getInstance().getMember(Bukkit.getOfflinePlayer(info.input()));
            Guild targetGuild = targetMember.getGuild();
            if (targetGuild == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("Unknown member: ").appendArgInput()
                );
            }
            return targetMember;
        }).includeSuggestions(
            ArgumentBuilder.getAsyncSuggestions(info -> {
                if (!(info.sender() instanceof Player player)) {
                    return new String[0];
                }
                Guild guild = MemberManager.getInstance().getMember(player).getGuild();
                if (guild == null) {
                    return new String[0];
                }
                return guild.getMembersRaw().keySet().stream()
                    .map(Member::getUsername)
                    .filter(s -> !s.equals("N/A"))
                    .toArray(String[]::new);
            })
        );
    }
    
}
