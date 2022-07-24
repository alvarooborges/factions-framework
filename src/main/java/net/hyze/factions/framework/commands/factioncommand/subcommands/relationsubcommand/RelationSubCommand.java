package net.hyze.factions.framework.commands.factioncommand.subcommands.relationsubcommand;

import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.relationsubcommand.inventories.IndexInventory;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class RelationSubCommand extends FactionSubCommand {

    public RelationSubCommand() {
        super("relacao", "relação", "rel");

        registerArgument(new Argument("tag", "Tag da facção que você irá convidar para aliança.", false));
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        Faction faction;

        if (args.length < 1) {

            if (relation == null) {
                player.spigot().sendMessage(getUsage(player, "relacao").create());
                return;
            }

            faction = relation.getFaction();

        } else {
            faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(args[0]);

            if (faction == null) {
                Message.ERROR.send(player, String.format("A facção %s não existe.", args[0]));
                return;
            }
        }

        boolean isLeader = relation != null && relation.getFaction().equals(faction) && relation.getRole() == FactionRole.LEADER;

        player.openInventory(new IndexInventory(faction, user, isLeader));
    }
}
