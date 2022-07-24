package net.hyze.factions.framework.bank;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hyze.factions.framework.bank.history.BankHistory;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class Bank {

    private final List<BankHistory> history = Lists.newArrayList();

    private double balance;

    public void addHistory(BankHistory history) {
        this.history.add(history);
    }

}
