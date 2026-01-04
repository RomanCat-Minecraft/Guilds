package uk.firedev.guilds.dialog;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.builders.dialog.DialogBuilder;
import uk.firedev.daisylib.builders.dialog.InformationDialogBuilder;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;
import uk.firedev.guilds.config.MessageConfig;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.utils.MessageUtil;

import java.util.List;

public class InfoDialog {

    private final InformationDialogBuilder builder;

    public InfoDialog(@NotNull Guild guild) {
        List<?> content = MessageConfig.getInstance().getInfoContent();
        this.builder = DialogBuilder.information()
            .addReplacer(getReplacer(guild))
            .withTitle(MessageConfig.getInstance().getInfoTitle())
            .withContent(content);
    }

    public @NotNull InformationDialogBuilder getBuilder() {
        return this.builder;
    }

    public void open(@NotNull Audience audience) {
        builder.open(audience);
    }

    private Replacer getReplacer(Guild guild) {
        return Replacer.replacer()
            .addReplacement("{name}", guild.getName())
            .addReplacement("{owner}", guild.getOwner().getUsername())
            .addReplacement("{board}", guild.getBoard() == null ? "N/A" : guild.getBoard())
            .addReplacement("{claimed}", guild.getClaims().size())
            .addReplacement("{max-claims}", "∞")
            .addReplacement("{balance}", MessageUtil.formatEconomy(guild.getBalance()))
            .addReplacement("{upkeep}", "N/A")
            .addReplacement("{tax}", MessageUtil.formatEconomy(guild.getTax()))
            .addReplacement("{open}", guild.isOpen())
            .addReplacement("{visitors}", guild.getAllowVisits());
    }

}
