package uk.firedev.guilds.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.command.requirement.CommandRequirement;
import uk.firedev.guilds.config.MessageConfig;

public class MainCommand {

    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("guild")
            .requires(sender -> sender.getSender().hasPermission("guilds.command"))
            .then(reload())
            .build();
    }

    private ArgumentBuilder<CommandSourceStack, ?> reload() {
        return Commands.literal("create")
            .requires(CommandRequirement.requireNotInGuild())
            .then(
                Commands.argument("name", StringArgumentType.string())
                    .executes(context -> {
                        Guilds.getInstance().reload();
                        MessageConfig.getInstance().getReloadedMessage().send(context.getSource().getSender());
                        return 1;
                    })
            );
    }

}
