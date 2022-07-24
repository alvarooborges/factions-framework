package net.hyze.factions.framework.misc.lostfortress;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.shared.misc.utils.Vector3D;
import net.hyze.core.spigot.commands.CommandRegistry;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.lostfortress.commands.LostFortressCommand;
import org.bukkit.Bukkit;

public class LostFortressSetup {

    public static void setup() {

        /**
         * Gambiarra braba.
         */
        WorldCuboid cuboid = new WorldCuboid("world", -308, 77, 323, 323, 77, -308);

        cuboid.getWalls(
                block -> LostFortressConstants.SPAWN_POINTS.add(
                        new Vector3D(
                                block.getX(),
                                block.getY(),
                                block.getZ()
                        )
                )
        );

        CommandRegistry.registerCommand(new LostFortressCommand());

        if (!(AppType.FACTIONS_LOSTFORTRESS.isCurrent() || AppType.FACTIONS_TESTS.isCurrent())) {
            return;
        }
        /**
         * Daqui pra baixo, registra apenas coisas que devem funcionar no
         * servidor da base perdida.
         */

        Bukkit.getPluginManager().registerEvents(new LostFortressListeners(), FactionsPlugin.getInstance());

        Pair<Integer, String> pair = FactionsProvider.Repositories.LOST_FORTRESS.provide().fetch();

        if (pair != null) {
            LostFortressConstants.CURRENT_ID = pair.getLeft();
            LostFortressConstants.CURRENT = CoreConstants.GSON.fromJson(pair.getRight(), LostFortress.class);
        }

    }

}
