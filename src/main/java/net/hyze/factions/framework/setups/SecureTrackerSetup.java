package net.hyze.factions.framework.setups;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.google.common.collect.*;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.factions.framework.FactionsCustomPlugin;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SecureTrackerSetup<T extends FactionsCustomPlugin> extends FactionsSetup<T> implements Listener {

    private PacketListener packetListener;
    private BukkitTask bukkitTask;

    private final Multimap<Player, Player> SENT = HashMultimap.create();
    private final Map<Player, PlayerInfoData> ORIGINAL_DATA = Maps.newHashMap();

    @Override
    public boolean test(T plugin) {
        return false;
//        return AppType.FACTIONS_TESTS.isCurrent();
    }

    @Override
    public void enable(FactionsCustomPlugin plugin) {
        packetListener = new PacketListener(plugin);

        ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> playerList = ImmutableList.copyOf(Bukkit.getOnlinePlayers());

                for (Player target : playerList) {
                    handle(target);
                }
            }
        }.runTaskTimer(plugin, 20, 5);
    }

    @Override
    public void disable(FactionsCustomPlugin plugin) {
        bukkitTask.cancel();
        ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        SENT.removeAll(event.getPlayer());
        SENT.values().remove(event.getPlayer());
        ORIGINAL_DATA.remove(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {

        if (LocationUtils.compareLocation(event.getTo(), event.getFrom())) {
            return;
        }

        handle(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerTeleportEvent event) {
        handle(event.getPlayer());
    }

    private void handle(Player player) {
        if (CoreSpigotPlugin.getInstance().isNPC(player)) {
            return;
        }

        EntityPlayer handle = ((CraftPlayer) player).getHandle();

        EntityTracker worldTracker = ((WorldServer) handle.world).tracker;
        EntityTrackerEntry handleEntry = worldTracker.trackedEntities.get(handle.getId());

        if (handleEntry != null) {

            List<Player> playerList = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
            List<Player> toSend = Lists.newArrayList();
            List<Player> toRemove = Lists.newArrayList();

            for (Player target : playerList) {

                if (target != player) {
                    EntityPlayer other = ((CraftPlayer) target).getHandle();

                    if (handleEntry.c(other)) {
                        if (!SENT.containsEntry(player, target)) {
                            toSend.add(target);
                            SENT.put(player, target);
                        }
                    } else {
                        if (SENT.containsEntry(player, target)) {
                            toRemove.add(target);
                            SENT.remove(player, target);
                        }
                    }
                }
            }

            if (!toSend.isEmpty()) {
                WrapperPlayServerPlayerInfo toPlayerAddPacket = build(toSend, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                toPlayerAddPacket.sendPacket(player);

                for (Player target : toSend) {
                    handleEntry.updatePlayer(((CraftPlayer) target).getHandle());
                }
            }

            if (!toRemove.isEmpty()) {
                WrapperPlayServerPlayerInfo toPlayerRemovePacket = build(toRemove, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                toPlayerRemovePacket.sendPacket(player);

                for (Player target : toSend) {
                    handleEntry.updatePlayer(((CraftPlayer) target).getHandle());
                }
            }

            handleEntry.updatePlayer(handle);
        }
    }

    private WrapperPlayServerPlayerInfo build(List<Player> list, EnumWrappers.PlayerInfoAction action) {

        WrapperPlayServerPlayerInfo wrapper = new WrapperPlayServerPlayerInfo();
        wrapper.setAction(action);

        List<PlayerInfoData> data = Lists.newArrayList();

        for (Player target : list) {
            if (ORIGINAL_DATA.containsKey(target)) {
                data.add(ORIGINAL_DATA.get(target));
            } else {
                System.out.println("original data not found to " + target.getName());
            }
        }

        wrapper.setData(data);

        wrapper.getHandle().setMeta("hyze", true);

        return wrapper;
    }

    private class PacketListener extends PacketAdapter {

        public PacketListener(Plugin plugin) {
            super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.PLAYER_INFO, PacketType.Play.Server.SPAWN_ENTITY);
        }

        private void handlePlayerInfo(PacketEvent event) {
            WrapperPlayServerPlayerInfo wrapper = new WrapperPlayServerPlayerInfo(event.getPacket());

            if (!event.getPacket().getMeta("hyze").isPresent()) {
                if (wrapper.getAction() == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {

                    Optional<PlayerInfoData> selfData = wrapper.getData().stream()
                            .filter(info -> info.getProfile().getName().equalsIgnoreCase(event.getPlayer().getName()))
                            .findFirst();

                    selfData.ifPresent(playerInfoData -> ORIGINAL_DATA.put(event.getPlayer(), playerInfoData));

                    EntityPlayer handle = ((CraftPlayer) event.getPlayer()).getHandle();

                    EntityTracker worldTracker = ((WorldServer) handle.world).tracker;
                    EntityTrackerEntry handleEntry = worldTracker.trackedEntities.get(handle.getId());

                    if (handleEntry != null) {
                        Iterator<PlayerInfoData> iterator = wrapper.getData().iterator();

                        boolean needModification = false;

                        while (iterator.hasNext()) {
                            PlayerInfoData data = iterator.next();

                            Player target = Bukkit.getPlayerExact(data.getProfile().getName());

                            if (target != null && target != event.getPlayer()) {
                                EntityPlayer other = ((CraftPlayer) target).getHandle();

                                if (!handleEntry.c(other)) {
                                    needModification = true;
                                    iterator.remove();
                                }
                            }
                        }

                        if (needModification) {
                            event.setCancelled(true);

                            WrapperPlayServerPlayerInfo newWrapper = new WrapperPlayServerPlayerInfo();

                            newWrapper.setAction(wrapper.getAction());
                            newWrapper.setData(Lists.newArrayList(iterator));

                            newWrapper.getHandle().setMeta("hyze", true);
                            newWrapper.sendPacket(event.getPlayer());
                        }
                    }
                }
            }

            if (wrapper.getAction() == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                if (!event.isCancelled()) {
                    for (PlayerInfoData data : wrapper.getData()) {

                        Player target = Bukkit.getPlayerExact(data.getProfile().getName());

                        if (target != null && target != event.getPlayer()) {
                            SENT.put(event.getPlayer(), target);
                        }
                    }
                }
            } else if (wrapper.getAction() == EnumWrappers.PlayerInfoAction.REMOVE_PLAYER) {
                if (!event.isCancelled()) {
                    for (PlayerInfoData data : wrapper.getData()) {

                        Player target = Bukkit.getPlayerExact(data.getProfile().getName());

                        if (target != null && target != event.getPlayer()) {
                            SENT.remove(event.getPlayer(), target);
                        }
                    }
                }
            }
        }

        private void handleSpawnEntity(PacketEvent event) {

        }

        @Override
        public void onPacketSending(PacketEvent event) {


            if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
                this.handlePlayerInfo(event);
            } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
                this.handleSpawnEntity(event);
            }
        }
    }
}
