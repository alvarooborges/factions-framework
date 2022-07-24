package net.hyze.factions.framework.listeners.player;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.echo.packets.UserDeathByUserPacket;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.utils.teleporter.Teleporter;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.UserPowerUpdatedPacket;
import net.hyze.factions.framework.misc.playerheads.PlayerHeadsUtils;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.user.stats.UserStats;
import net.hyze.factions.framework.user.stats.storage.UserStatsRepository;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PlayerDeathListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMonitor(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        Player player = event.getEntity();
        Location location = player.getLocation();
        FactionUser userVictim = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

        // Dropando t0do o XP que o jogador tinha
        int i = event.getDroppedExp();
        WorldServer nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
        while (i > 0) {
            int j = EntityExperienceOrb.getOrbValue(i);
            i -= j;
            nmsWorld.addEntity(new EntityExperienceOrb(
                    nmsWorld, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), j
            ));
        }
        //

        event.setNewExp(0);
        event.setDroppedExp(0);

        boolean inCombat = CombatManager.isTagged(userVictim.getHandle());

        CombatManager.untag(userVictim.getHandle());

        if (FactionsProvider.getSettings().getBackLocationEnabledAt().contains(CoreProvider.getApp().getType())) {
            if (userVictim.getHandle().hasGroup(Group.ARCANE)) {
                if (FactionUtils.canTeleportTo(userVictim, player.getLocation())) {
                    userVictim.getStats().setBackLocation(player.getLocation());
                }
            }
        }

        if (inCombat && !AppType.FACTIONS_WAR.isCurrent()) {
            int oldPower = userVictim.getStats().getPower();

            if (oldPower > 0) {

                int newPower = userVictim.getStats().decrement(UserStats.Field.POWER);
                FactionsProvider.Repositories.USER_STATS.provide().update(userVictim.getStats(), UserStats.Field.POWER);
                CoreProvider.Redis.ECHO.provide().publish(new UserPowerUpdatedPacket(userVictim.getId(), oldPower, newPower));

            }
        }

        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

            Player killer = null;

            if (damageEvent.getDamager() instanceof Player) {
                killer = (Player) damageEvent.getDamager();
            } else if (damageEvent.getDamager() instanceof Projectile) {
                ProjectileSource projectileSource = ((Projectile) damageEvent.getDamager()).getShooter();

                if (projectileSource instanceof Player) {
                    killer = (Player) projectileSource;
                }
            }

            if (killer != null && killer != player) {

                FactionUser userKiller = FactionsProvider.Cache.Local.USERS.provide().get(killer.getName());

                double dropHeadChance = 10.0;

                if (userKiller.getHandle().hasGroup(Group.ARCANE)) {
                    dropHeadChance = 25.0;
                } else if (userKiller.getHandle().hasGroup(Group.DIVINE)) {
                    dropHeadChance = 20.0;
                } else if (userKiller.getHandle().hasGroup(Group.ARCANE)) {
                    dropHeadChance = 15.0;
                }

                double rand = Math.random() * 100;
                if (rand <= dropHeadChance) {
                    location.getWorld().dropItem(location, PlayerHeadsUtils.make(player.getName(), userKiller.getHandle(), new Date()));
                }

                if (inCombat && userVictim.getStats().getPower() > 0) {
                    if (UserCooldowns.hasEnded(userVictim.getHandle(), "kdr_count_" + userKiller.getNick())) {
                        UserCooldowns.start(userVictim.getHandle(), "kdr_count_" + userKiller.getNick(), 1, TimeUnit.MINUTES);

                        UserStatsRepository repository = FactionsProvider.Repositories.USER_STATS.provide();

                        if (userVictim.getRelation() == null) {
                            userVictim.getStats().increment(UserStats.Field.CIVIL_DEATHS);
                            repository.update(userVictim.getStats(), UserStats.Field.CIVIL_DEATHS);

                            userKiller.getStats().increment(UserStats.Field.CIVIL_KILLS);
                            repository.update(userKiller.getStats(), UserStats.Field.CIVIL_KILLS);
                        } else {
                            userVictim.getStats().increment(UserStats.Field.NEUTRAL_DEATHS);
                            repository.update(userVictim.getStats(), UserStats.Field.NEUTRAL_DEATHS);

                            userKiller.getStats().increment(UserStats.Field.NEUTRAL_KILLS);
                            repository.update(userKiller.getStats(), UserStats.Field.NEUTRAL_KILLS);
                        }
                    }
                }

                // Mensagem local de morte
                {
                    userKiller.getPlayer().getWorld()
                            .getNearbyEntities(userKiller.getPlayer().getLocation(), 50, 50, 50)
                            .stream()
                            .filter(entity -> entity instanceof Player)
                            .map(entity -> (Player) entity)
                            .map(FactionUserUtils::getUser)
                            .filter(Objects::nonNull)
                            .forEach(user -> {
                                ChatColor victimColor = getAnnouncementColor(user, userVictim);
                                ChatColor killerColor = getAnnouncementColor(user, userKiller);

                                ComponentBuilder builder = new ComponentBuilder("");

                                builder.append(userVictim.getDisplayName(victimColor, victimColor))
                                        .append(" foi morto por ").color(ChatColor.GRAY)
                                        .append(userKiller.getDisplayName(killerColor, killerColor));

                                user.getPlayer().sendMessage(builder.create());
                            });

                    event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
                }

                CoreProvider.Redis.ECHO.provide().publish(new UserDeathByUserPacket(userVictim.getHandle().getId(), userKiller.getHandle().getId()));
            }
        }

//        Teleporter.builder()
//                .toAppType(AppType.FACTIONS_SPAWN)
//                .reason(ConnectReason.RESPAWN)
//                .welcomeMessage(TextComponent.fromLegacyText(ChatColor.RED + "Você morreu."))
//                .kickOnError(true)
//                .build()
//                .teleport(userVictim.getHandle());
    }

    @EventHandler
    public void onNormal(PlayerDeathEvent event) {

        //LISTENER DA BATATAÇÃO

        if (CoreProvider.getApp().getType() == AppType.FACTIONS_SPAWN) {
            Player entity = event.getEntity();

            boolean hasGoldenAppleEffect = entity.hasPotionEffect(PotionEffectType.REGENERATION) &&
                    entity.hasPotionEffect(PotionEffectType.ABSORPTION);

            boolean hasUnusedGoldenApple = Arrays.stream(entity.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .filter(itemStack -> itemStack.getType() == Material.GOLDEN_APPLE)
                    .anyMatch(itemStack -> itemStack.getDurability() > 0);

            if (!hasGoldenAppleEffect && hasUnusedGoldenApple) {

                User user = CoreProvider.Cache.Local.USERS.provide().get(entity.getName());

                ComponentBuilder builder = new ComponentBuilder("\n");

                builder.append("Batatinha 1... 2... 3...")
                        .color(ChatColor.YELLOW)
                        .append("\n")
                        .append(user.getHighestGroup().getDisplayTag(user.getNick()))
                        .append(" morreu e esqueceu de usar suas maçãs douradas, levou elas para o céu...")
                        .color(ChatColor.GRAY)
                        .append("\n");

                CoreProvider.Redis.ECHO.provide().publish(
                        BroadcastMessagePacket.builder()
                                .group(Group.DEFAULT)
                                .components(builder.create())
                                .build()
                );
            }
        }
    }

    private ChatColor getAnnouncementColor(FactionUser target, FactionUser user) {
        ChatColor color = ChatColor.RED;

        if (FactionUtils.isSame(target, user)) {
            color = ChatColor.GREEN;
        } else if (FactionUtils.isAlly(target, user)) {
            color = ChatColor.BLUE;
        }

        return color;
    }
}
