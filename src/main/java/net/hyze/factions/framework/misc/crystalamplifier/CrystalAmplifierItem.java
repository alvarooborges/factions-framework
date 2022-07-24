package net.hyze.factions.framework.misc.crystalamplifier;

import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.greenrobot.eventbus.Subscribe;

public class CrystalAmplifierItem extends CustomItem {

    public CrystalAmplifierItem() {
        super("crystal-amplifier-item");
    }

    @Override
    protected ItemBuilder getItemBuilder() {
        return ItemBuilder.of(Material.NETHER_STAR)
                .name(getDisplayName())
                .lore(
                        "&7Ao colocar este item em terras da",
                        "&7sua facção, os seus geradores",
                        "&7geram mobs 50% mais rápido pelas",
                        "&7próximas 2 horas.",
                        "",
                        "&eComo utilizar?",
                        "&fBasta colocá-lo em qualquer local",
                        "&fde uma terra da sua facção."
                );
    }

    @Override
    public String getDisplayName() {
        return "&5Cristal Amplificador";
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {

        event.setCancelled(true);

        Player player = event.getPlayer();
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUserId(user.getId());

        if (relation == null) {
            Message.ERROR.send(player, "Você precisa fazer parte de uma facção para utilizar o Cristal Amplificador.");
            return;
        }

        int facId = relation.getFaction().getId();

        if (FactionsProvider.Cache.Local.CRYSTAL_AMPLIFIER.provide().contains(facId)) {
            Message.ERROR.send(player, "&cSua facção não pode ter mais de um Cristal Amplificador ao mesmo tempo.");
            return;
        }

        Block block = event.getClickedBlock().getRelative(event.getBlockFace());

        Claim claim = LandUtils.getClaim(block.getLocation());

        if (claim == null
                || claim.getFaction() == null
                || !claim.getFactionId().equals(relation.getFaction().getId())) {
            Message.ERROR.send(player, "Você só pode colocar o Cristal Amplificador no terreno de sua facção.");
            return;
        }

        if (block.getType() != Material.AIR) {
            return;
        }

        InventoryUtils.subtractOneOnHand(event.getPlayer());

        CrystalAmplifier crystalAmplifier = new CrystalAmplifier(
                relation.getFaction().getId(),
                System.currentTimeMillis() + CrystalAmplifierConstants.DURATION,
                block.getLocation()
        );

        FactionsProvider.Cache.Local.CRYSTAL_AMPLIFIER.provide().put(crystalAmplifier);

        FactionsProvider.Repositories.CRYSTAL_AMPLIFIER.provide().insert(relation.getFaction(), block.getLocation());

        crystalAmplifier.spawn();

    }

}
