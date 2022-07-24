package net.hyze.factions.framework.menu.icons;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AllyInvitationsIcon extends MenuIcon {

    public AllyInvitationsIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE)
                .name("&aPedidos de Aliança")
                .lore(
                        "&7Veja os pedidos de aliança",
                        "&7que foram enviados para a",
                        "&7sua facção!",
                        ""
                );

        Set<Faction> invites = FactionsProvider.Cache.Redis.ALLY_INVITATIONS.provide().getInvitations(user.getRelation().getFaction());

        if (invites.isEmpty()) {
            builder.lore("&fPedidos pendentes: &c0");
        } else {
            builder.lore("&fPedidos pendentes: &a" + invites.size());
        }

        builder.lore(
                "",
                "&eDica: &fUse /f relacao",
                "",
                "&aClique para visualizar."
        );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            user.getPlayer().performCommand("f relacao");
        };
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
