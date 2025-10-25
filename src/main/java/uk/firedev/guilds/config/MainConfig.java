package uk.firedev.guilds.config;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.guilds.Guilds;

public class MainConfig extends ConfigBase {

    private static final MainConfig instance = new MainConfig();

    private MainConfig() {
        super("config.yml", "config.yml", Guilds.getInstance());
    }

    public static @NotNull MainConfig getInstance() {
        return instance;
    }

    public double getMaxGuildTax() {
        return getConfig().getDouble("guild.max-tax", 5000.0);
    }

    public double getMaxVisitCost() {
        return getConfig().getDouble("guild.max-visit-cost", 5000.0);
    }

}
