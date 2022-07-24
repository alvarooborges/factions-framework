package net.hyze.factions.framework.misc.customitem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.data.*;
import net.hyze.factions.framework.misc.customitem.data.CreeperEggItem;
import net.hyze.factions.framework.misc.customitem.data.MaxPowerItem;
import net.hyze.factions.framework.misc.customitem.data.MooshroomCowEggItem;
import net.hyze.factions.framework.misc.customitem.data.SuperCreeperEggItem;
import net.hyze.factions.framework.misc.spybook.SpyBookItem;
import net.hyze.factions.framework.misc.supportblocks.SupportBlock;
import net.hyze.hyzeskills.booster.SkillBoosterItem;
import net.hyze.obsidiandestroyer.tnts.FakeTNT;

@Getter
@RequiredArgsConstructor
public enum FactionsCustomItem {

    CREEPER_EGG(new CreeperEggItem()),
    SUPER_CREEPER_EGG(new SuperCreeperEggItem()),
    MOOSHROOM_COW_EGG(new MooshroomCowEggItem()),
    LAUNCHER(new LauncherItem()),
    SUPREME_LAUNCHER(new SupremeLauncherItem()),
    PROPELLANT(new PropellantItem()),
    REPAIR_COIN(new RepairCoinItem()),
    EXTRACTION_RUNE(new ExtractionRuneItem()),
    MAXPOWER(new MaxPowerItem()),
    ABSORPTION_PROTECTION_BLOCK(SupportBlock.REGENERATION.getCustomItem()),
    REINFORCEMENT_PROTECTION_BLOCK(SupportBlock.REINFORCEMENT.getCustomItem()),
    REGENERATION_PROTECTION_BLOCK(SupportBlock.ABSORPTION.getCustomItem()),
    SKILL_BOOSTER(new SkillBoosterItem()),
    SPY_BOOK(new SpyBookItem()),
    EYE_GOD(new EyeGod()),
    EXP_BOTTLE(new ExpBottle()),
    FAKE_TNT(new FakeTNT()),
    ;

    private final CustomItem customItem;
}
