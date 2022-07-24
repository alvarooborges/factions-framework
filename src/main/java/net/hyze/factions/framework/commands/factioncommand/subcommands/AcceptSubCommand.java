package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.UserJoinedFactionPacket;
import net.hyze.factions.framework.echo.packets.permission.FactionUserPermissionUpdatedPacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Set;

public class AcceptSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.PLAYER_TO_FACTION;

    public AcceptSubCommand() {
        super("aceitar");

        registerArgument(new Argument("tag", "Tag da facção que você aceitará o convite.", false));
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        if (relation != null) {
            Message.ERROR.send(player, "Você já está em uma facção.");
            return;
        }

        if (args.length != 1) {

            PaginateInventory.PaginateInventoryBuilder inventory = PaginateInventory.builder();

            Set<Faction> invites = FactionsProvider.Cache.Redis.FACTION_INVITATIONS.provide().getInvitations(user);

            invites.stream().forEach(faction -> {
                inventory.item(
                        FactionUtils.getBanner(faction, user).lore("", "&aClique para aceitar.").make(),
                        event -> {
                            player.performCommand("f aceitar " + faction.getTag());
                            player.closeInventory();
                        }
                );
            });

            player.openInventory(inventory.build("Convites"));
            return;
        }

        String tag = args[0];

        Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(tag);

        if (faction == null) {
            Message.ERROR.send(player, "Esta facção não existe.");
            return;
        }

        if (!FactionsProvider.Cache.Redis.FACTION_INVITATIONS.provide().hasInvite(user, faction)) {
            Message.ERROR.send(player, "Você não não tem um convite para esta facção.");
            return;
        }

        Set<FactionUser> allUsers = FactionUtils.getUsers(faction);

        if (allUsers.size() >= FactionsProvider.getSettings().getFactionMaxMembers()) {
            Message.ERROR.send(player, String.format(
                    "A fação %s já atingiu o número máximo de %s jogadores.",
                    faction.getDisplayName(),
                    FactionsProvider.getSettings().getFactionMaxMembers()
            ));
            return;
        }

        relation = new FactionUserRelation(user.getId(), faction, FactionRole.RECRUIT, new Date());

        FactionsProvider.Repositories.USERS_RELATIONS.provide().update(relation);

        // Limpando possiveis permissoes
        FactionsProvider.Repositories.FACTIONS_PERMISSIONS.provide().updateByUser(faction, user.getHandle(), null);
        CoreProvider.Redis.ECHO.provide().publish(new FactionUserPermissionUpdatedPacket(faction, user.getId(), -1));

        FactionsProvider.Cache.Redis.FACTION_INVITATIONS.provide().clearInvitations(user);

        CoreProvider.Redis.ECHO.provide().publish(new UserJoinedFactionPacket(
                faction, user.getId(), UserJoinedFactionPacket.Reason.INVITATION
        ));

        Message.SUCCESS.send(player, String.format("Você aceitou o convite para facção [%s] %s.", faction.getTag(), faction.getName()));
    }
}
