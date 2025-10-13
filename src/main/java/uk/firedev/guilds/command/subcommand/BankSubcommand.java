package uk.firedev.guilds.command.subcommand;

import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.DoubleArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.guilds.command.Requirements;
import uk.firedev.guilds.command.requirement.CommandRequirement;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.guild.rank.RankPermission;

import java.math.BigDecimal;
import java.util.Objects;

public class BankSubcommand {

    public static Argument<String> bank() {
        return new LiteralArgument("bank")
            .withRequirement(CommandRequirement.requireInGuild())
            .then(balance())
            .then(deposit())
            .then(withdraw());
    }

    private static Argument<String> balance() {
        return new LiteralArgument("balance")
            .withRequirement(RankPermission.BANK_VIEW)
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You are not in a guild.");
                    return;
                }
                info.sender().sendPlainMessage("Your guild's bank balance is: " + guild.getBalance());
            });
    }

    private static Argument<String> deposit() {
        return new LiteralArgument("deposit")
            .withRequirement(RankPermission.BANK_DEPOSIT)
            .then(
                new DoubleArgument("amount")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You are not in a guild.");
                            return;
                        }
                        double amount = Objects.requireNonNull(info.args().getUnchecked("amount"));
                        guild.deposit(info.sender(), BigDecimal.valueOf(amount));
                    })
            );
    }

    private static Argument<String> withdraw() {
        return new LiteralArgument("withdraw")
            .withRequirement(RankPermission.BANK_WITHDRAW)
            .then(
                new DoubleArgument("amount")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You are not in a guild.");
                            return;
                        }
                        double amount = Objects.requireNonNull(info.args().getUnchecked("amount"));
                        guild.withdraw(info.sender(), BigDecimal.valueOf(amount));
                    })
            );
    }

}
