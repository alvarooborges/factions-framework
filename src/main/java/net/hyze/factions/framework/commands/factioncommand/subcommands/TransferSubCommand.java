package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.UserRankUpdatedPacket;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

public class TransferSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_PLAYER;

    public TransferSubCommand() {
        super("transferir", FactionRole.LEADER);

        registerArgument(new Argument("nick", "Nick do jogador que receberá a facção"));
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        String targetNick = args[0];

        FactionUser targetUser = FactionsProvider.Cache.Local.USERS.provide().get(targetNick);

        if (targetUser == null) {
            Message.ERROR.send(player, "O jogador " + targetNick + " não foi encontrado.");
            return;
        }

        if (targetUser.equals(user)) {
            Message.ERROR.send(player, "Você precisa escolher outro jogador para transferir a facção.");
            return;
        }

        targetNick = targetUser.getNick();

        FactionUserRelation targetRelation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(targetUser.getId());

        if (targetRelation == null || targetRelation.getFaction() != relation.getFaction()) {
            Message.ERROR.send(player, String.format(
                    "O jogador %s não está na facção %s.",
                    targetNick,
                    relation.getFaction().getTag().toUpperCase()
            ));
            return;
        }

        {
            FactionRole oldRank = targetRelation.getRole();
            targetRelation.setRole(FactionRole.LEADER);
            FactionsProvider.Repositories.USERS_RELATIONS.provide().update(targetRelation);
            CoreProvider.Redis.ECHO.provide().publish(new UserRankUpdatedPacket(
                    targetUser.getId(),
                    user.getId(),
                    oldRank,
                    targetRelation.getRole(),
                    targetRelation.getFaction().getId()
            ));
        }

        {
            FactionRole oldRank = relation.getRole();
            relation.setRole(FactionRole.CAPTAIN);
            FactionsProvider.Repositories.USERS_RELATIONS.provide().update(relation);
            CoreProvider.Redis.ECHO.provide().publish(new UserRankUpdatedPacket(
                    user.getId(),
                    user.getId(),
                    oldRank,
                    relation.getRole(),
                    relation.getFaction().getId()
            ));
        }

        Message.SUCCESS.send(player, String.format(
                "Você transferiu a facção %s para o jogador %s.",
                relation.getFaction().getTag().toUpperCase(),
                targetUser.getNick()
        ));

        ComponentBuilder builder = new ComponentBuilder("")
                .append(FactionUserUtils.getChatComponents(user))
                .append(" transferiu a facção para ", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN)
                .append(FactionUserUtils.getChatComponents(targetUser))
                .append(".", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN);

        FactionUtils.broadcast(relation.getFaction(), builder.create());
    }
}
