package uk.firedev.guilds.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.utils.CommonUtils;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.rank.RankType;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class RankTypeArgument implements CustomArgumentType.Converted<RankType, String> {

    private static final DynamicCommandExceptionType INVALID_RANK = new DynamicCommandExceptionType(name ->
        MessageComponentSerializer.message().serialize(Component.text("Invalid rank: " + name))
    );

    private final boolean includeOwner;

    private RankTypeArgument(boolean includeOwner) {
        this.includeOwner = includeOwner;
    }

    public static RankTypeArgument get() {
        return new RankTypeArgument(false);
    }

    public static RankTypeArgument getWithOwner() {
        return new RankTypeArgument(true);
    }

    public List<String> getSuggestions(@NotNull CommandContext<?> context) {
        if (!(context.getSource() instanceof CommandSourceStack stack)) {
            return List.of();
        }
        Member member = MemberManager.get().getMember(stack.getSender());
        if (member == null) {
            return List.of();
        }
        Guild guild = member.getGuild();
        if (guild == null) {
            return List.of();
        }
        List<RankType> ranks = RankType.getTypesAsList(includeOwner);
        return ranks.stream()
            .map(type -> type.toString().toLowerCase())
            .toList();
    }

    @NonNull
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NonNull CommandContext<S> context, @NonNull SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        getSuggestions(context).stream()
            .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(remaining))
            .forEach(builder::suggest);
        return builder.buildFuture();
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
    public @NonNull RankType convert(@NonNull String nativeType) throws CommandSyntaxException {
        RankType type = CommonUtils.getEnumValue(RankType.class, nativeType);
        if (type == null) {
            throw INVALID_RANK.create(nativeType);
        }
        if (!includeOwner && type == RankType.OWNER) {
            throw INVALID_RANK.create(nativeType);
        }
        return type;
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
