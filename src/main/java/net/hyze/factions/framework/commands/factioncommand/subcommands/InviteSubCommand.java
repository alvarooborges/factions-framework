package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.commands.argument.impl.NickArgument;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.UserInvitedFactionPacket;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class InviteSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_PLAYER;

    public InviteSubCommand() {
        super("convidar", FactionRole.RECRUIT, "invite");

        registerArgument(new NickArgument("nick", "Nick do jogador que será convidado"));
    }

    @Override
    public void onCommand(Player player, FactionUser senderUser, FactionUserRelation relation, String[] args) {

        if (!FactionPermission.COMMAND_INVITE.allows(relation.getFaction(), senderUser)) {
            Message.ERROR.send(player, "Você não tem permissão para recrutar memberos para sua facção.");
            return;
        }

        if (relation.getFaction().isUnderAttack()) {
            Message.ERROR.send(player,"Você não pode convidar jogadores para sua facção enquanto ela estiver sob ataque.");
            return;
        }

        Set<FactionUser> allUsers = FactionUtils.getUsers(relation.getFaction());

        if (allUsers.size() >= FactionsProvider.getSettings().getFactionMaxMembers()) {
            Message.ERROR.send(player, String.format(
                    "Sua facção já atingiu o número máximo de %s jogadores.",
                    FactionsProvider.getSettings().getFactionMaxMembers()
            ));
            return;
        }

        String targetNick = args[0];

        FactionUser targetUser = FactionsProvider.Cache.Local.USERS.provide().get(targetNick);

        if (targetUser == null) {
            Message.ERROR.send(player, "O jogador " + targetNick + " não foi encontrado.");
            return;
        }

        if (targetUser.equals(senderUser)) {
            Message.ERROR.send(player, "Você precisa escolher outro jogador para convidar.");
            return;
        }

        targetNick = targetUser.getNick();

        App app = CoreProvider.Cache.Redis.USERS_STATUS.provide().getBukkitApp(targetNick);

        if (app == null || app.getServer() != FactionsProvider.getServer() || !targetUser.getHandle().isLogged()) {
            Message.ERROR.send(player, "O jogador " + targetNick + " não está online.");
            return;
        }

        FactionUserRelation targetRelation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(targetUser.getId());

        if (targetRelation != null) {
            Message.ERROR.send(player, "O jogador " + targetNick + " já está em uma facção.");
            return;
        }

        if (FactionsProvider.Cache.Redis.FACTION_INVITATIONS.provide().hasInvite(targetUser, relation.getFaction())) {
            Message.ERROR.send(player, "O jogador " + targetNick + " já foi convidado para sua facção.");
            return;
        }

        FactionsProvider.Cache.Redis.FACTION_INVITATIONS.provide().putInvitation(targetUser, relation.getFaction());

        CoreProvider.Redis.ECHO.provide().publish(new UserInvitedFactionPacket(
                relation.getFaction(), targetUser.getId(), senderUser.getId()
        ));

        Message.SUCCESS.send(player, "Você convidou o jogador " + targetNick + " para sua facção.");

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return super.tabComplete0(sender, alias, args);
    }
}
