package uk.firedev.guilds.claim.protections;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.jspecify.annotations.NonNull;
import uk.firedev.guilds.claim.Claim;
import uk.firedev.guilds.guild.Guild;

/**
 * Big scary listener for claim protection.
 */
public class ClaimProtectionListener implements Listener {

    private boolean isPermitted(@NonNull Location location, @NonNull Entity entity) {
        return isPermitted(location, entity, true);
    }

    private boolean isPermitted(@NonNull Location location, @NonNull Entity entity, boolean message) {
        Player player = switch (entity) {
            case Player p -> p;
            case Projectile projectile when projectile.getShooter() instanceof Player p -> p;
            case TNTPrimed tnt when tnt.getSource() instanceof Player p -> p;
            case Creeper creeper when creeper.isIgnited() && creeper.getIgniter() instanceof Player p -> p;
            default -> null;
        };
        if (player == null) {
            return true;
        }
        Guild owner = Claim.claim(location.getChunk()).getOwner();
        if (owner == null) {
            return true;
        }
        boolean permitted = owner.isMember(player.getUniqueId());
        if (permitted) {
            return true;
        }
        if (message) {
            player.sendPlainMessage("You are not permitted to do that here.");
        }
        return false;
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        if (!isPermitted(event.getMount().getLocation(), event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (!isPermitted(event.getFrom(), event.getPlayer())) {
            event.setCancelled(true);
        } else if (!isPermitted(event.getTo(), event.getPlayer())) {
            event.setCanCreatePortal(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent event) {
        if (!isPermitted(event.getBed().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!isPermitted(player.getLocation(), player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!isPermitted(event.getItem().getLocation(), event.getEntity(), false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (!isPermitted(event.getHook().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStand(PlayerArmorStandManipulateEvent event) {
        if (!isPermitted(event.getRightClicked().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeash(PlayerLeashEntityEvent event) {
        if (!isPermitted(event.getEntity().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        if (!isPermitted(event.getEntity().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        if (!isPermitted(event.getRightClicked().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractBlock(PlayerInteractEvent event) {
        Location interacted = event.getInteractionPoint();
        if (interacted != null && !isPermitted(interacted, event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (!isPermitted(event.getBlockClicked().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!isPermitted(event.getBlockClicked().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEntityEvent event) {
        if (!isPermitted(event.getEntity().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!isPermitted(event.getEntity().getLocation(), event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCombust(EntityCombustByEntityEvent event) {
        if (!isPermitted(event.getEntity().getLocation(), event.getCombuster())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBookTake(PlayerTakeLecternBookEvent event) {
        if (!isPermitted(event.getLectern().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (!isPermitted(event.getBlock().getLocation(), player)) {
            event.setCancelled(true);
        }
    }

}
