package net.hyze.factions.framework.listeners.player;

import net.hyze.core.spigot.misc.playerdata.storage.events.UserDataSaveEvent;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.hyzeskills.SkillsProvider;
import net.hyze.hyzeskills.booster.Booster;
import net.hyze.hyzeskills.datatypes.player.McMMOPlayer;
import net.hyze.hyzeskills.datatypes.player.PlayerProfile;
import net.hyze.hyzeskills.util.player.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HumanDataListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(UserDataSaveEvent event) {

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getUser());

        // SALVANDO SKILLS DO MCMMO.
        try {
            McMMOPlayer mMOPlayer = UserManager.getPlayer(user.getPlayer());

            if (mMOPlayer != null) {
                PlayerProfile profile = mMOPlayer.getProfile();
                profile.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (Booster booster : SkillsProvider.Cache.Local.BOOSTERS.provide().get(user.getHandle()).values()) {
                if (booster != null) {
                    SkillsProvider.Repositories.BOOSTERS.provide().update(booster);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
