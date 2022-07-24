package net.hyze.factions.framework.menu.inventories.lab;

import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;

public class LabIndexInventory extends CustomInventory {

    public LabIndexInventory() {
        super(4 * 9, "Laboratório");

        setItem(
                11,
                new ItemBuilder(Material.PAINTING)
                        .name("&eColetas especiais")
                        .lore(
                                "&7Clique para visualizar",
                                "&7o progresso de coletas especiais",
                                "&7da facção como um todo."
                        )
                        .make(),
                event -> event.getActor().performCommand("") //TODO: colocar pra abrir inventário de coletas individual
        );

        setItem(
                15,
                new ItemBuilder(Material.BEACON)
                        .name("&eBonificações")
                        .lore(
                                "&7Visualize as bonificações",
                                "&7que a facção possuí pendentes",
                                "&7para ativação.",
                                "",
                                "&eClique para visualizar."
                        )
                        .make(),
                event -> event.getActor().performCommand("kothrewards")
        );

        backOrCloseItem();

    }

}
