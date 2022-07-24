package net.hyze.factions.framework.commands.factioncommand.subcommands.permissionsubcommand.inventories;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.permission.FactionUserPermissionUpdatedPacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

public class IndividualInventory extends PaginateInventory {

    private final FactionUser viewer;
    private final Faction faction;
    private final FactionUser user;

    public IndividualInventory(FactionUser viewer, Faction faction, FactionUser user) {
        super("Permissões de " + user.getNick());
        this.viewer = viewer;
        this.faction = faction;
        this.user = user;

        for (FactionPermission permission : FactionPermission.values()) {
            if (permission.getRoleOnly() == null || permission.getRoleOnly()) {
                continue;
            }

            addItem(buildIcon(permission).make(), event -> handleClick(permission, event));
        }

        setItem(50, ItemBuilder.of(Material.BARRIER).name("&cResetar Permissões").make(), event -> {
            FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

            if (relation != null) {
                FactionsProvider.Repositories.FACTIONS_PERMISSIONS.provide().updateByUser(faction, user.getHandle(), null);
                CoreProvider.Redis.ECHO.provide().publish(new FactionUserPermissionUpdatedPacket(faction, user.getId(), -1));

                event.getWhoClicked().openInventory(new IndividualInventory(viewer, faction, user));
            } else {
                Message.ERROR.send(event.getWhoClicked(), "Algo de errado aconteceu, tente novamente.");
                event.getWhoClicked().closeInventory();
            }
        });

        backOrCloseItem();
    }

    private ItemBuilder buildIcon(FactionPermission permission) {
        boolean hasPermission = permission.allows(faction, user);

        return permission.getIcon()
                .name(ChatColor.GREEN + permission.getName())
                .lore(permission.getDescription())
                .lore("", "&fEstado: " + (hasPermission ? "&bLiberado" : "&cBloqueado"))
                .lore("", "&eClique para " + (hasPermission ? "bloquear." : "liberar."))
                .glowing(hasPermission)
                .flags(ItemFlag.values());
    }

    private void handleClick(FactionPermission permission, InventoryClickEvent event) {
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

        if (relation == null) {
            event.getWhoClicked().closeInventory();
            Message.ERROR.send(event.getWhoClicked(), "Algo de errado aconteceu, tente novamente.");
            return;
        }

        Integer value = FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().getByUser(relation);

        if (value == null) {
            value = FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().getByRole(relation);

            if (value == null) {
                value = FactionPermission.getDefaultRoleValue(relation.getRole());
            }
        }

        if (permission.allows(faction, user)) {
            value -= permission.getBit();
        } else {
            value |= permission.getBit();
        }

        FactionsProvider.Repositories.FACTIONS_PERMISSIONS.provide().updateByUser(faction, user.getHandle(), value);

        FactionUserPermissionUpdatedPacket packet = new FactionUserPermissionUpdatedPacket(faction, user.getId(), value);

        CoreProvider.Redis.ECHO.provide().publish(packet);

        FactionUtils.updateUserPermission(packet);

        event.getWhoClicked().openInventory(new IndividualInventory(viewer, faction, user));
    }
}
