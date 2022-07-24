package net.hyze.factions.framework.commands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.UserSpokePacket;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.chat.ChatManager;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

public class FactionChatCommand extends CustomCommand {

    public FactionChatCommand() {
        super("c", CommandRestriction.IN_GAME, ".");
    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(handle.getId());

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUserId(user.getId());

        if (relation == null) {
            Message.ERROR.send(sender, "OPS! Você não está em uma facção.");
            return;
        }

        if (args.length < 1) {
            Message.ERROR.send(sender, "&cVocê não digitou nada!");
            return;
        }

        String message = String.join(" ", args);

        BaseComponent[] components = ChatManager.buildFactionChatMessage(user, message);

        if (components != null) {
            CoreProvider.Redis.ECHO.provide().publish(new UserSpokePacket(
                    user.getId(), UserSpokePacket.Chat.FACTION, message, components
            ));
        }
    }
}
