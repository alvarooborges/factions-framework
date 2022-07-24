package net.hyze.factions.framework.commands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.events.chat.PlayerGlobalChatEvent;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.UserSpokePacket;
import net.hyze.factions.framework.misc.chat.ChatManager;
import net.hyze.factions.framework.settings.SettingsManager;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalChatCommand extends CustomCommand {

    public static boolean STATUS = true;

    public GlobalChatCommand() {
        super("g", CommandRestriction.IN_GAME);
        registerArgument(new Argument("mensagem", "Mensagem que será enviada para todos do servidor", true));
    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {

        PreferenceStatus status = CoreProvider.Cache.Local.USERS_PREFERENCES.provide()
                .get(handle)
                .getPreference(FactionsConstants.UserPreference.CHAT_GLOBAL);

        if (status.is(PreferenceStatus.OFF)) {
            Message.ERROR.send(sender, "Ops, você está com o chat global desabilitado. Utilize &f/toggle &cpara habilita-lo novamente.");
            return;
        }

        if (!handle.hasGroup(Group.ADMINISTRATOR) && !STATUS) {
            Message.ERROR.send(sender, "O chat global está temporáriamente desabilitado.");
            return;
        }

        if (handle.hasGroup(Group.GAME_MASTER) && args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            STATUS = !STATUS;
            Message.INFO.send(sender, "Global status: " + STATUS);
            SettingsManager.setGlobalStatus(STATUS);
            return;
        }

        String message = String.join(" ", args);

        String messageLow = message.toLowerCase();

        /*
        if ((messageLow.contains("kadoo")
                || messageLow.contains("kado")
                || messageLow.contains("master")
                || messageLow.contains("kaddo")
                || messageLow.contains("nathan")
                || messageLow.contains("nathanpr0"))
                && messageLow.contains("tp")) {
            Message.SUCCESS.send(sender, String.format("\n Precisando de ajuda?\n A maneira mais rápida de resolver qualquer problema é atráves de nosso fórum!\n Acesse agora: &f%s \n ", CoreConstants.Infos.FORUM_DOMAIN));
            return;
        }
         */

        PlayerGlobalChatEvent event = new PlayerGlobalChatEvent((Player) sender, handle, message);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(handle.getId());

        BaseComponent[] components = ChatManager.buildGlobalChatMessage(user, event.getMessage());

        if (components != null) {
            CoreProvider.Redis.ECHO.provide().publish(new UserSpokePacket(
                    user.getId(), UserSpokePacket.Chat.GLOBAL, event.getMessage(), components
            ));
        }
    }
}
