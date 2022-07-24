package net.hyze.factions.framework.skills;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.hyze.core.shared.misc.utils.Plural;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.hyzeskills.HyzeSkillsPlugin;
import net.hyze.hyzeskills.config.AdvancedConfig;
import net.hyze.hyzeskills.config.skills.alchemy.PotionConfig;
import net.hyze.hyzeskills.datatypes.player.McMMOPlayer;
import net.hyze.hyzeskills.datatypes.skills.SecondaryAbility;
import net.hyze.hyzeskills.datatypes.skills.SkillType;
import net.hyze.hyzeskills.datatypes.treasure.ExcavationTreasure;
import net.hyze.hyzeskills.skills.alchemy.Alchemy;
import net.hyze.hyzeskills.skills.alchemy.AlchemyManager;
import net.hyze.hyzeskills.skills.axes.Axes;
import net.hyze.hyzeskills.skills.axes.AxesManager;
import net.hyze.hyzeskills.skills.excavation.Excavation;
import net.hyze.hyzeskills.skills.repair.ArcaneForging;
import net.hyze.hyzeskills.skills.repair.Repair;
import net.hyze.hyzeskills.skills.repair.RepairManager;
import net.hyze.hyzeskills.skills.repair.repairables.Repairable;
import net.hyze.hyzeskills.util.player.UserManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillsUtils {

    public static List<String> getLevelDescription(SkillType skillType, int level,
                                                   boolean showNextInfo) {
        List<String> out = Lists.newLinkedList();

        double maxLevel = skillType.getMaxLevel();

        if (skillType == SkillType.MINING) {
            buildLore(out, "Minérios em dobro", "de chance", showNextInfo, SecondaryAbility.MINING_DOUBLE_DROPS, level, maxLevel);

            double maxSmeltChance = SkillsConstants.MINING_SMELT_MAX_CHANCE;

            double currentSmeltChance = (maxSmeltChance / maxLevel) * Math.min(level, maxLevel);
            double nextSmeltChance = (maxSmeltChance / maxLevel) * Math.min(Math.min(level + 1, maxLevel), maxSmeltChance);

            out.add("");
            buildLore(out, "Minérios derretidos", "de chance", level, nextSmeltChance, currentSmeltChance, showNextInfo);

            return out;
        }

        if (skillType == SkillType.EXCAVATION) {
            double maxDoubleDropMaxChance = SkillsConstants.EXCAVATION_TREASURE_DOUBLE_DROP_MAX_CHANCE;

            double currentDoubleDropChance = (maxDoubleDropMaxChance / maxLevel) * Math.min(level, maxLevel);
            double nextDoubleDropChance = (maxDoubleDropMaxChance / maxLevel) * Math.min(Math.min(level + 1, maxLevel), maxDoubleDropMaxChance);

            buildLore(out, "Tesouros em dobro", "de chance", level, nextDoubleDropChance, currentDoubleDropChance, showNextInfo);

            Multimap<MaterialData, ExcavationTreasure> treasureMap = Excavation.getTreasures();

            Multimap<MaterialData, MaterialData> drops = HashMultimap.create();

            for (Map.Entry<MaterialData, Collection<ExcavationTreasure>> dropEntry : treasureMap.asMap().entrySet()) {

                MaterialData block = dropEntry.getKey();
                Collection<ExcavationTreasure> treasures = dropEntry.getValue();

                for (ExcavationTreasure treasure : treasures) {
                    if (treasure.getDropLevel() == level) {
                        drops.put(treasure.getDrop().getData(), block);
                    }
                }
            }

            if (!drops.isEmpty()) {
                out.add("");
                out.add("&fDrops: ");

                for (Map.Entry<MaterialData, Collection<MaterialData>> entry : drops.asMap().entrySet()) {
                    out.add(String.format(
                            "  &8▪ &7%s &8(%s)",
                            CoreSpigotConstants.TRANSLATE_ITEM.get(entry.getKey().toItemStack(1)),
                            entry.getValue().stream()
                                    .map(b -> b.toItemStack(1))
                                    .map(CoreSpigotConstants.TRANSLATE_ITEM::get)
                                    .collect(Collectors.joining(", "))
                    ));
                }
            }

            return out;
        }

        if (skillType == SkillType.HERBALISM) {
            buildLore(out, "Colheita em dobro", "de chance", showNextInfo, SecondaryAbility.HERBALISM_DOUBLE_DROPS, level, maxLevel);
            out.add("");
            buildLore(out, "Replantação", "de chance", showNextInfo, SecondaryAbility.GREEN_THUMB_PLANT, level, maxLevel);

            return out;
        }

        if (skillType == SkillType.ACROBATICS) {
            buildLore(out, "Rolar", "de chance", showNextInfo, SecondaryAbility.ROLL, level, maxLevel);
            out.add("");
            buildLore(out, "Rolar perfeitamente", "de chance", showNextInfo, SecondaryAbility.GRACEFUL_ROLL, level, maxLevel);
            out.add("");
            buildLore(out, "Esquiva", "de chance", showNextInfo, SecondaryAbility.DODGE, level, maxLevel);

            return out;
        }

        if (skillType == SkillType.SWORDS) {
            buildLore(out, "Sangramento", "de chance", showNextInfo, SecondaryAbility.BLEED, level, maxLevel);
            out.add("");
            buildLore(out, "Contra-Ataque", "de chance", showNextInfo, SecondaryAbility.COUNTER, level, maxLevel);

            return out;
        }

        if (skillType == SkillType.AXES) {
            buildLore(out, "Ataque crítico", "de chance", showNextInfo, SecondaryAbility.CRITICAL_HIT, level, maxLevel);

            out.add("");

            int impactDamage = level / Axes.impactIncreaseLevel;
            int nextImpactDamage = (int) (Math.min(level + 1, maxLevel) / Axes.impactIncreaseLevel);

            buildLore(out, "Impacto em armaduras", Plural.of(impactDamage, "ponto adicional", "pontos adicionais"), level,
                    nextImpactDamage, impactDamage, showNextInfo, "", true);

            out.add("");
            buildLore(out, "Dano bônus", "de chance", showNextInfo, SecondaryAbility.AXE_MASTERY, level, maxLevel);

            return out;
        }

        if (skillType == SkillType.ALCHEMY) {

            double speedPerLevel = Alchemy.catalysisMaxSpeed / Alchemy.catalysisMaxBonusLevel;

            double currentSpeed = (level * speedPerLevel);
            double nextSpeed = (Math.min(level + 1, maxLevel) * speedPerLevel);

            buildLore(out, "Velocidade de criação", "de bônus", level,
                    nextSpeed, currentSpeed, showNextInfo, "x");

//            AlchemyManager alchemyManager = mcMMOPlayer.getAlchemyManager();

            for (Alchemy.Tier tier : Alchemy.Tier.values()) {
                if (level == tier.getLevel()) {
                    List<ItemStack> list = PotionConfig.getInstance().getIngredientsOnly(tier.toNumerical());

                    if (list.isEmpty()) {
                        break;
                    }

                    out.add("");
                    out.add("&fNovos ingredientes: ");

                    for (ItemStack stack : list) {
                        out.add(String.format(
                                "  &8▪ &7%s",
                                CoreSpigotConstants.TRANSLATE_ITEM.get(stack)
                        ));
                    }
                    break;
                }
            }

            return out;
        }

        if (skillType == SkillType.REPAIR) {

            buildLore(out, "Super Reparo", "de chance", showNextInfo, SecondaryAbility.SUPER_REPAIR, level, maxLevel);

            double repairMasteryMaxBonus = Repair.repairMasteryMaxBonus;
            int repairMasteryMaxBonusLevel = Repair.repairMasteryMaxBonusLevel;

            double currentMastery = Math.min(((repairMasteryMaxBonus / repairMasteryMaxBonusLevel) * level), repairMasteryMaxBonus);
            double nextMastery = Math.min(((repairMasteryMaxBonus / repairMasteryMaxBonusLevel) * Math.min(level + 1, maxLevel)), repairMasteryMaxBonus);

            out.add("");
            buildLore(out, "Maestria", "de durabilidade extra", level, nextMastery, currentMastery, showNextInfo);


            Repairable diamondRepairable = HyzeSkillsPlugin.getRepairableManager().getRepairable(Material.DIAMOND_PICKAXE);
            Repairable goldRepairable = HyzeSkillsPlugin.getRepairableManager().getRepairable(Material.GOLD_PICKAXE);
            Repairable ironRepairable = HyzeSkillsPlugin.getRepairableManager().getRepairable(Material.IRON_PICKAXE);
            Repairable stoneRepairable = HyzeSkillsPlugin.getRepairableManager().getRepairable(Material.STONE_PICKAXE);

            List<String> repairableMaterials = Lists.newLinkedList();

            if (stoneRepairable.getMinimumLevel() == level) {
                repairableMaterials.add("  &8▪ &9Pedra");
            }

            if (ironRepairable.getMinimumLevel() == level) {
                repairableMaterials.add("  &8▪ &9Ferro");
            }

            if (goldRepairable.getMinimumLevel() == level) {
                repairableMaterials.add("  &8▪ &9Ouro");
            }

            if (diamondRepairable.getMinimumLevel() == level) {
                repairableMaterials.add("  &8▪ &9Diamante");
            }

            if (!repairableMaterials.isEmpty()) {
                out.add("");
                out.add("&fNovos Materiais: ");
                out.addAll(repairableMaterials);
            }

            return out;
        }

        return out;
    }

    private static void buildLore(List<String> lore, String title, String label, boolean showNextInfo,
                                  SecondaryAbility ability, int level, double maxLevel) {
        buildLore(lore, title, label, showNextInfo, ability, level, maxLevel, "%");
    }

    private static void buildLore(List<String> lore, String title, String label, boolean showNextInfo,
                                  SecondaryAbility ability, int level, double maxLevel, String symbol) {

        double maxAbilityLevel = AdvancedConfig.getInstance().getMaxBonusLevel(ability);
        double maxAbilityChance = AdvancedConfig.getInstance().getMaxChance(ability);

        double currentChance = (maxAbilityChance / maxAbilityLevel) * Math.min(level, maxLevel);
        double nextChance = (maxAbilityChance / maxAbilityLevel) * Math.min(Math.min(level + 1, maxLevel), maxAbilityLevel);

        buildLore(lore, title, label, level, nextChance, currentChance, showNextInfo, symbol);
    }

    private static void buildLore(List<String> lore, String title, String label, int level, double nextValue,
                                  double currentValue, boolean showNextInfo) {
        buildLore(lore, title, label, level, nextValue, currentValue, showNextInfo, "%");
    }

    private static void buildLore(List<String> lore, String title, String label, int level, double nextValue,
                                  double currentValue, boolean showNextInfo, String symbol) {
        buildLore(lore, title, label, level, nextValue, currentValue, showNextInfo, symbol, false);
    }

    private static void buildLore(List<String> lore, String title, String label, int level, double nextValue,
                                  double currentValue, boolean showNextInfo, String symbol, boolean noDecimal) {

        lore.addAll(Lists.newArrayList(
                String.format("&f%s:", title),
                showNextInfo && nextValue > currentValue ?
                        String.format(
                                noDecimal ?
                                        " &8▪ &9%.0f%s &7%s &8(+%.0f%s no nível %s)" :
                                        " &8▪ &9%.2f%s &7%s &8(+%.2f%s no nível %s)",
                                currentValue,
                                symbol,
                                label,
                                nextValue - currentValue,
                                symbol,
                                level + 1
                        ) :
                        String.format(
                                noDecimal ?
                                        "  &8▪ &9%.0f%s &7%s" :
                                        "  &8▪ &9%.2f%s &7%s",
                                currentValue,
                                symbol,
                                label
                        )

        ));
    }

    public static String[] getDescription(SkillType skillType) {
        switch (skillType) {
            case MINING:
                return new String[]{
                        "&7Para ganhar experiência nessa",
                        "&7habilidade, você deve quebrar",
                        "&7blocos utilizando uma picareta.",
                        "",
                        "&8(Apenas blocos do mundo de",
                        "&8mineração contam para o ganho",
                        "&8de experiência)",
                };
            case EXCAVATION:
                return new String[]{
                        "&7Para ganhar experiência nessa",
                        "&7habilidade, você deve escavar",
                        "&7utilizando uma pá em sua mão.",
                        "",
                        "&8(Apenas alguns blocos contam",
                        "&8para o ganho de experiência)",
                };
            case HERBALISM:
                return new String[]{
                        "&7Para ganhar experiência nessa",
                        "&7habilidade, você deve colher",
                        "&7plantações.",
                        "",
                        "&8(Não é necessário a utilização",
                        "&8de ferramentas para o ganho de",
                        "&8experiência)",
                };
            case ACROBATICS:
                return new String[]{
                        "&7Para ganhar experiência nessa",
                        "&7habilidade, você deve receber",
                        "&7dano de queda.",
                        "",
                        "&8(Botas encantadas com Peso Pena",
                        "&8multiplicam por 2 a experiência",
                        "&8recebida)",
                };
            case SWORDS:
                return new String[]{
                        "&7Para ganhar experiência nessa",
                        "&7habilidade, você deve atingir",
                        "&7criaturas ou jogadores usando",
                        "&7uma espada.",
                        "",
                        "&8(Espadas encantadas com Afiação",
                        "&8multiplicam a experiência recebida",
                        "&8conforme o nível do encantamento)",
                };
            case AXES:
                return new String[]{
                        "&7Para ganhar experiência nessa",
                        "&7habilidade, você deve atingir",
                        "&7criaturas ou jogadores usando",
                        "&7um machado.",
                        "",
                        "&8(Machados encantados com Afiação",
                        "&8multiplicam a experiência recebida",
                        "&8conforme o nível do encantamento)",
                };
            case ALCHEMY:
                return new String[]{
                        "&7Para ganhar experiência nessa",
                        "&7habilidade, você deve criar",
                        "&7poções ou adicionar ingredientes",
                        "&7em poções já existentes.",
                        "",
                        "&8(Observe que você deve abrir",
                        "&8manualmente o suporte de poções",
                        "&8e inserir cada ingrediente para",
                        "&8o ganho de experiência)",
                };
            case REPAIR:
                return new String[]{
                        "&7Para ganhar experiência nessa",
                        "&7habilidade, você deve reparar",
                        "&7itens utilizando uma bigorna.",
                        "",
                        "&8(A durabilidade restaurada junto",
                        "&8ao tipo de material utilizado,",
                        "&8aumentam o ganho de experiência)"
                };
            default:
                return new String[0];
        }
    }
}
