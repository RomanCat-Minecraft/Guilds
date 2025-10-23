package uk.firedev.guilds.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.chatchannels.data.PlayerData;
import uk.firedev.daisylib.command.arguments.OfflinePlayerArgument;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.guilds.claim.Claim;
import uk.firedev.guilds.command.argument.GuildArgument;
import uk.firedev.guilds.command.requirement.CommandRequirement;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildChat;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.guild.rank.permissions.RankPermission;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;
import uk.firedev.guilds.utils.TeleportWarmup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Branches handled in separate classes
import static uk.firedev.guilds.command.subcommand.BankSubcommand.bank;
import static uk.firedev.guilds.command.subcommand.RankSubcommand.rank;

public class GuildCommand {

    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("guild")
            .requires(stack -> stack.getSender().hasPermission("guild.command.guild"))
            // Management Subcommands
            .then(create())
            .then(disband())
            .then(claim())
            .then(unclaim())
            .then(transfer())
            .then(rename())
            .then(setHome())
            .then(bank())
            .then(invite())
            .then(setPublic())
            .then(rank())
            // Member Subcommands
            .then(home())
            .then(join())
            .then(leave())
            .then(chat())
            // Info Subcommands
            .then(here())
            .then(list())
            .then(members())
            .build();
    }

    // Management

    private ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.argument("create", StringArgumentType.string())
            .requires(CommandRequirement.requireNotInGuild())
            .then(
                Commands.argument("name", StringArgumentType.string())
                    .executes(context -> {
                        Player player = CommandRequirement.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        String name = context.getArgument("name", String.class);
                        GuildManager.getInstance().createGuild(name, player);
                        player.updateCommands();
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> disband() {
        return Commands.literal("disband")
            .requires(RankPermission.GUILD_DISBAND)
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
                GuildManager.getInstance().disbandGuild(guild, player);
                // We can do this even after deletion as the object still exists.
                guild.updateCommandRequirements();
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> claim() {
        return Commands.literal("claim")
            .requires(RankPermission.LAND_CLAIM)
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
                guild.claimChunk(player);
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> unclaim() {
        return Commands.literal("unclaim")
            .requires(RankPermission.LAND_UNCLAIM)
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
                guild.unclaimChunk(player.getChunk(), player);
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> transfer() {
        return Commands.literal("transfer")
            .requires(RankPermission.GUILD_TRANSFER)
            .then(
                Commands.argument("newOwner", OfflinePlayerArgument.create(PlayerHelper::hasPlayerBeenOnServer))
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
                        OfflinePlayer newOwner = context.getArgument("newOwner", OfflinePlayer.class);
                        guild.transfer(newOwner, player);
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> rename() {
        return Commands.literal("rename")
            .requires(RankPermission.GUILD_RENAME)
            .then(
                Commands.argument("name", StringArgumentType.string())
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
                        String name = context.getArgument("name", String.class);
                        guild.rename(name, player);
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> setHome() {
        return Commands.literal("sethome")
            .requires(RankPermission.GUILD_SETHOME)
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
                guild.setHome(player);
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> invite() {
        return Commands.literal("invite")
            .requires(RankPermission.MEMBER_INVITE)
            .then(
                Commands.argument("player", PlayerArgument.create())
                    .executes(context -> {
                        Player sender = CommandRequirement.requirePlayer(context.getSource());
                        if (sender == null) {
                            return 1;
                        }
                        Guild guild = GuildManager.getInstance().getByMember(sender.getUniqueId());
                        if (guild == null) {
                            sender.sendPlainMessage("You are not in a guild.");
                            return 1;
                        }
                        Player invited = context.getArgument("player", Player.class);
                        guild.invite(sender, invited);
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> setPublic() {
        return Commands.literal("public")
            .requires(RankPermission.GUILD_SETPUBLIC)
            .then(
                Commands.argument("public", BoolArgumentType.bool())
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
                        boolean isPublic = context.getArgument("public", boolean.class);
                        guild.setPublic(player, isPublic);
                        return 1;
                    })
            );
    }

    // Member

    private ArgumentBuilder<CommandSourceStack, ?> home() {
        return Commands.literal("home")
            .requires(CommandRequirement.requireInGuild())
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
                Location home = guild.getHome();
                if (home == null) {
                    player.sendPlainMessage("Your guild has no home!");
                    return 1;
                }
                TeleportWarmup.teleportWarmup(player, home).start();
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> join() {
        return Commands.literal("join")
            .requires(CommandRequirement.requireNotInGuild())
            .then(
                Commands.argument("guild", GuildArgument.get())
                    .executes(context -> {
                        Player player = CommandRequirement.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        Guild guild = context.getArgument("guild", Guild.class);
                        Member member = MemberManager.getInstance().getMember(player);
                        if (guild.isPublic()) {
                            guild.addMember(member);
                            return 1;
                        }
                        member.accept(guild);
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> leave() {
        return Commands.literal("leave")
            .requires(CommandRequirement.requireInGuild())
            .executes(context -> {
                Player player = CommandRequirement.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                Member member = MemberManager.getInstance().getMember(player);
                Guild guild = member.getGuild();
                if (guild == null) {
                    player.sendPlainMessage("You are not in a guild.");
                    return 1;
                }
                guild.leave(member);
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> chat() {
        return Commands.literal("chat")
            .requires(CommandRequirement.requireInGuild())
            .then(
                Commands.argument("message", StringArgumentType.greedyString())
                    .executes(context -> {
                        Player sender = CommandRequirement.requirePlayer(context.getSource());
                        if (sender == null) {
                            return 1;
                        }
                        String message = context.getArgument("message", String.class);
                        GuildChat.getInstance().sendMessage(
                            sender,
                            ComponentMessage.componentMessage(message).get()
                        );
                        return 1;
                    })
            )
            .executes(context -> {
                Player sender = CommandRequirement.requirePlayer(context.getSource());
                if (sender == null) {
                    return 1;
                }
                new PlayerData(sender).setActiveChannel(GuildChat.getInstance());
                return 1;
            });
    }

    // Information

    private ArgumentBuilder<CommandSourceStack, ?> list() {
        return Commands.literal("list")
            .executes(context -> {
                String list = GuildManager.getInstance().getAllGuilds().stream()
                    .map(Guild::getName)
                    .collect(Collectors.joining(", "));
                context.getSource().getSender().sendMessage(Component.text("Guild List: " + list));
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> here() {
        return Commands.literal("here")
            .executes(context -> {
                Player player = CommandRequirement.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                Claim claim = Claim.claim(player.getChunk());
                Guild owner = claim.getOwner();
                if (owner == null) {
                    player.sendPlainMessage("There is no guild at this location!");
                    return 1;
                }
                // TODO expand when there's actual data to show.
                player.sendPlainMessage("The guild at this location is: " + owner.getName());
                listMembers(owner, player);
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> members() {
        return Commands.literal("members")
            .executes(context -> {
                Player player = CommandRequirement.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                Member member = MemberManager.getInstance().getMember(player);
                Guild guild = member.getGuild();
                if (guild == null) {
                    player.sendPlainMessage("You are not in a guild.");
                    return 1;
                }
                listMembers(guild, player);
                return 1;
            })
            .then(
                Commands.argument("guild", GuildArgument.get())
                    .executes(context -> {
                        Player player = CommandRequirement.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        Guild guild = context.getArgument("guild", Guild.class);
                        listMembers(guild, player);
                        return 1;
                    })
            );
    }

    private static void listMembers(@NotNull Guild guild, @NotNull Player player) {
        List<Component> components = new ArrayList<>();
        guild.getMembersRaw().forEach((member, rank) -> {
            String username = member.getUsername();
            if (username.equals("N/A")) {
                return;
            }
            components.add(
                Component.text(username).hoverEvent(
                    HoverEvent.showText(Component.text("Rank: " + rank.getDisplay()))
                )
            );
        });
        Component finalComponent = Component.text(guild.getName() + " Members: ").append(Component.join(JoinConfiguration.commas(true), components));
        player.sendMessage(finalComponent);
    }

}
