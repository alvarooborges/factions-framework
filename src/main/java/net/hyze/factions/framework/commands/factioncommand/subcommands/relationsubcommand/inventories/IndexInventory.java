package net.hyze.factions.framework.commands.factioncommand.subcommands.relationsubcommand.inventories;

import net.hyze.core.shared.misc.utils.Plural;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;

import java.util.Set;

public class IndexInventory extends CustomInventory {

    public IndexInventory(Faction faction, FactionUser user, boolean isLeader) {
        super(isLeader ? 45 : 27, String.format("Relações de %s", faction.getTag()));

        backOrCloseItem();

        {
            ItemBuilder tutorialIcon = new ItemBuilder(HeadTexture.getTempHead(
                    "90e65e6e5113a5187dad46dfad3d3bf85e8ef807f82aac228a59c4a95d6f6a"
            ))
                    .name("&eDefinir relação")
                    .lore(
                            "&7Defina relações com outra facção.",
                            "",
                            "&5Atalho: &a/f relacao &f(tag da facção)"
                    );

            setItem(11, tutorialIcon.make(), event -> ((Player) event.getWhoClicked()).performCommand("f relacao"));
        }

        {

            Faction ally = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(faction, FactionRelation.Type.ALLY)
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (ally != null) {
                ItemBuilder alliesIcon = new ItemBuilder(Material.DIAMOND_SWORD)
                        .name("&eAliança atual");


                alliesIcon.lore(
                        "&fFacção: &9" + ally.getStrippedDisplayName(),
                        "",
                        "&fBotão direito: &7Remova a aliança com esta facção."
                );

                this.setItem(15, alliesIcon.make(), event -> {
                    Player actor = event.getActor();
                    if (event.getAction() == InventoryAction.PICKUP_HALF) {

                        ConfirmInventory confirmInventory = ConfirmInventory.of(event1 -> {
                            ((Player) event1.getWhoClicked()).performCommand("f aliada " + ally.getTag() + " desfazer");
                            event1.getWhoClicked().closeInventory();
                        }, event1 -> {
                            event1.getWhoClicked().closeInventory();
                        }, FactionUtils.getBanner(ally, user).make());

                        actor.openInventory(confirmInventory.make("Confirmação"));

                    }

                });
            }
        }

//        {
//            ItemBuilder enemiesIcon = new ItemBuilder(Material.BANNER)
//                    .name("&cRivais")
//                    .color(DyeColor.RED)
//                    .flags(ItemFlag.HIDE_POTION_EFFECTS);
//
//            Set<Faction> enemies = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(faction, FactionRelation.Type.ENEMY);
//
//            if (enemies.isEmpty()) {
//                enemiesIcon.lore(String.format(
//                        "%s não possui rivais.",
//                        faction.getDisplayName()
//                ));
//            } else {
//                enemiesIcon.lore(String.format(
//                        "%s possui %s %s.",
//                        faction.getDisplayName(),
//                        enemies.size(),
//                        Plural.of(enemies.size(), "rival", "rivais")
//                ), "", "&eClique para ver!");
//            }
//
//            this.setItem(15, enemiesIcon.make(), event -> {
//                if (!enemies.isEmpty()) {
//                    PaginateInventory enimiesInventory = new PaginateInventory();
//
//                    enemies.forEach(ally -> {
//                        ItemBuilder enemyIcon = FactionsUtils.getBanner(ally, user);
//
//                        if (isLeader) {
//                            enemyIcon.lore("", "&eClique para desfazer a rivalidade!");
//                        }
//
//                        enimiesInventory.item(enemyIcon.make(), event0 -> {
//                            if (isLeader) {
//
//                            }
//                        });
//                    });
//
//                    enimiesInventory.backInventory(() -> new IndexInventory(faction, user, isLeader));
//
//                    event.getWhoClicked().openInventory(enimiesInventory.make("Rivais"));
//                }
//            });
//        }
        if (isLeader) {
            Set<Faction> invitations = FactionsProvider.Cache.Redis.ALLY_INVITATIONS.provide().getInvitations(faction);

            ItemBuilder invitationsIcon = new ItemBuilder(Material.STORAGE_MINECART)
                    .name("&ePedidos de aliança");

            int amount = invitations.size();

            if (invitations.isEmpty()) {
                invitationsIcon.lore("&7Nenhum pedido pendente.");
            } else {
                invitationsIcon.lore(
                        String.format("&f%s %s.", amount, Plural.of(amount, "pedido pendente", "pedidos pendentes")),
                        "",
                        "&eClique para ver!"
                );
            }

            this.setItem(14, invitationsIcon.make(), event -> {
                if (!invitations.isEmpty()) {
                    PaginateInventory.PaginateInventoryBuilder invitationsInventory = PaginateInventory.builder();

                    invitations.forEach(neutral -> {
                        invitationsInventory.item(FactionUtils.getBanner(neutral, user).make(), event0 -> {
                            event0.getWhoClicked().openInventory(ConfirmInventory.of(event1 -> {
                                ((Player) event1.getWhoClicked()).performCommand("f aliada " + neutral.getTag());
                                event1.getWhoClicked().closeInventory();
                            }, event1 -> {

                            }, null).make("&aAceitar convite."));
                        });
                    });

                    event.getWhoClicked().openInventory(invitationsInventory.build("Convites de aliança"));
                }
            });
        }
    }
}
