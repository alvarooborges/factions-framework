package net.hyze.factions.framework.skills.inventories;

import com.google.common.collect.Lists;
import lombok.Setter;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.skills.UserInventoryManager;
import net.hyze.hyzeskills.HyzeSkillsPlugin;
import net.hyze.hyzeskills.datatypes.database.PlayerStat;
import net.hyze.hyzeskills.datatypes.skills.SkillType;
import net.hyze.hyzeskills.tasks.McMMORanksTask;
import net.hyze.hyzeskills.util.skills.McMMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class TopSkillsInventory extends CustomInventory implements Runnable {

    private static final String INVENTORY_TITLE = "Rank de Skills";
    private BukkitTask task;
    @Setter
    private boolean availableSkills = false;

    public TopSkillsInventory(User user) {
        super(6 * 9, INVENTORY_TITLE);

        this.setItem(49, BACK_ARROW, event -> event.getWhoClicked().openInventory(UserInventoryManager.getSkillsInventory(user)));
    }

    @Override
    public void run() {

        setItem(13, getTotalLevelIcon());

        if (availableSkills) {

            int index = 19;

            for (SkillType type : SkillType.values()) {

                if (type.isActive()) {
                    if ((index + 1) % 9 == 0) {
                        index += 2;
                    }

                    this.setItem(index++, type);
                }

            }

            return;
        }

        // 20 - 24 e 29 - 33
        setItem(20, SkillType.SWORDS);
        setItem(21, SkillType.MINING);
        setItem(22, SkillType.EXCAVATION);
        setItem(23, SkillType.AXES);

        setItem(24, SkillType.REPAIR);
        setItem(29, SkillType.ACROBATICS);
        setItem(30, SkillType.ALCHEMY);
        setItem(31, SkillType.HERBALISM);
        setItem(32, SkillType.UNARMED);
        setItem(33, SkillType.TAMING);

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(HyzeSkillsPlugin.p, this, 0L, 20L * 60);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (this.task != null) {
            Bukkit.getScheduler().cancelTask(this.task.getTaskId());
        }

        this.task = null;
    }

    private void setItem(int i, SkillType skillType) {
        if (skillType.isActive()) {
            setItem(i, getSkillIcon(skillType));
        }
    }

    private ItemStack getSkillIcon(SkillType skillType) {

        ItemBuilder itemCustom = new ItemBuilder(McMMOUtils.getSkillItemStack(skillType));
        itemCustom
                .name("&e" + skillType.getName())
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);

        if (!skillType.isChildSkill()) {

            List<String> ranking = Lists.newArrayList();

            for (int i = 0; i < 10; i++) {
                PlayerStat rank = McMMORanksTask.getTopSkill(skillType, i);
                ranking.add("&f" + (i + 1) + "º: &7" + (rank == null ? "Indefinido." : rank.name + "&8 - &7" + rank.statVal));
            }

            itemCustom.lore(ranking.toArray(new String[0]));

            if (skillType.equals(SkillType.TAMING)) {
                itemCustom.lore("", "&eApenas jogadores &6VIP&e podem ", "&eevoluir esta habilidade.");
            }
        }

        return itemCustom.make();
    }

    private ItemStack getTotalLevelIcon() {

        ItemBuilder itemCustom = new ItemBuilder(Material.BOOK_AND_QUILL);
        List<String> ranking = Lists.newArrayList();

        for (int i = 0; i < 10; i++) {
            PlayerStat rank = McMMORanksTask.getTopSkill(null, i);
            ranking.add("&f" + (i + 1) + "º: &7" + (rank == null ? "Indefinido." : rank.name + "&8 - &7" + rank.statVal));
        }

        return itemCustom
                .name(ChatColor.YELLOW + "Nível Total").flags(ItemFlag.HIDE_ATTRIBUTES)
                .lore("Representa a soma de todas as habilidades.", "")
                .lore(ranking.toArray(new String[0]))
                .make();

    }

}
