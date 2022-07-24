package net.hyze.factions.framework.spawners.inventories;

import com.google.common.collect.ImmutableMap;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.BlockedWhenRestarting;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.evolutions.inventories.EvolutionDashboardInventory;
import net.hyze.factions.framework.spawners.log.LogAction;
import net.hyze.factions.framework.spawners.log.LogSourceType;
import net.hyze.factions.framework.spawners.log.SpawnerLog;
import net.hyze.factions.framework.user.FactionUser;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

public final class IndexSpawnerInventory extends CustomInventory implements BlockedWhenRestarting {

    private static final ItemStack MANAGER_ICON = new ItemBuilder(Material.COMMAND_MINECART)
            .name("&eGerenciar")
            .lore(
                    "&7Clique para gerenciar os geradores",
                    "&7de sua facção."
            )
            .make();

    private static final ItemStack UPGRADE_ICON = new ItemBuilder(Material.EXP_BOTTLE)
            .name("&eEvolução")
            .lore(
                    "&7Clique para gerenciar o progresso",
                    "&7de evolução de cada tipo de gerador",
                    "&7de sua facção."
            )
            .make();

    private final FactionUser user;
    private final Faction faction;

    public IndexSpawnerInventory(FactionUser user, Faction faction) {
        super(54, "Armazém de Geradores");

        this.user = user;
        this.faction = faction;

        backItem(49, event -> ((Player) event.getWhoClicked()).performCommand("f"));

        ItemBuilder icon = ItemBuilder.of(Material.WATCH)
                .name("&eRegistro de movimentações")
                .lore(
                        "&7Clique para visualizar o histórico",
                        "&7de movimentações no armazém de",
                        "&7geradores da facção."
                );

        setItem(15, icon.make(), event -> user.getPlayer().openInventory(new SpawnersLogInventory(user, faction)));
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        super.onOpen(event);

        build0();
    }

    private void build0() {
        setOnClose(event -> {
            EntityHuman human = ((CraftPlayer) event.getPlayer()).getHandle();
            MinecraftServer.getServer().getPlayerList().playerFileData.save(human);
        });

        setItem(11, MANAGER_ICON, event -> {
            user.getPlayer().openInventory(new ManagerSpawnerInventory(user, faction));
        });

        if (user.getHandle().hasGroup(Group.GAME_MASTER) || (user.getRelation() != null && user.getRelation().getRole() == FactionRole.LEADER)) {
            setItem(13, UPGRADE_ICON, event -> {
                event.getWhoClicked().openInventory(new EvolutionDashboardInventory(faction, () -> new IndexSpawnerInventory(user, faction)));
            });
        }

        Map<SpawnerType, Integer> map = FactionsProvider.Repositories.SPAWNERS.provide().countCollected(faction);

        int spawnerSlot = 29;

        for (Entry<SpawnerType, Integer> entry : map.entrySet()) {

            if ((spawnerSlot + 1) % 9 == 0) {
                spawnerSlot += 3;
            }

            ItemBuilder icon = new ItemBuilder(entry.getKey().getIcon().getHead())
                    .name(String.format(
                            "%s &6(x%s)",
                            entry.getKey().getDisplayName(),
                            entry.getValue()
                    ))
                    .lore(
                            "&fBotão esquerdo: &7Coletar apenas um gerador.",
                            "&fShift + Botão esquerdo: &7Coletar todos os geradores."
                    );

            if (!FactionPermission.WITHDRAW_SPAWNERS.allows(faction, user) && !user.getOptions().isAdminModeEnabled()) {
                icon.lore("", "&cVocê não pode retirar geradores.");
                setItem(spawnerSlot++, icon.make());
                continue;
            }

            setItem(spawnerSlot++, icon.make(), event -> {
                if (!FactionPermission.WITHDRAW_SPAWNERS.allows(faction, user) && !user.getOptions().isAdminModeEnabled()) {
                    user.getPlayer().closeInventory();
                    return;
                }

                int amount;
                if (event.isShiftClick()) {
                    ItemStack stack = entry.getKey().getCustomItem().asItemStack(entry.getValue());

                    if (!InventoryUtils.fits(user.getPlayer().getInventory(), stack)) {
                        Message.ERROR.send(user.getPlayer(), "Os geradores não cabem no seu invetário.");
                        return;
                    }

                    amount = FactionsProvider.Repositories.SPAWNERS.provide().withdraw(faction, entry.getKey(), -1);
                } else {
                    amount = FactionsProvider.Repositories.SPAWNERS.provide().withdraw(faction, entry.getKey(), 1);
                }

                if (amount == 0) {
                    Message.ERROR.send(user.getPlayer(), "Esses geradores já foram retirados por outro jogador, veja a log para saber mais.");
                } else {
                    ItemStack stack = entry.getKey().getCustomItem().asItemStack(amount);

                    if (!InventoryUtils.fits(user.getPlayer().getInventory(), stack)) {
                        Message.ERROR.send(user.getPlayer(), "Os geradores não cabem no seu invetário.");
                        FactionsProvider.Repositories.SPAWNERS.provide().deposit(faction, ImmutableMap.of(entry.getKey(), amount));
                        return;
                    }

                    user.getPlayer().getInventory().addItem(stack);
                }

                try {
                    SpawnerLog log = SpawnerLog.builder()
                            .faction(faction)
                            .type(LogSourceType.PLAYER)
                            .typeValue(String.valueOf(user.getId()))
                            .action(LogAction.WITHDRAW_ITEM)
                            .amount(amount)
                            .spawnerType(entry.getKey())
                            .date(new Date())
                            .build();

                    FactionsProvider.Repositories.SPAWNERS_LOG.provide().insert(log);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                user.getPlayer().openInventory(new IndexSpawnerInventory(user, faction));
            });
        }


    }
}
