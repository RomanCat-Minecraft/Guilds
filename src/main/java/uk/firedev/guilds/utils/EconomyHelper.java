package uk.firedev.guilds.utils;

import net.milkbowl.vault2.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.guilds.Guilds;

import java.math.BigDecimal;

public class EconomyHelper {

    public static EconomyResponse withdraw(@NotNull Player player, double amount) {
        return Guilds.getInstance().getEconomy().withdraw("Guilds", player.getUniqueId(), BigDecimal.valueOf(amount));
    }

    public static EconomyResponse deposit(@NotNull Player player, double amount) {
        return Guilds.getInstance().getEconomy().deposit("Guilds", player.getUniqueId(), BigDecimal.valueOf(amount));
    }

    public static boolean has(@NotNull Player player, double amount) {
        return Guilds.getInstance().getEconomy().has("Guilds", player.getUniqueId(), BigDecimal.valueOf(amount));
    }

}
