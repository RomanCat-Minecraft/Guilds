package uk.firedev.guilds.command.subcommand;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import uk.firedev.guilds.command.argument.MemberArgument;
import uk.firedev.guilds.command.argument.RankTypeArgument;
import uk.firedev.guilds.command.requirement.CommandRequirement;
import uk.firedev.guilds.config.MessageConfig;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.guild.rank.RankType;
import uk.firedev.guilds.guild.rank.permissions.RankPermission;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

public class RankSubcommand {

    public static ArgumentBuilder<CommandSourceStack, ?> rank() {
        return Commands.literal("rank")
            .requires(CommandRequirement.requireInGuild())
            .executes(context -> {
                Player player = CommandRequirement.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                Member member = MemberManager.getInstance().getMember(player);
                Guild guild = member.getGuild();
                Rank rank = member.getGuildRank();
                if (guild == null || rank == null) {
                    MessageConfig.getInstance().getNotInGuildMessage().send(player);
                    return 1;
                }
                MessageConfig.getInstance().getRankShowMessage(guild, rank).send(player);
                return 1;
            })
            .then(set())
            .then(rename());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set")
            .requires(RankPermission.MANAGE_RANKS)
            .then(
                Commands.argument("member", MemberArgument.get())
                    .then(
                        Commands.argument("rank", RankTypeArgument.get())
                            .executes(context -> {
                                Player player = CommandRequirement.requirePlayer(context.getSource());
                                if (player == null) {
                                    return 1;
                                }
                                Guild guild = GuildManager.getInstance().getByMember(player.getUniqueId());
                                if (guild == null) {
                                    MessageConfig.getInstance().getNotInGuildMessage().send(player);
                                    return 1;
                                }
                                Member member = context.getArgument("member", Member.class);
                                RankType rank = context.getArgument("rank", RankType.class);
                                guild.setRank(player, member, rank);
                                return 1;
                            })
                    )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> rename() {
        return Commands.literal("rename")
            .requires(RankPermission.MANAGE_RANKS)
            .then(
                Commands.argument("rank", RankTypeArgument.getWithOwner())
                    .then(
                        Commands.argument("name", StringArgumentType.string())
                            .executes(context -> {
                                Player player = CommandRequirement.requirePlayer(context.getSource());
                                if (player == null) {
                                    return 1;
                                }
                                Guild guild = GuildManager.getInstance().getByMember(player.getUniqueId());
                                if (guild == null) {
                                    MessageConfig.getInstance().getNotInGuildMessage().send(player);
                                    return 1;
                                }
                                RankType rank = context.getArgument("rank", RankType.class);
                                String name = context.getArgument("name", String.class);
                                guild.renameRank(player, rank, name);
                                return 1;
                            })
                    )
            );
    }

}
