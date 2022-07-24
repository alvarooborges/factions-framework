package net.hyze.factions.framework.listeners;

import com.google.common.base.Enums;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.spawners.SpawnerCustomBlock;
import net.hyze.factions.framework.spawners.SpawnersSetup;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.obsidiandestroyer.events.CustomBlockExplodeEvent;
import net.hyze.signshop.SignShop;
import net.hyze.signshop.SignShopProvider;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;

public class CustomBlockExplodeListener implements Listener {

    @EventHandler
    public void on(CustomBlockExplodeEvent event) {
        Entity entity = event.getEntity();

        CraftEntity craftEntity = (CraftEntity) entity;

        if (!craftEntity.hasMetadata("owner")) {
            return;
        }

        Faction faction;
        Object ownerObj = craftEntity.getMetadata("owner").get(0).value();
        if (ownerObj instanceof FactionUser) {
            FactionUser owner = (FactionUser) craftEntity.getMetadata("owner").get(0).value();

            if (owner == null || owner.getRelation() == null) {
                return;
            }

            faction = owner.getRelation().getFaction();
        } else if (ownerObj instanceof Faction) {
            faction = (Faction) ownerObj;
        } else {
            return;
        }

        Claim claim = LandUtils.getClaim(event.getAt());

        if (claim != null) {
            if (claim.getFaction() == faction) {
                return;
            }
        }

        if (event.getCustomBlock() instanceof SpawnerCustomBlock) {
            BlockState state = event.getState();

            if (!state.hasMetadata(SpawnersSetup.METADATA_TYPE_TAG)) {
                return;
            }

            SpawnerType type = Enums.getIfPresent(SpawnerType.class, state.getMetadata(SpawnersSetup.METADATA_TYPE_TAG).get(0).asString())
                    .orNull();

            if (type == null) {
                return;
            }

            Collection<SignShop> shops = SignShopProvider.Cache.Local.SHOPS.provide()
                    .get(type.getCustomItem().getKey());

            SignShop shop = shops.stream()
                    .min((o1, o2) -> o2.getBuyFromShop().compareTo(o1.getBuyFromShop()))
                    .orElse(null);

            if (shop == null) {
                return;
            }

            FactionsProvider.Repositories.FACTIONS_RANKING.provide().insertProfitValue(faction, shop.getBuyFromShop());
        }
    }
}
