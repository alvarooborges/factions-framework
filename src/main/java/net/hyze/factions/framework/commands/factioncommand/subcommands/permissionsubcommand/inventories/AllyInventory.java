package net.hyze.factions.framework.commands.factioncommand.subcommands.permissionsubcommand.inventories;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.permission.FactionAllyPermissionUpdatedPacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class AllyInventory extends CustomInventory {

    private int[] slots = {
        10, 11, 12, 13, 14, 15
    };

    private final Faction faction;
    private final Faction ally;
    private final Supplier<CustomInventory> back;

    public AllyInventory(Faction faction, Faction ally, Supplier<CustomInventory> back) {
        super(36, "Permissões da " + ally.getTag());
        this.faction = faction;
        this.ally = ally;
        this.back = back;

        List<FactionPermission> permissionList = Arrays.asList(
                FactionPermission.ACCESS_CONTAINERS,
                FactionPermission.ACTIVATE_REDSTONE,
                FactionPermission.PERSONAL_HOME,
                FactionPermission.TPACCEPT,
                FactionPermission.COMMAND_BASE
        );

        for (int i = 0; i < permissionList.size(); i++) {
            if (i >= slots.length) {
                break;
            }

            FactionPermission permission = permissionList.get(i);

            setItem(slots[i], buildIcon(permission).make(), event -> handleClick(permission, event));
        }

        backItem(back.get());
    }

    private ItemBuilder buildIcon(FactionPermission permission) {
        boolean hasPermission = permission.allows(faction, ally);

        return permission.getIcon()
                .name(ChatColor.GREEN + permission.getName())
                .lore(permission.getDescription())
                .lore("", "&fEstado: " + (hasPermission ? "&bLiberado" : "&cBloqueado"))
                .lore("", "&eClique para " + (hasPermission ? "bloquear." : "liberar."))
                .glowing(hasPermission)
                .flags(ItemFlag.values());
    }

    private void handleClick(FactionPermission permission, InventoryClickEvent event) {
        Set<Faction> allies = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(faction, FactionRelation.Type.ALLY);

        if (!allies.contains(ally)) {
            event.getWhoClicked().closeInventory();
            Message.ERROR.send(event.getWhoClicked(), "Algo de errado aconteceu, tente novamente.");
            return;
        }

        Integer value = FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().getByAlly(faction, ally);

        if (value == null) {
            value = FactionPermission.getDefaultAllyValue();
        }

        if (permission.allows(faction, ally)) {
            value -= permission.getBit();
        } else {
            value |= permission.getBit();
        }

        FactionsProvider.Repositories.FACTIONS_PERMISSIONS.provide().updateByAlly(faction, ally, value);

        FactionAllyPermissionUpdatedPacket packet = new FactionAllyPermissionUpdatedPacket(faction, ally.getId(), value);

        CoreProvider.Redis.ECHO.provide().publish(packet);

        FactionUtils.updateAllyPermission(packet);

        event.getWhoClicked().openInventory(new AllyInventory(faction, ally, back));
    }
}
