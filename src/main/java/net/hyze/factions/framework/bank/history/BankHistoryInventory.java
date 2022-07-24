package net.hyze.factions.framework.bank.history;

import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.factions.framework.bank.BankInventory;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;

import java.text.SimpleDateFormat;
import java.util.List;

public class BankHistoryInventory extends PaginateInventory {

    /**
     * Todo: Procurar no código uma útil para isso aqui.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public BankHistoryInventory(FactionUser user) {
        super("Histórico de Movimentações");

        List<BankHistory> list = user.getRelation().getFaction().getBank().getHistory();

        list.sort(
                (o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt())
        );

        list.forEach(
                history -> {

                    FactionUser target = history.getUser();

                    addItem(
                            ItemBuilder.of(Material.PAPER)
                            .name("&a" + history.getType().getDisplayName())
                            .lore("&fMembro: &7" + target.getHandle().getHighestGroup().getDisplayTag(target.getNick()))
                            .lore("&fValor: &a" + Currency.COINS.format(history.getValue()))
                            .lore("&fData: &7" + DATE_FORMAT.format(history.getCreatedAt()))
                            .make()
                    );

                }
        );

        backItem(new BankInventory(user));
    }

}
