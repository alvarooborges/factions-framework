package net.hyze.factions.framework.menu.icons;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FactionSpawnersIcon extends MenuIcon {

    public FactionSpawnersIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {

        Faction faction = user.getRelation().getFaction();

        ItemBuilder builder = ItemBuilder.of(Material.MOB_SPAWNER)
                .name("&aArmazém de geradores &8(/f geradores)")
                .lore(
                        "&7Mantenha em segurança os geradores",
                        "&7de sua facção num armazém exclusivo.",
                        "",
                        " &2Geradores armazenados:"
                );

        Map<SpawnerType, Integer> map = FactionsProvider.Repositories.SPAWNERS.provide().countCollected(faction);

       if (!map.isEmpty()) {
           map.forEach((spawnerType, integer) -> {
               builder.lore(" &7• &f" + integer + "x &7" + spawnerType.getDisplayName());
           });
       } else {
           builder.lore(" &7Nenhum");
       }

        builder.lore("", "&eClique para gerenciar.");

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> user.getPlayer().performCommand("f geradores");
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
