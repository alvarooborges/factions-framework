package net.hyze.factions.framework.spawners;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.hyze.core.shared.world.location.SerializedLocation;

import java.util.Date;

@Getter
@ToString
@AllArgsConstructor
public class Spawner {

    private final SerializedLocation location;

    @NonNull
    private final SpawnerState state;

    @NonNull
    private final SpawnerType type;

    @NonNull
    private final Date transactedAt;
}
