package net.hyze.factions.framework.commands.factioncommand.subcommands.permissionsubcommand.inventories;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.permission.FactionRolePermissionUpdatedPacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

public class RoleInventory extends PaginateInventory {

    private final FactionUser viewer;

    public RoleInventory(FactionUser viewer, Faction faction) {
        super("Permissões dos cargos");

        this.viewer = viewer;

        for (FactionPermission permission : FactionPermission.values()) {
            if (permission.getRoleOnly() == null) {
                continue;
            }

            addItem(buildIcon(permission, faction).make(), event -> handleClick(permission, faction, event));
        }

        backOrCloseItem();
    }

    private ItemBuilder buildIcon(FactionPermission permission, Faction faction) {
        FactionRole minRole = getMinRole(permission, faction);

        ItemBuilder out = permission.getIcon()
                .name(ChatColor.GREEN + permission.getName())
                .lore(permission.getDescription())
                .lore("");

        for (FactionRole role : FactionRole.values()) {
            String str = role.getDisplayName() + (role != FactionRole.LEADER ? " ou superior" : "");

            if (role == minRole) {
                out.lore(" &b▶ " + str);
            } else {
                out.lore(" &8" + str);
            }
        }

        out.lore("")
                .lore("&eClique para alterar!")
                .flags(ItemFlag.values());

        return out;
    }

    private void handleClick(FactionPermission permission, Faction faction, InventoryClickEvent event) {
        FactionRole minRole = getMinRole(permission, faction);

        for (FactionRole role : FactionRole.values()) {
            Integer value = FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().getByRole(faction, role);

            if (value == null) {
                value = FactionPermission.getDefaultRoleValue(role);
            }

            if (minRole != FactionRole.LEADER) {
                if (role.ordinal() <= minRole.ordinal()) {
                    if (permission.allows(faction, role)) {
                        value -= permission.getBit();
                    }
                } else {
                    value |= permission.getBit();
                }
            } else {
                value |= permission.getBit();
            }

            FactionsProvider.Repositories.FACTIONS_PERMISSIONS.provide().updateByRole(faction, role, value);

            FactionRolePermissionUpdatedPacket packet = new FactionRolePermissionUpdatedPacket(faction, role, value);

            CoreProvider.Redis.ECHO.provide().publish(packet);

            FactionUtils.updateRolePermission(packet);
        }

        event.getWhoClicked().openInventory(new RoleInventory(viewer, faction));
    }

    private FactionRole getMinRole(FactionPermission permission, Faction faction) {

        for (FactionRole role : FactionRole.values()) {
            if (permission.allows(faction, role)) {
                return role;
            }
        }

        return FactionRole.LEADER;
    }
}
