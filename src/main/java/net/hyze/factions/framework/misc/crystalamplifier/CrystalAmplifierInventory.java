package net.hyze.factions.framework.misc.crystalamplifier;

import com.google.common.collect.Lists;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.crystalamplifier.cache.local.CrystalAmplifierLocalCache;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CrystalAmplifierInventory extends CustomInventory {

    public CrystalAmplifierInventory(Faction faction) {
        super(27, "Cristal Amplificador");

        int factionId = faction.getId();

        CrystalAmplifierLocalCache cache = FactionsProvider.Cache.Local.CRYSTAL_AMPLIFIER.provide();
        Long endTime = cache.getEndTime(factionId);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (getViewers().isEmpty()) {
                    this.cancel();
                    return;
                }

                if (!cache.contains(factionId)) {
                    List<HumanEntity> list = Lists.newArrayList();
                    list.addAll(getViewers());

                    list.forEach(player -> player.closeInventory());
                    this.cancel();
                    return;
                }

                String[] string;

                if (endTime < System.currentTimeMillis()) {
                    string = new String[]{
                        "Este Cristal Amplificador será",
                        "removido em breve!"
                    };
                } else {
                    string = new String[]{TimeCode.getFormattedTimeLeft(endTime - System.currentTimeMillis())};
                }

                setItem(
                        12,
                        ItemBuilder.of(Material.WATCH)
                        .name("&aTempo restante:")
                        .lore(string)
                        .make()
                );

            }
        }.runTaskTimer(FactionsPlugin.getInstance(), 0L, 20L);

        setItem(
                14,
                ItemBuilder.of(Material.REDSTONE_BLOCK)
                .name("&aRemover")
                .make(),
                event -> {
                    CrystalAmplifierUtils.remove(faction.getId());
                    event.getWhoClicked().closeInventory();
                }
        );

    }

}
