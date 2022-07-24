package net.hyze.factions.framework.user.healthpoints;

import net.hyze.hyzeskills.datatypes.player.McMMOPlayer;
import net.hyze.hyzeskills.datatypes.player.PlayerProfile;
import net.hyze.hyzeskills.datatypes.skills.SkillType;
import net.hyze.hyzeskills.util.player.UserManager;
import org.bukkit.entity.Player;

import java.util.Map;

public class HealthPointsUtils {

    public static int getCurrentLevelByPlayer(Player player) {

        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if (mmoPlayer == null) {
            return getCurrentLevelByPlayer((PlayerProfile) null);
        }

        return getCurrentLevelByPlayer(mmoPlayer.getProfile());
    }

    public static int getCurrentLevelByPlayer(PlayerProfile profile) {

        if (profile == null) {
            return -1;
        }

        int currentGoalIndex = -1;

        MAIN_LOOP:
        for (Map.Entry<Integer, Map<SkillType, Integer>> entry : HealthPointsConstants.GOALS_MAP.entrySet()) {

            Integer goalIndex = entry.getKey();
            Map<SkillType, Integer> requirementMap = entry.getValue();

            if (goalIndex > currentGoalIndex) {

                for (Map.Entry<SkillType, Integer> requirementEntry : requirementMap.entrySet()) {

                    SkillType requiredSkillType = requirementEntry.getKey();
                    int requiredLevel = requirementEntry.getValue();

                    int playerSkillLevel = profile.getSkillLevel(requiredSkillType);

                    if (playerSkillLevel < requiredLevel) {
                        break MAIN_LOOP;
                    }
                }

                currentGoalIndex = goalIndex;
            }
        }

        return currentGoalIndex;
    }

    public static void updatePlayerMaxHealth(Player player) {

        //oque nasce quando loga.
        double defaultHearts = HealthPointsConstants.DEFAULT_HEARTS * 2; // 6

        int currentLevelByPlayer = HealthPointsUtils.getCurrentLevelByPlayer(player);

        double lifeToAdd = 0.0;

        if (currentLevelByPlayer >= 0) {
            for (int i = 1; i <= currentLevelByPlayer + 1; i++) {
                lifeToAdd += 1.0;
            }
        }

        player.setMaxHealth(defaultHearts + lifeToAdd);

    }

}
