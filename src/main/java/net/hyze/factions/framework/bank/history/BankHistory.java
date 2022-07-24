package net.hyze.factions.framework.bank.history;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.user.FactionUser;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class BankHistory {

    private final FactionUser user;
    private final BankHistoryType type;
    private final double value;
    private final Date createdAt;

    private String[] description;

    public BankHistory(FactionUser user, BankHistoryType type, double value, Date createdAt, String... description) {
        this(user, type, value, createdAt);
        this.description = description;
    }

}
