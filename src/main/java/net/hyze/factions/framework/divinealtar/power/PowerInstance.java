package net.hyze.factions.framework.divinealtar.power;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.divinealtar.power.impl.DivineProtectionPower;
import net.hyze.factions.framework.divinealtar.power.impl.MeteorRainPower;
import net.hyze.factions.framework.divinealtar.power.impl.ProsperityPower;
import net.hyze.factions.framework.divinealtar.power.impl.ThunderstormPower;
import net.hyze.factions.framework.divinealtar.power.impl.electromagnetic.ElectromagneticPulsePower;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum PowerInstance {

    PROSPERITY(new ProsperityPower()),
    DIVINE_PROTECTION(new DivineProtectionPower()),
    ELECTROMAGNETIC_POWER(new ElectromagneticPulsePower()),
    METEOR_RAIN(new MeteorRainPower()),
    THUNDERSTORM(new ThunderstormPower());

    @Getter
    private final Power power;
    
    public static PowerInstance getById(String powerId){
        return Stream.of(PowerInstance.values())
                .filter(target -> target.getPower().getId().equalsIgnoreCase(powerId))
                .findFirst()
                .orElse(null);
    }

}
