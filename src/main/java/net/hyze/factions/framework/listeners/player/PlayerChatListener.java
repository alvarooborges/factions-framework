package net.hyze.factions.framework.listeners.player;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.TextUtil;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.events.chat.PlayerGlobalChatEvent;
import net.hyze.core.spigot.events.chat.PlayerLocalChatEvent;
import net.hyze.core.spigot.events.chat.PlayerTellChatEvent;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.net.URI;
import java.util.List;

public class PlayerChatListener implements Listener {

    private boolean canSendUrl(User user, String text) {

        if (user.hasGroup(Group.ADMINISTRATOR)) {
            return true;
        }

        List<String> urls = TextUtil.extractUrls(text);

        if (!urls.isEmpty()) {
            for (String str : urls) {
                try {
                    URI uri = new URI(str);
                    String domain = uri.getHost();

                    domain = domain.startsWith("www.") ? domain.substring(4) : domain;

                    if (!domain.startsWith(CoreConstants.Infos.SITE_DOMAIN)
                            && !domain.startsWith(CoreConstants.Infos.STORE_DOMAIN)
                            && !domain.startsWith(CoreConstants.Infos.SHORT_DOMAIN)) {

                        return false;
                    }

                } catch (Exception ignored) {

                }
            }
        }

        return true;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(PlayerGlobalChatEvent event) {

        if (!canSendUrl(event.getUser(), event.getMessage())) {
            Message.ERROR.send(event.getPlayer(), "Não é permitido enviar links nos chats.");
            event.setCancelled(true);
        }

        if (!event.getUser().hasGroup(Group.ARCANE) && EconomyAPI.get(event.getUser(), Currency.COINS) < 500) {
            Message.EMPTY.send(event.getPlayer(), "");
            Message.EMPTY.send(event.getPlayer(), "&cVocê precisa ter pelo menos 500 moedas para usar o /g.");
            Message.EMPTY.send(event.getPlayer(), "&cVá até o '/mina' para conseguir moedas.");
            Message.EMPTY.send(event.getPlayer(), "");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(PlayerLocalChatEvent event) {

        if (!canSendUrl(event.getUser(), event.getMessage())) {
            Message.ERROR.send(event.getPlayer(), "Não é permitido enviar links nos chats.");
            event.setCancelled(true);
        }

        if (!event.getUser().hasGroup(Group.ARCANE) && EconomyAPI.get(event.getUser(), Currency.COINS) < 500) {
            Message.EMPTY.send(event.getPlayer(), "");
            Message.EMPTY.send(event.getPlayer(), "&cVocê precisa ter pelo menos 500 moedas para falar no chat local.");
            Message.EMPTY.send(event.getPlayer(), "&cVá até o '/mina' para conseguir moedas.");
            Message.EMPTY.send(event.getPlayer(), "");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(PlayerTellChatEvent event) {

        if (!canSendUrl(event.getUser(), event.getMessage())) {
            Message.ERROR.send(event.getPlayer(), "Não é permitido enviar links nos chats.");
            event.setCancelled(true);
        }

        if (!event.getUser().hasGroup(Group.ARCANE) && EconomyAPI.get(event.getUser(), Currency.COINS) < 500) {
            Message.EMPTY.send(event.getPlayer(), "");
            Message.EMPTY.send(event.getPlayer(), "&cVocê precisa ter pelo menos 500 moedas para usar o /tell.");
            Message.EMPTY.send(event.getPlayer(), "&cVá até o '/mina' para conseguir moedas.");
            Message.EMPTY.send(event.getPlayer(), "");
            event.setCancelled(true);
        }
    }
}
