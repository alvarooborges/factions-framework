package net.hyze.factions.framework.misc.tags;

import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.stream.Stream;

public class TagInventory extends PaginateInventory {

    public TagInventory(User user) {
        super("Tags");

        Stream.of(EnumTag.values()).forEach(enumTag -> {

            ItemBuilder icon = new ItemBuilder(Material.NAME_TAG)
                    .name(enumTag.getText())
                    .lore("&aClique para selecionar.");
            
            addItem(
                    icon.make(),
                    event -> {

                    }
            );
            
        });

    }

}
