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
        return Commands.literal("guilds")
            .requires(sender -> sender.getSender().hasPermission("guilds.command"))
            .then(reload())
            .build();
    }

    private ArgumentBuilder<CommandSourceStack, ?> reload() {
        return Commands.literal("reload")
            .requires(CommandRequirement.requireNotInGuild())
            .executes(context -> {
                Guilds.getInstance().reload();
                MessageConfig.getInstance().getReloadedMessage().send(context.getSource().getSender());
                return 1;
            });
    }

}
