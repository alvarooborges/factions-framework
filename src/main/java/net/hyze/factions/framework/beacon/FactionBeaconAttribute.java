package net.hyze.factions.framework.beacon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.beacon.attributes.Attributable;
import net.hyze.beacon.attributes.Attribute;
import net.hyze.beacon.attributes.data.effect.data.*;
import net.hyze.factions.framework.beacon.attributes.durability.DurabilityAttribute;
import net.hyze.factions.framework.beacon.attributes.restrict.RestrictAttribute;

@Getter
@RequiredArgsConstructor
public enum FactionBeaconAttribute implements Attributable {

    RESTRICT(new RestrictAttribute()),
    DURABILITY(new DurabilityAttribute()),
    HASTE(new HasteAttribute()),
    //REGENERATION(new RegenerationAttribute()),
    JUMP(new JumpAttribute()),
    //RESISTANCE(new ResistanceAttribute()),
    SPEED(new SpeedAttribute()),
    STRENGTH(new StrengthAttribute()),
    EXTRA_LIFE(new ExtraLifeAttribute());

    private final Attribute attribute;

}
