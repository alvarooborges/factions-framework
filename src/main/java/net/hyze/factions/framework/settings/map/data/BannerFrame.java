package net.hyze.factions.framework.settings.map.data;

import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.frame.Frame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.io.IOException;
import java.net.URL;

@RequiredArgsConstructor
public class BannerFrame {

    private final String url;
    private final Location location;
    private final BlockFace blockFace;

    public void setup() {
        try {

            while (Bukkit.createMap(Bukkit.getWorlds().get(0)).getId() < 100) {
            }

            {
                Frame frame = new Frame(new URL(this.url));
                frame.place(this.location, this.blockFace);
            }

        } catch (IOException | IllegalArgumentException ignore) {
        }
    }
}
