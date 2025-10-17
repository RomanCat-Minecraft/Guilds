package uk.firedev.guilds.guild;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.api.ChatChannel;
import uk.firedev.chatchannels.api.Messaging;
import uk.firedev.chatchannels.data.PlayerData;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

public class GuildChat extends ChatChannel {

    public GuildChat() {
        super("guild-chat.yml", Guilds.INSTANCE);
    }

    @NotNull
    @Override
    public String name() {
        return "guild";
    }

    @NotNull
    @Override
    public ComponentSingleMessage defaultFormat() {
        return ComponentMessage.componentMessage(
            "<#32CD32>[{guild}] <white>{rank} {name} ➻ {message}</white>"
        );
    }

    @Override
    public boolean hasAccess(@NotNull Player player) {
        Member member = MemberManager.getInstance().getMember(player);
        return member.hasGuild() || member.getGuildRank() != null;
    }

    @Override
    public boolean shouldSendToTarget(@NotNull Player player, @NotNull Player target) {
        Member playerMember = MemberManager.getInstance().getMember(player);
        Guild playerGuild = playerMember.getGuild();
        if (playerGuild == null) {
            return false;
        }
        Member targetMember = MemberManager.getInstance().getMember(target);
        return playerGuild.equals(targetMember.getGuild());
    }

    @Override
    public @Nullable Replacer replacer(@NotNull AsyncChatEvent event) {
        Member member = MemberManager.getInstance().getMember(event.getPlayer());
        Guild guild = member.getGuild();
        Rank rank = member.getGuildRank();
        if (guild == null || rank == null) {
            return null;
        }
        return Replacer.replacer()
            .addReplacement("{guild}", guild.getName())
            .addReplacement("{rank}", rank.getDisplay());
    }

}
