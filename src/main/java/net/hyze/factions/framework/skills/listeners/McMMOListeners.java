package net.hyze.factions.framework.skills.listeners;

import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.config.cache.ConfigLocalCache;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.skills.SkillsConstants;
import net.hyze.factions.framework.skills.SkillsSetup;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.hyzeskills.SkillsProvider;
import net.hyze.hyzeskills.booster.Booster;
import net.hyze.hyzeskills.booster.BoosterManager;
import net.hyze.hyzeskills.datatypes.player.McMMOPlayer;
import net.hyze.hyzeskills.datatypes.skills.SkillType;
import net.hyze.hyzeskills.events.experience.McMMOPlayerLevelUpEvent;
import net.hyze.hyzeskills.events.experience.McMMOPlayerXpGainEvent;
import net.hyze.hyzeskills.events.skills.secondaryabilities.TreasureWeightedActivationCheckEvent;
import net.hyze.hyzeskills.util.Misc;
import net.hyze.hyzeskills.util.player.UserManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class McMMOListeners implements Listener {

    private final SkillsSetup<?> setup;

    public McMMOListeners(SkillsSetup<?> setup) {
        this.setup = setup;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(TreasureWeightedActivationCheckEvent event) {

        int activationChance = 75;

        if ((event.getChance() * activationChance) > (Misc.getRandom().nextDouble() * activationChance)) {
            ItemStack treasure = event.getTreasure().getDrop().clone();

            double maxDoubleDropMaxChance = SkillsConstants.EXCAVATION_TREASURE_DOUBLE_DROP_MAX_CHANCE;

            int maxLevel = event.getSkill().getMaxLevel();

            McMMOPlayer mcPlayer = UserManager.getOfflinePlayer(event.getPlayer().getName());

            int level = mcPlayer.getProfile().getSkillLevel(event.getSkill());

            double currentDoubleDropChance = (maxDoubleDropMaxChance / maxLevel) * Math.min(level, maxLevel);

            if (currentDoubleDropChance / 100 > CoreConstants.RANDOM.nextDouble()) {
                event.getDrops().add(treasure.clone());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(McMMOPlayerLevelUpEvent event) {
        Player player = event.getPlayer();

        SkillType skillType = event.getSkill();
        int skillLevel = event.getSkillLevel();

        if (skillLevel == skillType.getMaxLevel()) {
            ComponentBuilder cb = new ComponentBuilder("\n")
                    .append("HABILIDADES: ").color(ChatColor.GOLD).bold(true)
                    .append(player.getName()).color(ChatColor.YELLOW).bold(false)
                    .append(" atingiu o ").append("nível ").color(ChatColor.GOLD)
                    .append(event.getSkillLevel() + "").append(" na habilidade de ").color(ChatColor.YELLOW)
                    .append(skillType.getName()).color(ChatColor.GOLD)
                    .append("!").color(ChatColor.YELLOW)
                    .append("\n ");

            CoreProvider.Redis.ECHO.provide().publish(new BroadcastMessagePacket(
                    cb.create(), Sets.newHashSet(Group.DEFAULT), false, Server.FACTIONS_ALPHA
            ));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMonitor(McMMOPlayerXpGainEvent event) {
        SkillType skill = event.getSkill();

        Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(), () -> {
            if (event.getPlayer().isOnline()) {
                McMMOPlayer mcPlayer = UserManager.getPlayer(event.getPlayer());

                if (mcPlayer != null) {
                    String skillName = skill.getName().replace("Mineracao", "Mineração");
                    int skillLevel = event.getSkillLevel();
                    int skillXpLevel = mcPlayer.getSkillXpLevel(skill);
                    int xpToLevel = mcPlayer.getXpToLevel(skill);
                    int finalExp = (int) event.getFinalXpGained();

                    if (finalExp <= 0) {
                        return;
                    }

                    String musicText = String.format(
                            "%s%s: %s (%s/%s) +%s XP",
                            ChatColor.GREEN,
                            skillName,
                            skillLevel,
                            skillXpLevel,
                            xpToLevel,
                            finalExp
                    );

                    if (event.getPlayer().hasMetadata("mcmmo_xp_gain_with_intelligence_" + event.getSkill())) {
                        musicText += ChatColor.YELLOW + " (Inteligência)";
                    }

                    if (doubleXPEnabled()) {
                        musicText += ChatColor.YELLOW + " (Evento Double XP)";
                    }

                    FactionUser user = FactionUserUtils.getUser(event.getPlayer());

                    if (user != null && user.getActionBarNotification() != null) {
                        user.getActionBarNotification().sendNotification(musicText);
                    }
                }
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void on(McMMOPlayerXpGainEvent event) {

        if (event.getRawXpGained() == 0) {
            return;
        }

        FactionUser user = FactionUserUtils.getUser(event.getPlayer());

        if (this.setup.getAllowGain().containsKey(event.getSkill())) {
            if (!this.setup.getAllowGain().get(event.getSkill()).contains(CoreProvider.getApp().getType())) {
                event.setCancelled(true);
                event.setRawXpGained(0F);

                String cooldownKey = "cancelled_gain_" + event.getSkill();

                if (UserCooldowns.hasEnded(user.getHandle(), cooldownKey)) {

                    String skillName = event.getSkill().getName().replace("Mineracao", "Mineração");

                    user.getActionBarNotification().sendNotification(
                            ChatColor.RED + "Habilidade de " + skillName + " desativada neste mundo."
                    );

                    UserCooldowns.start(user.getHandle(), cooldownKey, 30, TimeUnit.SECONDS);
                }
                return;
            }
        }

        float multiplier = BoosterManager.getVIPMultiplier(
                user.getHandle()) + getBoosterMultiplier(user.getHandle(), event.getSkill()
        );

        if (doubleXPEnabled()) {
            multiplier += 2;
        }

        event.setMultiplier(event.getMultiplier() + multiplier);
    }

    private float getBoosterMultiplier(User user, SkillType skill) {
        Booster booster = SkillsProvider.Cache.Local.BOOSTERS.provide().get(user, skill);
        return booster != null && !booster.isExpired() ? 2f : 0f;
    }

    private boolean doubleXPEnabled() {
        ConfigLocalCache config = CoreProvider.Cache.Local.CONFIG.provide();

        return config.get(String.format(
                "%s_double_xp_enabled",
                CoreProvider.getApp().getServer().getId()
        ).toLowerCase().replace("-", "_"), Boolean.class, false);
    }
}
