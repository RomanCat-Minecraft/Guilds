package uk.firedev.guilds.utils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import uk.firedev.chatchannels.libs.daisylib.external.vault.VaultWrapper;
import uk.firedev.guilds.Guilds;

import java.math.BigDecimal;

public class MessageUtil {

    public static String prepareLocation(@NotNull Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public static String prepareChunk(@NotNull Chunk chunk) {
        return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
    }

    public static String formatEconomy(double amount) {
        return VaultWrapper.get().getEconomy().format(amount);
    }

}
