package uk.firedev.guilds.dialog;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.builders.dialog.DialogBuilder;
import uk.firedev.daisylib.builders.dialog.InformationDialogBuilder;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;
import uk.firedev.guilds.config.MessageConfig;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.utils.MessageUtil;

import java.util.List;

public record InfoDialog(InformationDialogBuilder builder) {

    public InfoDialog(@NotNull Guild guild) {
        List<?> content = MessageConfig.getInstance().getInfoContent();
        this(DialogBuilder.information()
            .addReplacer(guild.getReplacer())
            .withTitle(MessageConfig.getInstance().getInfoTitle())
            .withContent(content));
    }

    @Override
    public @NotNull InformationDialogBuilder builder() {
        return this.builder;
    }

    public void open(@NotNull Audience audience) {
        builder.open(audience);
    }

}
