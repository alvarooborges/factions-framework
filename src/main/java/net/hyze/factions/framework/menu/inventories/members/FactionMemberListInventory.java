package net.hyze.factions.framework.menu.inventories.members;

import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyAPI;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.subcommands.MembersSubCommand;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.user.stats.UserStats;
import org.bukkit.Material;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FactionMemberListInventory extends CustomInventory {

    public FactionMemberListInventory(FactionUser user, Faction faction) {
        super(54, "Membros - [" + faction.getTag() + "]");

        FactionUserRelation relation = user.getRelation();
        boolean isLeader = relation != null && relation.getFaction().equals(faction) && relation.getRole() == FactionRole.LEADER;

        Set<FactionUser> allUsers = MembersSubCommand.ALL_USERS_CACHE.get(faction);
        Set<FactionUser> onlineUsers = MembersSubCommand.ONLINE_USERS_CACHE.get(faction);

        Iterator<FactionUser> allUsersIterator = allUsers.stream()
                .collect(Collectors.groupingBy(target -> target.getRelation().getRole()))
                .entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getKey().compareTo(o1.getKey()))
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .iterator();

        int slot = 11;

        for (int i = 0; i < FactionsProvider.getSettings().getFactionMaxMembers(); i++) {

            if (!allUsersIterator.hasNext()) {
                this.setItem(slot++, ItemBuilder.of(Material.SKULL_ITEM).name("&7Vago").make());
            } else {
                FactionUser target = allUsersIterator.next();

                FactionUserRelation targetRelation = target.getRelation();

                boolean isLogged = onlineUsers.contains(target);

                String displayName = "&7" + target.getNick() + " " + (isLogged ? "&a(Online)" : "&8(Offline)");

                UserStats userStats = target.getStats();

                ItemBuilder head = new ItemBuilder(HeadTexture.getPlayerHead(target.getNick()))
                        .name(displayName)
                        .lore(
                                "",
                                " &fCargo: &b" + targetRelation.getRole().getDisplayName(),
                                " &fGrupo: " + target.getHandle().getHighestGroup().getDisplayTag(),
                                " &fAbates: &7" + userStats.getTotalKills() + " &a▲",
                                " &fMortes: &7" + userStats.getTotalDeaths() + " &c▼",
                                " &fMoedas: &7" + NumberUtils.format(EconomyAPI.get(target.getHandle(), Currency.COINS)),
                                " &fPoder: &7" + userStats.getPower() + "/" + userStats.getTotalMaxPower(),
                                ""
                        );

                if (isLeader && !user.equals(target)) {
                    head.lore(
                            "&aClique para gerenciar este membro."
                    );
                }

                this.setItem(
                        slot++,
                        head.make(),
                        event -> {
                            if (isLeader && !user.equals(target)) {
                                event.getWhoClicked().openInventory(new FactionMemberManageInventory(user, target, faction));
                            }
                        }
                );
            }

            if (InventoryUtils.getColumn(slot) == 7) {
                slot += 4;
            }
        }

        backOrCloseItem(49);
    }

}
