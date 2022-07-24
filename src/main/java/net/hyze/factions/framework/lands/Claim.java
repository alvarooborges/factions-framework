package net.hyze.factions.framework.lands;

import lombok.*;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "factionId", callSuper = true)
public class Claim extends Land {

    private Integer factionId;

    private Date createdAt;

    @Setter
    private Integer contestantId;

    @Setter
    private Date contestedAt;

    @Setter
    private boolean temporary;

    public Claim(Integer factionId, Date createdAt, boolean temporary, String appId, int x, int z) {
        super(appId, x, z);
        this.factionId = factionId;
        this.createdAt = createdAt;
        this.temporary = temporary;
    }

    public Faction getFaction() {
        return FactionsProvider.Cache.Local.FACTIONS.provide().get(factionId);
    }

    public Faction getContestant() {
        if (this.contestantId == null) {
            return null;
        }

        return FactionsProvider.Cache.Local.FACTIONS.provide().get(this.contestantId);
    }

    public boolean isContested() {

        if (this.contestantId == null || this.contestedAt == null) {
            return false;
        }

        if (this.getContestant() == null) {
            return false;
        }

        return System.currentTimeMillis() - (this.contestedAt.getTime() + TimeUnit.MINUTES.toMillis(10)) < 0;
    }
}
