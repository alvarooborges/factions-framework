package net.hyze.factions.framework.commands.factioncommand.subcommands.adminsubcommand.data;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.FactionDisbandPacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class AdminDisbandSubCommand extends FactionSubCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group = Group.GAME_MASTER;

    public AdminDisbandSubCommand() {
        super("disband");

        registerArgument(new Argument("tag", "Tag da facção que será desfeita", true));
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

        ConfirmInventory confirmInventory = ConfirmInventory.of(event -> {

            Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(tag);

            if (faction == null) {
                Message.ERROR.send(sender, "Esta facção não existe.");
                return;
            }

            Map<SpawnerType, Integer> spawners = FactionsProvider.Repositories.SPAWNERS.provide().countCollected(faction);

            if (!spawners.isEmpty()) {
                Message.ERROR.send(sender, "Você não pode desfazer uma facção enquanto ela tiver geradores guardados no '/f geradores'!");
                player.closeInventory();
                return;
            }

            boolean result = FactionsProvider.Repositories.FACTIONS.provide().delete(faction.getTag());

            if (!result) {
                Message.ERROR.send(player, "Algo de errado aconteceu, tente novamente.");
                player.closeInventory();
                return;
            }

            CoreProvider.Redis.ECHO.provide().publish(new FactionDisbandPacket(faction));

            Message.SUCCESS.send(player, "Você excluiu a facção " + faction.getDisplayName());
        }, event -> {
            Message.ERROR.send(player, "Você cancelou a ação de desfazer facção " + checker.getTag());
        }, null);

        player.openInventory(confirmInventory.make("Excluir " + checker.getTag()));
    }
}
