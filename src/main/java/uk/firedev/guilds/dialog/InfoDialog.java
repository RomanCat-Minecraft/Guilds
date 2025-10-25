package uk.firedev.guilds.dialog;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.dialog.InformationDialog;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;
import uk.firedev.guilds.config.MessageConfig;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.utils.MessageUtil;

import java.util.List;

public class InfoDialog implements InformationDialog {

    private final Replacer replacer;

    public InfoDialog(@NotNull Guild guild) {
        this.replacer = getReplacer(guild);
    }

    @NotNull
    @Override
    public Replacer replacer() {
        return replacer;
    }

    @NotNull
    @Override
    public String title() {
        return MessageConfig.getInstance().getInfoTitle();
    }

    @NotNull
    @Override
    public List<String> content() {
        return MessageConfig.getInstance().getInfoContent();
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
