package net.hyze.factions.framework.user.options;

import lombok.*;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.FactionsProvider;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class UserOptions {

    private final User handle;

    private boolean seeChunksEnabled = false;

    private boolean adminModeEnabled = false;

    private boolean autoMapEnabled = false;

    private boolean lightEnabled = false;

    private boolean pvpEnabled = true;

    private boolean globalTabEnabled = true;

    private int AACKickCount = 0;
    private long AACBanAt = 0;

//    private boolean flightEnabled = false;
    public void sync() {
        FactionsProvider.Cache.Redis.USERS_OPTIONS.provide().update(this.handle, this);
    }
}
