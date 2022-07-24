package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.argument.impl.NickArgument;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.UserLeftFactionPacket;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class KickSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_PLAYER;

    public KickSubCommand() {
        super("kick", FactionRole.RECRUIT, "expulsar");

        registerArgument(new NickArgument("nick", "Nick do jogador que será expulso."));

    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        if (!FactionPermission.COMMAND_KICK.allows(relation.getFaction(), user)) {
            Message.ERROR.send(player, "Você não tem permissão para expulsar membros da sua facção.");
            return;
        }

        if (relation.getFaction().isUnderAttack()) {
            Message.ERROR.send(player,"Você não pode expulsar jogadores da sua facção enquanto ela estiver sob ataque.");
            return;
        }

        String targetNick = args[0];

        User targetHandle = CoreProvider.Cache.Local.USERS.provide().get(targetNick);

        if (targetHandle == null) {
            Message.ERROR.send(player, String.format("O jogador %s não foi encontrado.", targetNick));
            return;
        }

        FactionUserRelation targetRelation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(targetHandle.getId());

        if (targetRelation == null || !targetRelation.getFaction().equals(relation.getFaction())) {
            Message.ERROR.send(player, String.format("O jogador %s não está na sua facção.", targetNick));
            return;
        }

        if (relation.getRole().ordinal() <= targetRelation.getRole().ordinal()) {
            Message.ERROR.send(player, "Você não pode expulsar jogadores com o rank maior ou igual ao seu.");
            return;
        }

        FactionsProvider.Repositories.USERS_RELATIONS.provide().remove(targetHandle.getId());

        CoreProvider.Redis.ECHO.provide().publish(new UserLeftFactionPacket(
                targetRelation.getFaction(),
                targetHandle.getId(),
                UserLeftFactionPacket.Reason.KICK
        ));

        Message.SUCCESS.send(player, String.format("Você expulsou o jogador %s.", targetHandle.getNick()));
    }

}
