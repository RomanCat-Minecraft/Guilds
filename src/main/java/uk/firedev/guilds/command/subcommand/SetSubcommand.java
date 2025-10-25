package uk.firedev.guilds.command.subcommand;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.arguments.OfflinePlayerArgument;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.guilds.command.requirement.CommandRequirement;
import uk.firedev.guilds.config.MainConfig;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.guild.rank.permissions.RankPermission;
import uk.firedev.guilds.config.MessageConfig;

public class SetSubcommand {

    public static ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set")
            .requires(CommandRequirement.requireInGuild())
            .then(open())
            .then(owner())
            .then(name())
            .then(home())
            .then(tax());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> open() {
        return Commands.literal("open")
            .requires(RankPermission.MANAGE_OPEN)
            .then(
                Commands.argument("open", BoolArgumentType.bool())
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
                        boolean open = context.getArgument("open", boolean.class);
                        guild.setOpen(player, open);
                        return 1;
                    })
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> owner() {
        return Commands.literal("owner")
            .requires(RankPermission.TRANSFER)
            .then(
                Commands.argument("owner", OfflinePlayerArgument.create(PlayerHelper::hasPlayerBeenOnServer))
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
                        OfflinePlayer newOwner = context.getArgument("newOwner", OfflinePlayer.class);
                        guild.transfer(newOwner, player);
                        return 1;
                    })
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> name() {
        return Commands.literal("name")
            .requires(RankPermission.MANAGE_NAME)
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
                        String name = context.getArgument("name", String.class);
                        guild.rename(name, player);
                        return 1;
                    })
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> home() {
        return Commands.literal("home")
            .requires(RankPermission.MANAGE_HOME)
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
                guild.setHome(player);
                return 1;
            });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> tax() {
        return Commands.literal("tax")
            .requires(RankPermission.MANAGE_TAX)
            .then(
                Commands.argument("amount", DoubleArgumentType.doubleArg(0, MainConfig.getInstance().getMaxGuildTax()))
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
                        double tax = context.getArgument("amount", double.class);
                        guild.setTax(player, tax);
                        return 1;
                    })
            );
    }

}
