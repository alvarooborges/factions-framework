package net.hyze.factions.framework.bank;

import com.google.common.primitives.Doubles;
import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.bank.history.BankHistory;
import net.hyze.factions.framework.bank.history.BankHistoryInventory;
import net.hyze.factions.framework.bank.history.BankHistoryType;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Date;

public class BankInventory extends CustomInventory {

    public BankInventory(FactionUser user) {
        super(54, "Banco da Facção");

        Faction faction = user.getRelation().getFaction();
        Bank bank = faction.getBank();

        setItem(
                10,
                ItemBuilder.of(Material.CHEST)
                .name("&aDepositar")
                .lore(
                        "&fSaldo atual: &a" + NumberUtils.toK(bank.getBalance()),
                        "",
                        "&eClique para realizar um deposito."
                )
                .make(),
                event -> {

                    CoreSpigotPlugin.getSignFactory()
                    .newMenu(
                            (Player) event.getWhoClicked(),
                            new String[]{"", "^^^^", "Insira o valor", "a ser depositado."}
                    )
                    .response(
                            (player, args) -> {
                                Double value = Doubles.tryParse(args[0]);

                                if (value == null) {
                                    Message.ERROR.send(player, "Ops, o valor inserido para depósito é inválido.");
                                    return;
                                }

                                bank.setBalance(value);

                                bank.addHistory(new BankHistory(user, BankHistoryType.DEPOSIT, value, new Date(System.currentTimeMillis())));

                                Message.SUCCESS.send(player, "Depósito realizado com suceso!");
                            }
                    )
                    .open();

                }
        );

        setItem(
                12,
                ItemBuilder.of(Material.BOOK_AND_QUILL)
                .name("&eHistórico de Movimentações")
                .lore(
                        "Veja aqui o histórico de todas as",
                        "movimentações realizadas no",
                        "banco da facção.",
                        "",
                        "&eClique para abrir."
                )
                .make(),
                event -> {
                    event.getWhoClicked().openInventory(new BankHistoryInventory(user));
                }
        );

        setItem(
                14,
                ItemBuilder.of(Material.MAP)
                .name("&eEstatísticas de Membros")
                .lore(
                        "Clique para ver o quanto",
                        "cada membro está colaborando",
                        "com a facção.",
                        "",
                        "&eClique para abrir."
                )
                .make()
        );


        backOrCloseItem();
    }

}
