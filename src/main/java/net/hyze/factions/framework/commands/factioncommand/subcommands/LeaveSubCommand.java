package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.UserLeftFactionPacket;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class LeaveSubCommand extends FactionSubCommand {


    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.PLAYER_TO_FACTION;

    public LeaveSubCommand() {
        super("sair", FactionRole.RECRUIT);
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation runtimeRelation, String[] args) {

        if (runtimeRelation.getRole() == FactionRole.LEADER) {
            Message.ERROR.send(player, "Você precisa transferir ou terminar sua facção antes de sair.");
            return;
        }

        ConfirmInventory confirmInventory = ConfirmInventory.of(event -> {
            FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(user.getId());

            if (relation == null) {
                Message.ERROR.send(player, "Você não é membro de uma facção.");
                player.closeInventory();
                return;
            }

            if (relation.getRole() == FactionRole.LEADER) {
                Message.ERROR.send(player, "Você precisa transferir ou terminar sua facção antes de sair.");
                player.closeInventory();
                return;
            }

            FactionsProvider.Repositories.USERS_RELATIONS.provide().remove(user.getId());

            CoreProvider.Redis.ECHO.provide().publish(new UserLeftFactionPacket(
                    relation.getFaction(),
                    user.getId(),
                    UserLeftFactionPacket.Reason.LEAVE
            ));

            Message.SUCCESS.send(player, "Você saiu da facção.");

        }, event -> {
            Message.ERROR.send(player, "Você cancelou a ação de sair da sua facção.");
        }, null);

        player.openInventory(confirmInventory.make("Você sairá da facção."));
    }
}
