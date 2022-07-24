package net.hyze.factions.framework.misc.customitem.data;

import lombok.Getter;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.entities.SuperCreeperEntity;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import org.bukkit.Material;

public class SuperCreeperEggItem extends AbstractCreeperEggItem {

    @Getter
    private final ItemBuilder itemBuilder;

    public SuperCreeperEggItem() {
        super("super-creeper-item");

        this.itemBuilder = ItemBuilder.of(Material.MONSTER_EGG, (short) 50)
                .glowing(true)
                .name("&6Ovo de Super Creeper")
                .lore("Invoca um creeper eletrizado", "que causa mais dano e possui", "um raio de explosão maior.")
                .lore("")
                .lore("&fAlcance da explosão: &78 blocos", "&fDano em blocos: &75 pontos");
    }

    @Override
    public String getDisplayName() {
        return "&6Ovo de Super Creeper";
    }

    @Override
    public Class<? extends EntityCreeper> getCreeperClass() {
        return SuperCreeperEntity.class;
    }
}
