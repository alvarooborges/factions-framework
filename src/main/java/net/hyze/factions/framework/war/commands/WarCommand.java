package net.hyze.factions.framework.war.commands;

import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.factions.framework.commands.factioncommand.subcommands.KickSubCommand;
import net.hyze.factions.framework.war.War;
import net.hyze.factions.framework.war.commands.subcommands.*;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.function.Predicate;

public class WarCommand extends CustomCommand {

    public WarCommand() {
        super("guerra");

        registerSubCommand(new GiveSetSubCommand());
        registerSubCommand(new StartSubCommand());
        registerSubCommand(new NextSubCommand());
        registerSubCommand(new StopSubCommand());
        registerSubCommand(new InfoSubCommand());
        registerSubCommand(new TestSubCommand());
        registerSubCommand(new KickSubCommand());
        registerSubCommand(new BossSubCommand());
        registerSubCommand(new WhiteListSubCommand());
        registerSubCommand(new AllyFireSubCommand());
        registerSubCommand(new SendSubCommand());
        registerSubCommand(new ToggleSubCommand());
        registerSubCommand(new PauseSubCommand());
        registerSubCommand(new ResumeSubCommand());

    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (!War.OPEN) {
            Message.ERROR.send(sender, "Ops, o evento Guerra não está aberto.");
            return;
        }

        Player player = (Player) sender;

        if (War.TEST) {
            for (ItemStack item : player.getInventory().getArmorContents()) {

                if (item == null || item.getType().equals(Material.AIR)) {
                    continue;
                }

                Message.ERROR.send(sender, "Este é um evento com itens predefinidos, você precisa estar com o inventário totalmente vazio para entrar.");
                return;
            }

            for (ItemStack item : player.getInventory().getContents()) {

                if (item == null || item.getType().equals(Material.AIR)) {
                    continue;
                }

                Message.ERROR.send(sender, "Este é um evento com itens predefinidos, você precisa estar com o inventário totalmente vazio para entrar.");
                return;
            }

        } else {
            Predicate<Material> testMaterial = type -> type.equals(Material.DIAMOND_HELMET)
                    || type.equals(Material.DIAMOND_CHESTPLATE)
                    || type.equals(Material.DIAMOND_LEGGINGS)
                    || type.equals(Material.DIAMOND_BOOTS);

            boolean checkContents = Arrays.stream(player.getInventory().getArmorContents())
                    .anyMatch(item -> item == null || !(testMaterial.test(item.getType())
                            && item.getEnchantments().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)
                            && item.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) >= 4));

            if (checkContents) {
                Message.ERROR.send(player, "Ops, para entrar na Guerra você precisa estar utilizando uma armadura com proteção 4.");
                return;
            }
        }

        TeleportManager.teleport(user, AppType.FACTIONS_WAR, ConnectReason.WARP, "");

    }

}
