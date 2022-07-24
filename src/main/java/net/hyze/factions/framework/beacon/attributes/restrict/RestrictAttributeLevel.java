package net.hyze.factions.framework.beacon.attributes.restrict;

import net.hyze.beacon.attributes.data.buff.BuffAttributeLevel;

public class RestrictAttributeLevel extends BuffAttributeLevel {

    public RestrictAttributeLevel(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return this.getDescription();
    }

}
