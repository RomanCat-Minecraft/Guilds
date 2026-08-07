package uk.firedev.guilds.claim;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.libs.daisylib.utils.CommonUtils;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.database.serialize.DatabaseSerializable;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.utils.Keys;

import java.util.UUID;

public class Claim implements DatabaseSerializable<Claim> {

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

    public static Claim claim(@NotNull Chunk chunk) {
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

    public void setOwner(@NotNull Guild guild) {
        this.owner = guild;
        getChunk().getPersistentDataContainer().set(Keys.CLAIM_OWNER, PersistentDataType.STRING, guild.getId().toString());
    }

    public void removeOwner() {
        this.owner = null;
        getChunk().getPersistentDataContainer().remove(Keys.CLAIM_OWNER);
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
            Guilds.get().getLogging().warn("Invalid UUID format for claim owner: " + ownerStr);
            return null;
        }
        Guild guild = GuildManager.get().getByUuid(ownerUuid);
        if (guild == null) {
            Guilds.get().getLogging().warn("Guild not found with UUID: " + ownerUuid);
            return null;
        }
        return guild;
    }

    public boolean isOwned() {
        return this.owner != null;
    }

    public @Nullable Guild getOwner() {
        return owner;
    }

    // Database

    @Override
    public @NotNull String serialize() {
        return chunkWorld + "," + chunkX + "," + chunkZ;
    }

    public static @Nullable Claim deserialize(@NotNull String id) {
        String[] split = id.split(",", 3);
        if (split.length != 3) {
            return null;
        }
        String world = split[0];
        Integer x = CommonUtils.getInt(split[1]);
        Integer z = CommonUtils.getInt(split[2]);
        if (x == null || z == null) {
            Guilds.get().getLogging().warn("Attempted to deserialize invalid chunk: " + id);
            return null;
        }
        return new Claim(x, z, world);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Claim other)) {
            return false;
        }
        return other.getChunk().equals(this.getChunk());
    }

}
