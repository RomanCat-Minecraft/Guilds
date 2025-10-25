package uk.firedev.guilds.config;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.guild.Guild;
import uk.firedev.guilds.guild.rank.Rank;
import uk.firedev.guilds.utils.MessageUtil;

public class MessageConfig extends ConfigBase {

    private static final MessageConfig instance = new MessageConfig();

    private MessageConfig() {
        super("messages.yml", "messages.yml", Guilds.getInstance());
    }

    public static @NotNull MessageConfig getInstance() {
        return instance;
    }

    // Prefix

    public ComponentSingleMessage getPrefix() {
        return getComponentMessage("prefix", "<gray>[Guilds]</gray> ").toSingleMessage();
    }

    public Replacer getPrefixReplacer() {
        return Replacer.replacer().addReplacement("{prefix}", getPrefix());
    }

    public ComponentSingleMessage getGuildPrefix(@NotNull Guild guild) {
        return getComponentMessage("guild-prefix", "<gray>[<yellow>{guild}</yellow>]</gray> ").toSingleMessage()
            .replace("{guild}", guild.getName());
    }

    public Replacer getGuildPrefixReplacer(@NotNull Guild guild) {
        return Replacer.replacer()
            .addReplacement("{guild-prefix}", getGuildPrefix(guild))
            .addReplacement("{guild-name}", guild.getName());
    }

    // Messages

    // Common

    public ComponentMessage getReloadedMessage() {
        return getComponentMessage("reloaded", "{prefix}<white>Successfully reloaded the plugin.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getJoinedGuildMessage(@NotNull Guild guild, @NotNull String name) {
        return getComponentMessage("joined-guild", "{guild-prefix}<white>{name} has joined the guild.")
            .replace("{name}", name)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getNotInGuildMessage() {
        return getComponentMessage("not-in-guild", "{prefix}<white>You are not in a guild.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getNoGuildAtLocationMessage() {
        return getComponentMessage("no-guild-at-location", "{prefix}<white>There is no guild at this location.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getGuildNoPermissionMessage(@NotNull Guild guild) {
        return getComponentMessage("guild.no-permission", "{guild-prefix}<red>You do not have permission.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getPlayerNotOnlineMessage() {
        return getComponentMessage("player-not-online", "{prefix}<red>That player is not online.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getNotMemberMessage(@NotNull Guild guild) {
        return getComponentMessage("transfer.not-member", "{guild-prefix}<white>That player is not a member of this guild.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Create

    public ComponentMessage getCreateInGuildMessage() {
        return getComponentMessage("create.in-guild", "{prefix}<red>You cannot create a Guild until you leave your current one.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getCreateExistsMessage() {
        return getComponentMessage("create.exists", "{prefix}<red>A guild with that name already exists.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getCreateSuccessMessage(@NotNull Guild guild) {
        return getComponentMessage("create.success", "{guild-prefix}<white>Guild has been created!")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Disband

    public ComponentMessage getDisbandInvalidMessage() {
        return getComponentMessage("disband.invalid", "{prefix}<red>That guild does not exist.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getDisbandSuccessMessage(@NotNull Guild guild) {
        return getComponentMessage("disband.success", "{guild-prefix}<white>The guild has been disbanded.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Leave

    public ComponentMessage getLeaveIsOwnerMessage(@NotNull Guild guild) {
        return getComponentMessage("leave.is-owner", "{guild-prefix}<red>The owner cannot leave the guild.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getLeaveNotMemberMessage(@NotNull Guild guild) {
        return getComponentMessage("leave.not-member", "{guild-prefix}<red>You are not a member of this guild.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getLeaveSuccessMessage(@NotNull Guild guild, @NotNull String name) {
        return getComponentMessage("leave.success", "{guild-prefix}<white>{name} has left the guild!")
            .replace("{name}", name)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Invite

    public ComponentMessage getInviteSelfMessage() {
        return getComponentMessage("invite.self", "{prefix}<red>You cannot invite yourself.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getInviteAlreadyInGuildMessage() {
        return getComponentMessage("invite.already-in-guild", "{prefix}<red>That player is already in a guild.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getInviteAlreadyInvitedMessage() {
        return getComponentMessage("invite.already-invited", "{prefix}<red>That player is already invited to your guild.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getInviteAlreadyMemberMessage() {
        return getComponentMessage("invite.already-member", "{prefix}<red>That player is already a member of your guild.")
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getInviteSentMessage(@NotNull Guild guild, @NotNull String name) {
        return getComponentMessage("invite.sent", "{guild-prefix}<white>{name} has been invited to the guild!")
            .replace("{name}", name)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getInviteInvitedMessage(@NotNull Guild guild) {
        return getComponentMessage("invite.invited", "{guild-prefix}<white>You have been invited to {guild-name}.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getInviteNotInvitedMessage(@NotNull Guild guild) {
        return getComponentMessage("invite.not-invited", "{guild-prefix}<red>You have not been invited to {guild-name}.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Rename

    public ComponentMessage getRenameRenamedMessage(@NotNull Guild guild, @NotNull String oldName) {
        return getComponentMessage("rename.renamed", "{guild-prefix}<white>Guild {old-name} has been renamed to {guild-name}")
            .replace("{old-name}", oldName)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getRenameAlreadyNamedMessage(@NotNull Guild guild) {
        return getComponentMessage("rename.already-named", "{guild-prefix}<white>The Guild is already named {guild-name}")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Transfer

    public ComponentMessage getTransferSelfMessage(@NotNull Guild guild) {
        return getComponentMessage("transfer.self", "{guild-prefix}<white>You cannot transfer ownership to yourself.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getTransferSuccessMessage(@NotNull Guild guild, @NotNull String ownerName) {
        return getComponentMessage("transfer.success", "{guild-prefix}<white>{name} is the new owner of this guild!")
            .replace("{name}", ownerName)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Claim

    public ComponentMessage getClaimAlreadyClaimedMessage(@NotNull Guild guild, @NotNull Guild owner) {
        return getComponentMessage("claim.already-claimed", "{guild-prefix}<red>This chunk is claimed by {owner}!")
            .replace("{owner}", owner.getName())
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getClaimSuccessMessage(@NotNull Guild guild) {
        return getComponentMessage("claim.success", "{guild-prefix}<white>Successfully claimed this chunk.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Unclaim

    public ComponentMessage getUnclaimNotClaimedMessage(@NotNull Guild guild) {
        return getComponentMessage("unclaim.not-claimed", "{guild-prefix}<red>This chunk is not claimed.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getUnclaimContainsHomeMessage(@NotNull Guild guild) {
        return getComponentMessage("unclaim.contains-home", "{guild-prefix}<red>This chunk contains the Guild home. Cannot unclaim.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getUnclaimSuccessMessage(@NotNull Guild guild) {
        return getComponentMessage("unclaim.success", "{guild-prefix}<white>Successfully unclaimed this chunk.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Home

    public ComponentMessage getHomeInvalidLandMessage(@NotNull Guild guild) {
        return getComponentMessage("home.invalid-land", "{guild-prefix}<red>This chunk is not claimed by your guild.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getHomeSetMessage(@NotNull Guild guild, @NotNull Location location) {
        return getComponentMessage("home.set", "{guild-prefix}<white>The guild home has been set to {location}.")
            .replace("{location}", MessageUtil.prepareLocation(location))
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getHomeNoHomeMessage(@NotNull Guild guild) {
        return getComponentMessage("home.no-home", "{guild-prefix}<white>The guild has no home.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getHomeTeleportingMessage(@NotNull Guild guild) {
        return getComponentMessage("home.teleporting", "{guild-prefix}<white>Teleporting to guild home.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Bank

    public ComponentMessage getBankBalanceMessage(@NotNull Guild guild) {
        return getComponentMessage("bank.balance", "{guild-prefix}<white>Guild Bank Balance: {balance}")
            .replace("{balance}", MessageUtil.formatEconomy(guild.getBalance()))
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getBankDepositNotEnoughMessage(@NotNull Guild guild) {
        return getComponentMessage("bank.deposit.not-enough", "{guild-prefix}<white>You do not have enough money.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getBankDepositSuccessMessage(@NotNull Guild guild, @NotNull String name, double amount) {
        return getComponentMessage("bank.deposit.success", "{guild-prefix}<white>{name} deposited {amount} into the bank.")
            .replace("{name}", name)
            .replace("{amount}", MessageUtil.formatEconomy(amount))
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getBankWithdrawNotEnoughMessage(@NotNull Guild guild) {
        return getComponentMessage("bank.withdraw.not-enough", "{guild-prefix}<white>The guild does not have enough money.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getBankWithdrawFailedMessage(@NotNull Guild guild) {
        return getComponentMessage("bank.withdraw.failed", "{guild-prefix}<white>Failed to withdraw money from the Guild bank. Please try again.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getBankWithdrawSuccessMessage(@NotNull Guild guild, @NotNull String name, double amount) {
        return getComponentMessage("bank.withdraw.success", "{guild-prefix}<white>{name} withdrew {amount} from the bank.")
            .replace("{name}", name)
            .replace("{amount}", MessageUtil.formatEconomy(amount))
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Rank

    public ComponentMessage getRankShowMessage(@NotNull Guild guild, @NotNull Rank rank) {
        return getComponentMessage("rank.show", "{guild-prefix}<white>Your rank is: {rank}")
            .replace("{rank}", rank.getDisplay())
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getRankRenamedMessage(@NotNull Guild guild, @NotNull String oldName, @NotNull String newName) {
        return getComponentMessage("rank.rename.success", "{guild-prefix}<white>Rank {old-name} has been renamed to {new-name}.")
            .replace("{old-name}", oldName)
            .replace("{new-name}", newName)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getRankSelfMessage(@NotNull Guild guild) {
        return getComponentMessage("rank.set.self", "{guild-prefix}<red>You cannot change your own rank.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getRankSetChangedTargetMessage(@NotNull Guild guild, @NotNull Rank rank) {
        return getComponentMessage("rank.set.changed.target", "{guild-prefix}<white>Your rank has been changed to {rank}.")
            .replace("{rank}", rank.getDisplay())
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getRankSetChangedSenderMessage(@NotNull Guild guild, @NotNull String name, @NotNull Rank rank) {
        return getComponentMessage("rank.set.changed.sender", "{guild-prefix}<white>Set {name}'s rank to {rank}.")
            .replace("{name}", name)
            .replace("{rank}", rank.getDisplay())
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Open

    public ComponentMessage getOpenAlreadyOpenMessage(@NotNull Guild guild) {
        return getComponentMessage("open.already-open", "{guild-prefix}<red>The guild is already open.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getOpenSetOpenMessage(@NotNull Guild guild) {
        return getComponentMessage("open.set-open", "{guild-prefix}<white>The guild is now open.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getOpenAlreadyClosedMessage(@NotNull Guild guild) {
        return getComponentMessage("open.already-closed", "{guild-prefix}<red>The guild is already closed.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getOpenSetClosedMessage(@NotNull Guild guild) {
        return getComponentMessage("open.set-closed", "{guild-prefix}<white>The guild is now closed.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Tax

    public ComponentMessage getTaxTooLowMessage(@NotNull Guild guild) {
        String minString = MessageUtil.formatEconomy(0);
        return getComponentMessage("tax.too-low", "{guild-prefix}<red>You cannot set tax below {min}!")
            .replace("{min}", minString)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getTaxTooHighMessage(@NotNull Guild guild, double max) {
        String maxString = MessageUtil.formatEconomy(max);
        return getComponentMessage("tax.too-high", "{guild-prefix}<red>You cannot set tax that high! (Max: {max}).")
            .replace("{max}", maxString)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getTaxSetMessage(@NotNull Guild guild, @NotNull String name, double amount) {
        String amountString = MessageUtil.formatEconomy(amount);
        return getComponentMessage("tax.set", "{guild-prefix}<white>{name} has set the guild tax to {amount}.")
            .replace("{name}", name)
            .replace("{amount}", amountString)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Board

    public ComponentMessage getBoardMessage(@NotNull Guild guild, @NotNull String board) {
        return getComponentMessage("board", "{guild-prefix}<white>{board}")
            .replace("{board}", board)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    // Visit

    public ComponentMessage getVisitAllowedMessage(@NotNull Guild guild) {
        return getComponentMessage("visit.allowed", "{guild-prefix}<white>The guild is now open to visitors.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getVisitDisallowedMessage(@NotNull Guild guild) {
        return getComponentMessage("visit.disallowed", "{guild-prefix}<white>The guild is now closed to visitors.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getVisitSetCostMessage(@NotNull Guild guild, double amount) {
        return getComponentMessage("visit.cost.set", "{guild-prefix}<white>It now costs {amount} to visit the guild.")
            .replace("{amount}", MessageUtil.formatEconomy(amount))
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getVisitCostTooLowMessage(@NotNull Guild guild) {
        String minString = MessageUtil.formatEconomy(0);
        return getComponentMessage("visit.cost.too-low", "{guild-prefix}<red>You cannot set visit cost below {min}!")
            .replace("{min}", minString)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getVisitCostTooHighMessage(@NotNull Guild guild, double max) {
        String maxString = MessageUtil.formatEconomy(max);
        return getComponentMessage("tax.too-high", "{guild-prefix}<red>You cannot set visit cost that high! (Max: {max}).")
            .replace("{max}", maxString)
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getVisitCostConfirmationMessage(@NotNull Guild guild, double amount) {
        return getComponentMessage("visit.cost-confirmation", "{guild-prefix}<white>It costs {amount} to visit this guild. Run the command again to confirm.")
            .replace("{amount}", MessageUtil.formatEconomy(amount))
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getVisitClosedMessage(@NotNull Guild guild) {
        return getComponentMessage("visit.closed", "{guild-prefix}<red>The guild is not open to visitors.")
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

    public ComponentMessage getVisitNotEnoughMoneyMessage(@NotNull Guild guild, double amount) {
        return getComponentMessage("visit.not-enough-money", "{guild-prefix}<red>You do not have enough money to visit. (Required: {amount}).")
            .replace("{amount}", MessageUtil.formatEconomy(amount))
            .replace(getGuildPrefixReplacer(guild))
            .replace(getPrefixReplacer());
    }

}
