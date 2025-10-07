package uk.firedev.guilds.command;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.arguments.OfflinePlayerArgument;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.BooleanArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.guilds.claim.Claim;
import uk.firedev.guilds.command.argument.GuildArgument;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;
import uk.firedev.guilds.utils.TeleportWarmup;

import java.util.Objects;
import java.util.stream.Collectors;

// Branches handled in separate classes
import static uk.firedev.guilds.command.subcommand.BankSubcommand.bank;

public class GuildCommand {

    public static CommandTree getCommand() {
        return new CommandTree("guild")
            .withShortDescription("Guild command")
            .withPermission("guild.command.guild")
            // Management Subcommands
            .then(create())
            .then(delete())
            .then(claim())
            .then(unclaim())
            .then(transfer())
            .then(rename())
            .then(setHome())
            .then(bank())
            .then(invite())
            .then(setPublic())
            // Member Subcommands
            .then(home())
            .then(join())
            .then(leave())
            // Info Subcommands
            .then(here())
            .then(list())
            .then(members());
    }

    // Management

    private static Argument<String> create() {
        return new LiteralArgument("create")
            .withRequirement(Requirements.requireNotInGuild())
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
            .withRequirement(Requirements.requireInGuild())
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You are not in a guild.");
                    return;
                }
                GuildManager.getInstance().deleteGuild(guild, info.sender());
                // We can do this even after deletion as the object still exists.
                guild.updateCommandRequirements();
            });
    }

    private static Argument<String> claim() {
        return new LiteralArgument("claim")
            .withRequirement(Requirements.requireInGuild())
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You are not in a guild.");
                    return;
                }
                guild.claimChunk(info.sender());
            });
    }

    private static Argument<String> unclaim() {
        return new LiteralArgument("unclaim")
            .withRequirement(Requirements.requireInGuild())
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You are not in a guild.");
                    return;
                }
                guild.unclaimChunk(info.sender().getChunk(), info.sender());
            });
    }

    private static Argument<String> transfer() {
        return new LiteralArgument("transfer")
            .withRequirement(Requirements.requireInGuild())
            .then(
                OfflinePlayerArgument.create("newOwner")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You are not in a guild.");
                            return;
                        }
                        OfflinePlayer newOwner = Objects.requireNonNull(info.args().getUnchecked("newOwner"));
                        guild.transfer(newOwner, info.sender());
                    })
            );
    }

    private static Argument<String> rename() {
        return new LiteralArgument("rename")
            .withRequirement(Requirements.requireInGuild())
            .then(
                new StringArgument("name")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You are not in a guild.");
                            return;
                        }
                        String name = Objects.requireNonNull(info.args().getUnchecked("name"));
                        guild.rename(name, info.sender());
                    })
            );
    }

    private static Argument<String> setHome() {
        return new LiteralArgument("sethome")
            .withRequirement(Requirements.requireInGuild())
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You are not in a guild.");
                    return;
                }
                guild.setHome(info.sender());
            });
    }

    private static Argument<String> invite() {
        return new LiteralArgument("invite")
            .withRequirement(Requirements.requireInGuild())
            .then(
                PlayerArgument.create("player")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You are not in a guild.");
                            return;
                        }
                        Player invited = Objects.requireNonNull(info.args().getUnchecked("player"));
                        guild.invite(info.sender(), invited);
                    })
            );
    }

    private static Argument<String> setPublic() {
        return new LiteralArgument("setpublic")
            .withRequirement(Requirements.requireInGuild())
            .then(
                new BooleanArgument("public")
                    .executesPlayer(info -> {
                        Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                        if (guild == null) {
                            info.sender().sendPlainMessage("You are not in a guild.");
                            return;
                        }
                        boolean isPublic = Objects.requireNonNull(info.args().getUnchecked("public"));
                        guild.setPublic(info.sender(), isPublic);
                    })
            );
    }

    private static Argument<String> members() {
        return new LiteralArgument("members")
            .executesPlayer(info -> {
                Member member = MemberManager.getInstance().getMember(info.sender());
                Guild guild = member.getGuild();
                if (guild == null) {
                    info.sender().sendPlainMessage("You are not in a guild.");
                    return;
                }
                listMembers(guild, info.sender());
            })
            .then(
                GuildArgument.get()
                    .executesPlayer(info -> {
                        Guild guild = Objects.requireNonNull(info.args().getUnchecked("guild"));
                        listMembers(guild, info.sender());
                    })
            );
    }

    private static void listMembers(@NotNull Guild guild, @NotNull Player player) {
        String list = guild.getMembersRaw(true).stream()
            .map(Member::getUsername)
            .filter(s -> !s.equals("N/A"))
            .collect(Collectors.joining(", "));
        player.sendPlainMessage(guild.getName() + " Members: " + list);
    }

    // Member

    private static Argument<String> home() {
        return new LiteralArgument("home")
            .withRequirement(Requirements.requireInGuild())
            .executesPlayer(info -> {
                Guild guild = GuildManager.getInstance().getByMember(info.sender().getUniqueId());
                if (guild == null) {
                    info.sender().sendPlainMessage("You are not in a guild.");
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

    private static Argument<String> join() {
        return new LiteralArgument("join")
            .withRequirement(Requirements.requireNotInGuild())
            .then(
                GuildArgument.get()
                    .executesPlayer(info -> {
                        Guild guild = Objects.requireNonNull(info.args().getUnchecked("guild"));
                        Member member = MemberManager.getInstance().getMember(info.sender());
                        if (guild.isPublic()) {
                            guild.addMember(member);
                            return;
                        }
                        member.accept(guild);
                    })
            );
    }

    private static Argument<String> leave() {
        return new LiteralArgument("leave")
            .withRequirement(Requirements.requireInGuild())
            .executesPlayer(info -> {
                Member member = MemberManager.getInstance().getMember(info.sender());
                Guild guild = member.getGuild();
                if (guild == null) {
                    info.sender().sendPlainMessage("You are not in a guild.");
                    return;
                }
                guild.leave(member);
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

}
