package uk.firedev.guilds;

import org.bukkit.plugin.java.JavaPlugin;

public final class Guilds extends JavaPlugin {

    public static Guilds INSTANCE;

    public Guilds() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException(getClass().getName() + " has already been assigned!");
        }
        INSTANCE = this;
    }

    @Override
    public void onLoad() {}

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}
