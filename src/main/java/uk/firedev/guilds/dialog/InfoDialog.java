package uk.firedev.guilds.dialog;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import uk.firedev.guilds.config.MessageConfig;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.utils.InfoDialogBuilder;

import java.util.List;

public record InfoDialog(InfoDialogBuilder builder) {

    public InfoDialog(@NotNull Guild guild) {
        List<?> content = MessageConfig.getInstance().getInfoContent();
        this(new InfoDialogBuilder()
            .addReplacer(guild.getReplacer())
            .withTitle(MessageConfig.getInstance().getInfoTitle())
            .withContent(content));
    }

    @Override
    public @NotNull InfoDialogBuilder builder() {
        return this.builder;
    }

    public void open(@NotNull Audience audience) {
        builder.open(audience);
    }

}
