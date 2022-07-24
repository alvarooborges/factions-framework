package net.hyze.factions.framework.war;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.misc.cooldowns.Cooldowns;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.scoreboard.FactionsScoreboard;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.war.clock.phases.EnumWarPhase;
import org.bukkit.Bukkit;

public class WarScoreboardManager {

    private static final int ROWS = 6;

    public static void setup(FactionUser user) {
        if (user.getPlayer() == null || !user.getPlayer().isOnline()) {
            return;
        }

        WarFactionsScoreboard board = new WarFactionsScoreboard(user);
        board.registerTeams();

        user.getPlayer().setScoreboard(board.getScoreboard());
        user.setBoard(board);

        board.setTitle("&6&lGUERRA");

        for (int i = 1; i <= ROWS; i++) {
            board.set(i, new String(new char[i]).replace(Character.MIN_VALUE, ' '));
        }

        update(user);

        Bukkit.getOnlinePlayers().forEach(target -> {
            FactionUser targetUser = FactionsProvider.Cache.Local.USERS.provide().get(target.getName());

            if (targetUser.getBoard() == null) {
                return;
            }

            board.registerUser(targetUser.getHandle());

            ((FactionsScoreboard) targetUser.getBoard()).registerUser(user.getHandle());
        });

        board.set(0, String.format("&b%s  ", CoreConstants.Infos.STORE_DOMAIN));
    }

    public static void update(FactionUser user) {
        if (user.getBoard() == null) {
            return;
        }

        EnumWarPhase phase = War.CLOCK.getCurrentEnumWarPhase();

        String time = "...";

        if (War.CLOCK.getCurrentWarClockRunnable() != null) {
            time = Cooldowns.getFormattedTimeLeft(((long) War.CLOCK.getCurrentWarClockRunnable().getCount().get()) * 1000L);
        }

        user.getBoard().set(6, "&d");

        user.getBoard().set(5, String.format(" &fFase: &a%s", phase == null ? "Aguardando..." : phase.getWarPhase().getDisplayName()));
        user.getBoard().set(4, String.format(" &fTempo: &a%s", phase == null ? "..." : time));

        user.getBoard().set(3, "&f");

        if (user.getRelation() == null) {
            user.getBoard().set(2, " &cSem facção");
//            user.getBoard().set(3, "   &7...");
//            user.getBoard().set(2, "   &7...");
            return;
        }

        Faction faction = user.getRelation().getFaction();

        user.getBoard().set(2, String.format(" &e%s", faction.getDisplayName()));
//        user.getBoard().set(3, String.format("   &fOnline: &e%d", 16));
//        user.getBoard().set(2, String.format("   &fAbates: &e%d", 0));

    }

    public static void update() {
        Bukkit.getOnlinePlayers().forEach(player -> WarScoreboardManager.update(FactionsProvider.Cache.Local.USERS.provide().get(player)));
    }

}
