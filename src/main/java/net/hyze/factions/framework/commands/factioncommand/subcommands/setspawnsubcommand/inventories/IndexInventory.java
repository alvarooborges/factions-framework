package net.hyze.factions.framework.commands.factioncommand.subcommands.setspawnsubcommand.inventories;

import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.world.location.unserializer.BukkitLocationParser;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

public class IndexInventory extends PaginateInventory {

    public IndexInventory(FactionUser user) {
        super("Pontos de Spawn");

        for (SpawnerType type : FactionsProvider.getSettings().getEnabledSpawners()) {
            ItemBuilder builder = ItemBuilder.of(type.getIcon().getHead());

            builder.name(type.getDisplayName())
                    .lore(
                            "Clique para definir o",
                            "ponto de spawn para os",
                            "geradores de " + type.getRawDisplayName(),
                            "na sua localização atual."
                    );

            addItem(builder.make(), () -> {
                Player player = user.getPlayer();
                Location location = player.getLocation();

                Claim claim = LandUtils.getClaim(location);

                FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

                if (relation == null) {
                    player.closeInventory();
                    Message.ERROR.send(player, "Você precisa fazer parte de uma facção.");
                    return;
                }

                Faction faction = relation.getFaction();

                if (claim == null || !Objects.equals(claim.getFactionId(), faction.getId())) {
                    player.closeInventory();
                    Message.ERROR.send(player, "Você precisa estar em alguma terra da sua facção.");
                    return;
                }

                if (!FactionPermission.COMMAND_SPAWNER_SET_SPAWN.allows(relation.getFaction(), user)) {
                    player.closeInventory();
                    Message.ERROR.send(player, "Você não tem permissão para definir pontos de spawn para sua facção.");
                    return;
                }

                SerializedLocation serialized = BukkitLocationParser.serialize(location);

                FactionsProvider.Repositories.SPAWNERS.provide()
                        .defineSpawnerSpawnLocation(relation.getFaction(), type, serialized);

                FactionsProvider.Cache.Local.SPAWNERS_SPAWN.provide().put(faction, type, serialized);

                Message.SUCCESS.send(player, String.format(
                        "Você definiu o ponto de spawn para os geradores de %s.",
                        type.getRawDisplayName()
                ));
            });
        }
    }
}
