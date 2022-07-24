package net.hyze.factions.framework.ranking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.faction.Faction;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class FactionRankIcon<T> extends RankIcon<T> {

    @Getter
    private final Faction faction;

    @Getter
    private final T element;

    @Getter
    private final ItemStack icon;

}
