package net.hyze.factions.framework.spawners.evolutions.inventories;

import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import net.hyze.factions.framework.spawners.evolutions.EvolutionRegistry;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public class EvolutionsListInventory extends CustomInventory {

    private static final int[] SLOTS = new int[]{
            12, 13, 14, 15,
            21, 22, 23, 24,
    };

    private final Faction faction;
    private final SpawnerType spawnerType;
    private final Inventory backInventory;

    public EvolutionsListInventory(Faction faction, SpawnerType spawnerType, Inventory backInventory) {
        super(5 * 9, "Evoluções");

        this.spawnerType = spawnerType;
        this.faction = faction;
        this.backInventory = backInventory;
    }

    @Override
    public void onOpen(InventoryOpenEvent event0) {
        super.onOpen(event0);

        ItemBuilder builder = ItemBuilder.of(spawnerType.getIcon().getHead())
                .name(spawnerType.getDisplayName());

        setItem(10, builder.build());

        backItem(getSize() - 5, event -> {
            event.getActor().openInventory(new EvolutionDashboardInventory(faction, () -> backInventory));
        });

        List<Evolution<?>> evolutions = EvolutionRegistry.getEvolutions(spawnerType);

        for (int i = 0; i < evolutions.size(); i++) {
            Evolution<?> evolution = evolutions.get(i);

            ItemBuilder icon = ItemBuilder.of(evolution.getIcon())
                    .name("&e" + evolution.getDisplayName())
                    .lore(evolution.getDescription())
                    .flags(ItemFlag.values());

            int currentIndex = FactionsProvider.Cache.Local.SPAWNER_EVOLUTIONS.provide()
                    .getEvolutionLevel(evolution, faction, spawnerType);

            if (currentIndex < evolution.getLevels().size()) {
                icon.lore("");
                for (String str : evolution.getLevels().get(currentIndex).getDisplay(spawnerType)) {
                    icon.lore(" " + str);
                }
            }

            icon.lore("", "&aClique para acompanhar a evolução!");

            setItem(SLOTS[i], icon.build(), event -> {
                FactionUser user = FactionUserUtils.getUser(event.getActor());
                event.getWhoClicked().openInventory(new EvolutionLevelsInventory(user, evolution, faction, spawnerType, this));
            });
        }
    }
}
