package uk.firedev.guilds.placeholder;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.placeholders.IPlaceholder;
import uk.firedev.daisylib.placeholders.PlaceholderReceiver;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.placeholder.player.GuildBankPlaceholder;
import uk.firedev.guilds.placeholder.player.GuildNamePlaceholder;
import uk.firedev.guilds.placeholder.player.GuildOfflineMembersPlaceholder;
import uk.firedev.guilds.placeholder.player.GuildOnlineMembersPlaceholder;
import uk.firedev.guilds.placeholder.player.GuildOpenPlaceholder;
import uk.firedev.guilds.placeholder.player.GuildRankPlaceholder;
import uk.firedev.guilds.placeholder.player.GuildRankRawPlaceholder;
import uk.firedev.guilds.placeholder.player.GuildTotalMembersPlaceholder;
import uk.firedev.guilds.placeholder.global.TotalGuildsPlaceholder;

import java.util.ArrayList;
import java.util.List;

public class Placeholders extends PlaceholderReceiver {

    private final List<IPlaceholder> placeholders = new ArrayList<>();

    public Placeholders() {
        // Global
        placeholders.add(new TotalGuildsPlaceholder());

        // Player
        placeholders.add(new GuildBankPlaceholder());
        placeholders.add(new GuildNamePlaceholder());
        placeholders.add(new GuildOfflineMembersPlaceholder());
        placeholders.add(new GuildOnlineMembersPlaceholder());
        placeholders.add(new GuildOpenPlaceholder());
        placeholders.add(new GuildRankPlaceholder());
        placeholders.add(new GuildRankRawPlaceholder());
        placeholders.add(new GuildTotalMembersPlaceholder());
    }

    @Override
    public @NotNull String getIdentifier() {
        return "guilds";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", Guilds.get().getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return Guilds.get().getPluginMeta().getVersion();
    }

    @Override
    public @NonNull List<@NonNull IPlaceholder> getCustomPlaceholders() {
        return placeholders;
    }

}
