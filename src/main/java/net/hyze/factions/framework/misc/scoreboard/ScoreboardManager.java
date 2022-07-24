package net.hyze.factions.framework.misc.scoreboard;

import lombok.NonNull;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.npc.CustomNPC;
import net.hyze.core.spigot.misc.scoreboard.ScoreboardUtils;
import net.hyze.core.spigot.misc.scoreboard.bukkit.GroupScoreboard;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyAPI;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.Land;
import net.hyze.factions.framework.lands.LandState;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.misc.utils.LandChatHelper;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.ranking.FactionRankIcon;
import net.hyze.factions.framework.ranking.Ranking;
import net.hyze.factions.framework.ranking.RankingFactory;
import net.hyze.factions.framework.ranking.factions.ValuationRanking;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ScoreboardManager implements Listener {

    private static final int ROWS = 13;

    public static void setup(@NonNull FactionUser user) {

        if (!user.isOnline()) {
            return;
        }

        if (user.getBoard() == null) {
            FactionsScoreboard board = new FactionsScoreboard(user);

            board.registerTeams();
            user.getPlayer().setScoreboard(board.getScoreboard());
            user.setBoard(board);
            board.updateHealth(true);
        }

        FactionsScoreboard board = (FactionsScoreboard) user.getBoard();

        board.setTitle("&b" + CoreProvider.getApp().getServer().getDisplayName());

        for (int i = 1; i <= ROWS; i++) {
            board.set(i, new String(new char[i]).replace(Character.MIN_VALUE, ' '));
        }
        board.set(0, String.format("&a%s  ", CoreConstants.Infos.SITE_DOMAIN));

        CustomNPC.INSTANCES.forEach(npc -> board.registerNPC(npc.getName()));

        update(Collections.singleton(user), Slot.values());

        if (!FactionsProvider.getSettings().isGlobalTablistEnabled()) {

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (!target.isOnline()) {
                    continue;
                }

                FactionUser targetUser = FactionsProvider.Cache.Local.USERS.provide().get(target.getName());

                if (targetUser.getBoard() == null) {
                    continue;
                }

                board.registerUser(targetUser.getHandle());

                ((FactionsScoreboard) targetUser.getBoard()).registerUser(user.getHandle());
            }
        }

        int delay = 20 * 5;

        WeakReference<FactionUser> userWeakReference = new WeakReference<>(user);

        new BukkitRunnable() {
            @Override
            public void run() {
                FactionUser factionUser = userWeakReference.get();

                if (factionUser == null || !factionUser.isOnline()) {
                    cancel();
                    return;
                }

                FactionsScoreboard factionsScoreboard = (FactionsScoreboard) factionUser.getBoard();

                int factionsInfoIndex = factionsScoreboard.getCurrentFactionsInfoIndex();

                factionsInfoIndex++;

                if (factionsInfoIndex > 2) {
                    factionsInfoIndex = 0;
                }

                factionsScoreboard.setCurrentFactionsInfoIndex(factionsInfoIndex);

                updateFaction(factionUser);
            }
        }.runTaskTimer(FactionsPlugin.getInstance(), delay, delay);

        new BukkitRunnable() {
            @Override
            public void run() {
                FactionUser factionUser = userWeakReference.get();

                if (factionUser == null || !factionUser.isOnline()) {
                    cancel();
                    return;
                }

                updateSecondaryInfo(factionUser);
            }
        }.runTaskTimer(FactionsPlugin.getInstance(), 20, 20);
    }

    public static void update(FactionUser user, Slot... slots) {
        if (FactionsProvider.getSettings().getDefaultScoreboardDisabledAt().contains(CoreProvider.getApp().getType())) {
            return;
        }

        update(Collections.singleton(user), slots);
    }

    public static void update(@NonNull Set<FactionUser> users, Slot... slots) {
        if (FactionsProvider.getSettings().getDefaultScoreboardDisabledAt().contains(CoreProvider.getApp().getType())) {
            return;
        }

        for (FactionUser user : users) {
            if (!user.isOnline()) {
                continue;
            }

            if (user.getBoard() == null) {
                continue;
            }

            for (Slot slot : slots) {
                switch (slot) {
                    case TITLE:
                        ScoreboardManager.updateTitle(user);
                        break;
                    case FACTION:
                        ScoreboardManager.updateFaction(user);
                        break;
                    case POWER:
                        ScoreboardManager.updatePower(user);
                        break;
                    case CASH:
                        ScoreboardManager.updateCash(user);
                        break;
                    case COINS:
                        ScoreboardManager.updateCoins(user);
                        break;
                }
            }

            if (!Arrays.asList(slots).contains(Slot.FACTION)) {

                FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(user);

                if (relation != null) {
                    if (relation.getFaction().isUnderAttack() != ((FactionsScoreboard) user.getBoard()).isLastUnderAttack()) {
                        update(user, Slot.FACTION);
                    }
                }
            }
        }
    }

    private static void updateCoins(FactionUser user) {
        if (FactionsProvider.getSettings().getDefaultScoreboardDisabledAt().contains(CoreProvider.getApp().getType())) {
            return;
        }

        String coins = NumberUtils.toK(EconomyAPI.get(user.getHandle(), Currency.COINS));

        user.getBoard().set(11, String.format(" &fMoedas: &a%s", coins));
    }

    private static void updateCash(FactionUser user) {
        if (FactionsProvider.getSettings().getDefaultScoreboardDisabledAt().contains(CoreProvider.getApp().getType())) {
            return;
        }

        user.getBoard().set(10, String.format(" &fCubos: &a%s", NumberUtils.format(user.getHandle().getCash())));
    }

    private static void updatePower(FactionUser user) {
        if (FactionsProvider.getSettings().getDefaultScoreboardDisabledAt().contains(CoreProvider.getApp().getType())) {
            return;
        }

        String line = String.format(
                " &fPoder: &a%s/%s",
                user.getStats().getPower(),
                user.getStats().getAdditionalMaxPower() + FactionsProvider.getSettings().getMaxPower()
        );

        user.getBoard().set(12, line);
    }

    private static void updateTitle(@NonNull FactionUser user) {
        if (FactionsProvider.getSettings().getDefaultScoreboardDisabledAt().contains(CoreProvider.getApp().getType())) {
            return;
        }

        if (AppType.FACTIONS_MINE.isCurrent()) {
            user.getBoard().setTitle(MessageUtils.translateColorCodes(
                    "&8Mundo de Mineração"
            ));
        } else {
            Land land = LandUtils.getZone(user.getPlayer().getLocation());

            if (land == null) {
                land = LandUtils.getClaim(user.getPlayer().getLocation());
            }

            String newTitle = MessageUtils.translateColorCodes(LandState.getTitle(user, land));
            user.getBoard().setTitle(newTitle);
        }

    }

    private static void updateFaction(@NonNull FactionUser user) {
        if (FactionsProvider.getSettings().getDefaultScoreboardDisabledAt().contains(CoreProvider.getApp().getType())) {
            return;
        }

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(user);

//        updateTeam(user);
        if (relation != null) {

            String factionNameLine = "";

            ((FactionsScoreboard) user.getBoard()).setLastUnderAttack(relation.getFaction().isUnderAttack());

            if (relation.getFaction().isUnderAttack()) {
                factionNameLine += String.format("%s%s\u26A0%s", ChatColor.DARK_RED, ChatColor.BOLD, ChatColor.RESET);
            }

            factionNameLine += " &e" + relation.getFaction().getStrippedDisplayName();

            user.getBoard().set(8, factionNameLine);

            switch (((FactionsScoreboard) user.getBoard()).getCurrentFactionsInfoIndex()) {
                case 0:
                    Set<FactionUser> relations = FactionUtils.getUsers(relation.getFaction());
                    Set<FactionUser> loggedRelations = FactionUtils.getUsers(relation.getFaction(), true);

                    String onlineLine = String.format("  &fOnline: &e%s/%s", loggedRelations.size(), relations.size());

                    user.getBoard().set(7, onlineLine);
                    break;
                case 1:
                    String powerLine = String.format("  &fPoder: &e%s/%s",
                            FactionUtils.getPower(relation.getFaction()),
                            FactionUtils.getMaxPower(relation.getFaction())
                    );
                    user.getBoard().set(7, powerLine);

                    break;
                case 2:
                    Set<Claim> permanentClaims = LandUtils.getPermanentClaims(relation.getFaction());

                    String claimsLine = String.format("  &fTerras: &e%s", permanentClaims.size());

                    user.getBoard().set(7, claimsLine);
                    break;
            }
        } else {
            user.getBoard().set(8, " &7Sem facção");

            user.getBoard().reset(7);
        }
    }

    private static void updateSecondaryInfo(@NonNull FactionUser user) {

        ScoreboardSecondaryInfoUpdateEvent event = new ScoreboardSecondaryInfoUpdateEvent(user, (FactionsScoreboard) user.getBoard());

        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (ScoreboardUtils.updateCooldowns(user.getHandle(), user, 5, 3, 4)) {
            user.getBoard().reset(2);
        } else {

            Ranking ranking = RankingFactory.FACTIONS_VALUATION_RANKING.getRanking();
            user.getBoard().set(5, " &aLiga de Valores:");
            List<FactionRankIcon<ValuationRanking.RankValue>> rankItems = ranking.getItems();

            int score = 4;
            for (int i = 0; i < 3; i++) {
                if (i >= rankItems.size()) {
                    user.getBoard().set(score--, String.format(
                            "  %sº &7------",
                            i + 1
                    ));
                    continue;
                }

                FactionRankIcon<ValuationRanking.RankValue> item = rankItems.get(i);

                user.getBoard().set(score--, String.format(
                        "  &f%sº %s",
                        i + 1,
                        item.getFaction().getDisplayName()
                ));
            }
        }
    }

    //
    // Momentos em que o scoreboard só precisa ser atualizado para um jogar
    // especifico. Caso o scoreboard precise ser atualizado para varios
    // jogadores, a função de update deve ser chamada no listener especifico do
    // evento ou pacote
    //
    //
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerMoveEvent event) {

        if (FactionsProvider.getSettings().getDefaultScoreboardDisabledAt().contains(CoreProvider.getApp().getType())) {
            return;
        }

        Location to = event.getTo();
        Location from = event.getFrom();

        if (LocationUtils.compareLocation(to, from)) {
            return;
        }

        Player player = event.getPlayer();
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

        if (user.getOptions().isAutoMapEnabled() && !LocationUtils.isSameChunk(from, to)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(LandChatHelper.drawChatMap(user));
                }
            }.runTaskAsynchronously(FactionsPlugin.getInstance());
        }

       /*
       WorldCuboid spawnProtectedZoneCuboid = FactionsProvider.getSettings().getSpawnerProtectedZoneCuboid();

        if (AppType.FACTIONS_SPAWN.isCurrent()) {
            if (!spawnProtectedZoneCuboid.contains(from, true) && !spawnProtectedZoneCuboid.contains(to, true)) {
                return;
            }
        } else */
        if (LocationUtils.isSameChunk(from, to)) {
            return;
        }

        if (user.getBoard() != null) {
            Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
                String oldTitle = ((GroupScoreboard) user.getBoard()).getTitle();

                ScoreboardManager.update(Collections.singleton(user), Slot.TITLE);

                if (user.getBoard() instanceof GroupScoreboard) {
                    GroupScoreboard groupScoreboard = (GroupScoreboard) user.getBoard();

                    if (!oldTitle.equals(groupScoreboard.getTitle())) {
                        user.getActionBarNotification().sendNotification(
                                groupScoreboard.getTitle()
                        );
                    }
                }
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerTeleportEvent event) {
        if (CoreSpigotPlugin.getInstance().isNPC(event.getPlayer())) {
            return;
        }

        if (FactionsProvider.getSettings().getDefaultScoreboardDisabledAt().contains(CoreProvider.getApp().getType())) {
            return;
        }

        Player player = event.getPlayer();

        Location to = event.getTo();
        Location from = event.getFrom();

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

        if (user.getOptions().isAutoMapEnabled() && !LocationUtils.isSameChunk(to, from)) {
            player.sendMessage(LandChatHelper.drawChatMap(user));
        }

        if (user.getBoard() != null) {
            Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
                ScoreboardManager.update(Collections.singleton(user), Slot.TITLE);
            }, 1L);
        }
    }

    public enum Slot {
        TITLE, FACTION, POWER, COINS, CASH;
    }
}
