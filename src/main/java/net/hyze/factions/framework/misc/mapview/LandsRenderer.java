package net.hyze.factions.framework.misc.mapview;

import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.lands.Land;
import net.hyze.factions.framework.lands.LandState;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class LandsRenderer extends MapRenderer {

    private long lastClaimHash = 0;

    public LandsRenderer() {
        super(true);
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        Location playerLocation = player.getLocation();
        mapView.setCenterX(playerLocation.getBlockX());
        mapView.setCenterZ(playerLocation.getBlockZ());
        
        int playerChunkX = playerLocation.getBlockX() >> 4;
        int playerChunkZ = playerLocation.getBlockZ() >> 4;

        long currentPlayerClaimHash = LongHash.toLong(playerChunkX, playerChunkZ);

        if (lastClaimHash != currentPlayerClaimHash) {
            lastClaimHash = currentPlayerClaimHash;

            BufferedImage bufferedImage = generate(player);
            for (int x = 0; x <= 127; x++) {
                for (int y = 0; y <= 127; y++) {
                    mapCanvas.setPixel(x, y, (byte) bufferedImage.getRGB(x, y));
                }
            }
        }
    }

    public static void applyToMap(MapView map) {
        if (map != null) {
            for (MapRenderer renderer : map.getRenderers()) {
                map.removeRenderer(renderer);
            }

            map.addRenderer(new LandsRenderer());
        }
    }

    public static BufferedImage generate(Player player) {
        Location playerLocation = player.getLocation();
        int playerChunkX = playerLocation.getBlockX() >> 4;
        int playerChunkZ = playerLocation.getBlockZ() >> 4;

        BufferedImage bufferedImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

        byte[][] colors = new byte[25][25];

        int minChunkX = playerChunkX - 12;
        int minChunkZ = playerChunkZ - 12;

        int maxChunkX = playerChunkX + 12;
        int maxChunkZ = playerChunkZ + 12;

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                int indexX = x - minChunkX;
                int indexZ = z - minChunkZ;

                if (LocationUtils.isInBoundsOfBorder(playerLocation.getWorld().getChunkAt(x, z))) {
                    Land land = FactionsProvider.Cache.Local.LANDS.provide().get(x, z);
                    colors[indexX][indexZ] = LandState.get(user, land).getMapColor();
                } else {
                    colors[indexX][indexZ] = 87;
                }
            }
        }

        for (int x = 0; x <= 127; x++) {
            for (int y = 0; y <= 127; y++) {
                bufferedImage.setRGB(x, y, 0);
            }
        }

        for (int x = 0; x < 25; x++) {
            int pixelX = x * 4 + 2 + x;

            for (int z = 0; z < 25; z++) {
                int pixelY = z * 4 + 2 + z;

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        bufferedImage.setRGB(pixelX + i, pixelY + j, colors[x][z]);
                    }
                }
            }
        }

        return bufferedImage;
    }
}
