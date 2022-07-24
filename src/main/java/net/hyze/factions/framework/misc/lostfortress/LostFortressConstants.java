package net.hyze.factions.framework.misc.lostfortress;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.misc.utils.Vector3D;
import net.hyze.core.spigot.misc.utils.WorldCuboid;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LostFortressConstants {

    public static final String ITEM_NBT = "lost-fortress-item";

    public static final WorldCuboid LOST_FORSTRESS_CUBOID = new WorldCuboid("world", -1851, 255, 2349, -2151, 0, 2049);

    public static final WorldCuboid LOST_FORSTRESS_INSIDE_CUBOID = new WorldCuboid("world", -1968, 71, 2164, -2034, 128, 2234);

    public static final List<Vector3D> SPAWN_POINTS = Lists.newArrayList();

    public static Integer CURRENT_ID;
    public static LostFortress CURRENT;
    public static Boolean STATUS = false;
    public static Boolean FALL_DAMAGE = true;

    public static class Databases {

        public static class Mysql {

            public static class Tables {

                public static final String LOG_TABLE_NAME = "lost_fortress_log";

            }
        }
    }
}
