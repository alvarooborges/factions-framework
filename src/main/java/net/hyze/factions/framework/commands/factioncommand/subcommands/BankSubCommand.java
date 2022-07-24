package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.bank.BankInventory;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class BankSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public BankSubCommand() {
        super("banco");
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        if (relation == null) {
            Message.ERROR.send(player, "Ops, você precisa ser membro de uma facção para executar este comando.");
            return;
        }

        player.openInventory(new BankInventory(user));
    }

}
