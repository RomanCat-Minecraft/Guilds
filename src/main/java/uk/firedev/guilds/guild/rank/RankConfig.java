package uk.firedev.guilds.guild.rank;

import uk.firedev.chatchannels.libs.daisylib.config.BasicConfig;
import uk.firedev.chatchannels.libs.daisylib.config.ConfigBase;
import uk.firedev.guilds.Guilds;

public class RankConfig extends BasicConfig {

    private static final RankConfig instance = new RankConfig();

    private RankConfig() {
        super("ranks.yml", "ranks.yml", Guilds.get());
    }

    public static RankConfig get() {
        return instance;
    }

}
