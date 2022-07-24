package net.hyze.factions.framework.listeners;

import com.google.common.collect.Lists;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.factions.framework.FactionsConstants;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class MineBlockListeners implements Listener {

    private static final Set<Material> BLACK_LIST = EnumSet.of(
            Material.DIRT,
            Material.SAND,
            Material.STONE,
            Material.COBBLESTONE,
            Material.MYCEL
    );

    private final List<Block> LIST = Lists.newArrayList();

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(BlockBreakEvent event) {
        User user = CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

        PreferenceStatus status = CoreProvider.Cache.Local.USERS_PREFERENCES.provide()
                .get(user)
                .getPreference(FactionsConstants.UserPreference.MINING_DROPS);

        if (status.is(PreferenceStatus.OFF)) {
            LIST.add(event.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(ItemSpawnEvent event) {
        if (LIST.remove(event.getLocation().getBlock())) {
            if(BLACK_LIST.contains(event.getEntity().getItemStack().getType())){
                event.setCancelled(true);
            }
        }
    }

}
