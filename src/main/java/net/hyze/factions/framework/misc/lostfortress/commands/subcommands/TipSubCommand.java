package net.hyze.factions.framework.misc.lostfortress.commands.subcommands;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TipSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public TipSubCommand() {
        super("tip", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (!AppType.FACTIONS_LOSTFORTRESS.isCurrent()) {
            Message.ERROR.send(sender, "Digite este comando apenas no servidor da Base Perdida!");
            return;
        }

        if (LostFortressConstants.CURRENT_ID == null) {
            Message.ERROR.send(sender, "Ops, aparentemente o evento não está ocorrendo.");
            return;
        }

        if (args.length == 0) {
            Message.ERROR.send(sender, "Você não digitou nada...");
            return;
        }

        String message = String.join(" ", args);

        ConfirmInventory confirmInventory = ConfirmInventory.of(
                onAccept -> {
                    LostFortressConstants.CURRENT.addTip(message);

                    FactionsProvider.Repositories.LOST_FORTRESS.provide().update(
                            LostFortressConstants.CURRENT_ID,
                            CoreConstants.GSON.toJson(LostFortressConstants.CURRENT)
                    );

                    Message.INFO.send(sender, "Feito! &7Esta informação já foi enviada para o banco!");
                },
                onDeny -> {
                    Message.ERROR.send(sender, "Cancelado!");
                },
                ItemBuilder.of(Material.MAP).name("&aTexto que será enviado:").lore(message).make()
        );

        Player player = (Player) sender;
        player.openInventory(confirmInventory.make());

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
