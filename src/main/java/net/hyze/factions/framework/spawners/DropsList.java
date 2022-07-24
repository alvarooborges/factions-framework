package net.hyze.factions.framework.spawners;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DropsList extends ArrayList<ItemStack> {
    @Override
    public boolean add(ItemStack stack) {
        if (stack.getAmount() > 0) {
            return super.add(stack);
        }

        return false;
    }
}