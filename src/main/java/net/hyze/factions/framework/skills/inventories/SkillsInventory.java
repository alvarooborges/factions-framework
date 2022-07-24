package net.hyze.factions.framework.skills.inventories;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import lombok.Setter;
import net.hyze.core.shared.misc.utils.PercentageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.skills.SkillsUtils;
import net.hyze.factions.framework.skills.UserInventoryManager;
import net.hyze.hyzeskills.HyzeSkillsPlugin;
import net.hyze.hyzeskills.SkillsProvider;
import net.hyze.hyzeskills.booster.Booster;
import net.hyze.hyzeskills.booster.BoosterManager;
import net.hyze.hyzeskills.config.skills.alchemy.PotionConfig;
import net.hyze.hyzeskills.datatypes.player.McMMOPlayer;
import net.hyze.hyzeskills.datatypes.player.PlayerProfile;
import net.hyze.hyzeskills.datatypes.skills.SkillType;
import net.hyze.hyzeskills.datatypes.treasure.ExcavationTreasure;
import net.hyze.hyzeskills.skills.alchemy.Alchemy;
import net.hyze.hyzeskills.skills.excavation.Excavation;
import net.hyze.hyzeskills.tasks.McMMORanksTask;
import net.hyze.hyzeskills.util.player.UserManager;
import net.hyze.hyzeskills.util.skills.McMMOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class SkillsInventory extends CustomInventory {

    private static final int MAX_LEVEL = 1000;

    private final User user;
    private PlayerProfile mcMMOProfile;
    private final HashMap<SkillType, Integer> skills;

    @Setter
    private boolean availableSkills = false;

    public SkillsInventory(User user) {
        super(5 * 9, "Habilidades de " + user.getNick());

        this.user = user;
        McMMOPlayer mcPlayer = UserManager.getOfflinePlayer(user.getNick());
        this.mcMMOProfile = mcPlayer != null ? mcPlayer.getProfile() : null;
        this.skills = Maps.newHashMap(McMMORanksTask.getPlayerRank(user.getNick()));
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        if (this.mcMMOProfile == null) {
            McMMOPlayer mcPlayer = UserManager.getOfflinePlayer(user.getNick());
            this.mcMMOProfile = mcPlayer != null ? mcPlayer.getProfile() : null;

            if (this.mcMMOProfile == null) {
                this.mcMMOProfile = HyzeSkillsPlugin.getDatabaseManager().loadPlayerProfile(
                        user.getNick(),
                        UUID.nameUUIDFromBytes(("OfflinePlayer:" + user.getNick()).getBytes(Charsets.UTF_8)),
                        false
                );

                if (this.mcMMOProfile == null) {
                    return;
                }
            }
        }

        this.skills.clear();
        this.skills.putAll(Maps.newHashMap(McMMORanksTask.getPlayerRank(user.getNick())));

        this.setItem(9, getRankInventoryIcon(), e -> {
            e.getWhoClicked().openInventory(UserInventoryManager.getTopSkillsInventory(this.user));
        });
        this.setItem(18, getTotalLevelIcon());

        this.setItem(12, SkillType.MINING);
        this.setItem(13, SkillType.EXCAVATION);
        this.setItem(14, SkillType.HERBALISM);
        this.setItem(15, SkillType.ACROBATICS);

        this.setItem(30, SkillType.SWORDS);
        this.setItem(31, SkillType.AXES);
        this.setItem(32, SkillType.ALCHEMY);
        this.setItem(33, SkillType.REPAIR);
    }

    private void setItem(int i, SkillType skillType) {
        if (skillType.isActive()) {
            Consumer<InventoryClickEvent> consumer = getSkillConsumer(skillType);
            ItemStack icon = getSkillIcon(skillType);


            if (consumer != null) {
                icon = ItemBuilder.of(icon)
                        .lore("", "&eClique para mais informações.")
                        .build();
            }

            this.setItem(i, icon, consumer);
        }
    }

    private Consumer<InventoryClickEvent> getSkillConsumer(SkillType skillType) {

        if (true) {
            return event -> {
                event.getWhoClicked().openInventory(new SkillsProgressInventory(user, mcMMOProfile, skillType));
            };
        }

        if (skillType == SkillType.ALCHEMY) {
            return event -> {

                PaginateInventory.PaginateInventoryBuilder builder = PaginateInventory.builder();

                List<Alchemy.Tier> tiers = Lists.newArrayList(Alchemy.Tier.values()).stream()
                        .sorted(Comparator.comparingInt(Alchemy.Tier::toNumerical))
                        .collect(Collectors.toList());

                for (Alchemy.Tier tier : tiers) {
                    boolean has = this.mcMMOProfile.getSkillLevel(skillType) >= tier.getLevel();

                    ItemBuilder i = ItemBuilder.of(has ? Material.MAP : Material.PAPER)
                            .name("&bTier " + tier.toNumerical())
                            .flags(ItemFlag.HIDE_ATTRIBUTES)
                            .glowing(has)
                            .lore(
                                    "&7Nível necessário: &f" + tier.getLevel(),
                                    "",
                                    "&eClique para ver os",
                                    "&eingredientes deste Tier."
                            );

                    builder.item(i.make(), e -> {
                        //  Inventory back = InventoryUtils.getCurrent((Player) e.getWhoClicked());

                        PaginateInventory.PaginateInventoryBuilder ingredientsBuilder = PaginateInventory.builder();

                        List<ItemStack> previousIngredients = Lists.newArrayList();

                        if (tier != Alchemy.Tier.ONE) {
                            previousIngredients.addAll(PotionConfig.getInstance().getIngredients(tier.toNumerical() - 1));
                        }

                        List<ItemStack> ingredients = Lists.newArrayList(PotionConfig.getInstance().getIngredients(tier.toNumerical()))
                                .stream()
                                .filter(ingredient -> !previousIngredients.contains(ingredient))
                                .collect(Collectors.toList());

                        for (ItemStack ingredient : ingredients) {
                            ItemBuilder ingredientIcon = ItemBuilder.of(ingredient)
                                    .name("&e" + CoreSpigotConstants.TRANSLATE_ITEM.get(ingredient));

                            String[] desc = WordUtils.wrap(PotionConfig.getInstance().getDesc(ingredient), 26)
                                    .split(SystemUtils.LINE_SEPARATOR);

                            for (String d : desc) {
                                ingredientIcon.lore("&7" + d);
                            }

                            ingredientsBuilder.item(ingredientIcon.make(), null);
                        }


                        PaginateInventory inventory = ingredientsBuilder.build("Ingredientes");
                        inventory.backOrCloseItem();

                        e.getWhoClicked().openInventory(inventory);
                    });
                }

                PaginateInventory inventory = builder.build("Tiers");
                inventory.backOrCloseItem();

                event.getWhoClicked().openInventory(inventory);

            };
        }

        if (skillType == SkillType.EXCAVATION) {
            return event -> {
                Multimap<MaterialData, ExcavationTreasure> treasureMap = Excavation.getTreasures();

                PaginateInventory.PaginateInventoryBuilder builder = PaginateInventory.builder();

                Multimap<ExcavationTreasure, MaterialData> icons = HashMultimap.create();

                treasureMap.asMap().forEach((key, value) -> {
                    for (ExcavationTreasure treasure : value) {
                        ItemStack drop = treasure.getDrop();

                        ExcavationTreasure any = icons.keys().stream()
                                .filter(k -> k.getDrop().getType() == drop.getType()
                                        && k.getDrop().getDurability() == drop.getDurability())
                                .findFirst()
                                .orElse(null);

                        if (any == null) {
                            icons.put(treasure, key);
                        } else {
                            icons.put(any, key);
                        }
                    }
                });

                icons.asMap().entrySet()
                        .stream()
                        .sorted(Comparator.comparingInt(o -> o.getKey().getDropLevel()))
                        .forEach(entry -> {
                            ExcavationTreasure treasure = entry.getKey();

                            ItemBuilder i = ItemBuilder.of(treasure.getDrop())
                                    .name("&e" + CoreSpigotConstants.TRANSLATE_ITEM.get(treasure.getDrop()))
                                    .flags(ItemFlag.HIDE_ATTRIBUTES)
                                    .lore(
                                            "&7Nível: &f" + treasure.getDropLevel(),
                                            "&7Chance: &f" + treasure.getDropChance(),
                                            "&7XP: &f" + treasure.getXp()
                                    )
                                    .lore("&7Blocos: ");

                            for (MaterialData data : entry.getValue()) {
                                i.lore(" &f- " + CoreSpigotConstants.TRANSLATE_ITEM.get(new ItemStack(
                                        data.getItemType(), 1, data.getData()
                                )));
                            }

                            builder.item(i.make(), null);
                        });

                PaginateInventory inventory = builder.build("Tesouros");
                inventory.backOrCloseItem();

                event.getWhoClicked().openInventory(inventory);
            };
        }

        return null;
    }

    private ItemStack getSkillIcon(SkillType skillType) {

        int level = this.mcMMOProfile.getSkillLevel(skillType);

        ItemBuilder itemCustom = new ItemBuilder(McMMOUtils.getSkillItemStack(skillType))
                .name("&e" + skillType.getName())
                .lore(SkillsUtils.getDescription(skillType));

        int xp = this.mcMMOProfile.getSkillXpLevel(skillType);
        int xpToLevel = this.mcMMOProfile.getXpToLevel(skillType);

        double progressPercentage = PercentageUtils.getPercentage(xpToLevel, xp);

        itemCustom
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
                .lore(" ",
                        String.format(
                                "&fNível atual: &a%s",
                                level
                        ));

        if (level >= skillType.getMaxLevel()) {
            itemCustom.lore("&bNível máximo atingido.");
        } else {
            itemCustom.lore(String.format(
                    "%s &a%s&7/&a%s &8(%.0f%%)",
                    PercentageUtils.getProgressBar(xp, xpToLevel, 25, "▍", "▍", "&a", "&7"),
                    xp,
                    xpToLevel,
                    progressPercentage
            ));
        }

        List<String> rewardLore = SkillsUtils.getLevelDescription(skillType, level, true);

        if (!rewardLore.isEmpty()) {
            itemCustom.lore("").lore(rewardLore.toArray(new String[0]));
        }

        return itemCustom.build();
    }


    private ItemStack getTotalLevelIcon() {

        ItemBuilder itemCustom = ItemBuilder.of(HeadTexture.getPlayerHead(user.getNick()));

        Integer rank = this.skills.get(null);
        // PlayerStat top = McMMORanksTask.getTopSkill(null);

        itemCustom.name(this.user.getHighestGroup().getDisplayTag(this.user.getNick()))
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .lore("&fNível total: &a" + getPowerLevel());

        float bonusVip = BoosterManager.getVIPMultiplier(user);

        if (bonusVip > 0) {
            itemCustom.lore(String.format(
                    "&fBônus %s: &a%.1f",
                    user.getHighestGroup().getColor() + user.getHighestGroup().getDisplayName(),
                    bonusVip
            ));
        }

        List<Booster> boosters = SkillsProvider.Cache.Local.BOOSTERS.provide().get(user).values()
                .stream()
                .filter(booster -> !booster.isExpired())
                .sorted(Comparator.comparing(Booster::getSkill))
                .collect(Collectors.toCollection(LinkedList::new));

        if (boosters.isEmpty()) {
            itemCustom.lore("&fBoosters: &8Nenhum");
        } else {
            itemCustom.lore("&fBoosters: &a(Experiência em dobro)");

            boosters.forEach(booster -> {
                        itemCustom.lore(String.format(
                                " &7\u25AA &e%s: &b%s restantes.",
                                booster.getSkill().getName(),
                                getTimeLeft(booster.getDuration())
                        ));
                    }
            );
        }

        itemCustom.lore("&fPosição no rank geral: &e" + (rank == null ? "Indefinido." : rank + "º"));

        return itemCustom.make();
    }

    private ItemStack getRankInventoryIcon() {

        ItemBuilder itemCustom = new ItemBuilder(Material.ARMOR_STAND);

        itemCustom
                .name(ChatColor.YELLOW + "Rank de Habilidades")
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .lore("&7Clique para abrir o Rank de Habilidades.");

        return itemCustom.make();
    }

    public int getPowerLevel() {
        int powerLevel = 0;

        for (SkillType type : SkillType.NON_CHILD_SKILLS()) {
            if (Bukkit.getPlayerExact(user.getNick()) != null ? type.getPermissions(Bukkit.getPlayerExact(user.getNick())) : true) {
                powerLevel += mcMMOProfile.getSkillLevel(type);
            }
        }

        return powerLevel;
    }

    private String getTimeLeft(long millis) {
        StringBuilder sb = new StringBuilder();
        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long hours, minutes, seconds, aux = totalSeconds;

        if (totalSeconds >= 3600) {
            hours = totalSeconds / 3600;
            aux = aux - (hours * 3600);
            sb.append(hours).append("h");
        }
        if (aux >= 60) {
            minutes = aux / 60;
            aux = aux - (minutes * 60);
            sb.append(minutes).append("m");
        }
        if (aux >= 0) {
            seconds = aux;
            sb.append(seconds).append("s");
        }
        return sb.toString();
    }

}
