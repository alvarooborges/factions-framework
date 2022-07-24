package net.hyze.factions.framework.misc.utils;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

@NoArgsConstructor
public class FactionUserUtils {

    public static FactionUserRelation getRelation(FactionUser user) {
        return FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);
    }

    public static FactionUser getUser(String nick) {
        return FactionsProvider.Cache.Local.USERS.provide().get(nick);
    }

    public static FactionUser getUser(User user) {
        return FactionsProvider.Cache.Local.USERS.provide().get(user);
    }

    public static FactionUser getUser(Player player) {
        return FactionsProvider.Cache.Local.USERS.provide().get(player);
    }

    public static String[] getDescription(@NonNull FactionUser user) {
        Group group = user.getHandle().getHighestGroup();

        return new String[]{
            MessageUtils.translateFormat(
            "&fGrupo: &7%s",
            group.getTag()
            ),
            MessageUtils.translateFormat(
            "&fPoder: &7%s/%s",
            user.getStats().getPower(),
            user.getStats().getTotalMaxPower()
            ),
            MessageUtils.translateFormat(
            "&fKDR: &7%s",
            new DecimalFormat("0.00").format(user.getStats().getKDR())
            ),
            MessageUtils.translateFormat(
            "&fAbates: &7%s",
            user.getStats().getTotalKills()
            ),
            MessageUtils.translateFormat(
            "&fMortes: &7%s",
            user.getStats().getTotalDeaths()
            )

        };
    }

    public static BaseComponent[] getChatComponents(FactionUser user) {
        return getChatComponents(user, false);
    }

    public static BaseComponent[] getChatComponents(FactionUser user, boolean groupPrefix) {
        ComponentBuilder builder = new ComponentBuilder("");

        if (groupPrefix) {
            builder.append(user.getHandle().getHighestGroup().getDisplayTag(user.getNick()));
        } else {
            builder.color(user.getHandle().getHighestGroup().getColor())
                    .append(user.getNick());
        }

        ComponentBuilder hoverBuilder = new ComponentBuilder("");

        String[] infos = getDescription(user);

        hoverBuilder.append(user.getHandle().getHighestGroup().getDisplayTag(user.getNick()))
                .append("\n");

        for (int i = 0; i < infos.length; i++) {
            hoverBuilder.append(infos[i]);

            if (i < infos.length - 1) {
                hoverBuilder.append("\n");
            }
        }

        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.create()));

        return builder.create();
    }

}
