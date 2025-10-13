package uk.firedev.guilds.command.subcommand;

import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.guilds.command.argument.MemberArgument;
import uk.firedev.guilds.command.argument.RankArgument;
import uk.firedev.guilds.command.requirement.CommandRequirement;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.guild.rank.RankPermission;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.Objects;

public class RankSubcommand {

    public static Argument<String> rank() {
        return new LiteralArgument("rank")
            .withRequirement(CommandRequirement.requireInGuild())
            .executesPlayer(info -> {
                Member member = MemberManager.getInstance().getMember(info.sender());
                Rank rank = member.getGuildRank();
                if (rank == null) {
                    info.sender().sendPlainMessage("You are not in a guild.");
                    return;
                }
                info.sender().sendPlainMessage("Your guild rank is: " + rank.getDisplay());
            })
            .then(set())
            .then(rename());
    }

    private static Argument<String> set() {
        return new LiteralArgument("set")
            .withRequirement(RankPermission.MEMBER_RANK)
            .thenNested(
                MemberArgument.get(),
                RankArgument.get()
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You are not in a guild.");
                            return;
                        }
                        Member member = Objects.requireNonNull(info.args().getUnchecked("member"));
                        Rank rank = Objects.requireNonNull(info.args().getUnchecked("rank"));
                        guild.setRank(info.sender(), member, rank);
                    })
            );
    }

    private static Argument<String> rename() {
        return new LiteralArgument("rename")
            .withRequirement(RankPermission.GUILD_RANKS)
            .thenNested(
                RankArgument.getWithOwner(),
                new StringArgument("name")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You are not in a guild.");
                            return;
                        }
                        Rank rank = Objects.requireNonNull(info.args().getUnchecked("rank"));
                        String name = Objects.requireNonNull(info.args().getUnchecked("name"));
                        guild.renameRank(info.sender(), rank, name);
                    })
            );
    }

}
