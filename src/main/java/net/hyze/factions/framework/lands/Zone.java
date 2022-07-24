package net.hyze.factions.framework.lands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString(of = "type", callSuper = true)
public class Zone extends Land {

    protected Type type;

    public Zone(Type type, String appId, int x, int z) {
        super(appId, x, z);
        this.type = type;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        WAR(false, true, false),
        PROTECTED(false, false, false),
        NEUTRAL(true, true, true),
        VOID(false, false, false),
        LOST_FORTRESS(true, true, false);

        private final boolean explosionsEnabled;
        private final boolean pvpEnabled;
        private final boolean buildEnabled;
    }
}
