package net.hyze.factions.framework.listeners;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.npc.CustomNPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FactionsCitizensListeners implements Listener {

    private void handle(NPCClickEvent event) {
        User user = CoreProvider.Cache.Local.USERS.provide().get(event.getClicker().getName());

        for (CustomNPC custom : CustomNPC.INSTANCES) {
            if (Objects.equals(custom.getNpc(), event.getNPC())) {

                int id = custom.getNpc().getEntity().getEntityId();

                if (!UserCooldowns.hasEnded(user, "click_npc_" + id)) {
                    return;
                }

                UserCooldowns.start(user, "click_npc_" + id, 1, TimeUnit.SECONDS);

                custom.handleClick(user, event);
                return;
            }
        }
    }

    @EventHandler
    public void event(NPCRightClickEvent event) {
        handle(event);
    }

    @EventHandler
    public void event(NPCLeftClickEvent event) {
        handle(event);
    }

}
