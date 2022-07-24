package net.hyze.factions.framework.misc.lostfortress.commands;

import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.misc.utils.Position;
import net.hyze.core.shared.misc.utils.Vector3D;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import net.hyze.factions.framework.misc.lostfortress.commands.subcommands.*;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.command.CommandSender;

public class LostFortressCommand extends CustomCommand {

    public LostFortressCommand() {
        super("baseperdida", CommandRestriction.IN_GAME, "bp", "invasao");

        registerSubCommand(new FallDamageSubCommand());
        registerSubCommand(new UpdateSubCommand());
        registerSubCommand(new StartSubCommand());
        registerSubCommand(new HistorySubCommand());
        registerSubCommand(new TagItemsSubCommand());
        registerSubCommand(new ToggleSubCommand());
        registerSubCommand(new BroadcastSubCommand());
        registerSubCommand(new TipSubCommand());
        registerSubCommand(new TestSubCommand());
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        if (!LostFortressConstants.STATUS) {
            Message.ERROR.send(sender, "Ops, aparentemente a Base Perdida continua perdida...");
            return;
        }

        int random = RandomUtils.nextInt(LostFortressConstants.SPAWN_POINTS.size());

        Vector3D vector = LostFortressConstants.SPAWN_POINTS.get(random);

        TeleportManager.teleport(
                user,
                AppType.FACTIONS_LOSTFORTRESS,
                ConnectReason.WARP,
                new Position(vector.getX(), vector.getY(), vector.getZ())
        );
    }

}
