package net.hyze.factions.framework.menu.inventories.information;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;

import java.util.Set;
import java.util.function.Consumer;

public class FactionInformationInventory extends CustomInventory {

    public FactionInformationInventory(Faction faction, FactionUser user) {
        super(9 * 4, "Informações da Facção");

        setItem(
                12,
                FactionUtils.getInformationIcon(faction, user).make()
        );

        ItemBuilder icon = ItemBuilder.of(Material.PAPER)
                .name("&aMembros da Facção");

        Set<FactionUser> raw = FactionUtils.getUsers(faction);

        Multimap<FactionRole, FactionUser> roles = HashMultimap.create();

        raw.forEach(targetUser -> roles.put(targetUser.getRelation().getRole(), targetUser));

        Consumer<FactionUser> consumer = targetUser -> {
            Group group = targetUser.getHandle().getHighestGroup();
            icon.lore("&8• &7" + group.getDisplayTag(targetUser.getNick()));
        };

        roles.get(FactionRole.LEADER).forEach(consumer);
        roles.get(FactionRole.CAPTAIN).forEach(consumer);
        roles.get(FactionRole.MEMBER).forEach(consumer);
        roles.get(FactionRole.RECRUIT).forEach(consumer);

        setItem(
                14,
                icon.make()
        );
    }

}
