package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class BaseSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public BaseSubCommand() {
        super("base", FactionRole.RECRUIT);

        registerArgument(new Argument("nick", "Nick do jogador que será convidado", false));
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        Faction target;

        if (args.length > 0) {
            target = FactionsProvider.Cache.Local.FACTIONS.provide().get(args[0]);

            if (target == null) {
                Message.ERROR.send(player, String.format("A facção &7%s &cnão existe.", args[0].toUpperCase()));
                return;
            }
        } else {
            target = relation.getFaction();
        }

        if (!FactionPermission.COMMAND_BASE.allows(target, user)) {
            if (relation.getFaction().equals(target)) {
                Message.ERROR.send(player, "Você não tem permissão de ir até a base da sua facção usando o comando \"/f base\".");
            } else {
                Message.ERROR.send(player, String.format(
                        "Você não tem permissão para ir até a base da facção %s&c.",
                        target.getDisplayName()
                ));
            }

            return;
        }

        if (target.getHome() == null) {
            if (relation.getFaction().equals(target)) {
                Message.ERROR.send(player, "Sua facção não possui um base definida.");
            } else {
                Message.ERROR.send(player, String.format(
                        "A facção %s&c não possui uma base definida.",
                        target.getDisplayName()
                ));
            }

            return;
        }

        TeleportManager.teleport(
                user.getHandle(),
                target.getHome(),
                ConnectReason.HOME,
                String.format("&aTeleportado para a base da facção %s.", target.getDisplayName())
        );
    }
}
