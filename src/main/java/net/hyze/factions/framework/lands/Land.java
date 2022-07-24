package net.hyze.factions.framework.lands;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class Land {

    protected String appId;
    protected int chunkX;
    protected int chunkZ;
}
