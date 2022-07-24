package net.hyze.factions.framework.misc.playerheads;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerHeadsUtils {

    public static ItemStack make(String owner, User killer, Date at) {
        String killerName = killer.getHighestGroup().getColor() + killer.getNick();

        return make(owner, killerName, at.getTime());
    }

    public static ItemStack make(String owner, String killer, long at) {
        ItemBuilder head = ItemBuilder.of(HeadTexture.getPlayerHead(owner));

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        head.name("&6Cabeça de " + owner)
                .lore("&fDropada por: " + killer)
                .lore("&fDropada em: &7" + format.format(new Date(at)));

        head.nbt("player_head:owner", owner);
        head.nbt("player_head:killer", killer);
        head.nbt("player_head:at", at);

        return head.make();
    }
}
