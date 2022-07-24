package net.hyze.factions.framework.beacon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FactionBeaconConstants {

    public static final String BEACON_SUPREME = "beacon-supreme";
    public static final String BEACON_DEFAULT = "beacon-default";

    public static final String BEACON_BREAKED = "beacon-breaked";

    public static final String BEACON_DURABILITY = "beacon-durability";

    public static final Long BEACON_BREAKED_COOLDOWN = 72L * (60L * 60000L);

}
