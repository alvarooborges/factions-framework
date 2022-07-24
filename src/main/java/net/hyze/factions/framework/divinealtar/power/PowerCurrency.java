package net.hyze.factions.framework.divinealtar.power;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@Getter
@RequiredArgsConstructor
public enum PowerCurrency {

    COINS(
            "Moeda",
            "Moedas",
            ChatColor.GREEN
    ),
    CASH(
            "Cash",
            "Cash",
            ChatColor.GOLD
    ),
    GEM(
            "Gema",
            "Gemas",
            ChatColor.AQUA
    );

    private final String singular;
    private final String plural;
    private final ChatColor color;

    public String format(int amount) {
        return String.format("%s %s", amount, (amount > 1 ? this.plural : this.singular));
    }

}
