package net.hyze.factions.framework.war;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import net.hyze.core.shared.world.location.SerializedLocation;
import org.bukkit.World;

@Getter
@Builder
public class WarConfig {
    
    @NonNull
    private World world;
    
    @NonNull
    private SerializedLocation spawn;
}
