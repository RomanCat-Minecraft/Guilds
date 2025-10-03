package uk.firedev.guilds.commands;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.arguments.OfflinePlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.guilds.claims.Claim;
import uk.firedev.guilds.guilds.Guild;
import uk.firedev.guilds.guilds.GuildManager;
import uk.firedev.guilds.utils.TeleportWarmup;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// Branches handled in separate classes
import static uk.firedev.guilds.commands.subcommands.BankSubcommand.bank;

public class GuildCommand {

    public static CommandTree getCommand() {
        return new CommandTree("guild")
            .withShortDescription("Guild command")
            .withPermission("guilds.command.guild")
            // Management Subcommands
            .then(create())
            .then(delete())
            .then(claim())
            .then(unclaim())
            .then(transfer())
            .then(rename())
            .then(setHome())
            .then(bank())
            // Member Subcommands
            .then(home())
            .then(here())
            // Info Subcommands
            .then(list());
    }

    // Management

    private static Argument<String> create() {
        return new LiteralArgument("create")
            .withRequirement(requireNotInGuild())
            .then(
                new StringArgument("name")
                    .executesPlayer(info -> {
                        String name = Objects.requireNonNull(info.args().getUnchecked("name"));
                        GuildManager.getInstance().createGuild(name, info.sender());
                        CommandAPI.updateRequirements(info.sender());
                    })
            );
    }

    private static Argument<String> delete() {
        return new LiteralArgument("delete")
            .withRequirement(requireInGuild())
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You do not own a guild.");
                    return;
                }
                GuildManager.getInstance().deleteGuild(guild, info.sender());
                // We can do this even after deletion as the object still exists.
                guild.updateCommandRequirements();
            });
    }

    private static Argument<String> claim() {
        return new LiteralArgument("claim")
            .withRequirement(requireInGuild())
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You do not own a guild.");
                    return;
                }
                guild.claimChunk(info.sender());
            });
    }

    private static Argument<String> unclaim() {
        return new LiteralArgument("unclaim")
            .withRequirement(requireInGuild())
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You do not own a guild.");
                    return;
                }
                guild.unclaimChunk(info.sender().getChunk(), info.sender());
            });
    }

    private static Argument<String> transfer() {
        return new LiteralArgument("transfer")
            .withRequirement(requireInGuild())
            .then(
                OfflinePlayerArgument.create("newOwner")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You do not own a guild.");
                            return;
                        }
                        OfflinePlayer newOwner = Objects.requireNonNull(info.args().getUnchecked("newOwner"));
                        guild.transfer(newOwner, info.sender());
                    })
            );
    }

    private static Argument<String> rename() {
        return new LiteralArgument("rename")
            .withRequirement(requireInGuild())
            .then(
                new StringArgument("name")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You do not own a guild.");
                            return;
                        }
                        String name = Objects.requireNonNull(info.args().getUnchecked("name"));
                        guild.rename(name, info.sender());
                    })
            );
    }

    private static Argument<String> setHome() {
        return new LiteralArgument("sethome")
            .withRequirement(requireInGuild())
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You do not own a guild.");
                    return;
                }
                guild.setHome(info.sender());
            });
    }

    // Member

    private static Argument<String> home() {
        return new LiteralArgument("home")
            .withRequirement(requireInGuild())
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByOwner(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You do not own a guild.");
                    return;
                }
                Location home = guild.getHome();
                if (home == null) {
                    info.sender().sendPlainMessage("Your guild has no home!");
                    return;
                }
                TeleportWarmup.start(info.sender(), home);
            });
    }

    // Information

    private static Argument<String> list() {
        return new LiteralArgument("list")
            .executes(info -> {
                String list = GuildManager.getInstance().getAllGuilds().stream()
                    .map(Guild::getName)
                    .collect(Collectors.joining(", "));
                info.sender().sendPlainMessage("Guild List: " + list);
            });
    }

    private static Argument<String> here() {
        return new LiteralArgument("here")
            .executesPlayer(info -> {
                Claim claim = Claim.claim(info.sender().getChunk());
                Guild owner = claim.getOwner();
                if (owner == null) {
                    info.sender().sendPlainMessage("There is no guild at this location!");
                    return;
                }
                // TODO expand when there's actual data to show.
                info.sender().sendPlainMessage("The guild at this location is: " + owner.getName());
            });
    }

    // Extra

    private static Predicate<CommandSender> requireNotInGuild() {
        return sender -> {
            if (!(sender instanceof Player player)) {
                return false;
            }
            Guild guild = GuildManager.getInstance().getByOwner(player.getUniqueId());
            return guild == null;
        };
    }

    private static Predicate<CommandSender> requireInGuild() {
       return sender -> {
           if (!(sender instanceof Player player)) {
               return false;
           }
           Guild guild = GuildManager.getInstance().getByOwner(player.getUniqueId());
           return guild != null;
       };
    }

}
