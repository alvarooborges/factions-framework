package net.hyze.factions.framework.beacon.listeners;

import com.google.common.primitives.Longs;
import net.hyze.beacon.BeaconConstants;
import net.hyze.beacon.BeaconProperties;
import net.hyze.beacon.BeaconProvider;
import net.hyze.beacon.events.*;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.beacon.FactionBeaconConstants;
import net.hyze.factions.framework.beacon.attributes.restrict.RestrictAttribute;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.personalmail.PersonalMailAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BeaconListener implements Listener {

    private final long BEACON_BREAK_DELAY = TimeUnit.HOURS.toMillis(1);

    @EventHandler
    public void on(BeaconPlaceEvent event) {
        Claim claim = LandUtils.getClaim(event.getLocation());

        if (claim == null) {
            event.setCancelled(true);
            Message.ERROR.send(event.getPlayer(), "Você só pode colocar sinalizadores em terras dominadas.");
            return;
        }

        if (FactionsProvider.getSettings().getSpawnerMode().isBreakCooldownEnabled()) {
            Message.INFO.send(event.getPlayer(), "* Você pode retirar este sinalizador durante o próximo minuto.");
            Message.INFO.send(event.getPlayer(), String.format(
                    "* Após isso, ele só poderá ser retirado depois de %s.",
                    UserCooldowns.getFormattedTimeLeft(BEACON_BREAK_DELAY)
            ));

            if (FactionsProvider.getSettings().isAutoRemoveSpawnersEnabled()) {
                Message.INFO.send(event.getPlayer(), "* Ao completar 24 horas colocado, ele será enviado para o Correio do Líder da facção.");
            }
        }
    }

    @EventHandler
    public void on(BeaconBreakEvent event) {
        Date placeAt = event.getProperties().getPlacedAt();

        if (placeAt == null) {
            return;
        }

        if (FactionsProvider.getSettings().getSpawnerMode().isBreakCooldownEnabled()) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -1);

            if (placeAt.after(calendar.getTime())) {
                return;
            }

            calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, (int) -BEACON_BREAK_DELAY);

            if (placeAt.after(calendar.getTime())) {
                FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

                long diff = placeAt.getTime() - calendar.getTimeInMillis();

                if (user.getOptions().isAdminModeEnabled()) {
                    Message.INFO.send(event.getPlayer(), String.format("Este sinalizador só poderia ser retirado em %s.",
                            UserCooldowns.getFormattedTimeLeft(diff)
                    ));
                    return;
                }

                Message.ERROR.send(event.getPlayer(), String.format("Este sinalizador apenas poderá ser retirado em %s.",
                        UserCooldowns.getFormattedTimeLeft(diff)
                ));

                event.setCancelled(true);
                return;
            }
        }

        if (FactionsProvider.getSettings().getSpawnerMode().isUnderAttackEnabled()) {
            Claim claim = LandUtils.getClaim(event.getLocation());

            if (claim != null && claim.getFaction().isUnderAttack()) {
                Message.ERROR.send(event.getPlayer(), "Seus Sinalizadores não podem ser retirados pois sua facção está sob-ataque!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(BeaconTickEvent event) {
        BeaconProperties properties = event.getProperties();
        Location location = event.getLocation();

        if (LandUtils.getClaim(location) == null) {
            event.setCancelled(true);
            return;
        }

        String stringTime = properties.getMetadata().getOrDefault(FactionBeaconConstants.BEACON_BREAKED, null);

        if (stringTime != null) {
            Long time = Longs.tryParse(stringTime);

            if (time != null && (System.currentTimeMillis() - time) < FactionBeaconConstants.BEACON_BREAKED_COOLDOWN) {
                event.setCancelled(true);
            }
        }

        if (properties.getPlacedAt() == null) {
            return;
        }

        if (FactionsProvider.getSettings().getSpawnerMode().isBreakCooldownEnabled()) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, -24);

            if (properties.getPlacedAt().before(calendar.getTime())) {
                event.setCancelled(true);

                Claim claim = LandUtils.getClaim(location);

                if (claim != null) {
                    Set<FactionUser> leaders = FactionUtils.getUsers(claim.getFaction(), FactionRole.LEADER);

                    if (!leaders.isEmpty()) {
                        location.getBlock().setType(Material.AIR);

                        String propertiesSerialized = CoreConstants.GSON.toJson(properties);
                        ItemStack item = new ItemBuilder(new ItemStack(Material.BEACON))
                                .name("&b" + properties.getName())
                                .nbt(BeaconConstants.METADATA_TYPE_KEY, propertiesSerialized)
                                .make();

                        BeaconProvider.Cache.Local.BEACON.provide().remove(location);
                        BeaconProvider.Repositories.BEACON.provide().delete(location);

                        PersonalMailAPI.post(leaders.iterator().next().getId(), item);

                        ComponentBuilder builder = new ComponentBuilder("* O Sinalizador da sua facção foi removido e enviado para o correio do seu Líder.")
                                .color(ChatColor.YELLOW);

                        FactionUtils.broadcast(claim.getFaction(), builder.create(), true, FactionRole.values());
                    }
                }
            }
        }
    }

    @EventHandler
    public void on(BeaconInventoryOpenEvent event) {
        String stringTime = event.getProperties().getMetadata().getOrDefault(FactionBeaconConstants.BEACON_BREAKED, null);

        if (stringTime != null) {
            Long time = Longs.tryParse(stringTime);

            if ((System.currentTimeMillis() - time) < FactionBeaconConstants.BEACON_BREAKED_COOLDOWN) {
                event.setCancelled(true);

                Long timeLeft = FactionBeaconConstants.BEACON_BREAKED_COOLDOWN - (System.currentTimeMillis() - time);

                Message.ERROR.send(
                        event.getPlayer(),
                        String.format(
                                "Este Sinalizador Supremo está quebrado. Você poderá abri-lo novamente em %s.",
                                TimeCode.toText(timeLeft, 5)
                        )
                );
            }
        }
    }

    @EventHandler
    public void on(BeaconAddPotionEffectEvent event) {

        BeaconProperties properties = event.getProperties();

        Claim claim = LandUtils.getClaim(event.getLocation());
        Faction faction;

        if (claim == null || (faction = claim.getFaction()) == null) {
            event.setCancelled(true);
            return;
        }

        if (faction.isUnderAttack()) {
            event.getEffects().remove(PotionEffectType.JUMP);
            event.getEffects().remove(PotionEffectType.INCREASE_DAMAGE);
            event.getEffects().remove(PotionEffectType.REGENERATION);
            event.getEffects().remove(PotionEffectType.DAMAGE_RESISTANCE);
            event.getEffects().remove(PotionEffectType.HEALTH_BOOST);
            event.getEffects().remove(PotionEffectType.ABSORPTION);
        }

        if (properties.getActiveAttributes().contains(RestrictAttribute.ID)) {
            event.getPlayers().removeIf(player -> {
                FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);
                return !FactionUtils.isMember(user.getHandle(), faction);
            });
        }
    }
}
