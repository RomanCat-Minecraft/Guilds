package uk.firedev.guilds.command.argument;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.ArgumentBuilder;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.CustomArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.MultiLiteralArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.function.Predicate;

public class RankArgument {

    public static Argument<Rank> get() {
        return new CustomArgument<>(new StringArgument("rank"), info -> {
            Member member = MemberManager.getInstance().getMemberByAudience(info.sender());
            if (member == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("You are not a player.")
                );
            }
            Guild guild = member.getGuild();
            if (guild == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("You are not in a guild.")
                );
            }
            switch (info.input()) {
                case "officer" -> {
                    return guild.getOfficerRank();
                }
                case "treasurer" -> {
                    return guild.getTreasurerRank();
                }
                case "recruiter" -> {
                    return guild.getRecruiterRank();
                }
                case "member" -> {
                    return guild.getMemberRank();
                }
                default -> throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("Invalid rank: ").appendArgInput()
                );
            }
        }).includeSuggestions(
            ArgumentBuilder.getAsyncSuggestions(info -> {
                Member member = MemberManager.getInstance().getMemberByAudience(info.sender());
                if (member == null) {
                    return new String[0];
                }
                Guild guild = member.getGuild();
                if (guild == null) {
                    return new String[0];
                }
                return new String[]{
                    "officer",
                    "treasurer",
                    "recruiter",
                    "member"
                };
            })
        );
    }
    
}
