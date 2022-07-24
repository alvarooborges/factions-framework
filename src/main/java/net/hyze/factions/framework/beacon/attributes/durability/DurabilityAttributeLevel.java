package net.hyze.factions.framework.beacon.attributes.durability;

import lombok.Getter;
import net.hyze.beacon.attributes.data.buff.BuffAttributeLevel;

public class DurabilityAttributeLevel extends BuffAttributeLevel {

    @Getter
    private final int durability;
    
    public DurabilityAttributeLevel(int durability) {
        super("Durabilidade do Sinalizador: " + durability);
        this.durability = durability;
    }
    
    @Override
    public String getName(){
        return "Durabilidade do Sinalizador: &f" + this.durability + ".";
    }

}
