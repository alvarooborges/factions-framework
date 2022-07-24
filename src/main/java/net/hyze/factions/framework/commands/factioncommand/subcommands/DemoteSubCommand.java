package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.argument.impl.NickArgument;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.UserRankUpdatedPacket;
import net.hyze.factions.framework.echo.packets.permission.FactionUserPermissionUpdatedPacket;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class DemoteSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public DemoteSubCommand() {
        super("rebaixar", FactionRole.CAPTAIN, "demote", "demote");

        registerArgument(new NickArgument("nick", "Nick do jogador que será rebaixado"));
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        String targetNick = args[0];

        FactionUser targetUser = FactionsProvider.Cache.Local.USERS.provide().get(targetNick);

        if (targetUser == null) {
            Message.ERROR.send(player, String.format("O jogador %s não foi encontrado.", targetNick));
            return;
        }

        if (targetUser.equals(user)) {
            Message.ERROR.send(player, "Você precisa escolher outro jogador para rebaixar.");
            return;
        }

        FactionUserRelation targetRelation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(targetUser.getId());

        if (targetRelation == null || !targetRelation.getFaction().equals(relation.getFaction())) {
            Message.ERROR.send(player, String.format("O jogador %s não está na sua facção.", targetNick));
            return;
        }

        if (targetRelation.getRole() == FactionRole.RECRUIT) {
            Message.ERROR.send(player, String.format("Você não pode rebaixar um %s.", FactionRole.RECRUIT.getDisplayName()));
            return;
        }

        FactionRole oldRank = targetRelation.getRole();

        if (relation.getRole().ordinal() <= oldRank.ordinal()) {
            Message.ERROR.send(player, "Você não pode rebaixar jogadores com o rank maior ou igual ao seu.");
            return;
        }

        // Limpando possiveis permissoes
        FactionsProvider.Repositories.FACTIONS_PERMISSIONS.provide().updateByUser(relation.getFaction(), targetUser.getHandle(), null);
        CoreProvider.Redis.ECHO.provide().publish(new FactionUserPermissionUpdatedPacket(
                relation.getFaction(), targetUser.getId(), -1
        ));

        FactionRole newRank = oldRank.previous();

        targetRelation.setRole(newRank);

        FactionsProvider.Repositories.USERS_RELATIONS.provide().update(targetRelation);

        CoreProvider.Redis.ECHO.provide().publish(new UserRankUpdatedPacket(
                targetUser.getId(),
                user.getId(),
                oldRank,
                newRank,
                targetRelation.getFaction().getId()
        ));

        Message.SUCCESS.send(player, String.format(
                "FEITO! Você rebaixou o jogador %s para %s.",
                targetUser.getNick(),
                newRank.getDisplayName()
        ));
    }
}
