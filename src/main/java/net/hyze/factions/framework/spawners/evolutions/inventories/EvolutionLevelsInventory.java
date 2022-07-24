package net.hyze.factions.framework.spawners.evolutions.inventories;

import com.google.common.collect.Lists;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import net.hyze.factions.framework.spawners.evolutions.EvolutionCost;
import net.hyze.factions.framework.spawners.evolutions.EvolutionLevel;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EvolutionLevelsInventory extends CustomInventory {

    private static final int[] SLOTS = new int[]{
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private final FactionUser user;
    private final Evolution<?> evolution;
    private final Faction faction;
    private final SpawnerType type;
    private final Inventory backInventory;

    public EvolutionLevelsInventory(FactionUser user, Evolution<?> evolution, Faction faction, SpawnerType type, Inventory backInventory) {
        super(9 * 5, evolution.getDisplayName());

        this.user = user;
        this.evolution = evolution;
        this.faction = faction;
        this.type = type;
        this.backInventory = backInventory;
    }

    @Override
    public void onOpen(InventoryOpenEvent event0) {
        super.onOpen(event0);

        ItemBuilder builder = ItemBuilder.of(type.getIcon().getHead())
                .name(type.getDisplayName());

        setItem(4, builder.build());

        backItem(getSize() - 5, event -> {
            event.getActor().openInventory(backInventory);
        });

        int currentLevel = FactionsProvider.Cache.Local.SPAWNER_EVOLUTIONS.provide().getEvolutionLevel(evolution, faction, type);

        for (int levelIndex = 0; levelIndex < evolution.getLevels().size(); levelIndex++) {

            EvolutionLevel<?> level = evolution.getLevels().get(levelIndex);

            final boolean isNextLevel = currentLevel + 1 == levelIndex;

            Material material = Material.MINECART;
            String name = "&c(Bloqueado)";

            if (isNextLevel) {
                material = Material.EXPLOSIVE_MINECART;
                name = "&6(Disponível)";
            } else if (levelIndex <= currentLevel) {
                material = Material.STORAGE_MINECART;
                name = "&a(Adquirido)";
            }

            ItemBuilder levelIcon = ItemBuilder.of(new ItemStack(material))
                    .name(String.format("&eNível %s %s", levelIndex + 1, name));

            levelIcon.lore("");

            for (String display : level.getDisplay(type)) {
                levelIcon.lore("&7" + display);
            }

            EvolutionCost[] costs = level.getCosts(type);

            AtomicBoolean hasAllCosts = new AtomicBoolean(true);

            if (levelIndex > currentLevel) {
                if (costs.length > 0) {
                    levelIcon.lore("", " &eRequer:", "");

                    for (EvolutionCost cost : costs) {
                        List<String> lines = cost.getDisplay(user, faction);
                        String firstLine = lines.get(0);

                        boolean has = cost.has(user, faction);

                        if (hasAllCosts.get()) {
                            hasAllCosts.set(has);
                        }

                        levelIcon.lore(String.format(
                                "  %s &f%s",
                                has ? "&a✔" : "&c✖",
                                firstLine
                        ));

                        for (int j = 1; j < lines.size(); j++) {
                            levelIcon.lore("   " + lines.get(j));
                        }

                        levelIcon.lore(" ");
                    }
                }
            } else {
                List<String> inUseDisplay = Lists.newLinkedList();

                for (EvolutionCost cost : costs) {
                    List<String> inUseCostDisplay = cost.getInUseDisplay(user, faction);

                    if (!inUseCostDisplay.isEmpty()) {
                        inUseDisplay.addAll(inUseCostDisplay);
                        inUseDisplay.add("");
                    }
                }

                if (!inUseDisplay.isEmpty()) {
                    levelIcon.lore("", " &eEm uso:", "");

                    for (String line : inUseDisplay) {
                        levelIcon.lore("   " + line);
                    }
                }
            }

            levelIcon.removeLastEmptyLoreLine();

            if (hasAllCosts.get() && isNextLevel) {
                levelIcon.lore("", "&aClique para evoluir!");
            }

            setItem(SLOTS[levelIndex], levelIcon.build(), event -> {

                if (isNextLevel && (hasAllCosts.get() || (user.getHandle().hasGroup(Group.GAME_MASTER) && event.isShiftClick()))) {

                    for (EvolutionCost cost : level.getCosts(type)) {
                        if (user.getHandle().hasGroup(Group.GAME_MASTER) && event.isShiftClick()) {
                            continue;
                        }

                        if (!cost.has(user, faction)) {
                            Message.ERROR.send(event.getActor(), "Algo de errado aconteceu.");
                            return;
                        }
                    }

                    for (EvolutionCost cost : level.getCosts(type)) {
                        cost.transaction(user, faction);
                    }

                    FactionsProvider.Repositories.SPAWNER_EVOLUTIONS.provide()
                            .updateLevelIndex(evolution, faction, type, currentLevel + 1);

                    FactionsProvider.Cache.Local.SPAWNER_EVOLUTIONS.provide()
                            .refresh(faction);

                    event.getActor().openInventory(this);
                }
            });
        }
    }
}
