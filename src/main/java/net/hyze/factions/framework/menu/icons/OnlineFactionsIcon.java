package net.hyze.factions.framework.menu.icons;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OnlineFactionsIcon extends MenuIcon {

    public OnlineFactionsIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(HeadTexture.getTempHead("e79add3e5936a382a8f7fdc37fd6fa96653d5104ebcadb0d4f7e9d4a6efc454"))
                .name("&eFacções Online")
                .lore(
                        "&7Veja todas as facções que possuem",
                        "&7pelo menos um membro online!",
                        "",
                        "&eClique para visualizar."
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            PaginateInventory.PaginateInventoryBuilder builder = PaginateInventory.builder();

            Multimap<Faction, User> factionUsers = HashMultimap.create();
            Set<User> onlineUsers = CoreProvider.Cache.Local.USERS.provide()
                    .getOnlineUsersByServer(FactionsProvider.getServer());

            Set<Faction> onlineFactions = onlineUsers.stream()
                    .map(user -> {
                        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

                        if (relation != null && relation.getFaction() != null) {

                            factionUsers.put(relation.getFaction(), user);
                            return relation.getFaction();
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .sorted((f1, f2) -> {
                        int result = Ints.compare(factionUsers.get(f2).size(), factionUsers.get(f1).size());

                        if (result == 0) {
                            return f1.getTag().compareTo(f2.getTag());
                        }

                        return result;
                    })
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            for (Faction faction : onlineFactions) {
                ItemBuilder icon = FactionUtils.getBanner(faction, this.user);

                icon.lore("&7Jogadores Online: &f" + factionUsers.get(faction).size());

                builder.item(icon.make(), null);
            }

            CustomInventory inventory = builder.build("Facções Online");

            inventory.backOrCloseItem();

            user.getPlayer().openInventory(inventory);
        };
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
