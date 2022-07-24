package net.hyze.factions.framework.spawners;

import com.comphenix.packetwrapper.WrapperPlayServerTileEntityData;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsPlugin;
import org.bukkit.entity.Player;

public class SpawnersPacketListener extends PacketAdapter {

    public SpawnersPacketListener() {
        super(FactionsPlugin.getInstance(), PacketType.Play.Server.TILE_ENTITY_DATA);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.isPlayerTemporary()) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        PacketContainer packet = event.getPacket();

        if (packet.getMeta("custom").isPresent()) {
            return;
        }

        if (packet.getIntegers().read(0) != 1) {
            return;
        }

        User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

        PreferenceStatus status = CoreProvider.Cache.Local.USERS_PREFERENCES.provide()
                .get(user)
                .getPreference(FactionsConstants.UserPreference.SPAWNER_EFFECT);

        if (status.is(PreferenceStatus.OFF)) {
            event.setCancelled(true);

            WrapperPlayServerTileEntityData data = new WrapperPlayServerTileEntityData(event.getPacket());

            packet = data.getHandle();

            packet.setMeta("custom", true);

            NbtCompound nbt = (NbtCompound) packet.getNbtModifier().read(0);

            if (nbt.getKeys().contains("EntityId")) {
                // tira a entidade de dentro
                nbt.put("EntityId", "null");
            }

            if (nbt.getKeys().contains("SpawnData")) {
                // tira a entidade de dentro
                nbt.put("SpawnData", nbt.getCompound("SpawnData").put("id", "null"));
            }

            // tira particulas.
            nbt.put("RequiredPlayerRange", (short) 0);

            data.sendPacket(event.getPlayer());
        }
    }
}