package uk.firedev.guilds.guild;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.firedev.guilds.config.MessageConfig;

public class GuildListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Guild guild = GuildManager.get().getByMember(event.getPlayer().getUniqueId());
        if (guild == null) {
            return;
        }
        String board = guild.getBoard();
        if (board == null) {
            return;
        }
        MessageConfig.get().getBoardMessage(guild, board).send(event.getPlayer());
    }

}
