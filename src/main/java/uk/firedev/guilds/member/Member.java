package uk.firedev.guilds.member;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.messages.message.ComponentMessage;
import uk.firedev.guilds.config.MessageConfig;
import uk.firedev.guilds.exception.UnknownMemberException;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.guild.rank.RankType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Member {

    private final @NotNull OfflinePlayer player;
    private @Nullable Guild guild;
    private final @NotNull List<UUID> guildInvites = new ArrayList<>();

    public Member(@NotNull UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player.getFirstPlayed() == 0) {
            throw new UnknownMemberException("Member has never played before! " + uuid);
        }
        this.player = player;
    }

    public static @NotNull Member load(@NotNull ResultSet set) throws SQLException, IllegalArgumentException {
        Member member = new Member(UUID.fromString(set.getString("uuid")));
        String guildId = set.getString("guild");
        if (guildId != null) {
            member.guild = GuildManager.get().getByUuid(UUID.fromString(guildId));
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

    public boolean hasGuild() {
        return guild != null;
    }

    public void setGuild(@Nullable Guild guild) {
        this.guild = guild;
    }

    public @NotNull String getUsername() {
        String name = player.getName();
        if (name == null) {
            throw new IllegalStateException("OfflinePlayer has no name: " + player.getUniqueId());
        }
        return name;
    }

    public @NotNull List<UUID> getGuildInvites() {
        return guildInvites;
    }

    public @Nullable Rank getGuildRank() {
        if (guild == null) {
            return null;
        }
        return guild.getMemberRank(this);
    }

    public void setGuildRank(@NotNull Rank rank) {
        if (guild == null) {
            return;
        }
        guild.setMemberRank(this, rank);
    }

    public @Nullable RankType getGuildRankType() {
        if (guild == null) {
            return null;
        }
        return guild.getMemberRankType(this);
    }

    public void setGuildRankType(@NotNull RankType rankType) {
        if (guild == null) {
            return;
        }
        guild.setMemberRankType(this, rankType);
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
            MessageConfig.get().getInviteSelfMessage().send(inviter);
            return;
        }
        if (this.guild != null) {
            MessageConfig.get().getInviteAlreadyInGuildMessage().send(inviter);
            return;
        }
        Player member = getPlayer();
        if (member == null) {
            MessageConfig.get().getPlayerNotOnlineMessage().send(inviter);
            return;
        }
        if (guildInvites.contains(guild.getId())) {
            MessageConfig.get().getInviteAlreadyInvitedMessage().send(inviter);
            return;
        }
        guildInvites.add(guild.getId());
        // Tell the guild members.
        guild.broadcastOnline(
            MessageConfig.get().getInviteSentMessage(guild, member.getName())
        );
        // Tell the invited member.
        MessageConfig.get().getInviteInvitedMessage(guild).send(member);
    }

    public void accept(@NotNull Guild guild) {
        boolean removed = guildInvites.remove(guild.getId());
        if (removed) {
            guild.addMember(this);
        } else {
            sendOnlineMessage(MessageConfig.get().getInviteNotInvitedMessage(guild));
        }
    }

    public boolean hasInvite(@NotNull Guild guild) {
        return guildInvites.contains(guild.getId());
    }

    // Messaging

    /**
     * Sends a {@link ComponentMessage} to this member.
     * @param message The message to send.
     */
    public void sendMessage(@NotNull ComponentMessage<?, ?> message) {
        Player online = getPlayer();
        if (online == null) {
            // TODO if the player is offline, send mail.
        } else {
            message.send(online);
        }
    }

    /**
     * Sends a {@link ComponentMessage} to this member if they are online.
     * @param message The message to send.
     */
    public void sendOnlineMessage(@NotNull ComponentMessage<?, ?> message) {
        Player online = getPlayer();
        if (online != null) {
            message.send(online);
        }
    }

    // Misc

    public void updateCommandRequirements() {
        Player online = getPlayer();
        if (online != null) {
            online.updateCommands();
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
