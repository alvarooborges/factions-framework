package net.hyze.factions.framework.skills.listeners;

import com.google.common.collect.Lists;
import net.hyze.core.shared.misc.utils.RandomUtils;
import net.hyze.core.spigot.misc.mining.events.CustomMineEvent;
import net.hyze.core.spigot.misc.utils.ItemStackUtils;
import net.hyze.factions.framework.skills.SkillsConstants;
import net.hyze.hyzeskills.datatypes.player.McMMOPlayer;
import net.hyze.hyzeskills.datatypes.skills.SkillType;
import net.hyze.hyzeskills.util.player.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.List;

public class CustomMineListener implements Listener {

    @EventHandler
    public void on(CustomMineEvent event) {
        McMMOPlayer mcPlayer = UserManager.getPlayer(event.getPlayer().getName());

        int miningLevel = mcPlayer.getSkillLevel(SkillType.MINING);

        double maxSmeltLevel = SkillType.MINING.getMaxLevel();
        double maxSmeltChance = SkillsConstants.MINING_SMELT_MAX_CHANCE;

        double currentSmeltChance = (maxSmeltChance / maxSmeltLevel) * Math.min(miningLevel, maxSmeltChance);

        if (RandomUtils.randomInt(1, 100) <= currentSmeltChance) {
            List<ItemStack> drops = Lists.newArrayList(event.getDrops());
            event.getDrops().clear();

            for (ItemStack stack : drops) {
                MaterialData data = ItemStackUtils.melt(stack.getData());

                if (data == null) {
                    event.getDrops().add(stack);
                } else {
                    event.getDrops().add(data.toItemStack(stack.getAmount()));
                }
            }
        }
    }
}
