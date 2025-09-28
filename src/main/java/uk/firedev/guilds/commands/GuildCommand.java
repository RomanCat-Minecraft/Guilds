package uk.firedev.guilds.commands;

import org.bukkit.OfflinePlayer;
import uk.firedev.daisylib.command.arguments.OfflinePlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.guilds.claims.Claim;
import uk.firedev.guilds.guilds.Guild;
import uk.firedev.guilds.guilds.GuildManager;

import java.util.Objects;

public class GuildCommand {

    public static CommandTree getCommand() {
        return new CommandTree("guild")
            .withShortDescription("Guild command")
            .withPermission("guilds.command.guild")
            .then(create())
            .then(delete())
            .then(claim())
            .then(unclaim())
            .then(setOwner());
    }

    private static Argument<String> create() {
        return new LiteralArgument("create")
            .then(
                new StringArgument("name")
                    .executesPlayer(info -> {
                        String name = Objects.requireNonNull(info.args().getUnchecked("name"));
                        GuildManager.getInstance().createGuild(name, info.sender());
                    })
            );
    }

    private static Argument<String> delete() {
        return new LiteralArgument("delete")
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You do not own a guild.");
                    return;
                }
                GuildManager.getInstance().deleteGuild(guild, info.sender());
            });
    }

    private static Argument<String> claim() {
        return new LiteralArgument("claim")
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You do not own a guild.");
                    return;
                }
                guild.claimChunk(info.sender().getChunk(), info.sender());
            });
    }

    private static Argument<String> unclaim() {
        return new LiteralArgument("unclaim")
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You do not own a guild.");
                    return;
                }
                guild.unclaimChunk(info.sender().getChunk(), info.sender());
            });
    }

    private static Argument<String> setOwner() {
        return new LiteralArgument("setOwner")
            .then(
                OfflinePlayerArgument.create("newOwner")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You do not own a guild.");
                            return;
                        }
                        OfflinePlayer newOwner = Objects.requireNonNull(info.args().getUnchecked("owner"));
                        guild.setOwner(newOwner, info.sender());
                    })
            );
    }

}
