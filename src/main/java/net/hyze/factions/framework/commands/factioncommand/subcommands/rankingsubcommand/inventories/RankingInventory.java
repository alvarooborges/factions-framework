package net.hyze.factions.framework.commands.factioncommand.subcommands.rankingsubcommand.inventories;

import com.google.common.collect.Lists;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.ranking.FactionRankIcon;
import net.hyze.factions.framework.ranking.RankIcon;
import net.hyze.factions.framework.ranking.Ranking;
import net.hyze.factions.framework.ranking.RankingFactory;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class RankingInventory extends PaginateInventory {

    private static final List<Ranking<?>> rankings = Lists.<Ranking<?>>newArrayList(
            RankingFactory.FACTIONS_VALUATION_RANKING.getRanking(),
            RankingFactory.FACTIONS_WEEKLY_TYCOON_RANKING.getRanking(),
            RankingFactory.FACTIONS_DAILY_TYCOON_RANKING.getRanking(),
            RankingFactory.FACTIONS_COINS_RANKING.getRanking(),
            RankingFactory.FACTIONS_SPAWNERS_RANKING.getRanking(),
            RankingFactory.FACTIONS_INVASION_PROFIT_RANKING.getRanking(),
            RankingFactory.FACTIONS_KDR_RANKING.getRanking()
    );

    public RankingInventory(FactionUser user, int currentIndex) {
        this(user, currentIndex, false);
    }

    private final FactionUser user;
    private int currentIndex;
    private boolean onlineFactions;

    public RankingInventory(FactionUser user, int currentIndex, boolean onlineFactions) {
        super("Liga de Facções");

        this.user = user;
        this.currentIndex = currentIndex;
        this.onlineFactions = onlineFactions;
    }

    @Override
    public void onOpen(InventoryOpenEvent event0) {
        build();

        super.onOpen(event0);
    }

    private void build() {
        clearItems();
        clearMenus();
        resetCurrentPage();

        Ranking<?> currentRank = rankings.get(currentIndex);
        Collection<RankIcon> icons = currentRank.getItems();

        Consumer<RankIcon> consumer = icon -> {
            ItemStack i = icon.getIcon();

            if (icon instanceof FactionRankIcon) {
                ItemBuilder banner = FactionUtils.getBanner(((FactionRankIcon) icon).getFaction(), user);

                i = ItemBuilder.of(i)
                        .patterns(banner.patterns())
                        .dyeColor(banner.dyeColor())
                        .make();
            }

            addItem(i, icon::onClick);
        };

        if (onlineFactions) {
            icons.stream()
                    .filter(icon -> {
                        Faction faction = ((FactionRankIcon<?>) icon).getFaction();
                        Set<FactionUser> users = FactionUtils.getUsers(faction, true, false);
                        return users.size() > 0;
                    })
                    .forEach(consumer);
        } else {
            icons.forEach(consumer);
        }

        ItemBuilder icon = ItemBuilder.of(currentRank.getIcon())
                .name("&aFiltrar por liga")
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .lore(" ");

        for (Ranking<?> ranking : rankings) {
            if (currentRank == ranking) {
                icon.lore("&b▶ " + ranking.getName());
            } else {
                icon.lore("&8" + ranking.getName());
            }
        }

        icon.lore(" ", "&eClique para alternar o liga");

        backOrCloseItem(49);

        addMenu(
                48,
                icon.make(),
                event -> {
                    currentIndex = nextIndex(currentIndex);
                    build();
                    drawPage();
                }
        );

        ItemBuilder onlineFilterIcon = ItemBuilder.of(Material.INK_SACK)
                .durability(onlineFactions ? 9 : 8)
                .name("&aMostrar apenas facções online")
                .lore("&fStatus: " + (onlineFactions ? "&aativo" : "&cdesativado"))
                .flags(ItemFlag.HIDE_ATTRIBUTES);

        addMenu(
                50,
                onlineFilterIcon.make(),
                event -> {
                    onlineFactions = !onlineFactions;
                    build();
                    drawPage();
                }
        );
    }

    private static int nextIndex(int index) {
        if (index >= rankings.size() - 1) {
            return 0;
        }

        return ++index;
    }
}
