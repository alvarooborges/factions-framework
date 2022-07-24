package net.hyze.factions.framework.menu.inventories.members;

import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FactionMemberManageInventory extends CustomInventory {

    public FactionMemberManageInventory(FactionUser user, FactionUser target, Faction faction) {
        super(9 * 4, target.getNick());

        setItem(
                11,
                ItemBuilder.of(Material.INK_SACK)
                        .durability(1)
                        .name("&cExpulsar Jogador!")
                        .make(),
                event -> {

                    Player player = (Player) event.getWhoClicked();

                    ConfirmInventory confirmInventory = ConfirmInventory.of(
                            acceptEvenmt -> {
                                player.performCommand("f kick " + target.getNick());
                                player.closeInventory();
                            },
                            refuseEvent -> {
                                player.openInventory(new FactionMemberManageInventory(user, target, faction));
                            },
                            null
                    );

                    player.openInventory(confirmInventory.make());

                }
        );

        String currentRole = "&6" + target.getRelation().getRole().getDisplayName();

        setItem(
                13,
                ItemBuilder.of(Material.IRON_PICKAXE)
                        .name("&ePromover")
                        .lore("Clique para promover este jogador.", "", "&fCargo atual: " + currentRole)
                        .make(),
                event -> {

                    Player player = (Player) event.getWhoClicked();

                    ConfirmInventory confirmInventory = ConfirmInventory.of(
                            acceptEvenmt -> {
                                player.performCommand("f promover " + target.getNick());
                                player.closeInventory();
                            },
                            refuseEvent -> {
                                player.openInventory(new FactionMemberManageInventory(user, target, faction));
                            },
                            null
                    );

                    player.openInventory(confirmInventory.make());

                }
        );

        setItem(
                15,
                ItemBuilder.of(Material.WOOD_PICKAXE)
                        .name("&eRebaixar")
                        .lore("Clique para rebaixar este jogador.", "", "&fCargo atual: " + currentRole)
                        .make(),
                event -> {

                    Player player = (Player) event.getWhoClicked();

                    ConfirmInventory confirmInventory = ConfirmInventory.of(
                            acceptEvenmt -> {
                                player.performCommand("f rebaixar " + target.getNick());
                                player.closeInventory();
                            },
                            refuseEvent -> {
                                player.openInventory(new FactionMemberManageInventory(user, target, faction));
                            },
                            null
                    );

                    player.openInventory(confirmInventory.make());

                }
        );

        backOrCloseItem();
    }

}
