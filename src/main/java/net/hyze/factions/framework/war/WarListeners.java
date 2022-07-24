package net.hyze.factions.framework.war;

import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.customitem.events.PlayerUseCustomItemEvent;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.playerdata.storage.events.UserDataPreLoadEvent;
import net.hyze.core.spigot.misc.playerdata.storage.events.UserDataPreSaveEvent;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.war.clock.phases.EnumWarPhase;
import net.hyze.factions.framework.war.commands.subcommands.GiveSetSubCommand;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;
import java.util.regex.Pattern;

public class WarListeners implements Listener {

    @EventHandler
    public void on(UserDataPreLoadEvent event) {
        if (War.TEST) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(UserDataPreSaveEvent event) {
        if (War.TEST) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerInitialSpawnEvent event) {
        event.setSpawnLocation(War.CONFIG.getSpawn().parser(CoreSpigotConstants.LOCATION_PARSER));
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer());

        WarScoreboardManager.setup(user);

        if (user.getHandle().hasGroup(Group.MODERATOR)) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        } else {

            if(War.TEST){
                GiveSetSubCommand.giveItems(event.getPlayer());
            }

        }
    }

    @EventHandler
    public void on(EntityDamageEvent event) {

        if (War.PAUSE) {
            event.setCancelled(true);
            return;
        }

        EnumWarPhase phase = War.CLOCK.getCurrentEnumWarPhase();

        if (phase == null || !phase.getWarPhase().isPvp()) {
            event.setCancelled(true);
        }

    }

    private static Pattern PATTERN = Pattern.compile(" ");

    @EventHandler
    public void on(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

        if (user.getHandle().hasGroup(Group.MODERATOR)) {
            return;
        }

        EnumWarPhase phase = War.CLOCK.getCurrentEnumWarPhase();

        Set<String> whitelist = phase != null && phase.getWarPhase().isPvp() ? War.COMMAND_LATE_GAME_WHITELIST : War.COMMAND_PRE_START_WHITELIST;

        for (String s : whitelist) {
            if (PATTERN.split(event.getMessage().trim().toLowerCase())[0].startsWith(s)) {
                return;
            }
        }

        event.setCancelled(true);
        Message.ERROR.send(event.getPlayer(), "Você não pode executar este comando durante a Guerra.");
    }

    @EventHandler
    public void on(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void on(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        EnumWarPhase phase = War.CLOCK.getCurrentEnumWarPhase();

        if (phase == null || phase.getWarPhase().equals(EnumWarPhase.ANNOUNCE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerUseCustomItemEvent event) {
        if (event.getItem().getKey().equalsIgnoreCase("custom_item_laucher")
                || event.getItem().getKey().equalsIgnoreCase("custom_item_repair_coin")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {

//        Player player = event.getEntity();
//
//        Player killer = player.getKiller();
//
//        if (killer != null) {
//
//            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);
//
//            String playerName = user.getRelation().getFaction().;
//            String killerName = "";
//
//            Bukkit.broadcastMessage(
//                    MessageUtils.translateColorCodes(
//                            String.format("&7%s &cfoi morto por &7%s&c.", playerName, killerName)
//                    )
//            );
//
//        }
    }

}
