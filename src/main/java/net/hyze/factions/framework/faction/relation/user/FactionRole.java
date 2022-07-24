package net.hyze.factions.framework.faction.relation.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FactionRole {

    RECRUIT("Recruta", "Recrutas", "-"),
    MEMBER("Membro", "Membros", "+"),
    CAPTAIN("Capitão", "Capitães", "*"),
    LEADER("Líder", "Líderes", "#");

    private final static FactionRole[] VALS = values();

    @Getter
    private final String displayName;

    @Getter
    private final String displayPluralName;

    @Getter
    private final String symbol;

//    @Getter
//    private final ChatColor color;
    public FactionRole next() {
        return VALS[(this.ordinal() + 1) % VALS.length];
    }

    public FactionRole previous() {
        int index = (this.ordinal() - 1) % VALS.length;
        return VALS[index >= 0 ? index : VALS.length - 1];
    }

    public boolean isHigher(FactionRole role) {
        return this.ordinal() > role.ordinal();
    }

    public boolean isSameOrHigher(FactionRole role) {
        return this.ordinal() >= role.ordinal();
    }
}
