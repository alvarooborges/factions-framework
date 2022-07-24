package net.hyze.factions.framework.menu.icons;

import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyAPI;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.user.stats.UserStats;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PersonalInformationIcon extends MenuIcon {

    public PersonalInformationIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        User handle = user.getHandle();

        UserStats stats = user.getStats();

        return  new ItemBuilder(HeadTexture.getPlayerHead(this.user.getNick()))
                .name("&eInformações pessoais")
                .lore(
                        "&fGrupo: " + handle.getHighestGroup().getDisplayTag(),
                        "&fAbates: &7" + stats.getTotalKills() + " &a▲",
                        "&fMortes: &7" + stats.getTotalDeaths() + " &c▼",
                        "&fMoedas: &7" + NumberUtils.format(EconomyAPI.get(handle, Currency.COINS)),
                        "&fPoder: &7" + String.format("%s/%s", stats.getPower(), stats.getTotalMaxPower())
                )
                .build();
    }

    @Override
    public Runnable getRunnable() {
        return null;
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
