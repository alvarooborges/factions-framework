package net.hyze.factions.framework.faction.relation.user;

import lombok.*;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.user.FactionUser;

import java.util.Date;

@Getter
@ToString(callSuper = true)
@AllArgsConstructor
@EqualsAndHashCode(of = "userId")
public class FactionUserRelation {

    private final Integer userId;

    @Setter
    @NonNull
    private Faction faction;

    @Setter
    @NonNull
    private FactionRole role;

    @Setter
    @NonNull
    private Date since;

    public FactionUser getUser() {
        return FactionsProvider.Cache.Local.USERS.provide().get(this.userId);
    }
}
