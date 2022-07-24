package net.hyze.factions.framework.user;

import lombok.*;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.cooldowns.Cooldowns;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.Credentialable;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.notification.actionbar.ActionBarNotification;
import net.hyze.core.spigot.misc.scoreboard.IBoard;
import net.hyze.core.spigot.misc.scoreboard.IBoardable;
import net.hyze.core.spigot.misc.utils.Title;
import net.hyze.economy.Currency;
import net.hyze.economy.echo.packets.ChangeCurrencyPacket;
import net.hyze.factions.framework.CooldownConstants;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.options.UserOptions;
import net.hyze.factions.framework.user.stats.UserStats;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

@Getter
@ToString(exclude = {"invites", "board"})
@EqualsAndHashCode(of = "handle")
public class FactionUser implements Credentialable, IBoardable {

    @NonNull
    private final User handle;

    @NonNull
    private final UserStats stats;

    @NonNull
    private final UserOptions options;

    @Setter
    private IBoard board;

    private Map<Integer, Long> invites = new HashMap<>();

    @Setter
    private boolean seeingChunks = false;

    @Setter
    private ActionBarNotification actionBarNotification;

    private BukkitTask flyingTask;
    private Integer flyingTaskCountdown;

    private WeakReference<Player> playerWeakReference;

    public FactionUser(@NonNull User handle, @NonNull UserStats stats, @NonNull UserOptions options) {
        this.handle = handle;
        this.stats = stats;
        this.options = options;

        this.handle.setOnCashUpdate(cash -> {
            CoreProvider.Redis.ECHO.provide().publish(new ChangeCurrencyPacket(
                    handle.getId(), Currency.CASH
            ));
        });
    }

    public BaseComponent[] getDisplayName(ChatColor tagColor, ChatColor nickColor) {
        FactionUserRelation relation = getRelation();

        ComponentBuilder builder = new ComponentBuilder("");

        if (relation != null) {
            builder.append("[")
                    .color(tagColor)
                    .append(relation.getFaction().getTag().toUpperCase())
                    .append("] ");
        }

        builder.append(getNick())
                .color(nickColor);

        return builder.create();
    }

    public Player getPlayer() {
        if (playerWeakReference == null) {
            playerWeakReference = new WeakReference<>(Bukkit.getPlayerExact(handle.getNick()));
        }

        return playerWeakReference.get();
    }

    public boolean isOnline() {
        Player player = getPlayer();

        return player != null && player.isOnline();
    }

    @Override
    public Integer getId() {
        return this.handle.getId();
    }

    @Override
    public String getNick() {
        return this.handle.getNick();
    }

    @Nullable
    public FactionUserRelation getRelation() {
        return FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(this);
    }

    public void startFlyingTask() {
        Player player = getPlayer();

        player.setAllowFlight(true);
        player.setFlying(true);

        Integer cooldown = CooldownConstants.Fly.FLY_COMMAND.get(getHandle().getHighestGroup());

        if (cooldown == null) {
            return;
        }

        this.flyingTaskCountdown = CooldownConstants.Fly.FLY_TIME.get(getHandle().getHighestGroup());

        if (this.flyingTaskCountdown != null) {
            Message.INFO.send(
                    player,
                    String.format(
                            "Você tem %s de voo, aproveite.",
                            Cooldowns.getFormattedTimeLeft(this.flyingTaskCountdown * 1000L)
                    )
            );

            this.flyingTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        stopFlyingTask();
                        return;
                    }

                    flyingTaskCountdown--;

                    if (flyingTaskCountdown < 0) {
                        stopFlyingTask();
                        return;
                    }

                    if (flyingTaskCountdown > 5) {
                        return;
                    }

                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);

                    Title.builder()
                            .subTitle(String.format("&cTempo de Voo: &f%s", flyingTaskCountdown))
                            .stay(30)
                            .fadeOut(20)
                            .build()
                            .send(player);

                }
            }.runTaskTimer(FactionsPlugin.getInstance(), 0L, 20L);
        }
    }

    public void stopFlyingTask() {
        Player player = getPlayer();

        if (player != null && player.isOnline()) {
            player.setFallDistance(0);
            player.setFlying(false);
            player.setAllowFlight(false);
        }

        if (this.flyingTask != null) {
            this.flyingTask.cancel();
            this.flyingTask = null;
        }

        Integer cooldown = CooldownConstants.Fly.FLY_COMMAND.get(getHandle().getHighestGroup());

        if (cooldown == null) {
            return;
        }

        UserCooldowns.start(getHandle(), CooldownConstants.Fly.FLY_COOLDOWN_KEY, cooldown, TimeUnit.SECONDS);
    }
}
