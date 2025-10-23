package uk.firedev.guilds.command.subcommand;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.entity.Player;
import uk.firedev.guilds.command.requirement.CommandRequirement;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.guild.rank.permissions.RankPermission;

import java.math.BigDecimal;
import java.util.Objects;

public class BankSubcommand {

    public static ArgumentBuilder<CommandSourceStack, ?> bank() {
        return Commands.literal("bank")
            .requires(sender ->
                RankPermission.BANK_VIEW.test(sender) || RankPermission.BANK_DEPOSIT.test(sender) || RankPermission.BANK_WITHDRAW.test(sender)
            )
            .then(balance())
            .then(deposit())
            .then(withdraw());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> balance() {
        return Commands.literal("balance")
            .requires(RankPermission.BANK_VIEW)
            .executes(context -> {
                Player player = CommandRequirement.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                Guild guild = GuildManager.getInstance().getByMember(player.getUniqueId());
                if (guild == null) {
                    player.sendPlainMessage("You are not in a guild.");
                    return 1;
                }
                player.sendPlainMessage("Your guild's bank balance is: " + guild.getBalance());
                return 1;
            });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> deposit() {
        return Commands.literal("deposit")
            .requires(RankPermission.BANK_DEPOSIT)
            .then(
                Commands.argument("amount", DoubleArgumentType.doubleArg(1))
                    .executes(context -> {
                        Player player = CommandRequirement.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        Guild guild = GuildManager.getInstance().getByMember(player.getUniqueId());
                        if (guild == null) {
                            player.sendPlainMessage("You are not in a guild.");
                            return 1;
                        }
                        double amount = context.getArgument("amount", Double.class);
                        guild.deposit(player, BigDecimal.valueOf(amount));
                        return 1;
                    })
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> withdraw() {
        return Commands.literal("withdraw")
            .requires(RankPermission.BANK_WITHDRAW)
            .then(
                Commands.argument("amount", DoubleArgumentType.doubleArg(1))
                    .executes(context -> {
                        Player player = CommandRequirement.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        Guild guild = GuildManager.getInstance().getByMember(player.getUniqueId());
                        if (guild == null) {
                            player.sendPlainMessage("You are not in a guild.");
                            return 1;
                        }
                        double amount = context.getArgument("amount", Double.class);
                        guild.withdraw(player, BigDecimal.valueOf(amount));
                        return 1;
                    })
            );
    }

}
