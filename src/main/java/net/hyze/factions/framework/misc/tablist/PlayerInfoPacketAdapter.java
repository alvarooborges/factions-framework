package net.hyze.factions.framework.misc.tablist;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.google.common.collect.Sets;
import net.hyze.core.shared.misc.cooldowns.Cooldowns;
import net.hyze.factions.framework.FactionsPlugin;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PlayerInfoPacketAdapter extends PacketAdapter {

    private final TabListManager tabListManager;

    public PlayerInfoPacketAdapter(TabListManager tabListManager) {
        super(FactionsPlugin.getInstance(), PacketType.Play.Server.PLAYER_INFO);
        this.tabListManager = tabListManager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        WrapperPlayServerPlayerInfo wrapper = new WrapperPlayServerPlayerInfo(event.getPacket());

        if (wrapper.getHandle().getMeta(tabListManager.CUSTOM_METADATA_KEY).isPresent()) {

            if (wrapper.getAction() == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                List<PlayerInfoData> data = new ArrayList<>();

                for (PlayerInfoData info : wrapper.getData()) {
                    PlayerInfoData orDefault = tabListManager.ORIGINALS_PLAYER_INFO.getOrDefault(info.getProfile().getUUID(), info);
                    data.add(orDefault);
                }

                wrapper.setData(data);
            }

        } else {

            if (wrapper.getAction() == EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE
                    || wrapper.getAction() == EnumWrappers.PlayerInfoAction.UPDATE_LATENCY) {

                for (PlayerInfoData info : wrapper.getData()) {
                    if (wrapper.getAction() == EnumWrappers.PlayerInfoAction.UPDATE_LATENCY) {

                        String key = tabListManager.COOLDOWNS_LATENCY_KEY_PARSER.apply(info.getProfile().getName());

                        if (!Cooldowns.hasEnded(key)) {
                            continue;
                        }

                        Cooldowns.start(key, 5, TimeUnit.SECONDS);
                    }

                }
            }

            /*
             * Quando o pacote for vanilla, apenas NPC podem ser
             * removidos.
             */
            if (wrapper.getAction() == EnumWrappers.PlayerInfoAction.REMOVE_PLAYER) {

                Set<String> npcs = Sets.newHashSet(
                        "NPC_DUNGEON",
                        "npc_ranking",
                        "npc_cash",
                        "npc_safe"
                );

                List<PlayerInfoData> data = new ArrayList<>();

                for (PlayerInfoData info : wrapper.getData()) {
                    if (npcs.contains(info.getProfile().getName())) {
                        PlayerInfoData playerInfoData = new PlayerInfoData(info.getProfile(), info.getLatency(), info.getGameMode(), info.getDisplayName());
                        data.add(playerInfoData);
                    }
                }

                event.setCancelled(true);

                if (!data.isEmpty()) {
                    WrapperPlayServerPlayerInfo removePacket = new WrapperPlayServerPlayerInfo();

                    removePacket.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    removePacket.setData(data);
                    removePacket.getHandle().setMeta(tabListManager.CUSTOM_METADATA_KEY, true);
                    removePacket.sendPacket(Bukkit.getPlayerExact(event.getPlayer().getName()));
                }

                return;
            }

            if (!event.isCancelled() && wrapper.getAction() == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
                    if (event.getPlayer() != null && event.getPlayer().isOnline()) {
                        tabListManager.updateScoreboardTeams(event.getPlayer().getName(), wrapper);
                    }
                }, 5L);
            }

        }
    }
}