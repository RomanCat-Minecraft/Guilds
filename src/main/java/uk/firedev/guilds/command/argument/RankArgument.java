package uk.firedev.guilds.command.argument;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.ArgumentBuilder;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.CustomArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.guilds.guild.rank.Rank;

import java.util.Arrays;
import java.util.function.Predicate;

public class RankArgument {

    public static Argument<Rank> get() {
        return getWithPredicateSuggestions(rank -> true);
    }

    public static Argument<Rank> getWithPredicateSuggestions(@NotNull Predicate<Rank> predicate) {
        return new CustomArgument<>(new StringArgument("rank"), info -> {
            Rank rank = ObjectUtils.getEnumValue(Rank.class, info.input());
            if (rank == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("Unknown rank: ").appendArgInput()
                );
            }
            return rank;
        }).includeSuggestions(
            ArgumentBuilder.getAsyncSuggestions(info ->
                Arrays.stream(Rank.values())
                    .filter(predicate)
                    .map(rank -> rank.toString().toLowerCase())
                    .toArray(String[]::new)
            )
        );
    }
    
}
