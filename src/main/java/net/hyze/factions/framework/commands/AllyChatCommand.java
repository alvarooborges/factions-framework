package net.hyze.factions.framework.commands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.UserSpokePacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.chat.ChatManager;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class AllyChatCommand extends CustomCommand {

    public AllyChatCommand() {
        super("a", CommandRestriction.IN_GAME);
        registerArgument(new Argument("mensagem", "Mensagem que será enviada para todos os aliados", true));
    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(handle.getId());

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

        if (relation == null) {
            Message.ERROR.send(sender, "&cVocê precisa ter uma facção para utilizar o chat de aliados.");
            return;
        }

        Set<Faction> allies = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(relation.getFaction(), FactionRelation.Type.ALLY);

        if (allies.isEmpty()) {
            Message.ERROR.send(sender, "&cSua facção precisa ter aliados para utilizar o chat de aliados.");
            return;
        }

        String message = MessageUtils.stripColor(MessageUtils.translateColorCodes(String.join(" ", args)));

        if (message.isEmpty()) {
            Message.ERROR.send(sender, "&cVocê não digitou nada!");
            return;
        }

        BaseComponent[] components = ChatManager.buildAllianceChatMessage(user, message);

        if (components != null) {
            CoreProvider.Redis.ECHO.provide().publish(new UserSpokePacket(
                    user.getId(), UserSpokePacket.Chat.ALLIANCE, message, components
            ));
        }
    }
}
