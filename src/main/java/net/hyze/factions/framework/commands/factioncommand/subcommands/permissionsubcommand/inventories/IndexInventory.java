package net.hyze.factions.framework.commands.factioncommand.subcommands.permissionsubcommand.inventories;

import com.google.common.collect.Lists;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class IndexInventory extends CustomInventory {

    private final FactionUser viewer;

    public IndexInventory(FactionUser viewer, final Faction faction) {
        super(9 * 4, "Permissões");

        this.viewer = viewer;

        {
            ItemBuilder icon = new ItemBuilder(Material.SKULL_ITEM)
                    .name("&ePermissões individuais")
                    .lore("&7Gerencie as permissões de", "cada integrante de sua facção.", "")
                    .durability(SkullType.PLAYER.ordinal())
                    .flags(ItemFlag.values());

            Set<FactionUser> users = FactionUtils.getUsers(faction, FactionRole.RECRUIT, FactionRole.MEMBER, FactionRole.CAPTAIN);

            if (users.isEmpty()) {
                icon.lore("&cNenhum jogador na sua facção.");
            } else {
                icon.lore("&eClique para escolher um jogador.");
            }

            setItem(11, icon.make(), (event) -> {
                if (!users.isEmpty()) {
                    event.getWhoClicked().openInventory(buildPlayerChooserInventory(faction));
                }
            });
        }

        {
            ItemBuilder icon = new ItemBuilder(Material.NAME_TAG)
                    .name("&aPermissões de cargos")
                    .lore("&7Gerencie as permissões dos", "cargos da sua facção.", "", "&eClique para gerenciar.")
                    .flags(ItemFlag.values());

            setItem(13, icon.make(), event -> {
                event.getWhoClicked().openInventory(new RoleInventory(viewer, faction));
            });
        }

        {
            ItemBuilder icon = new ItemBuilder(Material.BANNER)
                    .dyeColor(DyeColor.LIME)
                    .name("&aPermissões de aliados")
                    .lore("&7Gerencie as permissões que", "os seus aliados possuem em", "terras da sua facção.", "")
                    .flags(ItemFlag.values());

            Set<Faction> allies = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(faction, FactionRelation.Type.ALLY);

            if (allies.isEmpty()) {
                icon.lore("&cNenhuma aliança foi definida.");

                setItem(15, icon.make());
            } else {

                icon.lore("&eClique para gerenciar.");

                setItem(15, icon.make(), event -> {
                    Set<Faction> alliesChecker = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(faction, FactionRelation.Type.ALLY);

                    if (alliesChecker.isEmpty()) {
                        Message.ERROR.send(event.getWhoClicked(), "Algo de errado aconteceu, tente novamente.");
                        event.getWhoClicked().closeInventory();
                        return;
                    }

                    event.getWhoClicked().openInventory(buildAllyChooserInventory(faction, allies));
                });
            }
        }

        backOrCloseItem();
    }

    private CustomInventory buildPlayerChooserInventory(Faction faction) {
        PaginateInventory.PaginateInventoryBuilder selectUserInventory = PaginateInventory.builder();

        for (FactionUser user : FactionUtils.getUsers(faction, FactionRole.RECRUIT, FactionRole.MEMBER, FactionRole.CAPTAIN)) {
            FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

            if (relation != null) {
                ItemBuilder icon = FactionUtils.getHead(user)
                        .name(String.format("&c[%s] %s", relation.getRole().getSymbol(), user.getNick()))
                        .lore("", "&eClique para gerenciar.");

                selectUserInventory.item(icon.make(), (event) -> {
                    event.getWhoClicked().openInventory(new IndividualInventory(viewer, faction, user));
                });
            }
        }

        return selectUserInventory.build("Escolha um jogador");
    }

    private CustomInventory buildAllyChooserInventory(Faction faction, Set<Faction> allies) {
        return new CustomInventory(36, "Escolha uma aliada") {
            {

                Function<Faction, Consumer<InventoryClickEvent>> function = ally -> event -> {
                    Set<Faction> alliesChecker = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(faction, FactionRelation.Type.ALLY);

                    if (!alliesChecker.contains(ally)) {
                        Message.ERROR.send(event.getWhoClicked(), "Algo de errado aconteceu, tente novamente.");
                        event.getWhoClicked().closeInventory();
                        return;
                    }

                    event.getWhoClicked().openInventory(new AllyInventory(faction, ally, () -> buildAllyChooserInventory(faction, alliesChecker)));
                };

                if (allies.size() == 1) {
                    setItem(13,
                            FactionUtils.getBanner(Lists.newArrayList(allies).get(0), faction).make(),
                            function.apply(Lists.newArrayList(allies).get(0))
                    );
                } else {
                    setItem(12,
                            FactionUtils.getBanner(Lists.newArrayList(allies).get(0), faction).make(),
                            function.apply(Lists.newArrayList(allies).get(0))
                    );

                    setItem(14,
                            FactionUtils.getBanner(Lists.newArrayList(allies).get(1), faction).make(),
                            function.apply(Lists.newArrayList(allies).get(1))
                    );
                }

                backItem(31, event -> {
                    event.getWhoClicked().openInventory(new IndexInventory(viewer, faction));
                });
            }
        };
    }
}
