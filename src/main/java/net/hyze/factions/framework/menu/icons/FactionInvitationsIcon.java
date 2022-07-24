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

public class FactionInvitationsIcon extends MenuIcon {

    public FactionInvitationsIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        Set<Faction> invites = FactionsProvider.Cache.Redis.FACTION_INVITATIONS.provide().getInvitations(user);

        ItemBuilder builder = ItemBuilder.of(Material.PAPER)
                .name("&aConvites de Facções &8(/f convites)")
                .amount(Math.min(invites.size(), 64))
                .lore(
                        "&7Veja as facções que você está",
                        "&7sendo convidado a participar!",
                        ""
                );

        builder.lore("&fConvites pendentes: &c" + invites.size());

        builder.lore(
                "",
                "&6Clique para visualizar."
        );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            user.getPlayer().performCommand("f aceitar");
        };
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
