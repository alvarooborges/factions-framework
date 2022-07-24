package net.hyze.factions.framework.misc.npc.impl;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.npc.CustomNPC;
import net.hyze.core.spigot.misc.npc.NPCInfo;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@NPCInfo(id = "npc_ranking", name = "&lENCANTAMENTOS")
public class EnchantmentNPC extends CustomNPC {

    public EnchantmentNPC(Location location) {
        super(EntityType.WITCH, location);
    }

    @Override
    public void handleClick(User user, NPCClickEvent event) {
        event.getClicker().performCommand("encantar");
    }

}
