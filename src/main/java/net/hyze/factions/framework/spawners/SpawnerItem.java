package net.hyze.factions.framework.spawners;

import lombok.Getter;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.utils.ItemBuilder;

public class SpawnerItem extends CustomItem {

    @Getter
    private final SpawnerType type;

    public SpawnerItem(SpawnerType type) {
        super(String.format("spawner_%s_item", type.name().toLowerCase()));
        this.type = type;
    }

    @Override
    public ItemBuilder getItemBuilder() {
        return new ItemBuilder(this.type.getIcon().getHead())
                .name(this.getDisplayName())
                .nbt(SpawnersSetup.METADATA_TYPE_TAG, this.type.name());
    }

    @Override
    public String getDisplayName() {
        return this.type.getDisplayName();
    }
}
