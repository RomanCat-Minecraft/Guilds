package uk.firedev.guilds;

import net.milkbowl.vault2.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.guilds.command.GuildCommand;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.placeholder.Placeholders;

public final class Guilds extends JavaPlugin {

    public static Guilds INSTANCE;

    private Economy economy;

    public Guilds() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException(getClass().getName() + " has already been assigned!");
        }
        INSTANCE = this;
    }

    @Override
    public void onLoad() {}

    @Override
    public void onEnable() {
        // Checks for things like the presence of Vault Economy before attempting to load the plugin.
        checkDependencies();

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
