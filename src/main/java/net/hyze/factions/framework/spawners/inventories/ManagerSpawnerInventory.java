package net.hyze.factions.framework.spawners.inventories;

import com.google.common.base.Enums;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.BlockedWhenRestarting;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.SpawnersSetup;
import net.hyze.factions.framework.spawners.log.LogAction;
import net.hyze.factions.framework.spawners.log.LogSourceType;
import net.hyze.factions.framework.spawners.log.SpawnerLog;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class ManagerSpawnerInventory extends CustomInventory implements BlockedWhenRestarting {

    private final FactionUser user;
    private final Faction faction;

    public ManagerSpawnerInventory(FactionUser user, Faction faction) {
        super(4 * 9, "Gerenciando geradores");

        this.user = user;
        this.faction = faction;

        setItem(11, new ItemBuilder(Material.DIAMOND_PICKAXE)
                        .glowing(true)
                        .name("&eDepositar todos")
                        .lore(
                                "&7Deposite todos os geradores",
                                "&7presentes em seu inventário."
                        )
                        .make(),
                this::handleDepositFromInventoryClick);

        {
            ItemBuilder builder = ItemBuilder.of(Material.HOPPER)
                    .name("&eColetar todos");

            builder.lore(
                    "&7Mover todos os geradores",
                    "&7armazenados para seu",
                    "&7inventário."
            );

            if (!FactionPermission.WITHDRAW_SPAWNERS.allows(faction, user)) {
                builder.lore("", "&cVocê não tem permissão para", "coletar geradores.");
            }


            setItem(15, builder.make(), this::handleCollectToInventoryClick);
        }

//        setItem(15, new ItemBuilder(Material.MAP).name("&aBase").make());
//
//        {
//            ItemBuilder builder = new ItemBuilder(HeadTexture.ARROW_WHITE_UP.getHead())
//                    .name("&aRemover todos")
//                    .lore("&eClique para &lremover &ee")
//                    .lore("&e&ldepositar &etodos os geradores que ")
//                    .lore("&eestão &lcolocados em sua base");
//
//            if (FactionPermission.DEPOSIT_PLACED_SPAWNERS.allows(faction, user)) {
//                builder.lore("&eClique para &lremover.");
//            } else {
//                builder.lore("&cVocê não tem permissão para", "remover geradores.");
//            }
//
//            setItem(24,
//                    builder.make(),
//                    event -> {
//                        if (!FactionPermission.DEPOSIT_PLACED_SPAWNERS.allows(faction, user)) {
//                            return;
//                        }
//
//                        Set<Claim> claims = FactionsProvider.Cache.Local.LANDS.provide().get(faction);
//
//                        if (claims.isEmpty()) {
//                            Message.ERROR.send(user.getPlayer(), "Sua facção não possui terras dominadas.");
//                            return;
//                        }
//
//                        Claim claim = claims.stream().findFirst().get();
//
//                        Message.INFO.send(user.getPlayer(), "Removendo geradores...");
//
//                        FactionRemovePlacedSpawnersRequest packet = new FactionRemovePlacedSpawnersRequest(faction, user.getId());
//
//                        CoreProvider.Redis.ECHO.provide().publish(packet, claim.getAppId(), response -> {
//
//                            if (response == null) {
//                                Message.ERROR.send(user.getPlayer(), "Erro ao tentar remover os geradores.");
//                                return;
//                            }
//
//
//                            if (user.getPlayer() != null && user.getPlayer().isOnline()) {
//                                Message.SUCCESS.send(user.getPlayer(), String.format(
//                                        "Foram retirados %s geradores",
//                                        response.getAmount().values().stream().mapToInt(Integer::intValue).sum()
//                                ));
//                            }
//
//                        });
//                    }
//            );
//        }
//
//        {
//            ItemBuilder builder = ItemBuilder.of(HeadTexture.ARROW_WHITE_DOWN.getHead())
//                    .name("&aColocar todos");
//
//            if (FactionPermission.WITHDRAW_SPAWNERS.allows(faction, user)) {
//                builder.lore("&eClique para &lcolocar &etodos", "os geradores que foram", "&lremovidos da sua base");
//            } else {
//                builder.lore("&cVocê não tem permissão para", "coletar geradores.");
//            }
//
//            setItem(33,
//                    builder.make(),
//                    () -> {
//                        if (!FactionPermission.WITHDRAW_SPAWNERS.allows(faction, user)) {
//                            return;
//                        }
//
//                        Set<Claim> claims = FactionsProvider.Cache.Local.LANDS.provide().get(faction);
//
//                        if (claims.isEmpty()) {
//                            Message.ERROR.send(user.getPlayer(), "Sua facção não possui terras dominadas.");
//                            return;
//                        }
//
//                        Claim claim = claims.stream().findFirst().get();
//
//                        Message.INFO.send(user.getPlayer(), "Colocando geradores...");
//
//                        FactionPlaceCollectedSpawnersRequest packet = new FactionPlaceCollectedSpawnersRequest(faction, user.getId());
//                        CoreProvider.Redis.ECHO.provide().publish(packet, claim.getAppId(), response -> {
//                            if (response == null) {
//                                Message.ERROR.send(user.getPlayer(), "Erro ao tentar colocar os geradores.");
//                                return;
//                            }
//
//                            if (user.getPlayer() != null && user.getPlayer().isOnline()) {
//                                Message.SUCCESS.send(user.getPlayer(), String.format("Foram colocados %s geradores", response.getAmount().values().stream().mapToInt(Integer::intValue).sum()));
//                            }
//                        });
//                    }
//            );
//        }

        backItem(getSize() - 5, event -> event.getActor().performCommand("f geradores"));
    }

    private void handleCollectToInventoryClick() {
        if (!FactionPermission.WITHDRAW_SPAWNERS.allows(faction, user)) {
            return;
        }

        Map<SpawnerType, Integer> spawners = FactionsProvider.Repositories.SPAWNERS.provide().withdraw(faction);

        if (spawners.isEmpty()) {
            Message.ERROR.send(user.getPlayer(), "Não existe nenhum gerador depositado.");
            return;
        }

        spawners.forEach((type, amount) -> {
            if (amount > 0) {
                int finalAmount = amount;

                ItemStack stack = type.getCustomItem().asItemStack(amount);

                AtomicInteger didNotFitAmount = new AtomicInteger();

                HashMap<Integer, ItemStack> didNotFit = user.getPlayer().getInventory().addItem(stack);

                for (Entry<Integer, ItemStack> entry : didNotFit.entrySet()) {
                    ItemStack change = entry.getValue();

                    finalAmount -= change.getAmount();

                    didNotFitAmount.addAndGet(change.getAmount());
                    FactionsProvider.Repositories.SPAWNERS.provide().deposit(faction, ImmutableMap.of(type, change.getAmount()));
                }

                try {
                    SpawnerLog log = SpawnerLog.builder()
                            .faction(faction)
                            .type(LogSourceType.PLAYER)
                            .typeValue(String.valueOf(user.getId()))
                            .action(LogAction.WITHDRAW_ITEM)
                            .amount(finalAmount)
                            .spawnerType(type)
                            .date(new Date())
                            .build();

                    FactionsProvider.Repositories.SPAWNERS_LOG.provide().insert(log);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleDepositFromInventoryClick() {
        HashMap<Integer, ? extends ItemStack> items = user.getPlayer().getInventory().all(Material.SKULL_ITEM);

        Map<SpawnerType, Integer> spawners = Maps.newHashMap();

        for (Entry<Integer, ? extends ItemStack> entry : items.entrySet()) {
            ItemStack stack = entry.getValue();

            ItemBuilder builder = new ItemBuilder(stack);

            String typeName = builder.nbtString(SpawnersSetup.METADATA_TYPE_TAG);

            if (typeName == null) {
                continue;
            }

            SpawnerType type = Enums.getIfPresent(SpawnerType.class, typeName).orNull();

            if (type == null) {
                continue;
            }

            spawners.put(type, spawners.getOrDefault(type, 0) + stack.getAmount());

            user.getPlayer().getInventory().setItem(entry.getKey(), null);
        }

        if (!spawners.isEmpty()) {
            FactionsProvider.Repositories.SPAWNERS.provide().deposit(faction, spawners);

            Message.SUCCESS.send(
                    user.getPlayer(),
                    String.format(
                            "Você &ldepositou &a%s geradores!",
                            spawners.values().stream().mapToInt(Integer::intValue).sum()
                    )
            );

            try {
                spawners.forEach((type, amount) -> {
                    SpawnerLog log = SpawnerLog.builder()
                            .faction(faction)
                            .type(LogSourceType.PLAYER)
                            .typeValue(String.valueOf(user.getId()))
                            .action(LogAction.DEPOSIT_ITEM)
                            .amount(amount)
                            .spawnerType(type)
                            .date(new Date())
                            .build();

                    FactionsProvider.Repositories.SPAWNERS_LOG.provide().insert(log);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            user.getPlayer().updateInventory();
        }
    }
}
