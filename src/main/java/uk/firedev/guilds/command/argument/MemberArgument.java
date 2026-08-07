package uk.firedev.guilds.command.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class MemberArgument implements CustomArgumentType.Converted<Member, String> {

    private static final DynamicCommandExceptionType UNKNOWN_MEMBER = new DynamicCommandExceptionType(name ->
        new LiteralMessage("Unknown member: " + name)
    );
    private static final DynamicCommandExceptionType CANNOT_BE_USED = new DynamicCommandExceptionType(name ->
        new LiteralMessage("This member cannot be used: " + name)
    );

    private final Predicate<Member> filter;

    private MemberArgument(@NotNull Predicate<Member> filter) {
        this.filter = filter;
    }

    public static MemberArgument get() {
        return new MemberArgument(member -> true);
    }

    public static MemberArgument getWithPredicate(@NotNull Predicate<Member> filter) {
        return new MemberArgument(filter);
    }

    public List<String> getSuggestions(@NotNull CommandContext<?> context) {
        if (!(context.getSource() instanceof CommandSourceStack stack)) {
            return List.of();
        }
        if (!(stack instanceof Player player)) {
            return List.of();
        }
        Guild guild = MemberManager.get().getMember(player.getUniqueId()).getGuild();
        if (guild == null) {
            return List.of();
        }
        return guild.getMembersRaw().keySet().stream()
            .filter(filter)
            .map(Member::getUsername)
            .filter(s -> !s.equals("N/A"))
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
    public @NonNull Member convert(@NonNull String nativeType) throws CommandSyntaxException {
        Member member = MemberManager.get().getMember(nativeType);
        if (member == null) {
            throw UNKNOWN_MEMBER.create(nativeType);
        }
        if (!filter.test(member)) {
            throw CANNOT_BE_USED.create(nativeType);
        }
        return member;
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
