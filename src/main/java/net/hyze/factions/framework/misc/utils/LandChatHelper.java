package net.hyze.factions.framework.misc.utils;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.Maps;
import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.spigot.misc.utils.DirectionUtils;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.Land;
import net.hyze.factions.framework.lands.LandState;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LandChatHelper {

    //    private static final String CUBE = "\u2B1B";
    private static final String CUBE = "\u2588";

    private static final Timing DRAW_CHAT_MAP_TIMING = Timings.of(
            FactionsPlugin.getInstance(),
            "LandChatHelper: drawChatMap"
    );

    public static BaseComponent[] drawChatMap(FactionUser user) {

        DRAW_CHAT_MAP_TIMING.startTimingIfSync();

        Player player = user.getPlayer();

        if (player == null || !player.isOnline()) {
            return new BaseComponent[0];
        }

        Location playerLocation = player.getLocation();

        int playerChunkX = playerLocation.getBlockX() >> 4;
        int playerChunkZ = playerLocation.getBlockZ() >> 4;

        int minChunkX = playerChunkX - 9;
        int minChunkZ = playerChunkZ - 9;

        int maxChunkX = playerChunkX + 9;
        int maxChunkZ = playerChunkZ + 9;

        Pair<ChatColor, String>[][] colors = new Pair[19][19];

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {

                int indexX = x - minChunkX;
                int indexY = z - minChunkZ;

                if (!LocationUtils.isInsideBorder(playerLocation.getWorld(), x, z)) {
                    colors[indexX][indexY] = new Pair<>(LandState.VOID_ZONE.getChatColor(), null);
                    continue;
                }

                Land land = FactionsProvider.Cache.Local.LANDS.provide().get(x, z);
                LandState state = LandState.get(user, land);

                String hover = null;

                if (land instanceof Claim) {
                    Claim claim = ((Claim) land);

                    hover = claim.getFaction().getStrippedDisplayName();

                    if (claim.isTemporary()) {

                        long hash = LongHash.toLong(claim.getChunkX(), claim.getChunkZ());

                        boolean claimUnderAttack = LandState.UNDER_ATTACK_CHUNK.contains(hash);
                        boolean claimIsOld = claim.getCreatedAt().getTime() + TimeUnit.MINUTES.toMillis(3) < System.currentTimeMillis();

                        if (!(claimUnderAttack && !claimIsOld)) {
                            state = LandState.FREE_LAND;

                            if (!FactionUtils.isMember(user.getHandle(), claim.getFaction())) {
                                hover = null;
                            }
                        }
                    }
                }

                colors[indexX][indexY] = new Pair<>(state.getChatColor(), hover);
            }
        }

        if (colors[9][9].getRight() != null) {
            colors[9][9] = new Pair<>(ChatColor.YELLOW, "Sua posição\n" + colors[9][9].getRight());
        } else {
            colors[9][9] = new Pair<>(ChatColor.YELLOW, "Sua posição");
        }

        BlockFace direction = DirectionUtils.yawToFace(playerLocation.getYaw());
        BlockFace vectorDirection = DirectionUtils.vectorToFace(playerLocation.getDirection());

        switch (vectorDirection) {
            case EAST:
                colors = rotateToRight(colors, 1);
                break;
            case SOUTH:
                colors = rotateToRight(colors, 2);
                break;
            case WEST:
                colors = rotateToRight(colors, 3);
                break;
        }

        ComponentBuilder builder = new ComponentBuilder("\n");

        Map<Integer, LandState> states = Maps.newHashMap();

        states.put(7, LandState.YOUR_POSITION);
        states.put(8, LandState.FREE_LAND);
        states.put(9, LandState.PROTECTED_ZONE);
        states.put(10, LandState.NEUTRAL_ZONE);
        states.put(11, LandState.WAR_ZONE);
        states.put(12, LandState.YOUR_CLAIM);
        states.put(13, LandState.ALLY_CLAIM);
        states.put(14, LandState.NEUTRAL_CLAIM);
        states.put(15, LandState.TEMPORARY_CLAIM);
        states.put(16, LandState.CONTESTED_CLAIM);
        states.put(17, LandState.UNDER_ATTACK_CLAIM);

        for (int y = 0; y < 19; y++) {

            Pair<ChatColor, String> last = null;

            for (int x = 0; x < 19; x++) {
                if (colors[x][y].equals(last)) {
                    builder.append(CUBE);
                } else {
                    builder.append(CUBE, ComponentBuilder.FormatRetention.NONE);

                    if (colors[x][y].getRight() != null) {
                        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(colors[x][y].getRight())));
                    }

                    builder.bold(false)
                            .color(colors[x][y].getLeft());
                }

                last = colors[x][y];
            }

            if (y == 2) {
                builder
                        .append("  ")
                        .bold(true)
                        .append("\\").color(direction == BlockFace.NORTH_WEST ? ChatColor.RED : ChatColor.GOLD)
                        .append("N").color(direction == BlockFace.NORTH ? ChatColor.RED : ChatColor.GOLD)
                        .append("/").color(direction == BlockFace.NORTH_EAST ? ChatColor.RED : ChatColor.GOLD);
            } else if (y == 3) {
                builder
                        .append("  ")
                        .bold(true)
                        .append("O").color(direction == BlockFace.WEST ? ChatColor.RED : ChatColor.GOLD)
                        .append("+").color(ChatColor.GOLD)
                        .append("L").color(direction == BlockFace.EAST ? ChatColor.RED : ChatColor.GOLD);
            } else if (y == 4) {
                builder
                        .append("  ")
                        .bold(true)
                        .append("/").color(direction == BlockFace.SOUTH_EAST ? ChatColor.RED : ChatColor.GOLD)
                        .append("S").color(direction == BlockFace.SOUTH ? ChatColor.RED : ChatColor.GOLD)
                        .append("\\").color(direction == BlockFace.SOUTH_WEST ? ChatColor.RED : ChatColor.GOLD);
            } else if (y >= 7) {

                LandState state = states.get(y);

                if (state != null) {
                    builder.append("  ")
                            .append(CUBE)
                            .color(state.getChatColor())
                            .append(state.getName())
                            .color(ChatColor.WHITE);
                }
            }

            builder.append("\n");
        }

        DRAW_CHAT_MAP_TIMING.stopTimingIfSync();

        return builder.create();
    }

    public static Pair<ChatColor, String>[][] rotateToRight(Pair<ChatColor, String>[][] colors, int times) {
        Pair<ChatColor, String>[][] out = new Pair[colors.length][colors.length];

        for (int x = 0; x < colors.length; x++) {
            for (int z = 0; z < colors.length; z++) {
                out[z][(colors.length - 1) - x] = colors[x][z];
            }
        }

        if (times > 1) {
            return rotateToRight(out, --times);
        }

        return out;
    }

}
