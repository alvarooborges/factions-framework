package net.hyze.factions.framework.faction.relation.faction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;

@AllArgsConstructor
@EqualsAndHashCode(of = {"factionIdA", "factionIdB"})
@ToString
public class FactionRelation {

    private final Integer factionIdA;
    private final Integer factionIdB;

    @Getter
    private final Type type;

    public Integer getFactionIdMin() {
        return Math.min(factionIdA, factionIdB);
    }

    public Integer getFactionIdMax() {
        return Math.max(factionIdA, factionIdB);
    }

    public Faction getFactionMin() {
        return FactionsProvider.Cache.Local.FACTIONS.provide().get(getFactionIdMin());
    }

    public Faction getFactionMax() {
        return FactionsProvider.Cache.Local.FACTIONS.provide().get(getFactionIdMax());
    }

    public enum Type {
        ALLY,
    }
}
