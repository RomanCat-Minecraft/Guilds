package uk.firedev.guilds.guild.protection;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import uk.firedev.guilds.claim.Claim;
import uk.firedev.guilds.guild.Guild;

public class ProtectionListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        Claim claim = Claim.claim(event.getClickedBlock().getChunk());
        Guild owner = claim.getOwner();
        if (owner == null || owner.isMember(event.getPlayer())) {
            return;
        }
        cancel(event.getPlayer(), event);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Claim claim = Claim.claim(event.getBlock().getChunk());
        Guild owner = claim.getOwner();
        if (owner == null || owner.isMember(event.getPlayer())) {
            return;
        }
        cancel(event.getPlayer(), event);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Claim claim = Claim.claim(event.getBlock().getChunk());
        Guild owner = claim.getOwner();
        if (owner == null || owner.isMember(event.getPlayer())) {
            return;
        }
        cancel(event.getPlayer(), event);
    }

    private void cancel(@NotNull Player target, @NotNull Cancellable event) {
        target.sendPlainMessage("You cannot do that here!");
        event.setCancelled(true);
    }

}
