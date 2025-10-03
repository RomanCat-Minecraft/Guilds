package uk.firedev.guilds.member;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.guilds.exception.UnknownMemberException;
import uk.firedev.messagelib.message.ComponentMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Member {

    private final @NotNull OfflinePlayer player;
    private @Nullable UUID guild;

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
            member.guild = UUID.fromString(guildId);
        }
        return member;
    }

    // Getters

    public @NotNull UUID getUuid() {
        return player.getUniqueId();
    }

    public @NotNull OfflinePlayer getOfflinePlayer() {
        return player;
    }

    public @Nullable Player getPlayer() {
        return player.getPlayer();
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
            // TODO mail.
        } else {
            message.send(online);
        }
    }

}
