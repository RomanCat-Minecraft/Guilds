package uk.firedev.guilds.utils;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.guilds.Guilds;

import java.math.BigDecimal;

public class EconomyHelper {

    public static EconomyResponse withdraw(@NotNull Player player, double amount) {
        return Guilds.getInstance().getEconomy().withdrawPlayer(player, amount);
    }

    public static EconomyResponse deposit(@NotNull Player player, double amount) {
        return Guilds.getInstance().getEconomy().depositPlayer(player, amount);
    }

    public static boolean has(@NotNull Player player, double amount) {
        return Guilds.getInstance().getEconomy().has(player, amount);
    }

}
