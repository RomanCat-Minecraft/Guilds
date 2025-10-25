package uk.firedev.guilds.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.ArgumentBase;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

import java.util.List;
import java.util.function.Predicate;

public class MemberArgument implements ArgumentBase<Member, String> {

    private static final DynamicCommandExceptionType UNKNOWN_MEMBER = new DynamicCommandExceptionType(name ->
        MessageComponentSerializer.message().serialize(Component.text("Unknown member: " + name))
    );
    private static final DynamicCommandExceptionType CANNOT_BE_USED = new DynamicCommandExceptionType(name ->
        MessageComponentSerializer.message().serialize(Component.text("This member cannot be used: " + name))
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

    @Override
    public List<String> getSuggestions(@NotNull CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return List.of();
        }
        Guild guild = MemberManager.getInstance().getMember(player).getGuild();
        if (guild == null) {
            return List.of();
        }
        return guild.getMembersRaw().keySet().stream()
            .filter(filter)
            .map(Member::getUsername)
            .filter(s -> !s.equals("N/A"))
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
    public Member convert(String nativeType) throws CommandSyntaxException {
        Member member = MemberManager.getInstance().getMemberByName(nativeType);
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
