package net.hyze.factions.framework.misc.npc.impl;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.npc.CustomNPC;
import net.hyze.core.spigot.misc.npc.NPCInfo;
import net.hyze.core.spigot.misc.npc.PlayerCustomNPC;
import org.bukkit.Location;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;

@NPCInfo(id = "cash_npc", name = "&b&lITENS ESPECIAIS")
public class CashNPC extends CustomNPC {

    public CashNPC(Location location) {
        super(EntityType.IRON_GOLEM, location);
    }

    @Override
    public double getHologramOffset() {
        return 2.8;
    }

    @Override
    protected void postSpawn() {
        this.npc.getEntity().setCustomNameVisible(false);
    }

    @Override
    public void handleClick(User user, NPCClickEvent event) {
        event.getClicker().performCommand("especiais");
    }

}
