//package net.hyze.factions.commands.factioncommand.subcommands;
//
//import net.hyze.core.shared.CoreProvider;
//import net.hyze.core.shared.commands.argument.Argument;
//import net.hyze.core.spigot.misc.message.Message;
//import net.hyze.factions.FactionsProvider;
//import net.hyze.factions.commands.factioncommand.FactionbSubCommand;
//import net.hyze.factions.echo.packets.permission.FactionAllyPermissionUpdatedPacket;
//import net.hyze.factions.echo.packets.relation.FactionRelationCreatedPacket;
//import net.hyze.factions.echo.packets.relation.FactionRelationDeletedPacket;
//import net.hyze.factions.echo.packets.relation.FactionRelationInvitePacket;
//import net.hyze.factions.faction.Faction;
//import net.hyze.factions.faction.relation.user.FactionRole;
//import net.hyze.factions.misc.utils.FactionsUtils;
//import net.hyze.factions.faction.relation.faction.FactionRelation;
//import net.hyze.factions.faction.relation.user.FactionUserRelation;
//import net.hyze.factions.user.FactionUser;
//import java.util.Set;
//import org.bukkit.entity.Player;
//
//public class EnemySubCommand extends FactionbSubCommand {
//
//    public EnemySubCommand() {
//        super("rivalidade", FactionRole.LEADER, "rival", "enemy", "rivais");
//
//        registerArgument(new Argument("tag", "Tag da facção que você irá convidar para rivalidade.", true));
//    }
//
//    @Override
//    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
//        Faction sender = relation.getFaction();
//        Faction target = FactionsProvider.Cache.Local.FACTIONS.provide().get(args[0]);
//
//        if (target == null) {
//            Message.ERROR.send(player, String.format("A facção &7%s &cnão existe.", args[0].toUpperCase()));
//            return;
//        }
//
//        Set<Faction> ownsRelations = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(relation.getFaction(), FactionRelation.Type.ENEMY);
//
//        if (args.length == 2 && args[1].equalsIgnoreCase("desfazer")) {
//            if (!ownsRelations.contains(target)) {
//                Message.ERROR.send(player, String.format("Sua facção não é rival da facção %s&c.", target.getDisplayName()));
//                return;
//            }
//
//            FactionsProvider.Repositories.FACTIONS_RELATIONS.provide().delete(new FactionRelation(sender.getId(), target.getId(), FactionRelation.Type.ENEMY));
//            CoreProvider.Redis.ECHO.provide().publish(new FactionRelationDeletedPacket(relation.getFaction(), target, FactionRelation.Type.ENEMY));
//            Message.INFO.send(player, String.format("Sua facção desfez a rivalidade com a facção %s&e.", target.getDisplayName()));
//
//            return;
//        }
//
//        if (FactionsUtils.isEnemy(relation.getFaction(), target)) {
//            Message.ERROR.send(player, String.format("A facção %s &cjá é rival da sua facção.", target.getDisplayName()));
//            return;
//        }
//
//        if (FactionsUtils.isAlly(relation.getFaction(), target)) {
//            FactionsProvider.Repositories.FACTIONS_RELATIONS.provide().delete(new FactionRelation(sender.getId(), target.getId(), FactionRelation.Type.ALLY));
//        }
//
//        FactionsProvider.Repositories.FACTIONS_RELATIONS.provide().insert(new FactionRelation(sender.getId(), target.getId(), FactionRelation.Type.ENEMY));
//
//        // Limpando possiveis permissoes
//        FactionsProvider.Repositories.FACTIONS_PERMISSIONS.provide().updateByAlly(sender, target, null);
//        FactionsProvider.Repositories.FACTIONS_PERMISSIONS.provide().updateByAlly(target, sender, null);
//        
//        CoreProvider.Redis.ECHO.provide().publish(new FactionAllyPermissionUpdatedPacket(target, sender, null));
//        CoreProvider.Redis.ECHO.provide().publish(new FactionAllyPermissionUpdatedPacket(sender, target, null));
//
//        CoreProvider.Redis.ECHO.provide().publish(new FactionRelationCreatedPacket(sender, target, FactionRelation.Type.ENEMY));
//
//        Message.SUCCESS.send(player, String.format("Você declarou a facção %s &acomo rival.", target.getDisplayName()));
//    }
//}
