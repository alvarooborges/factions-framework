package net.hyze.factions.framework.skills;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.spigot.commands.CommandRegistry;
import net.hyze.factions.framework.FactionsCustomPlugin;
import net.hyze.factions.framework.setups.FactionsSetup;
import net.hyze.factions.framework.skills.listeners.CustomMineListener;
import net.hyze.factions.framework.skills.listeners.McMMOListeners;
import net.hyze.hyzeskills.booster.BoosterInventory;
import net.hyze.hyzeskills.datatypes.skills.AbilityType;
import net.hyze.hyzeskills.datatypes.skills.SkillType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class SkillsSetup<T extends FactionsCustomPlugin> extends FactionsSetup<T> {

    @Singular("disable")
    private final Set<SkillType> disabled;

    @Singular
    private final Map<Integer, SkillType> skills;

    @Singular("gainOn")
    private final Map<SkillType, List<AppType>> allowGain;

    @Override
    public void enable(FactionsCustomPlugin plugin) {
        for (SkillType type : disabled) {
            type.setActive(false);
        }

        for (Map.Entry<Integer, SkillType> entry : skills.entrySet()) {
            BoosterInventory.addSkill(entry.getKey(), entry.getValue());
        }

        Arrays.stream(AbilityType.values()).forEach(ability -> ability.setActive(false));

        plugin.getServer().getPluginManager().registerEvents(new McMMOListeners(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new CustomMineListener(), plugin);

        CommandRegistry.registerCommand(new SkillsCommand());
    }
}
