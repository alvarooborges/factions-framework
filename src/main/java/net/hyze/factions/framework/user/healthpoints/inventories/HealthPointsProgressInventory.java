package net.hyze.factions.framework.user.healthpoints.inventories;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.misc.utils.PercentageUtils;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.user.healthpoints.HealthPointsConstants;
import net.hyze.factions.framework.user.healthpoints.HealthPointsUtils;
import net.hyze.hyzeskills.datatypes.player.McMMOPlayer;
import net.hyze.hyzeskills.datatypes.skills.SkillType;
import net.hyze.hyzeskills.util.player.UserManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Supplier;

public class HealthPointsProgressInventory extends CustomInventory {

    public HealthPointsProgressInventory(Player player, Supplier<CustomInventory> back) {
        super((back != null ? 5 : 4) * 9, "Pontos de Vida");

        int currentLevel = HealthPointsUtils.getCurrentLevelByPlayer(player);

        int index = 10;

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        for (int i = 0; i < HealthPointsConstants.GOALS_MAP.size(); i++) {

            if ((index + 1) % 9 == 0) {
                index += 2;
            }

            setItem(index++, buildIcon(mcMMOPlayer, i, currentLevel));
        }

        if (back != null) {
            backItem(back.get());
        }
    }

    private ItemStack buildIcon(McMMOPlayer player, int level, int currentLevel) {

        LevelState levelState = defineLevelState(currentLevel, level);

        ItemBuilder builder = new ItemBuilder(levelState.material)
                .name(String.format(
                        "&eNível %s %s",
                        level + 1,
                        levelState.displayName
                ))
                .lore(
                        "",
                        "&f Objetivos:"
                );

        Map<SkillType, Integer> goalsMap = HealthPointsConstants.GOALS_MAP.get(level);

        int totalRequired = 0;
        int totalReached = 0;

        for (Map.Entry<SkillType, Integer> entry : goalsMap.entrySet()) {
            SkillType skillType = entry.getKey();

            int skillLevel = player.getSkillLevel(skillType);
            Integer requiredLevel = entry.getValue();

            totalRequired += requiredLevel;

            builder.lore(
                    String.format(
                            "  %s &7Alcance &f%s &7níveis em %s",
                            skillLevel >= requiredLevel ? "&a✔" : "&c✖",
                            requiredLevel,
                            skillType.getName()
                    )
            );

            if (skillLevel < requiredLevel) {
                totalReached += skillLevel;
                builder.lore(String.format("  &8(Você está no nível %s)", skillLevel));
            } else {
                totalReached = requiredLevel;
            }

            builder.lore("");
        }

        if (levelState == LevelState.IN_PROGRESS) {
            double progressPercentage = PercentageUtils.getPercentage(totalRequired, totalReached);

            builder.lore(
                    String.format(
                            " &fProgresso: %s &a%.0f%%",
                            PercentageUtils.getProgressBar(totalReached, totalRequired, 15, "▍", "▍", "&a", "&7"),
                            progressPercentage
                    ),
                    ""
            );
        }

        builder.lore(
                " &fRecompensa:",
                "  &c0.5 Ponto de Vida ❤"
        );

        return builder.make();
    }

    private LevelState defineLevelState(int currentLevel, int level) {

        if (currentLevel + 1 == level) {
            return LevelState.IN_PROGRESS;
        }

        if (currentLevel >= level) {
            return LevelState.FINISHED;
        }

        return LevelState.NOT_INITIATED;
    }

    @RequiredArgsConstructor
    private enum LevelState {

        NOT_INITIATED("&c(Bloqueado)", Material.MINECART),
        IN_PROGRESS("&6(Em progresso)", Material.EXPLOSIVE_MINECART),
        FINISHED("&a(Concluído)", Material.STORAGE_MINECART);

        private final String displayName;
        private final Material material;

    }

}
