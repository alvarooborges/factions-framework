package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.FactionDisbandPacket;
import net.hyze.factions.framework.echo.packets.UserLeftFactionPacket;
import net.hyze.factions.framework.echo.packets.relation.FactionRelationDeletedPacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class DisbandSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public DisbandSubCommand() {
        super("desfazer", FactionRole.LEADER, "terminar", "excluir");
    }

    @Override
    public void onCommand(Player player, FactionUser user, String[] args) {
        ConfirmInventory confirmInventory = ConfirmInventory.of(event -> {
            FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(user.getId());

            if (relation == null || relation.getRole() != FactionRole.LEADER) {
                Message.ERROR.send(player, "Você não é o líder da facção.");
                player.closeInventory();
                return;
            }

            if (relation.getFaction().isUnderAttack()) {
                Message.ERROR.send(player,"Você não pode desfazer sua facção enquanto ela estiver sob ataque.");
                return;
            }

            Map<SpawnerType, Integer> collectedCount = FactionsProvider.Repositories.SPAWNERS.provide().countCollected(relation.getFaction());

            if (!collectedCount.isEmpty()) {
                Message.ERROR.send(player, "Você não pode desfazer sua facção enquanto ela tiver geradores guardados no '/f geradores'!");
                player.closeInventory();
                return;
            }

            Map<SpawnerType, Integer> placedCount = FactionsProvider.Repositories.SPAWNERS.provide().countPlaced(relation.getFaction());

            if (!placedCount.isEmpty()) {
                Message.ERROR.send(player, "Você não pode desfazer sua facção enquanto ela tiver geradores colocados nas terras!");
                player.closeInventory();
                return;
            }

            /*
             * Remove alianças antes de terminar a facção.
             */
            Faction sender = relation.getFaction();

            Set<Faction> ownsRelations = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(relation.getFaction(), FactionRelation.Type.ALLY);

            ownsRelations.forEach(targetFaction -> {
                FactionsProvider.Repositories.FACTIONS_RELATIONS.provide().delete(new FactionRelation(sender.getId(), targetFaction.getId(), FactionRelation.Type.ALLY));
                CoreProvider.Redis.ECHO.provide().publish(new FactionRelationDeletedPacket(relation.getFaction(), targetFaction, FactionRelation.Type.ALLY));
            });
            //

            boolean result = FactionsProvider.Repositories.FACTIONS.provide().delete(relation.getFaction().getTag());

            if (!result) {
                Message.ERROR.send(player, "Algo de errado aconteceu, tente novamente.");
                player.closeInventory();
                return;
            }

            Set<FactionUser> users = FactionUtils.getUsers(relation.getFaction());

            for (FactionUser member : users) {
                CoreProvider.Redis.ECHO.provide().publish(new UserLeftFactionPacket(
                        relation.getFaction(),
                        member.getId(),
                        UserLeftFactionPacket.Reason.DISBAND
                ));
            }

            CoreProvider.Redis.ECHO.provide().publish(new FactionDisbandPacket(relation.getFaction()));

            Message.SUCCESS.send(player, "Você desfez sua facção.");
        }, event -> {
            Message.ERROR.send(player, "Você cancelou a ação de desfazer sua facção.");
        }, null);

        player.openInventory(confirmInventory.make("&cSua facção será excluida."));
    }
}
