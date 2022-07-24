package net.hyze.factions.framework.furypoints;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class FurySword {

    private final ItemStack item;
    private final int points;
    private final FuryType type;

    private final BiFunction<ItemBuilder, FuryType, Integer> function = (sword, type) -> {
        return (sword.hasNbt(type.name()) ? sword.nbtInt(type.name()) : 0) / type.getPoints();
    };

    public void apply(Player player) {
        ItemBuilder sword = ItemBuilder.of(this.item, true);

        int currentPoints = sword.hasNbt(this.type.name()) ? sword.nbtInt(this.type.name()) : 0;

        List<String> oldLore = new LinkedList<>(sword.lore());

        if (sword.hasNbt(FuryConstants.KEY_LORE)) {
            oldLore.remove(oldLore.size() - 1);
            oldLore.remove(oldLore.size() - 1);
        } else {
            sword.nbt(FuryConstants.KEY_LORE, 0);
        }

        int currentPlayerPoints = this.function.apply(sword, FuryType.FURY_PLAYERS_KEY);
        int currentMobsPoints = this.function.apply(sword, FuryType.FURY_MOBS_KEY);

        int fury = currentPlayerPoints + currentMobsPoints;

        if (fury >= FuryConstants.MAX_FURY_POINTS) {

            player.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 1, 1);
            Message.EMPTY.send(player, String.format("\n&5[Fúria] &dAgora sua espada possui %s.\n ", FuryEffect.ENCARNATION_OF_RUIN.getName()));

            oldLore.add("");
            oldLore.add("&fFúria: &c" + FuryEffect.ENCARNATION_OF_RUIN.getName());

            sword.lore(true, oldLore.toArray(new String[oldLore.size()])).nbt(FuryConstants.KEY_FURY, FuryEffect.ENCARNATION_OF_RUIN.name());

            return;
        }

        oldLore.add("");
        oldLore.add(String.format("&fPontos de Fúria: &c%s/%s", NumberUtils.format(fury), NumberUtils.format(FuryConstants.MAX_FURY_POINTS)));

        String[] lore = new String[oldLore.size()];

        sword.lore(true, oldLore.toArray(lore)).nbt(type.name(), currentPoints + points);

    }

}
