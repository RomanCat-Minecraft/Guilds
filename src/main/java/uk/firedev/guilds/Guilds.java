package uk.firedev.guilds;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.libs.daisylib.DaisyLib;
import uk.firedev.chatchannels.libs.daisylib.external.vault.VaultWrapper;
import uk.firedev.chatchannels.libs.daisylib.logging.Logging;
import uk.firedev.guilds.command.GuildCommand;
import uk.firedev.guilds.command.MainCommand;
import uk.firedev.guilds.config.MainConfig;
import uk.firedev.guilds.config.MessageConfig;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.guild.rank.RankConfig;
import uk.firedev.guilds.member.MemberManager;
import uk.firedev.guilds.placeholder.Placeholders;
import uk.firedev.guilds.config.CurseFilter;
import uk.firedev.guilds.utils.TestingEconomy;

import java.util.List;

public final class Guilds extends JavaPlugin {

    private static Guilds instance;

    private final Logging logging = Logging.logging(this);

    public Guilds() {
        if (instance != null) {
            throw new UnsupportedOperationException(getClass().getName() + " has already been assigned!");
        }
        instance = this;
    }

    public static Guilds get() {
        return instance;
    }

    @Override
    public void onLoad() {}

    @Override
    public void onEnable() {
        // Checks for things like the presence of Vault Economy before attempting to load the plugin.
        checkDependencies();

        // Order is important.
        GuildManager.get().load();
        MemberManager.get().load();

        loadCommands();
        new Placeholders().register();
    }

    @Override
    public void onDisable() {
        // Order is important. Reverse of onEnable.
        MemberManager.get().unload();
        GuildManager.get().unload();
    }

    public void reload() {
        CurseFilter.get().reload();
        MainConfig.get().reload();
        MessageConfig.get().reload();
        RankConfig.get().reload();
        GuildManager.get().reload();
    }

    public @NonNull Logging getLogging() {
        return this.logging;
    }

    private void checkDependencies() {
        new TestingEconomy().register(this, ServicePriority.Highest);
        DaisyLib.get().init(this);
        if (!VaultWrapper.get().isEconomyAvailable()) {
            throw new IllegalStateException("A Vault economy must be loaded to use Guilds!");
        }
    }

    private void loadCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new MainCommand().getCommand());
            commands.registrar().register(
                new GuildCommand().getCommand(),
                List.of("g", "t", "town")
            );
        });
    }

}
