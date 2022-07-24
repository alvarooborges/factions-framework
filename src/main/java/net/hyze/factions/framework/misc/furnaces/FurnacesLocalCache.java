package net.hyze.factions.framework.misc.furnaces;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.FactionsPlugin;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.TileEntityFurnace;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.LinkedList;

public class FurnacesLocalCache implements LocalCache {

    private final Int2ObjectMap<LinkedList<VirtualFurnace>> furnacesMap = new Int2ObjectOpenHashMap<>();

    private LinkedList<VirtualFurnace> get(User user) {
        LinkedList<VirtualFurnace> virtualFurnaces = furnacesMap.get(user.getId().intValue());

        if (virtualFurnaces == null) {
            virtualFurnaces = new LinkedList<>();
            furnacesMap.put(user.getId().intValue(), virtualFurnaces);
        }

        return virtualFurnaces;
    }

    public VirtualFurnace get(User user, int index) {
        LinkedList<VirtualFurnace> virtualFurnaces = furnacesMap.get(user.getId().intValue());

        if (virtualFurnaces == null || virtualFurnaces.isEmpty()) {
            return null;
        }

        if (virtualFurnaces.size() - 1 < index) {
            return null;
        }

        return virtualFurnaces.get(index);
    }

    public VirtualFurnace add(User user, int index) {
        LinkedList<VirtualFurnace> virtualFurnaces = get(user);

        VirtualFurnace virtualFurnace = new VirtualFurnace(
                ((CraftPlayer) Bukkit.getPlayer(user.getNick())).getHandle()
        );

        virtualFurnaces.add(index, virtualFurnace);

        return virtualFurnace;
    }

    @Override
    public void populate() {
        Bukkit.getScheduler().runTaskTimer(FactionsPlugin.getInstance(), () -> {

            for (LinkedList<VirtualFurnace> value : this.furnacesMap.values()) {

                for (VirtualFurnace virtualFurnace : value) {
                    try {
                        virtualFurnace.c();
                    } catch (Exception ignore) {
                    }
                }

            }

        }, 0L, 20L);
    }

    public static class VirtualFurnace extends TileEntityFurnace {

        public VirtualFurnace(EntityHuman entity) {
            world = entity.world;
        }

        @Override
        public Block w() {
            return (isBurning() || getProperty(3) > 0) ? Blocks.LIT_FURNACE : Blocks.FURNACE;
        }

        @Override
        public boolean a(EntityHuman entityhuman) {
            return true;
        }

        /**
         * burnTime - 0
         * <p>
         * ticksForCurrentFuel - 1
         * <p>
         * cookTime - 2
         * <p>
         * cookTimeTotal - 3
         *
         * @param i
         * @param j
         */
        public void setProperty(int i, int j) {
            b(i, j);
        }
    }
}
