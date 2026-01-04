package uk.firedev.guilds.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.argument.ArgumentBase;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;

import java.util.List;
import java.util.function.Predicate;

public class GuildArgument implements ArgumentBase<Guild, String> {

    private static final DynamicCommandExceptionType UNKNOWN_GUILD = new DynamicCommandExceptionType(name ->
        MessageComponentSerializer.message().serialize(Component.text("Unknown guild: " + name))
    );
    private static final DynamicCommandExceptionType CANNOT_BE_USED = new DynamicCommandExceptionType(name ->
        MessageComponentSerializer.message().serialize(Component.text("This guild cannot be used: " + name))
    );

    private final Predicate<Guild> filter;

    private GuildArgument(@NotNull Predicate<Guild> filter) {
        this.filter = filter;
    }

    public static GuildArgument get() {
        return new GuildArgument(guild -> true);
    }

    public static GuildArgument getWithPredicate(@NotNull Predicate<Guild> filter) {
        return new GuildArgument(filter);
    }

    @Override
    public List<String> getSuggestions(@NotNull CommandContext<CommandSourceStack> commandContext) {
        return GuildManager.getInstance().getAllGuilds().stream()
            .filter(filter)
            .map(Guild::getName)
            .toList();
    }

    /**
     * Converts the value from the native type to the custom argument type.
     *
     * @param nativeType native argument provided value
     * @return converted value
     * @throws CommandSyntaxException if an exception occurs while parsing
     * @see #convert(Object, Object)
     */
    @Override
    public Guild convert(String nativeType) throws CommandSyntaxException {
        Guild guild = GuildManager.getInstance().getByName(nativeType);
        if (guild == null) {
            throw UNKNOWN_GUILD.create(nativeType);
        }
        if (!filter.test(guild)) {
            throw CANNOT_BE_USED.create(nativeType);
        }
        return guild;
    }

    /**
     * Gets the native type that this argument uses,
     * the type that is sent to the client.
     *
     * @return native argument type
     */
    @NotNull
    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

}
