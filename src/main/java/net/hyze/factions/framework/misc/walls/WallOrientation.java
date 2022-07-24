package net.hyze.factions.framework.misc.walls;

import com.sk89q.worldedit.Vector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WallOrientation {

    EAST_WEST(90, new Vector(0, -5, 0)), NORTH_SOUTH(0, new Vector(0, -5, 0));

    private final int angle;
    private final Vector offset;
}
