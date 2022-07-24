package net.hyze.factions.framework.spawners.evolutions.inventories;

import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import net.hyze.factions.framework.spawners.evolutions.EvolutionRegistry;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.function.Supplier;

public class EvolutionDashboardInventory extends PaginateInventory {

    private final Faction faction;
    private final Supplier<Inventory> back;

    public EvolutionDashboardInventory(Faction faction, Supplier<Inventory> back) {
        super("Geradores");

        this.faction = faction;
        this.back = back;
    }

    @Override
    public void onOpen(InventoryOpenEvent event0) {

        clearItems();

        if (back != null) {
            addMenu(getSize() - 5, BACK_ARROW, event -> {
                event.getActor().openInventory(back.get());
            });
        } else {
            addMenu(getSize() - 5, CLOSE_BARRIER, event -> {
                event.getActor().closeInventory();
            });
        }

        for (SpawnerType spawnerType : FactionsProvider.getSettings().getEnabledSpawners()) {
            String lore = String.format(
                    "Adquira melhorias exclusivas para os geradores de %s de sua facção.",
                    spawnerType.getRawDisplayName()
            );

            ItemBuilder builder = ItemBuilder.of(spawnerType.getIcon().getHead())
                    .name("&e" + spawnerType.getRawDisplayName())
                    .lore(WordUtils.wrap(lore, 33).split(SystemUtils.LINE_SEPARATOR))
                    .lore(
                            "",
                            "&eProgresso atual:",
                            ""
                    );

            List<Evolution<?>> evolutions = EvolutionRegistry.getEvolutions(spawnerType);

            for (Evolution<?> evolution : evolutions) {
                String evolutionDisplayName = evolution.getDisplayName();
                int index = FactionsProvider.Cache.Local.SPAWNER_EVOLUTIONS.provide()
                        .getEvolutionLevel(evolution, faction, spawnerType);

                builder.lore(String.format("  &a▪ &f%s: &bNível %s", evolutionDisplayName, index + 1));
            }

            builder.lore("", "&aClique para acessar as evoluções");

            addItem(builder.build(), event -> {
                event.getWhoClicked().openInventory(new EvolutionsListInventory(faction, spawnerType, this));
            });
        }

        super.onOpen(event0);
    }
}
