package net.hyze.factions.framework.skills;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.skills.inventories.SkillsInventory;
import net.hyze.factions.framework.skills.inventories.TopSkillsInventory;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInventoryManager {

    private static final Map<String, SkillsInventory> SKILLS_INVENTORIES = Maps.newHashMap();
    private static final Map<String, TopSkillsInventory> TOP_SKILLS_INVENTORIES = Maps.newHashMap();

    public static SkillsInventory getSkillsInventory(User user) {
        SkillsInventory inventory = SKILLS_INVENTORIES.getOrDefault(user.getNick(), new SkillsInventory(user));

        if (!SKILLS_INVENTORIES.containsKey(user.getNick())) {
            SKILLS_INVENTORIES.put(user.getNick(), inventory);
        }

        return inventory;
    }

    public static TopSkillsInventory getTopSkillsInventory(User user) {
        TopSkillsInventory inventory = TOP_SKILLS_INVENTORIES.getOrDefault(user.getNick(), new TopSkillsInventory(user));

        if (!TOP_SKILLS_INVENTORIES.containsKey(user.getNick())) {
            TOP_SKILLS_INVENTORIES.put(user.getNick(), inventory);
        }

        return inventory;
    }

}
