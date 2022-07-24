package net.hyze.factions.framework.misc.spybook;

import com.google.common.collect.Lists;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.ranking.FactionRankIcon;
import net.hyze.factions.framework.ranking.RankingFactory;
import net.hyze.factions.framework.ranking.factions.ValuationRanking;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SpyBookItem extends CustomItem {

    public SpyBookItem() {
        super("spy-book");
    }

    @Override
    protected ItemBuilder getItemBuilder() {
        return ItemBuilder.of(Material.BOOK)
                .name(getDisplayName())
                .lore(
                        "&7Utilize este item para receber as",
                        "&7coordenadas de uma das facções",
                        "&7do top 50 do ranking de valor.",
                        "",
                        "&eComo utilizar?",
                        "&fPara utilizar este item, basta clicar",
                        "&fcom o botão direito do mouse",
                        "&fenquanto estiver com ele na mão."
                );
    }

    @Override
    public String getDisplayName() {
        return "&6Livro de Localização";
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }

        event.setCancelled(true);

        InventoryUtils.subtractOneOnHand(event.getPlayer());

        ValuationRanking ranking = (ValuationRanking) RankingFactory.FACTIONS_VALUATION_RANKING.getRanking();
        List<FactionRankIcon<ValuationRanking.RankValue>> items = ranking.getItems();

        User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

        LinkedList<FactionRankIcon<ValuationRanking.RankValue>> top = items.stream()
                .limit(50)
                .filter(icon -> relation == null || !icon.getFaction().equals(relation.getFaction()))
                .collect(Collectors.toCollection(LinkedList::new));

        if (top.isEmpty()) {
            Message.ERROR.send(player, "Nenhuma facção foi encontrada.");
            return;
        }

        Collections.shuffle(top);

        Faction faction = null;
        Claim claim = null;

        for (FactionRankIcon<ValuationRanking.RankValue> icon : top) {
            faction = icon.getFaction();
            Set<Claim> claims = LandUtils.getPermanentClaims(faction);

            if (!claims.isEmpty()) {
                claim = claims.iterator().next();
                break;
            }
        }

        if (faction == null || claim == null) {
            Message.ERROR.send(player, "Nenhuma facção foi encontrada.");
            return;
        }

        List<IChatBaseComponent> pages = Lists.newArrayList();

        String string = "§6§lLivro §6§lde §6§lLocalização\n" +
                "\n" +
                "§0A dica de localização da " +
                "§0facção §l%s §0é:\n" +
                "§lX: §0%s\n" +
                "§lZ: §0%s\n" +
                "\n" +
                "§0§oEste livro foi criado\n§0§oem: §7§o%s";

        int posX = claim.getChunkX() << 4;
        int posZ = claim.getChunkZ() << 4;

        String tipX;
        if (posX > 0) {
            tipX = "É um número positivo";
        } else {
            tipX = "É um número negativo";
        }

        String tipZ;
        if (posZ > 0) {
            tipZ = "É um número positivo";
        } else {
            tipZ = "É um número negativo";
        }

        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        ComponentBuilder page = new ComponentBuilder(
                String.format(
                        string,
                        claim.getFaction().getTag(),
                        tipX,
                        tipZ,
                        date
                )
        );

        pages.add(IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(new TextComponent(page.create()))));

        ItemStack bookItemStack = new ItemStack(Material.WRITTEN_BOOK);

        BookMeta bookMeta = (BookMeta) bookItemStack.getItemMeta();
        bookMeta.setTitle("...");
        bookMeta.setAuthor("...");

        CraftMetaBook craftMetaBook = (CraftMetaBook) bookMeta;

        craftMetaBook.pages = pages;

        bookItemStack.setItemMeta(bookMeta);

        event.getPlayer().getInventory().addItem(
                new ItemBuilder(bookItemStack)
                        .clearLores()
                        .name("&6Livro de Localização: " + claim.getFaction().getTag())
                        .lore("&7Data de criação: " + date)
                        .make()
        );

    }

}
