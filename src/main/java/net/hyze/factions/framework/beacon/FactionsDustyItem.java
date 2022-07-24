package net.hyze.factions.framework.beacon;

import net.hyze.beacon.BeaconConstants;
import net.hyze.beacon.attributes.Attribute;
import net.hyze.beacon.attributes.AttributeRegistry;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;

public class FactionsDustyItem extends CustomItem {

    public FactionsDustyItem() {
        super("factions-dusty-item");
    }

    @Override
    public ItemBuilder getItemBuilder() {

        ItemBuilder itemBuilder = ItemBuilder.of(Material.FIREWORK_CHARGE)
                .name(getDisplayName())
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .flags(ItemFlag.HIDE_ENCHANTS)
                .flags(ItemFlag.HIDE_POTION_EFFECTS)
                .lore(
                        "&eComo utilizar?",
                        "&7Clique com o botão direito enquanto",
                        "&7segura este item para receber uma",
                        "&7Chave de Ativação aleatória!"
                );

        ItemStack stack = itemBuilder.build();

        FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) stack.getItemMeta();

        FireworkEffect.Builder fireworkMetaBuilder = FireworkEffect.builder();

        fireworkMetaBuilder.withColor(Color.WHITE);
        fireworkMetaBuilder.withColor(Color.GREEN);

        fireworkMeta.setEffect(fireworkMetaBuilder.build());

        stack.setItemMeta(fireworkMeta);

        return ItemBuilder.of(stack);
    }

    @Override
    public String getDisplayName() {
        return "&6Chave de Ativação Empoeirada";
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {
        ArrayList<Attribute> list = new ArrayList(AttributeRegistry.get().values());
        Collections.shuffle(list);

        Attribute attribute = list.get(0);

        Player player = event.getPlayer();
        InventoryUtils.subtractOneOnHand(player);

        player.getInventory().addItem(CustomItemRegistry.getItem(BeaconConstants.GET_ATTRIBUTE_KEY_ID.apply(attribute, 1)).asItemStack());
    }

}
