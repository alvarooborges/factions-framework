package net.hyze.factions.framework.commands;

import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnerCommand extends CustomCommand implements GroupCommandRestrictable {

    public SpawnerCommand() {
        super("spawner", CommandRestriction.IN_GAME);

        registerArgument(new Argument("Tipo", "Tipo do gerador que será entregue"));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        String typeRaw = args[0].toUpperCase();

        SpawnerType type = Enums.getIfPresent(SpawnerType.class, typeRaw).orNull();

        if (type == null) {
            Message.ERROR.send(player, "Tipo inválido! Use: " + Joiner.on(", ").join(SpawnerType.values()) + ".");
            return;
        }

        ItemStack stack = type.getCustomItem().asItemStack();
        if (!InventoryUtils.fits(player.getInventory(), stack)) {
            Message.ERROR.send(player, "Seu invetário está cheio.");
            return;
        }

        player.getInventory().addItem(stack);
        player.updateInventory();

        Message.SUCCESS.send(player, "Você recebeu 1 " + type.getDisplayName());
    }
}
