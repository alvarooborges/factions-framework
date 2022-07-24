package net.hyze.factions.framework.skills.inventories;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.skills.SkillsUtils;
import net.hyze.hyzeskills.datatypes.player.PlayerProfile;
import net.hyze.hyzeskills.datatypes.skills.SkillType;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class SkillsProgressInventory extends CustomInventory {

    private final User user;
    private final PlayerProfile mcMMOProfile;
    private final SkillType skillType;

    private static final Map<Integer, List<Integer>> PROGRESS_SLOTS = Maps.newHashMap();

    static {
        PROGRESS_SLOTS.put(0, Lists.newArrayList(
                9, 18, 27, 36, 45, 46,
                47, 38, 29, 20, 11, 2, 3,
                4, 13, 22, 31, 40, 49, 50,
                51, 42, 33, 24, 15, 6, 7,
                8, 17, 26, 35, 44
        ));

        PROGRESS_SLOTS.put(1, Lists.newArrayList(
                9, 18, 19, 20, 11, 2, 3,
                4, 13, 22, 23, 24, 15, 6, 7, 8, 17, 26
        ));
    }

    public SkillsProgressInventory(User user, PlayerProfile mcMMOProfile, SkillType skillType) {
        this(user, mcMMOProfile, skillType, 0);
    }

    public SkillsProgressInventory(User user, PlayerProfile mcMMOProfile, SkillType skillType, int page) {
        super(page == 0 ? 6 * 9 : 3 * 9, "Habilidades de " + user.getNick());

        this.user = user;
        this.mcMMOProfile = mcMMOProfile;
        this.skillType = skillType;

        int currentLevel = this.mcMMOProfile.getSkillLevel(skillType);

        List<Integer> slots = PROGRESS_SLOTS.get(page);

        int slotIndex = 0;
        for (int level = page * PROGRESS_SLOTS.get(0).size() + 1; level < skillType.getMaxLevel() + 1; level++) {

            if (slotIndex >= slots.size()) {
                break;
            }

            int paneData = currentLevel == level ? 4 : currentLevel > level ? 5 : 15;
            String title = currentLevel == level ?
                    String.format("&eNível %s &8(Em progresso)", level) :
                    currentLevel > level ?
                            String.format("&aNível %s &8(Concluído)", level) :
                            String.format("&7Nível %s &8(Bloqueado)", level);

            if (level == skillType.getMaxLevel() && currentLevel == level) {
                title = String.format("&eNível %s &b(Máximo)", level);
            }

            int slot = slots.get(slotIndex++);

            ItemBuilder builder = ItemBuilder.of(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) paneData))
                    .name(title)
                    .lore(" ");

            List<String> description = SkillsUtils.getLevelDescription(skillType, level, false);

            builder.lore(description.toArray(new String[0]));

            setItem(slot, builder.build());

            ItemBuilder backIcon = ItemBuilder.of(Material.ARROW)
                    .name("&eVoltar");

            if (page > 0) {
                setItem(0, backIcon.build(), event -> {
                    event.getWhoClicked().openInventory(new SkillsProgressInventory(user, mcMMOProfile, skillType, page - 1));
                });
            } else {
                setItem(0, backIcon.build(), event -> {
                    event.getWhoClicked().openInventory(new SkillsInventory(user));
                });
            }

            if (page == 0) {
                ItemBuilder nextPageIcon = ItemBuilder.of(Material.ARROW)
                        .name("&eAvançar");

                setItem(getSize() - 1, nextPageIcon.build(), event -> {
                    event.getWhoClicked().openInventory(new SkillsProgressInventory(user, mcMMOProfile, skillType, page + 1));
                });
            }
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        super.onOpen(event);

    }

}
