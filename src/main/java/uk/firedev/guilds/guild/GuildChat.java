package uk.firedev.guilds.guild;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.chatchannels.api.ConfigChatChannel;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

public class GuildChat extends ConfigChatChannel {

    private static final GuildChat instance = new GuildChat();

    private GuildChat() {
        super("guild-chat.yml", "guild-chat.yml", Guilds.getInstance(), true);
    }

    public static @NotNull GuildChat getInstance() {
        return instance;
    }

    @NotNull
    @Override
    public ComponentSingleMessage defaultFormat() {
        return ComponentMessage.componentMessage(
            "<gray>[<yellow>{guild}<gray>] <white>{rank} {name} ➻ {message}</white>"
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
    public @Nullable Replacer replacer(@NotNull Player player) {
        Member member = MemberManager.getInstance().getMember(player);
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
