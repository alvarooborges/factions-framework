package net.hyze.factions.framework.menu.icons;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.menu.inventories.members.FactionMemberListInventory;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FactionMembersIcon extends MenuIcon {

    public FactionMembersIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        Set<FactionUser> onlineUsers = FactionUtils.getUsers(user.getRelation().getFaction(), true);

        ItemBuilder builder = ItemBuilder.of(Material.SKULL_ITEM)
                .durability(3)
                .name("&aIntegrantes &8(/f membros)")
                .lore(
                        "&7Clique e confira informações sobre",
                        "&7os integrantes da facção.",
                        "",
                        "&a" + onlineUsers.size() + " integrante(s) online!"
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            user.getPlayer().openInventory(new FactionMemberListInventory(user, user.getRelation().getFaction()));
        };
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
