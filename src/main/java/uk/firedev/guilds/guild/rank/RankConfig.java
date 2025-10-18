package uk.firedev.guilds.guild.rank;

import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.guilds.Guilds;

public class RankConfig extends ConfigBase {

    private static final RankConfig instance = new RankConfig();

    private RankConfig() {
        super("ranks.yml", "ranks.yml", Guilds.getInstance());
    }

    public static RankConfig getInstance() {
        return instance;
    }

}
