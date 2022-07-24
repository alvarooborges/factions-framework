package net.hyze.factions.framework.commands.factioncommand.subcommands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.permission.FactionAllyPermissionUpdatedPacket;
import net.hyze.factions.framework.echo.packets.relation.FactionRelationCreatedPacket;
import net.hyze.factions.framework.echo.packets.relation.FactionRelationDeletedPacket;
import net.hyze.factions.framework.echo.packets.relation.FactionRelationInvitePacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

import java.util.Set;

public class AllySubCommand extends FactionSubCommand {

    public AllySubCommand() {
        super("alianca", FactionRole.LEADER, "aliada", "aliados", "ally", "aliadas");

        registerArgument(new Argument("tag", "Tag da facção que você irá convidar para aliança.", true));
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        Faction sender = relation.getFaction();
        Faction target = FactionsProvider.Cache.Local.FACTIONS.provide().get(args[0]);

        if (target == null) {
            Message.ERROR.send(player, String.format("A facção &7%s &cnão existe.", args[0].toUpperCase()));
            return;
        }

        if (target.equals(sender)) {
            Message.ERROR.send(player, "Escolha outra facção para fazer aliança.");
            return;
        }

        Set<Faction> ownsRelations = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(relation.getFaction(), FactionRelation.Type.ALLY);

        if (args.length == 2 && args[1].equalsIgnoreCase("desfazer")) {
            if (!ownsRelations.contains(target)) {
                Message.ERROR.send(player, String.format("Sua facção não é aliada da facção %s&c.", target.getDisplayName()));
                return;
            }

            FactionsProvider.Repositories.FACTIONS_RELATIONS.provide().delete(new FactionRelation(sender.getId(), target.getId(), FactionRelation.Type.ALLY));

            CoreProvider.Redis.ECHO.provide().publish(new FactionRelationDeletedPacket(relation.getFaction(), target, FactionRelation.Type.ALLY));

            Message.INFO.send(player, String.format("Sua facção desfez a aliança com a facção %s&e.", target.getDisplayName()));

            return;
        }

        int allyLimit = FactionsProvider.getSettings().getAllyLimit();

        if (ownsRelations.size() >= allyLimit) {
            Message.ERROR.send(player, "Sua facção já atingiu o limite de " + allyLimit + " alianças.");
            return;
        }

        if (FactionUtils.isAlly(relation.getFaction(), target)) {
            Message.ERROR.send(player, String.format("A facção %s &cjá é aliada da sua facção.", target.getDisplayName()));
            return;
        }

        if (FactionsProvider.Cache.Redis.ALLY_INVITATIONS.provide().hasInvite(target, sender)) {

//            if (FactionsUtils.isEnemy(relation.getFaction(), target)) {
//                FactionsProvider.Repositories.FACTIONS_RELATIONS.provide().delete(new FactionRelation(sender.getId(), target.getId(), FactionRelation.Type.ENEMY));
//            }
            boolean success = FactionsProvider.Repositories.FACTIONS_RELATIONS.provide().insert(new FactionRelation(sender.getId(), target.getId(), FactionRelation.Type.ALLY));

            if (!success) {
                Message.ERROR.send(player, "Algo de errado aconteceu, tente novamente.");
                return;
            }

            FactionsProvider.Cache.Redis.ALLY_INVITATIONS.provide().removeInvitation(target, sender);

            // Limpando possiveis permissoes
            FactionsProvider.Repositories.FACTIONS_PERMISSIONS.provide().updateByAlly(sender, target, null);
            FactionsProvider.Repositories.FACTIONS_PERMISSIONS.provide().updateByAlly(target, sender, null);

            CoreProvider.Redis.ECHO.provide().publish(new FactionAllyPermissionUpdatedPacket(target, sender.getId(), -1));
            CoreProvider.Redis.ECHO.provide().publish(new FactionAllyPermissionUpdatedPacket(sender, target.getId(), -1));

            CoreProvider.Redis.ECHO.provide().publish(new FactionRelationCreatedPacket(sender, target, FactionRelation.Type.ALLY));

            Message.SUCCESS.send(player, String.format("Sua facção começou uma aliança com a facção %s&a.", target.getDisplayName()));

            return;
        }

        if (FactionsProvider.Cache.Redis.ALLY_INVITATIONS.provide().hasInvite(sender, target)) {
            Message.ERROR.send(player, "Já existe um convite pendente para esta facção.");
            return;
        }

        FactionsProvider.Cache.Redis.ALLY_INVITATIONS.provide().putInvitation(sender, target);
        CoreProvider.Redis.ECHO.provide().publish(new FactionRelationInvitePacket(sender, target, FactionRelation.Type.ALLY));

        Message.SUCCESS.send(player, String.format("Você convidou a facção %s &apara uma aliança.", target.getDisplayName()));

    }
}
