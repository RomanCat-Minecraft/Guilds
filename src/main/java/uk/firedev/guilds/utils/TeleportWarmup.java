package uk.firedev.guilds.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.utils.WarmupHandler;
import uk.firedev.guilds.Guilds;

public class TeleportWarmup {

    public static void start(@NotNull Player teleportingPlayer, @NotNull Location location) {
        // TODO configurable messages and warmup time.
        WarmupHandler.create(3, teleportingPlayer)
            .withWaitAction((timeLeft, player) -> {
                player.sendActionBar(Component.text("Teleporting in " + timeLeft));
            })
            .withCompletionAction(player -> {
                player.teleportAsync(location).thenAccept(success -> {
                    if (success) {
                        player.sendPlainMessage("Successfully teleported.");
                    } else {
                        player.sendPlainMessage("Failed to teleport. Please try again.");
                    }
                });
            }).start(Guilds.INSTANCE);
    }

}
