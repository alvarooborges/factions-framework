package net.hyze.factions.framework.misc.customitem.data;

import lombok.Getter;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.entities.CreeperEntity;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import org.bukkit.Material;

public class CreeperEggItem extends AbstractCreeperEggItem {

    public static final String NBT_KEY = "creeper-item";

    @Getter
    private final ItemBuilder itemBuilder;

    public CreeperEggItem() {
        super(NBT_KEY);

        this.itemBuilder = ItemBuilder.of(Material.MONSTER_EGG, (short) 50)
                .name("&aOvo de Creeper")
                .lore("Invoca um creeper que pode", "ser utilizado para explodir", "blocos e geradores!")
                .lore("")
                .lore("&fAlcance da explosão: &74 blocos", "&fDano em blocos: &71 pontos");
    }

    @Override
    public String getDisplayName() {
        return "&aOvo de Creeper";
    }

    @Override
    public Class<? extends EntityCreeper> getCreeperClass() {
        return CreeperEntity.class;
    }
}
