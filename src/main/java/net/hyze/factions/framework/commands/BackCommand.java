package net.hyze.factions.framework.commands;

import lombok.Getter;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

public class BackCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group;

    public BackCommand(Group group) {
        super("back", CommandRestriction.IN_GAME);

        this.group = group;
    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(handle);

        SerializedLocation backLocation = user.getStats().getBackLocation();

        if (backLocation == null) {
            Message.ERROR.send(sender, "&cVocê não possui uma localização para voltar.");
            return;
        }

        BaseComponent[] baseComponents = new ComponentBuilder("Teleportado com sucesso.")
                .color(ChatColor.GREEN)
                .create();

        TeleportManager.teleport(handle, backLocation, ConnectReason.HOME, baseComponents);
    }
}
