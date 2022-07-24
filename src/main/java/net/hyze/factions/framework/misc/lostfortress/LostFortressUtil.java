package net.hyze.factions.framework.misc.lostfortress;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.lostfortress.inventories.LostFortressInventory;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class LostFortressUtil {

    /**
     * Caso o jogador não esteja dentro da base perdida, o comando fica full
     * banco, por isso o cooldown de utilização.
     *
     * @param user
     * @param player
     */
    public static void openInventory(User user, Player player) {

        if (!(AppType.FACTIONS_LOSTFORTRESS.isCurrent() || AppType.FACTIONS_TESTS.isCurrent())) {

            if (LostFortressConstants.CURRENT == null) {
                Message.ERROR.send(player, "Ops, o evento não está ocorrendo.");
                return;
            }

            player.openInventory(
                    new LostFortressInventory(LostFortressConstants.CURRENT, null)
            );
            return;
        }

        if (!UserCooldowns.hasEnded(user, "lost_fortress_command")) {
            Message.ERROR.send(player, "Aguarde para digitar este comando novamente.");
            return;
        }

        Pair<Integer, String> pair = FactionsProvider.Repositories.LOST_FORTRESS.provide().fetch();

        if (pair == null) {
            Message.ERROR.send(player, "Ops, o evento não está ocorrendo.");
            return;
        }

        UserCooldowns.start(user, "lost_fortress_command", 10, TimeUnit.SECONDS);

        LostFortress log = CoreConstants.GSON.fromJson(pair.getRight(), LostFortress.class);

        player.openInventory(
                new LostFortressInventory(log, null)
        );

    }

}
