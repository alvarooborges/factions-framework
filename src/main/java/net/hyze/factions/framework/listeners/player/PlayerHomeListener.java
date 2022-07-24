package net.hyze.factions.framework.listeners.player;

import com.google.common.collect.Maps;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.DefaultMessage;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.homes.Home;
import net.hyze.homes.HomesProvider;
import net.hyze.homes.events.PlayerHomeEvent;
import net.hyze.homes.events.PlayerSetHomeEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Set;

public class PlayerHomeListener implements Listener {

    private static final Map<Group, Integer> HOME_LIMIT = Maps.newEnumMap(Group.class);

    static {
        HOME_LIMIT.put(Group.DEFAULT, 5);
        HOME_LIMIT.put(Group.ARCANE, 10);
        HOME_LIMIT.put(Group.DIVINE, 15);
        HOME_LIMIT.put(Group.HEAVENLY, 20);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerSetHomeEvent event) {

        if (AppType.FACTIONS_WAR.isCurrent() || AppType.FACTIONS_MINE.isCurrent() || AppType.FACTIONS_VIP.isCurrent()) {
            Message.ERROR.send(event.getPlayer(), "Você não pode definir home aqui.");
            event.setCancelled(true);
            return;
        }

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

        if (AppType.FACTIONS_LOSTFORTRESS.isCurrent()) {
            Zone zone = LandUtils.getZone(event.getLocation());

            if (zone != null && zone.getType() == Zone.Type.LOST_FORTRESS) {
                Message.ERROR.send(event.getPlayer(), "Você não pode definir uma home dentro da Base Perdida.");
                event.setCancelled(true);
                return;
            }
        }

        Set<Home> homes = HomesProvider.Cache.Local.HOMES.provide().get(user.getId());

        int limit = HOME_LIMIT.get(Group.DEFAULT);

        if (user.getHandle().hasGroup(Group.ARCANE)) {
            limit = HOME_LIMIT.get(Group.ARCANE);
        }

        if (user.getHandle().hasGroup(Group.DIVINE)) {
            limit = HOME_LIMIT.get(Group.DIVINE);
        }

        if (user.getHandle().hasGroup(Group.HEAVENLY)) {
            limit = HOME_LIMIT.get(Group.HEAVENLY);
        }

        if (homes.size() >= limit) {
            Message.ERROR.send(event.getPlayer(), String.format("&cVocê atingiu o limite de %s homes definidas.", limit));
            event.setCancelled(true);

            ComponentBuilder builder;

            switch (user.getHandle().getHighestGroup()) {
                case DEFAULT:
                    builder = new ComponentBuilder("VIPs podem definir mais do que " + limit + " homes,")
                            .color(ChatColor.YELLOW)
                            .append(" garanta já o seu em ")
                            .append(CoreConstants.Infos.STORE_DOMAIN)
                            .bold(true)
                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, CoreConstants.Infos.STORE_URL))
                            .append(".", ComponentBuilder.FormatRetention.NONE)
                            .color(ChatColor.YELLOW);

                    event.getPlayer().spigot().sendMessage(builder.create());
                    break;
                case ARCANE:
                case DIVINE:
                    Group nextGroup = Group.DIVINE;

                    if (user.getHandle().getHighestGroup() == Group.DIVINE) {
                        nextGroup = Group.HEAVENLY;
                    }

                    builder = new ComponentBuilder("Jogadores com o grupo ")
                            .color(ChatColor.YELLOW)
                            .append(nextGroup.getDisplayTag())
                            .append(" podem definir mais do que " + limit + " homes,")
                            .color(ChatColor.YELLOW)
                            .append(" garanta já o seu em ")
                            .append(CoreConstants.Infos.STORE_DOMAIN)
                            .bold(true)
                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, CoreConstants.Infos.STORE_URL))
                            .append(".", ComponentBuilder.FormatRetention.NONE)
                            .color(ChatColor.YELLOW);

                    event.getPlayer().spigot().sendMessage(builder.create());
                    break;
            }

            return;
        }

        Location location = event.getLocation();

        Claim claim = LandUtils.getClaim(location);

        if (claim == null) {
            return;
        }

        if (!FactionPermission.PERSONAL_HOME.allows(claim.getFaction(), user)) {
            event.setCancelled(true);

            Message.ERROR.send(event.getPlayer(), String.format(
                    "Você não tem permissão para definir homes nas terras da facção %s&c.",
                    claim.getFaction().getDisplayName()
            ));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerHomeEvent event) {
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

        if(user.getHandle().hasGroup(Group.MANAGER)){
            return;
        }

        if (CombatManager.isTagged(user.getHandle())) {
            Message.ERROR.sendDefault(event.getPlayer(), DefaultMessage.COMBAT_TELEPORT_ERROR);
            event.setCancelled(true);
            return;
        }

        String appId = event.getHome().getSerializedLocation().getAppId();
        Location location = event.getHome().getSerializedLocation().parser(CoreSpigotConstants.LOCATION_PARSER);

        Claim claim = LandUtils.getClaim(appId, location);

        if (claim == null) {
            return;
        }

        if (!user.getOptions().isAdminModeEnabled() && !FactionPermission.PERSONAL_HOME.allows(claim.getFaction(), user)) {
            event.setCancelled(true);

            Message.ERROR.send(event.getPlayer(), String.format(
                    "Você não tem permissão para ir até as terras da facção \n%s&c usando \"/home\".",
                    claim.getFaction().getDisplayName()
            ));
        }
    }
}
