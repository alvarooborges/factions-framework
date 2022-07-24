package net.hyze.factions.framework.misc.lostfortress.inventories;

import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.misc.lostfortress.LostFortress;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class LostFortressInventory extends CustomInventory {

    private final LostFortress log;
    private final CustomInventory backInventory;

    public LostFortressInventory(LostFortress log, CustomInventory backInventory) {
        super(9 * 3, "Base Perdida");

        this.log = log;
        this.backInventory = backInventory;

        tipsIcon();
        notablePlayers();
        playersIcon();

        if (backInventory != null) {
            backItem(backInventory);
        }
    }

    public void tipsIcon() {
        ItemBuilder icon = ItemBuilder.of(Material.MAP)
                .name("&aDicas")
                .lore(
                        "A Base Perdida surgiu em uma",
                        "localização aleatória. Confira",
                        "dicas de onde encontrá-la:",
                        ""
                );

        if (this.log.getTips().isEmpty()) {
            icon.lore("&eSem dicas até o momento.");
        } else {
            this.log.getTips().forEach(
                    tip -> {
                        icon.lore(" &f• " + tip);
                    }
            );
        }

        setItem(
                11,
                icon.make()
        );
    }

    public void notablePlayers() {
        ItemBuilder icon = ItemBuilder.of(Material.GOLD_CHESTPLATE)
                .glowing(true)
                .name("&bJogadores notáveis")
                .lore(
                        "Confira os jogadores que estão",
                        "se destacando nesta edição",
                        "do evento.",
                        ""
                );

        if (this.log.getFirstPlayer() != null) {
            icon.lore("&fPrimeiro jogador a invadir:");

            if (this.log.getFirstPlayer() != null) {
                icon.lore("&7" + this.log.getFirstPlayer().getDisplayName());
            } else {
                icon.lore("&7A base ainda não foi encontrada.");
            }

            icon.lore("");

            if (this.log.getFirstFaction() != null) {
                icon.lore("&fPrimeira facção a invadir:");
                icon.lore(this.log.getFirstFaction().getDisplayName());
            }
        }

        setItem(
                13,
                icon.make()
        );
    }

    public void playersIcon() {
        setItem(
                15,
                ItemBuilder.of(Material.DIAMOND_SWORD)
                .glowing(true)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .name("&aJogadores que pegaram itens")
                .lore(
                        "Aqui você pode conferir quais jogadores",
                        "se deram bem e coletaram recompensas",
                        "durante o evento.",
                        "",
                        "&eClique para ver!"
                )
                .make(),
                event -> {
                    event.getWhoClicked().openInventory(new LostFortressPlayersInventory(this.log, new LostFortressInventory(this.log, this.backInventory)));
                }
        );
    }

}
