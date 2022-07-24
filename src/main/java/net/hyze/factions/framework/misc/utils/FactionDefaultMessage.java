package net.hyze.factions.framework.misc.utils;

import net.hyze.core.shared.misc.utils.DefaultMessage;

public class FactionDefaultMessage extends DefaultMessage {

    public static FactionDefaultMessage FACTION_NOT_FOUND = new FactionDefaultMessage("Não existe uma facção com a tag '%s'.");
    public static FactionDefaultMessage NO_FACTIONS = new FactionDefaultMessage("Você não faz parte de uma facção.");
    
    public FactionDefaultMessage(String rawMessage) {
        super(rawMessage);
    }

}
