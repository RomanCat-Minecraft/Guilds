package uk.firedev.guilds;

import net.milkbowl.vault2.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.guilds.command.GuildCommand;
import uk.firedev.guilds.database.DataManager;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.placeholder.Placeholders;

public final class Guilds extends JavaPlugin {

    private static Guilds instance;

    private Economy economy;

    public Guilds() {
        if (instance != null) {
            throw new UnsupportedOperationException(getClass().getName() + " has already been assigned!");
        }
        instance = this;
    }

    public static Guilds getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {}

    @Override
    public void onEnable() {
        // Checks for things like the presence of Vault Economy before attempting to load the plugin.
        checkDependencies();

        // DataManager has to come first.
        DataManager.getInstance().init(this);
        GuildManager.getInstance().load();
        GuildCommand.getCommand().register(this);
        Placeholders.init(this);
    }

    @Override
    public void onDisable() {}

    public void reload() {}

    public @NotNull Economy getEconomy() {
        return economy;
    }

    private void checkDependencies() {
        Economy economy = VaultManager.getInstance().getEconomy();
        if (economy == null) {
            throw new IllegalStateException("A Vault economy must be loaded to use Guilds!");
        }
        this.economy = economy;
    }

}
