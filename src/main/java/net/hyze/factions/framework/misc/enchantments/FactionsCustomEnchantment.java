package net.hyze.factions.framework.misc.enchantments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.data.*;
import net.hyze.hyzeskills.misc.enchantments.data.IntelligenceCustomEnchantment;

@Getter
@RequiredArgsConstructor
public enum FactionsCustomEnchantment {

    DRAGON_LORE(new DragonLoreCustomEnchantment()),
    BATTLE_IMPETUS(new BattleImpetusCustomEnchantment()),
    EXECUTION(new ExecutionCustomEnchantment()),
    FURY(new FuryCustomEnchantment()),
    THIRST_BLOOD(new ThirstBloodCustomEnchantment()),
    RICOCHET(new RicochetCustomEnchantment()),
    BURST(new BurstCustomEnchantment()),
    EXPLOSION_RESISTANCE(new ExplosionResistanceCustomEnchantment()),
    TOUGHNESS(new ToughnessCustomEnchantment()),
    BRUTE_FORCE(new BruteForceCustomEnchantment()),
    DIVINE_PROTECTION(new DivineProtectionCustomEnchantment()),
    MYSTIC_PRISON(new MysticPrisonCustomEnchantment()),
    OVERLOAD(new OverloadCustomEnchantment()),
    CRITICAL_IMPACT(new CriticalImpactCustomEnchantment()),
    TIRELESS_PURSUIT(new TirelessPursuitCustomEnchantment()),
    SUPER_AREA(new SuperAreaCustomEnchantment()),
    AUTO_REPAIR(new AutoRepairCustomEnchantment()),
    INTELLIGENCE(new IntelligenceCustomEnchantment()),
    RETURN(new ReturnCustomEnchantment()),
    SOULS_USURPER(new SoulsUsurperCustomEnchantment()),
    CONFUSION(new ConfusionCustomEnchantment()),
    HANDCUFFS(new HandCuffsCustomEnchantment()),
    RAPTURE(new RaptureCustomEnchantment());

    private final CustomEnchantment enchantment;
}
