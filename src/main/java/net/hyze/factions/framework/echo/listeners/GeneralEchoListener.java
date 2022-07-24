package net.hyze.factions.framework.echo.listeners;

import dev.utils.echo.IEchoListener;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.packets.server.UserServerJoinPacket;
import net.hyze.core.shared.echo.packets.server.UserServerQuitPacket;
import net.hyze.core.shared.echo.packets.tablist.RequestTabListInfoPacket;
import net.hyze.core.shared.echo.packets.tablist.TabListInfoPacket;
import net.hyze.core.shared.group.due.GroupDue;
import net.hyze.core.shared.misc.utils.Plural;
import net.hyze.core.shared.user.User;
import net.hyze.economy.echo.packets.ChangeCurrencyPacket;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.GlobalChatCommand;
import net.hyze.factions.framework.echo.packets.*;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import net.hyze.factions.framework.misc.power.PowerManager;
import net.hyze.factions.framework.misc.scoreboard.ScoreboardManager;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.war.War;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.greenrobot.eventbus.Subscribe;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GeneralEchoListener implements IEchoListener {

    @Subscribe
    public void on(PowerSchedulerUpdatedPacket packet) {
        PowerManager.update(packet.getUserId(), packet.getTime());
    }

    @Subscribe
    public void on(GlobalChatTogglePacket packet) {
        GlobalChatCommand.STATUS = packet.getStatus();
    }

    @Subscribe
    public void on(WarTogglePacket packet) {
        War.OPEN = packet.getStatus();
        System.out.println("Recebeu o pacote da guerra.");
    }

    @Subscribe
    public void on(WarTestTogglePacket packet) {
        War.TEST = packet.getStatus();
        System.out.println("Recebeu o pacote da guerra test.");
    }

    @Subscribe
    public void on(ExplostionsEnabledTogglePacket packet) {
        FactionsProvider.getSettings().setExplosionsEnabled(packet.getStatus());
    }

    @Subscribe
    public void on(UserServerJoinPacket packet) {
      //  Debug debug = new Debug("UserServerJoinPacket", 2);

        if (!Objects.equals(packet.getServer(), FactionsProvider.getServer())) {
            return;
        }

        //  debug.anchor();

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(packet.getUserId());

        // debug.anchor();

        if (relation != null) {
            Set<FactionUser> members = FactionUtils.getUsers(relation.getFaction(), true);

            //   debug.anchor();

            ScoreboardManager.update(members, ScoreboardManager.Slot.FACTION);

            //   debug.anchor();

            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(packet.getUserId());

            ComponentBuilder builder = new ComponentBuilder("");

            builder.append(
                    String.format(
                            "[%s%s] ",
                            relation.getRole().getSymbol(),
                            relation.getFaction().getTag()
                    )
            )
                    .color(ChatColor.GRAY)
                    .append(user.getHandle().getHighestGroup().getDisplayTag(user.getNick()))
                    .append(" entrou.", ComponentBuilder.FormatRetention.NONE)
                    .color(ChatColor.YELLOW);

            FactionUtils.broadcast(relation.getFaction(), builder.create(), true);
        }

        //   debug.anchor();

        final User user = CoreProvider.Cache.Local.USERS.provide().getIfPresent(packet.getUserId());

        if (user == null) {
            return;
        }

        //   debug.anchor();

        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayerExact(user.getNick());

                if (player != null && player.isOnline()) {
                    List<GroupDue> groupDues = CoreProvider.Repositories.GROUPS_DUE.provide()
                            .fetchAll(user);

                    if (!groupDues.isEmpty()) {
                        ComponentBuilder builder = new ComponentBuilder("\n")
                                .color(ChatColor.YELLOW)
                                .append(" Seu tempo de vip:")
                                .bold(true)
                                .append("\n");

                        groupDues.forEach(groupDue -> {
                            long diff = groupDue.getDueAt().getTime() - new Date().getTime();

                            builder.append("  * ")
                                    .bold(false)
                                    .color(ChatColor.YELLOW)
                                    .append(groupDue.getGroup().getDisplayName())
                                    .color(groupDue.getGroup().getColor())
                                    .append(" ")
                                    .color(ChatColor.YELLOW);

                            long value = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                            if (value > 0) {
                                builder.append(value + " " + Plural.DAY.of(value) + " ");
                            } else {
                                value = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
                                builder.append(value + " " + Plural.HOUR.of(value) + " ");
                            }

                            builder.append(Plural.of(value, "restante", "restantes") + ".")
                                    .color(ChatColor.YELLOW)
                                    .append("\n");
                        });

                        player.sendMessage(builder.create());
                    }
                }
            }
        }.runTaskLaterAsynchronously(FactionsPlugin.getInstance(), 10);


        // debug.done();
    }

    @Subscribe
    public void on(RequestTabListInfoPacket packet) {
        User user = CoreProvider.Cache.Local.USERS.provide().getIfPresent(packet.getUserId());

        String[][] data = new String[3][20];

        data[0][1] = "Membros da Facção";
        data[1][1] = "Aliados";
        data[2][1] = "Status";


        System.out.println("Send TabListInfoPacket");
        System.out.println(Arrays.deepToString(data));
        CoreProvider.Redis.ECHO.provide().publish(new TabListInfoPacket(user.getId(), data));
    }

    @Subscribe
    public void on(UserServerQuitPacket packet) {
        if (!Objects.equals(packet.getServer(), FactionsProvider.getServer())) {
            return;
        }

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(packet.getUserId());

        if (relation != null) {
            Set<FactionUser> members = FactionUtils.getUsers(relation.getFaction(), true);
            ScoreboardManager.update(members, ScoreboardManager.Slot.FACTION);

            ComponentBuilder builder = new ComponentBuilder("");

            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(packet.getUserId());

            builder.append(
                    String.format(
                            "[%s%s] ",
                            relation.getRole().getSymbol(),
                            relation.getFaction().getTag()
                    )
            )
                    .color(ChatColor.GRAY)
                    .append(user.getHandle().getHighestGroup().getDisplayTag(user.getNick()))
                    .append(" saiu.", ComponentBuilder.FormatRetention.NONE)
                    .color(ChatColor.YELLOW);

            FactionUtils.broadcast(relation.getFaction(), builder.create(), true);
        }
    }

    @Subscribe
    public void on(ChangeCurrencyPacket packet) {
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(packet.getUserId());

        switch (packet.getCurrency()) {
            case CASH:
                ScoreboardManager.update(user, ScoreboardManager.Slot.CASH);
                break;
            case COINS:
                ScoreboardManager.update(user, ScoreboardManager.Slot.COINS);
                break;
        }
    }

    @Subscribe
    public void on(WorldTimePacket packet) {
        if (FactionsProvider.getSettings().isSyncWorldTime()) {
            Bukkit.getWorld("world").setTime(packet.getTime());
        }
    }

    @Subscribe
    public void on(LostFortressTogglePacket packet) {
        LostFortressConstants.STATUS = packet.getStatus();
    }
}
