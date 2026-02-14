package uk.firedev.guilds;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.util.VaultManager;
import uk.firedev.guilds.command.GuildCommand;
import uk.firedev.guilds.command.MainCommand;
import uk.firedev.guilds.config.MainConfig;
import uk.firedev.guilds.config.MessageConfig;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.guild.rank.RankConfig;
import uk.firedev.guilds.placeholder.Placeholders;
import uk.firedev.guilds.config.CurseFilter;

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

        GuildManager.getInstance().load();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new MainCommand().getCommand());
            commands.registrar().register(new GuildCommand().getCommand());
        });
        Placeholders.init(this);
    }

    @Override
    public void onDisable() {}

    public void reload() {
        CurseFilter.getInstance().reload();
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        RankConfig.getInstance().reload();
        GuildManager.getInstance().reload();
    }

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
