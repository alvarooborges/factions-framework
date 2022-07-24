package net.hyze.factions.framework.bank.history;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BankHistoryType {

    DEPOSIT("Depósito"), BUY_ITEM("Compra de Item");

    private final String displayName;

}
