package uk.firedev.guilds.placeholder;

import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.placeholders.PlaceholderProvider;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.GuildManager;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.member.Member;
import uk.firedev.guilds.member.MemberManager;

public class Placeholders {

    public static void init(@NotNull Guilds plugin) {
        PlaceholderProvider provider = PlaceholderProvider.create(plugin);

        loadGlobal(provider);
        loadAudience(provider);

        provider.register();
    }

    private static void loadGlobal(@NotNull PlaceholderProvider provider) {
        provider.addGlobalPlaceholder(
            "total_guilds",
            () -> Component.text(GuildManager.getInstance().getAllGuilds().size())
        );
    }

    private static void loadAudience(@NotNull PlaceholderProvider provider) {
        provider.addAudiencePlaceholder("guild_name", audience -> {
            Member member = MemberManager.getInstance().getMemberByAudience(audience);
            if (member == null) {
                return Component.text("You cannot be in a guild.");
            }
            Guild guild = member.getGuild();
            return Component.text(guild == null ? "No guild" : guild.getName());
        });
        provider.addAudiencePlaceholder("guild_bank", audience -> {
            Member member = MemberManager.getInstance().getMemberByAudience(audience);
            if (member == null) {
                return Component.text("You cannot be in a guild.");
            }
            Guild guild = member.getGuild();
            return guild == null ? Component.text("No Guild") : Component.text(guild.getBalance());
        });
        provider.addAudiencePlaceholder("guild_offline_members", audience -> {
            Member member = MemberManager.getInstance().getMemberByAudience(audience);
            if (member == null) {
                return Component.text("You cannot be in a guild.");
            }
            Guild guild = member.getGuild();
            if (guild == null) {
                return Component.text("No Guild");
            }
            // All Members - Online Members = Offline Members
            return Component.text(guild.getMembersRaw().size() - guild.getOnlineMembers().size());
        });
        provider.addAudiencePlaceholder("guild_online_members", audience -> {
            Member member = MemberManager.getInstance().getMemberByAudience(audience);
            if (member == null) {
                return Component.text("You cannot be in a guild.");
            }
            Guild guild = member.getGuild();
            return guild == null ? Component.text("No Guild") : Component.text(guild.getOnlineMembers().size());
        });
        provider.addAudiencePlaceholder("guild_total_members", audience -> {
            Member member = MemberManager.getInstance().getMemberByAudience(audience);
            if (member == null) {
                return Component.text("You cannot be in a guild.");
            }
            Guild guild = member.getGuild();
            return guild == null ? Component.text("No Guild") : Component.text(guild.getMembersRaw().size());
        });
        provider.addAudiencePlaceholder("guild_rank", audience -> {
            Member member = MemberManager.getInstance().getMemberByAudience(audience);
            if (member == null) {
                return Component.text("You cannot be in a guild.");
            }
            Rank rank = member.getGuildRank();
            return Component.text(rank == null ? "No guild" : rank.getDisplay());
        });
        provider.addAudiencePlaceholder("guild_rank_raw", audience -> {
            Member member = MemberManager.getInstance().getMemberByAudience(audience);
            if (member == null) {
                return Component.text("You cannot be in a guild.");
            }
            Rank rank = member.getGuildRank();
            return Component.text(rank == null ? "No guild" : rank.getDefaultDisplay());
        });
        provider.addAudiencePlaceholder("guild_public", audience -> {
            Member member = MemberManager.getInstance().getMemberByAudience(audience);
            if (member == null) {
                return Component.text("You cannot be in a guild.");
            }
            Guild guild = member.getGuild();
            if (guild == null) {
                return Component.text("No guild");
            }
            return Component.text(guild.isPublic());
        });
    }

}
