package net.hyze.factions.framework.misc.furnaces.inventories;

import com.google.common.collect.Maps;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.furnaces.FurnacesLocalCache;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class FurnacesListInventory extends CustomInventory {

    private static final Map<Group, Integer> LIMIT_MAP = Maps.newEnumMap(Group.class);

    static {
        LIMIT_MAP.put(Group.DEFAULT, 3);
        LIMIT_MAP.put(Group.ARCANE, 5);
        LIMIT_MAP.put(Group.DIVINE, 7);
        LIMIT_MAP.put(Group.HEAVENLY, 10);
    }

    public FurnacesListInventory(User user) {
        super(4 * 9, "Fornalhas virtuais");

        Group highestGroup = user.getHighestGroup();

        Integer limit = LIMIT_MAP.getOrDefault(highestGroup, 3);

        int itemIndex = 1;
        int slotIndex = 10;

        for (int i = 0; i < limit; i++) {
            ItemBuilder builder = new ItemBuilder(Material.FURNACE)
                    .name("&eFornalha &f#" + itemIndex++)
                    .lore(
                            "&7Clique para abrir."
                    );

            int finalIndex = i;

            setItem(
                    slotIndex++,
                    builder.make(),
                    event -> {

                        Player player = event.getActor();

                        FurnacesLocalCache.VirtualFurnace virtualFurnace = FactionsProvider.Cache.Local.FURNACES.provide().get(
                                user,
                                finalIndex
                        );

                        if (virtualFurnace == null) {
                            virtualFurnace = FactionsProvider.Cache.Local.FURNACES.provide().add(user, finalIndex);

                            if (virtualFurnace == null) {
                                Message.ERROR.send(player, "Algo de errado aconteceu.");

                                player.closeInventory();
                                return;
                            }

                        }

                        ((CraftPlayer) player).getHandle().openContainer(virtualFurnace);
                    }
            );

        }
    }
}
