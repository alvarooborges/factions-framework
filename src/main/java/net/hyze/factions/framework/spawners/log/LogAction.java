package net.hyze.factions.framework.spawners.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogAction {

    DEPOSIT_ITEM("Depositou"), // depositar da mão
    WITHDRAW_ITEM("Retirou"), // sacar depositados  
//    PLACE_ITEM, // colocar no chão
//    BREAK_PLACED, // quebrar do chão
    DEPOSIT_PLACED("Armazenou"), // depositar colocado
    PLACE_COLLECTED("Recolocou"); // colocar coletado
    
    
    private final String actionName;
}
