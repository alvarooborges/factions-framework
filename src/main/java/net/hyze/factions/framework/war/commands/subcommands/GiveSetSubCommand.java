package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.war.War;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class GiveSetSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public GiveSetSubCommand() {
        super("giveset");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (War.CONFIG == null) {
            Message.ERROR.send(sender, "Este não é o servidor da guerra.");
            return;
        }

        AtomicInteger count = new AtomicInteger();

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> !player.getGameMode().equals(GameMode.CREATIVE))
                .forEach(
                        player -> {
                            giveItems(player);
                            count.getAndIncrement();
                        }
                );

        Message.SUCCESS.send(sender, "Jogadores que recebram itens: &f" + count.get());
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    public static void giveItems(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        playerInventory.clear();

        Supplier<ItemStack[]> supplier = () -> {
            return new ItemStack[]{
                    new ItemBuilder(Material.DIAMOND_BOOTS)
                            .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                            .enchantment(Enchantment.DURABILITY, 3)
                            .markAntiDupeId()
                            .nbt("guera", true)
                            .make(),
                    new ItemBuilder(Material.DIAMOND_LEGGINGS)
                            .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                            .enchantment(Enchantment.DURABILITY, 3)
                            .markAntiDupeId()
                            .nbt("guera", true)
                            .make(),
                    new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                            .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                            .enchantment(Enchantment.DURABILITY, 3)
                            .markAntiDupeId()
                            .nbt("guera", true)
                            .make(),
                    new ItemBuilder(Material.DIAMOND_HELMET)
                            .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                            .enchantment(Enchantment.DURABILITY, 3)
                            .markAntiDupeId()
                            .nbt("guera", true)
                            .make()
            };
        };

        ItemStack[] armor1 = supplier.get();
        ItemStack[] armor2 = supplier.get();
        ItemStack[] armor3 = supplier.get();
        ItemStack[] armor4 = supplier.get();
        ItemStack[] armor5 = supplier.get();
        ItemStack[] armor6 = supplier.get();

        playerInventory.setArmorContents(supplier.get());

        for (int i = 0; i < 4; i++) {
            playerInventory.setItem(9 + i, armor1[3 - i]);
            playerInventory.setItem(14 + i, armor2[3 - i]);
            playerInventory.setItem(18 + i, armor3[3 - i]);
            playerInventory.setItem(23 + i, armor4[3 - i]);
            playerInventory.setItem(27 + i, armor5[3 - i]);
            playerInventory.setItem(32 + i, armor6[3 - i]);
        }

        ItemStack sword = ItemBuilder.of(Material.DIAMOND_SWORD)
                .enchantment(Enchantment.DAMAGE_ALL, 5)
                .enchantment(Enchantment.DURABILITY, 3)
                .enchantment(Enchantment.FIRE_ASPECT, 2)
                .nbt("guera", true)
                .make();

//        FactionsCustomEnchantment.BATTLE_IMPETUS.getEnchantment().apply(sword, 3);
//        FactionsCustomEnchantment.FURY.getEnchantment().apply(sword, 3);

        ItemStack sword2 = ItemBuilder.of(Material.DIAMOND_SWORD)
                .enchantment(Enchantment.DAMAGE_ALL, 5)
                //.enchantment(Enchantment.KNOCKBACK, 2)
                .enchantment(Enchantment.DURABILITY, 3)
                .enchantment(Enchantment.FIRE_ASPECT, 2)
                .nbt("guera", true)
                .make();

//        FactionsCustomEnchantment.BATTLE_IMPETUS.getEnchantment().apply(sword2, 3);
//        FactionsCustomEnchantment.FURY.getEnchantment().apply(sword2, 3);

        ItemStack bow = ItemBuilder.of(Material.BOW)
                .enchantment(Enchantment.ARROW_DAMAGE, 4)
                .enchantment(Enchantment.DURABILITY, 3)
                .enchantment(Enchantment.ARROW_FIRE, 1)
                .enchantment(Enchantment.ARROW_INFINITE, 1)
                .nbt("guera", true)
                .make();

        playerInventory.setItem(0, sword);
        playerInventory.setItem(1, sword2);
        playerInventory.setItem(2, bow);
        playerInventory.setItem(3, new ItemStack(Material.ARROW, 32));
        playerInventory.setItem(4, ItemBuilder.of(Material.POTION, 1).nbt("guera", true).durability(8233).make());
        playerInventory.setItem(5, ItemBuilder.of(Material.POTION, 1).nbt("guera", true).durability(8233).make());
        playerInventory.setItem(6, ItemBuilder.of(Material.POTION, 1).nbt("guera", true).durability(8233).make());
        playerInventory.setItem(7, ItemBuilder.of(Material.GOLDEN_APPLE, 64).nbt("guera", true).durability(1).make());
        playerInventory.setItem(8, ItemBuilder.of(Material.GOLDEN_APPLE, 32).nbt("guera", true).durability(1).make());

        playerInventory.addItem(ItemBuilder.of(Material.POTION, 1).nbt("guera", true).durability(8233).make());
        playerInventory.addItem(ItemBuilder.of(Material.POTION, 1).nbt("guera", true).durability(8233).make());
    }

}
