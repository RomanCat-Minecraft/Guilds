package uk.firedev.guilds.config;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.config.BasicConfig;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.guilds.Guilds;

public class MainConfig extends BasicConfig {

    private static final MainConfig instance = new MainConfig();

    private MainConfig() {
        super("config.yml", "config.yml", Guilds.get());
    }

    public static @NotNull MainConfig get() {
        return instance;
    }

    public double getMaxGuildTax() {
        return getConfig().getDouble("guild.max-tax", 5000.0);
    }

    public double getMaxVisitCost() {
        return getConfig().getDouble("guild.max-visit-cost", 5000.0);
    }

}
