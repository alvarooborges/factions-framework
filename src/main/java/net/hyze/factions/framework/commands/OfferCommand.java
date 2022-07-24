package net.hyze.factions.framework.commands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.offers.Offer;
import net.hyze.factions.framework.misc.offers.inventories.OfferInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class OfferCommand extends CustomCommand {

    public static boolean eneblad = false;

    public OfferCommand() {
        super("ofertas", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;

        LinkedList<Offer> offers = null;

        if (args.length == 1 && user.hasGroup(Group.GAME_MASTER)) {

            User target = CoreProvider.Cache.Local.USERS.provide().get(args[0]);
            offers = FactionsProvider.Repositories.OFFERS.provide().get(target.getId());

        }

        if (offers == null) {
            offers = FactionsProvider.Repositories.OFFERS.provide().get(user.getId());
        }

        player.openInventory(new OfferInventory(offers));
    }

}
