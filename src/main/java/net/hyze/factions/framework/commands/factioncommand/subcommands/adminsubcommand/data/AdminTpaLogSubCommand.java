package net.hyze.factions.framework.commands.factioncommand.subcommands.adminsubcommand.data;

import lombok.Getter;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.tpa.inventories.TpaAcceptLogInventory;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminTpaLogSubCommand extends FactionSubCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group = Group.GAME_MASTER;

    public AdminTpaLogSubCommand() {
        super("tpalog");

        registerArgument(new Argument("tag", "Tag da facção", true));
    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {
        String tag = args[0];

        Faction checker = FactionsProvider.Cache.Local.FACTIONS.provide().get(tag);

        if (checker == null) {
            Message.ERROR.send(sender, "Esta facção não existe.");
            return;
        }

        Player player = (Player) sender;

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(handle);

        player.openInventory(new TpaAcceptLogInventory(user, checker));
    }
}
