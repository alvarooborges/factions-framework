package net.hyze.factions.framework.misc.lostfortress.commands.subcommands;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

public class TagItemsSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public TagItemsSubCommand() {
        super("tagitems", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        if (!AppType.FACTIONS_LOSTFORTRESS.isCurrent()) {
            Message.ERROR.send(sender, "Digite este comando apenas no servidor da Base Perdida!");
            return;
        }

        Player player = (Player) sender;

        WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

        Selection selection = worldEditPlugin.getSelection(player);

        if (selection == null) {
            Message.ERROR.send(sender, "Você precisa fazer uma seleção usando o WorldEdit primeiro.");
            return;
        }

        Vector min = selection.getNativeMinimumPoint();
        Vector max = selection.getNativeMaximumPoint();

        WorldCuboid cuboid = new WorldCuboid(
                "world",
                (int) min.getX(),
                (int) min.getY(),
                (int) min.getZ(),
                (int) max.getX(),
                (int) max.getY(),
                (int) max.getZ()
        );

        AtomicInteger count = new AtomicInteger();

        cuboid.getSolidBlocks(
                block -> {

                    if (!block.getType().equals(Material.CHEST)) {
                        return;
                    }
                    BlockState blockState = block.getState();

                    Chest chest = (Chest) blockState;

                    for (ItemStack itemStack : chest.getBlockInventory().getContents()) {

                        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                            continue;
                        }

                        ItemBuilder.of(itemStack, true).nbt(LostFortressConstants.ITEM_NBT, true);
                        count.incrementAndGet();
                    }

                }
        );

        Message.INFO.send(sender, count.get() + " itens foram marcados.");

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
