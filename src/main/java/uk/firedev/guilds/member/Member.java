package uk.firedev.guilds.member;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.guilds.exception.UnknownMemberException;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.messagelib.message.ComponentMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Member {

    private final @NotNull OfflinePlayer player;
    private @Nullable Guild guild;
    private @NotNull List<UUID> guildInvites = new ArrayList<>();

    public Member(@NotNull UUID uuid) {
        OfflinePlayer player = PlayerHelper.getOfflinePlayer(uuid);
        if (player == null) {
            throw new UnknownMemberException("Member has never played before! " + uuid);
        }
        this.player = player;
    }

    public static @NotNull Member load(@NotNull ResultSet set) throws SQLException, IllegalArgumentException {
        Member member = new Member(
            UUID.fromString(set.getString("uuid"))
        );
        String guildId = set.getString("guild");
        if (guildId != null) {
            member.guild = GuildManager.getInstance().getByUuid(UUID.fromString(guildId));
        }
        return member;
    }

    // Getters and Setters

    public @NotNull UUID getUuid() {
        return player.getUniqueId();
    }

    public @NotNull OfflinePlayer getOfflinePlayer() {
        return player;
    }

    public @Nullable Player getPlayer() {
        return player.getPlayer();
    }

    public @Nullable Guild getGuild() {
        return guild;
    }

    public void setGuild(@Nullable Guild guild) {
        this.guild = guild;
    }

    public @NotNull String getUsername() {
        String name = player.getName();
        return name == null ? "N/A" : name;
    }

    public @NotNull List<UUID> getGuildInvites() {
        return guildInvites;
    }

    // Invitation

    /**
     * Invites this member to the provided guild.
     * Checks:
     * <p>- If the member is the inviting player
     * <p>- If the member is in a guild
     * <p>- If the member is online
     * <p>- If the member is already invited
     * @param inviter The player sending the invite.
     * @param guild The guild the member is being invited to.
     */
    public void invite(@NotNull Player inviter, @NotNull Guild guild) {
        if (inviter.getUniqueId().equals(getUuid())) {
            inviter.sendPlainMessage("You cannot invite yourself!");
            return;
        }
        if (this.guild != null) {
            inviter.sendPlainMessage("This player is already in a Guild!");
            return;
        }
        Player member = getPlayer();
        if (member == null) {
            inviter.sendPlainMessage("This player is not online!");
            return;
        }
        if (guildInvites.contains(guild.getId())) {
            inviter.sendPlainMessage("This player is already invited to your Guild!");
            return;
        }
        guildInvites.add(guild.getId());
        guild.broadcastOnline(member.getName() + " has been invited to the Guild!");
        member.sendPlainMessage("You have been invited to " + guild.getName());
    }

    public void accept(@NotNull Guild guild) {
        boolean removed = guildInvites.remove(guild.getId());
        if (removed) {
            sendMessage("Accepted invitation from Guild " + guild.getName());
            guild.addMember(this);
        } else {
            sendMessage("You have not been invited to " + guild.getName() + ".");
        }
    }

    public void leaveGuild() {
        if (guild == null) {
            sendMessage("You are not in a guild!");
            return;
        }
        guild.removeMember(this);
        sendMessage("You have left Guild " + guild.getName());
        guild = null;
    }

    public boolean hasInvite(@NotNull Guild guild) {
        return guildInvites.contains(guild.getId());
    }

    // Messaging

    /**
     * Sends a plaintext message to this member.
     * @param message The message to send.
     */
    public void sendMessage(@NotNull String message) {
        Component component = Component.text(message);
        sendMessage(ComponentMessage.componentMessage(component));
    }

    /**
     * Sends a {@link ComponentMessage} to this member.
     * @param message The message to send.
     */
    public void sendMessage(@NotNull ComponentMessage message) {
        Player online = getPlayer();
        if (online == null) {
            // TODO if the player is offline, send mail.
        } else {
            message.send(online);
        }
    }

    // Misc

    public void updateCommandRequirements() {
        Player online = getPlayer();
        if (online != null) {
            CommandAPI.updateRequirements(online);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Member member)) {
            return false;
        }
        return member.getUuid().equals(this.getUuid());
    }

}
