package uk.firedev.guilds.claims;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.guilds.Guild;
import uk.firedev.guilds.guilds.GuildManager;
import uk.firedev.guilds.utils.Keys;

import java.util.UUID;

public class Claim {

    private final int chunkX;
    private final int chunkZ;
    private final String chunkWorld;

    private @Nullable Guild owner;

    private Claim(int chunkX, int chunkZ, @NotNull String chunkWorld) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunkWorld = chunkWorld;
        this.owner = readOwner();
    }

    public static Claim get(@NotNull Chunk chunk) {
        return new Claim(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
    }

    public @NotNull World getWorld() {
        World world = Bukkit.getWorld(chunkWorld);
        if (world == null) {
            throw new UnsupportedOperationException("World not found: " + chunkWorld);
        }
        return world;
    }

    public @NotNull Chunk getChunk() {
        return getWorld().getChunkAt(chunkX, chunkZ);
    }

    public void claim(@NotNull CommandSender sender, @NotNull Guild guild) {
        if (this.owner != null) {
            sender.sendPlainMessage("This chunk is already claimed by " + this.owner.getName());
            return;
        }
        this.owner = guild;
        getChunk().getPersistentDataContainer().set(Keys.CLAIM_OWNER, PersistentDataType.STRING, guild.getOwner().toString());
        sender.sendPlainMessage("Claimed this chunk for your guild.");
    }

    public void unclaim(@NotNull CommandSender sender) {
        if (this.owner == null) {
            sender.sendPlainMessage("This chunk is not claimed.");
            return;
        }
        if (sender instanceof Player player && !this.owner.getOwner().equals(player.getUniqueId())) {
            sender.sendPlainMessage("You are not the owner of this guild.");
            return;
        }
        this.owner = null;
        sender.sendPlainMessage("Unclaimed this chunk.");
    }

    private Guild readOwner() {
        String ownerStr = getChunk().getPersistentDataContainer().get(Keys.CLAIM_OWNER, PersistentDataType.STRING);
        if (ownerStr == null) {
            return null;
        }
        UUID ownerUuid;
        try {
            ownerUuid = UUID.fromString(ownerStr);
        } catch (IllegalArgumentException e) {
            Loggers.warn(Guilds.INSTANCE.getLogger(), "Invalid UUID format for claim owner: " + ownerStr);
            return null;
        }
        Guild guild = GuildManager.getInstance().getByOwner(ownerUuid);
        if (guild == null) {
            Loggers.warn(Guilds.INSTANCE.getLogger(), "Guild not found for owner UUID: " + ownerUuid);
            return null;
        }
        return guild;
    }

    public boolean isOwned() {
        return this.owner != null;
    }

}
