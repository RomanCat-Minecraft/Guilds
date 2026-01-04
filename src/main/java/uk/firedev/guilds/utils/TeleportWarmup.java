package uk.firedev.guilds.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.util.warmup.WarmupHandler;
import uk.firedev.guilds.Guilds;

// TODO make all messages configurable.
public class TeleportWarmup extends WarmupHandler {

    private final @NotNull Location location;

    private TeleportWarmup(@NotNull Player teleportingPlayer, @NotNull Location location) {
        super(3, teleportingPlayer, Guilds.getInstance());
        this.location = location;
        applyWaitAction();
        applyCompletionAction();
        applyMovementAction();
    }

    private void applyWaitAction() {
        withWaitAction((timeLeft, player) -> {
            player.sendActionBar(Component.text("Teleporting in " + timeLeft));
        });
    }

    private void applyCompletionAction() {
        withCompletionAction(player -> {
            player.teleportAsync(location).thenAccept(success -> {
                if (success) {
                    player.sendActionBar(Component.text("Successfully teleported."));
                } else {
                    player.sendActionBar(Component.text("Failed to teleport. Please try again."));
                }
            });
        });
    }

    private void applyMovementAction() {
        allowMovement(false);
        withMovementAction(player -> {
            player.sendActionBar(Component.text("You moved! Cancelling teleport."));
        });
    }

    public static TeleportWarmup teleportWarmup(@NotNull Player teleportingPlayer, @NotNull Location location) {
        return new TeleportWarmup(teleportingPlayer, location);
    }

}
