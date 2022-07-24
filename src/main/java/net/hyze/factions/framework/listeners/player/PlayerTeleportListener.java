package net.hyze.factions.framework.listeners.player;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.CustomSound;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.user.FactionUser;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

public class PlayerTeleportListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(PlayerTeleportEvent event) {
        if (CoreSpigotPlugin.getInstance().isNPC(event.getPlayer())) {
            return;
        }

        if (AppType.FACTIONS_TESTS.isCurrent() && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location to = event.getTo().clone();

            double width = 0.4;
            double height = 0.5;

            AxisAlignedBB axisalignedbb = new AxisAlignedBB(
                    to.getX() - width / 2, to.getY() + height, to.getZ() - width / 2,
                    to.getX() + width / 2, to.getY(), to.getZ() + width / 2
            );

            World world = ((CraftWorld) to.getWorld()).getHandle();

            IBlockData block;
            BlockPosition blockposition;
            int x, y, z;
            Chunk chunk;

            for (AxisAlignedBB bb : world.a(axisalignedbb)) {
                x = (int) bb.a;
                y = (int) bb.b;
                z = (int) bb.c;

                blockposition = new BlockPosition(x, y, z);
                chunk = world.getChunkAt(x >> 4, z >> 4);
                block = chunk.getBlockData(blockposition);

                if (block.getBlock().getMaterial().isSolid()) {
                    axisalignedbb = new AxisAlignedBB(
                            to.getX() - width / 2, to.getY() - .5 + height, to.getZ() - width / 2,
                            to.getX() + width / 2, to.getY() - .5, to.getZ() + width / 2
                    );

                    for (AxisAlignedBB bb2 : world.a(axisalignedbb)) {
                        x = (int) bb2.a;
                        y = (int) bb2.b;
                        z = (int) bb2.c;

                        blockposition = new BlockPosition(x, y, z);
                        chunk = world.getChunkAt(x >> 4, z >> 4);
                        block = chunk.getBlockData(blockposition);


                        if (block.getBlock().getMaterial().isSolid()) {
                            User user = CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

                            UserCooldowns.end(user, "enderpearl");
                            event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));

                            Message.ERROR.send(event.getPlayer(), "Ops, sua Pérola do Fim caiu em um local que você ficaria sufocado.");
                            event.setCancelled(true);
                            return;
                        }
                    }

                    event.setTo(event.getTo().clone().subtract(0, .5, 0));
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHighest(PlayerTeleportEvent event) {
        if (CoreSpigotPlugin.getInstance().isNPC(event.getPlayer())) {
            return;
        }

        Player player = event.getPlayer();

        if (isNpc(player)) {
            event.setCancelled(true);
            return;
        }

        if (player.getName().equalsIgnoreCase("kadoo")) {

            if (AppType.FACTIONS_SAFE.isCurrent() && event.getCause().equals(TeleportCause.ENDER_PEARL)) {
                event.setCancelled(true);
                return;
            }
        }

        //ender peal é liberado em combate,
        if (event.getCause() == TeleportCause.ENDER_PEARL) {
            return;
        }

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

        if (!event.getCause().equals(TeleportCause.UNKNOWN) && CombatManager.isTagged(user.getHandle())) {
            event.setCancelled(true);
            Message.ERROR.send(player, "Você não pode teleportar em combate.");
            CustomSound.BAD.play(player);
            player.closeInventory();
            return;
        }
    }

    private boolean isNpc(Entity entity) {
        return entity.hasMetadata("NPC");
    }
}
