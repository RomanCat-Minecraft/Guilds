package uk.firedev.guilds.command.argument;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.ArgumentBuilder;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.CustomArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;

import java.util.function.Predicate;

public class GuildArgument {

    public static Argument<Guild> get() {
        return getWithPredicateSuggestions(guild -> true);
    }

    public static Argument<Guild> getWithPredicateSuggestions(@NotNull Predicate<Guild> predicate) {
        return new CustomArgument<>(new StringArgument("guild"), info -> {
            Guild guild = GuildManager.getInstance().getByName(info.input());
            if (guild == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                    new CustomArgument.MessageBuilder("Unknown guild: ").appendArgInput()
                );
            }
            return guild;
        }).includeSuggestions(
            ArgumentBuilder.getAsyncSuggestions(info ->
                GuildManager.getInstance().getAllGuilds().stream()
                    .filter(predicate)
                    .map(Guild::getName)
                    .toArray(String[]::new)
            )
        );
    }

}
