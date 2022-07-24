package net.hyze.factions.framework.misc.suspect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.cooldowns.Cooldowns;
import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.customitem.data.LauncherItem;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentUtil;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.misc.customitem.data.AbstractCreeperEggItem;
import net.hyze.factions.framework.misc.customitem.data.MaxPowerItem;
import net.hyze.factions.framework.misc.enchantments.FactionsCustomEnchantment;
import net.hyze.factions.framework.spawners.SpawnerItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SuspectListener implements Listener {

    // Aqui É possível usar a classe pai
    // AbstractCreeperEggItem.class
    static Set<Class<? extends CustomItem>> CLASSES = Sets.newHashSet(
            SpawnerItem.class,
            LauncherItem.class,
            AbstractCreeperEggItem.class,
            MaxPowerItem.class
    );

    private static final int DEFAULT_LIMIT = 20;

    // Aqui NÃO É possível usar a classe pai
    // SuperCreeperEggItem.class ao invés de AbstractCreeperEggItem.class
    // CreeperEggItem.class ao invés de AbstractCreeperEggItem.class
    static Map<Class<? extends CustomItem>, Integer> AMOUNT_LIMIT = Maps.newHashMap();

    static Map<Predicate<ItemStack>, Pair<String, Integer>> PREDICATES = Maps.newHashMap();

    static {
        AMOUNT_LIMIT.put(MaxPowerItem.class, 5);
        AMOUNT_LIMIT.put(SpawnerItem.class, 64 * 3);
        AMOUNT_LIMIT.put(LauncherItem.class, 32);

        PREDICATES.put(stack -> {
            if (stack.getType() != Material.DIAMOND_SWORD) {
                return false;
            }

            ItemBuilder builder = ItemBuilder.of(stack);

            int lvl = builder.enchantment(Enchantment.DAMAGE_ALL);

            return lvl == 5;
        }, new Pair<>("Espada sharp 5", 10));

        PREDICATES.put(stack -> {
            if (stack.getType() != Material.DIAMOND_SWORD) {
                return false;
            }

            ItemBuilder builder = ItemBuilder.of(stack);

            int lvl = builder.enchantment(Enchantment.DAMAGE_ALL);

            return lvl > 5;
        }, new Pair<>("Espada sharp > 5", 5));

        PREDICATES.put(stack -> {
            if (stack.getType() != Material.DIAMOND_SWORD) {
                return false;
            }

            int lvl = CustomEnchantmentUtil.getEnchantmentLevel(stack, FactionsCustomEnchantment.FURY.getEnchantment());

            return lvl >= 3;
        }, new Pair<>("Espada c/ Fúria", 5));
    }

    public static void callSuspect(Player player, List<ItemStack> stacks) {
        callSuspect(player, stacks, null);
    }

    public static void callSuspect(Player player, List<ItemStack> stacks, Consumer<ComponentBuilder> appender) {

        Map<Class<? extends CustomItem>, Integer> countCustomItems = Maps.newHashMap();
        Map<Pair<String, Integer>, Integer> countPredicates = Maps.newHashMap();

        for (ItemStack stack : stacks) {
            if (stack != null) {
                CustomItem custom = CustomItemRegistry.getByItemStack(stack);

                if (custom != null) {

                    for (Class<? extends CustomItem> c : CLASSES) {

                        if (c.isInstance(custom) || c.isAssignableFrom(custom.getClass())) {
                            countCustomItems.put(custom.getClass(), countCustomItems.getOrDefault(custom.getClass(), 0) + stack.getAmount());
                        }
                    }
                }

                for (Map.Entry<Predicate<ItemStack>, Pair<String, Integer>> entry : PREDICATES.entrySet()) {

                    boolean result = entry.getKey().test(stack);

                    if (result) {
                        countPredicates.put(entry.getValue(), countPredicates.getOrDefault(entry.getValue(), 0) + 1);
                    }
                }
            }
        }

        for (Map.Entry<Class<? extends CustomItem>, Integer> entry : countCustomItems.entrySet()) {
            if (entry.getValue() >= AMOUNT_LIMIT.getOrDefault(entry.getKey(), DEFAULT_LIMIT)) {
                sendNotification(player, entry.getKey().getSimpleName(), entry.getValue(), appender);
            }
        }

        for (Map.Entry<Pair<String, Integer>, Integer> entry : countPredicates.entrySet()) {
            if (entry.getValue() >= entry.getKey().getRight()) {
                sendNotification(player, entry.getKey().getLeft(), entry.getValue(), appender);
            }
        }
    }

    private static void sendNotification(Player player, String key, int amount, Consumer<ComponentBuilder> appender) {
        String cooldownKey = "notify_suspect_" + player.getName() + "_" + key + "_" + amount;

        if (!Cooldowns.hasEnded(cooldownKey)) {
            return;
        }

        Cooldowns.start(cooldownKey, 10, TimeUnit.SECONDS);

        ComponentBuilder hover = new ComponentBuilder("Suspeita de dupe\n")
                .color(ChatColor.GRAY)
                .append(player.getName() + " está com " + amount + " " + key + ".\n");

        if (appender != null) {
            appender.accept(hover);
        }

        hover.append("\nClique para ir até o jogador")
                .color(ChatColor.YELLOW);

        ComponentBuilder builder = new ComponentBuilder("\u26A0\u26A0 " + player.getName() + " \u26A0\u26A0")
                .bold(true)
                .color(ChatColor.RED)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName() + " alert"));

        BroadcastMessagePacket messagePacket = BroadcastMessagePacket.builder()
                .components(builder.create())
                .groups(Collections.singleton(Group.MANAGER))
                .build();

        CoreProvider.Redis.ECHO.provide().publish(messagePacket);
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        List<ItemStack> stacks = Lists.newArrayList();

        for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
            if (stack != null && stack.getType() != Material.AIR) {
                stacks.add(stack);
            }
        }

        callSuspect(event.getPlayer(), stacks, hover -> {
            hover.append("NO INVENTÁRIO");
        });
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        List<ItemStack> stacks = Lists.newArrayList();

        for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
            if (stack != null && stack.getType() != Material.AIR) {
                stacks.add(stack);
            }
        }

        callSuspect(event.getPlayer(), stacks, hover -> {
            hover.append("NO INVENTÁRIO");
        });
    }

    @EventHandler
    public void on(PlayerPickupItemEvent event) {
        List<ItemStack> stacks = Lists.newArrayList();

        for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
            if (stack != null && stack.getType() != Material.AIR) {
                stacks.add(stack);
            }
        }

        callSuspect(event.getPlayer(), stacks, hover -> {
            hover.append("NO INVENTÁRIO");
        });
    }

    @EventHandler
    public void on(PlayerTeleportEvent event) {
        List<ItemStack> stacks = Lists.newArrayList();

        for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
            if (stack != null && stack.getType() != Material.AIR) {
                stacks.add(stack);
            }
        }

        callSuspect(event.getPlayer(), stacks, hover -> {
            hover.append("NO INVENTÁRIO");
        });
    }

    @EventHandler
    public void on(InventoryOpenEvent event) {
        try {
            Player player = (Player) event.getPlayer();

            List<ItemStack> stacks = Lists.newArrayList();

            for (ItemStack stack : event.getView().getTopInventory().getContents()) {
                if (stack != null && stack.getType() != Material.AIR) {
                    stacks.add(stack);
                }
            }

            callSuspect(player, stacks, hover -> {
                if (event.getInventory().getHolder() instanceof BlockState) {
                    hover.append("HOLDER: " + ((BlockState) event.getInventory().getHolder()).getLocation().toVector());
                }
            });

            stacks.clear();

            for (ItemStack stack : event.getView().getBottomInventory().getContents()) {
                if (stack != null && stack.getType() != Material.AIR) {
                    stacks.add(stack);
                }
            }

            callSuspect(player, stacks, hover -> {
                hover.append("NO INVENTÁRIO");
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }
}
