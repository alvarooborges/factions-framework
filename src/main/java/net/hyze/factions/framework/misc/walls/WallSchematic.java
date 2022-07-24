package net.hyze.factions.framework.misc.walls;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;

import java.io.File;

@Getter
@RequiredArgsConstructor
public enum WallSchematic {

    NORMAL(new File(CoreConstants.CLOUD_DIRECTORY, "schematics/magma-wall.schematic")),
    WITH_PORTAL(new File(CoreConstants.CLOUD_DIRECTORY, "schematics/magma-portal.schematic"));

    private final File file;

}
