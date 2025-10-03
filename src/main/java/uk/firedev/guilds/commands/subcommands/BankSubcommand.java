package uk.firedev.guilds.commands.subcommands;

import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.DoubleArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.guilds.guilds.Guild;
import uk.firedev.guilds.guilds.GuildManager;

import java.math.BigDecimal;
import java.util.Objects;

public class BankSubcommand {

    public static Argument<String> bank() {
        return new LiteralArgument("bank")
            .then(balance())
            .then(deposit())
            .then(withdraw());
    }

    private static Argument<String> balance() {
        return new LiteralArgument("balance")
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You do not own a guild.");
                    return;
                }
                info.sender().sendPlainMessage("Your guild's bank balance is: " + guild.getBalance());
            });
    }

    private static Argument<String> deposit() {
        return new LiteralArgument("deposit")
            .then(
                new DoubleArgument("amount")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You do not own a guild.");
                            return;
                        }
                        double amount = Objects.requireNonNull(info.args().getUnchecked("amount"));
                        guild.deposit(info.sender(), BigDecimal.valueOf(amount));
                    })
            );
    }

    private static Argument<String> withdraw() {
        return new LiteralArgument("withdraw")
            .then(
                new DoubleArgument("amount")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You do not own a guild.");
                            return;
                        }
                        double amount = Objects.requireNonNull(info.args().getUnchecked("amount"));
                        guild.withdraw(info.sender(), BigDecimal.valueOf(amount));
                    })
            );
    }

}
