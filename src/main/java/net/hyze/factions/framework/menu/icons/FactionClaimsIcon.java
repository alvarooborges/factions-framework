package net.hyze.factions.framework.menu.icons;

import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.LandState;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FactionClaimsIcon extends MenuIcon {

    public FactionClaimsIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.COMPASS)
                .name("&aTerras &8(/f terras)")
                .lore(
                        "&7Acesse informações sobre as",
                        "&7terras de sua facção.",
                        ""
                );

        if (FactionPermission.COMMAND_BASE.allows(user.getRelation().getFaction(), user)) {
            builder.lore("&eClique para visualizar.");
        } else {
            builder.lore("&cSem permissão.");
        }

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {

        if (!FactionPermission.COMMAND_BASE.allows(user.getRelation().getFaction(), user)) {
            return null;
        }

        return () -> {
            Optional<Faction> optional0 = Optional.ofNullable(user.getRelation()).map(FactionUserRelation::getFaction);

            if (!optional0.isPresent()) {
                Message.ERROR.send(user.getPlayer(), "Você não precisa fazer parte de uma facção");
                user.getPlayer().closeInventory();
                return;
            }

            if (!FactionPermission.COMMAND_BASE.allows(user.getRelation().getFaction(), user)) {
                return;
            }

            Set<Claim> claims = FactionsProvider.Cache.Local.LANDS.provide().get(optional0.get());

            PaginateInventory.PaginateInventoryBuilder inventory = PaginateInventory.builder();

            for (Claim claim : claims) {
                LandState state = LandState.get(user, claim);

                Material material = Material.MYCEL;
                if (state == LandState.TEMPORARY_CLAIM) {
                    material = Material.ICE;
                } else if (state == LandState.UNDER_ATTACK_CLAIM) {
                    material = Material.TNT;
                }

                ItemBuilder icon = ItemBuilder.of(material);

                icon.name(state.getChatColor() + state.getName())
                        .lore("&fLocalização:")
                        .lore("&7X: " + (claim.getChunkX() * 16))
                        .lore("&7Z: " + (claim.getChunkZ() * 16));

                if (state.equals(LandState.TEMPORARY_CLAIM)) {
                    long endAt = claim.getCreatedAt().getTime() + (FactionsProvider.getSettings().getTemporaryClaimMinutes() * 60000L);

                    icon.lore(
                            "",
                            "A proteção acaba em:",
                            TimeCode.toText(endAt - System.currentTimeMillis(), 2)
                    );
                }

                inventory.item(icon.make(), null);
            }

            CustomInventory customInventory = inventory.build("Terras da sua facção");
            customInventory.backOrCloseItem();

            user.getPlayer().openInventory(customInventory);
        };
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
