package net.hyze.factions.framework.commands.profilecommand.inventories;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyAPI;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.user.healthpoints.inventories.HealthPointsProgressInventory;
import net.hyze.hyzeskills.util.player.UserManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.Locale;

public class UserProfileInventory extends CustomInventory {

    public UserProfileInventory(Player viewer, FactionUser factionUser) {
        super(5 * 9, "Perfil de " + factionUser.getNick());

        User handle = factionUser.getHandle();

        boolean isOwn = viewer.getName().equals(factionUser.getNick());

        setItem(11, buildInfoIcon(handle, factionUser, isOwn), event -> ((Player) event.getWhoClicked()).performCommand("skills"));

        setItem(14, buildHelpIcon(), event -> {

            ComponentBuilder componentBuilder = new ComponentBuilder("Clique ")
                    .color(net.md_5.bungee.api.ChatColor.YELLOW)
                    .append("AQUI", ComponentBuilder.FormatRetention.NONE)
                    .color(net.md_5.bungee.api.ChatColor.YELLOW)
                    .bold(true)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, CoreConstants.Infos.DISCORD_INVITE_URL))
                    .append(" para ser redirecionado ao nosso Discord.")
                    .bold(false);

            event.getWhoClicked().sendMessage(componentBuilder.create());
        });

        setItem(15, buildFactionIcon(factionUser));

        setItem(30, buildSocialIcon());

        if (isOwn) {
            setItem(29, buildPreferencesIcon(), event -> ((Player) event.getWhoClicked()).performCommand("toggle"));
            setItem(31, buildMailIcon(), event -> ((Player) event.getWhoClicked()).performCommand("correio"));
            setItem(32, buildCollectionsIcon(), event -> ((Player) event.getWhoClicked()).performCommand("collections"));

            setItem(33, buildHeartsIcon(viewer), event -> {
                Player player = (Player) event.getWhoClicked();
                player.openInventory(new HealthPointsProgressInventory(player, () -> new UserProfileInventory(player, factionUser)));
            });
        }

    }

    private ItemStack buildInfoIcon(User handle, FactionUser factionUser, boolean isOwn) {
        Group highestGroup = handle.getHighestGroup();

        int skillsPowerLevel = UserManager.getPlayer(factionUser.getNick()).getPowerLevel();

        FactionUserRelation relation = factionUser.getRelation();

        Calendar loginCalendar = Calendar.getInstance();
        loginCalendar.setTimeInMillis(handle.getCreatedAt().getTime());

        return new ItemBuilder(HeadTexture.getPlayerHead(factionUser.getNick()))
                .name(highestGroup.getColor() + factionUser.getNick())
                .lore(
                        "",
                        (isOwn ? " &eSuas" : "") + " informações:",
                        "  &7▪ &fGrupo: " + highestGroup.getDisplayTag(),
                        "  &7▪ &fCubos: &a" + handle.getCash(),
                        "  &7▪ &fNível em habilidades: &b" + skillsPowerLevel,
                        "  &7▪ &fMoedas: &a" + NumberUtils.format(EconomyAPI.get(handle, Currency.COINS)),
                        "  &7▪ &fFacção: &7" + (relation == null ? "Nenhuma" : relation.getFaction().getDisplayName()),
                        "",
                        " &fCadastrado em: &7" + loginCalendar.get(Calendar.DATE) + " de "
                                + loginCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                                + ". de " + loginCalendar.get(Calendar.YEAR)
                                + ", às " + loginCalendar.get(Calendar.HOUR_OF_DAY) + ":" +
                                String.format("%02d", loginCalendar.get(Calendar.MINUTE)) + ".",
                        "",
                        "&aClique para mais informações",
                        "&asobre suas habilidades."
                )
                .make();
    }

    private ItemStack buildPreferencesIcon() {
        return new ItemBuilder(Material.DIODE)
                .name("&ePreferências")
                .lore(
                        "&7Altere suas preferências da",
                        "&7maneira que melhor desejar.",
                        "",
                        "&aClique para editar."
                )
                .make();
    }

    private ItemStack buildSocialIcon() {
        return new ItemBuilder(Material.FEATHER)
                .name("&eSocial")
                .lore(
                        "&7Acompanhe o seu estado de",
                        "&7relacionamento com outros",
                        "&7jogadores.",
                        "",
                        "&bEm breve!"
                )
                .make();
    }

    private ItemStack buildMailIcon() {
        return new ItemBuilder(Material.STORAGE_MINECART)
                .name("&eCorreio")
                .lore(
                        "&7Clique para acessar os",
                        "&7itens presentes em seu",
                        "&7correio."
                )
                .make();
    }

    private ItemStack buildHeartsIcon(Player player) {

        double currentHearts = player.getMaxHealth() / 2;

        ItemBuilder builder = new ItemBuilder(Material.APPLE)
                .name("&eVida")
                .lore(
                        "&7Conclua objetivos para",
                        "&7receber pontos de vida",
                        "&7como recompensa.",
                        "",
                        String.format("&fVocê possui &c%.1f corações", currentHearts),
                        "",
                        "&aClique para acompanhar seu",
                        "&aprogresso."
                );

        return builder.make();
    }

    private ItemStack buildHelpIcon() {
        return new ItemBuilder(HeadTexture.getTempHead(
                "b4d7cc4dca986a53f1d6b52aaf376dc6acc73b8b287f42dc8fef5808bb5d76"
        ))
                .name("&eAjuda")
                .lore(
                        "&7Assista pequenos tutoriais de",
                        "&7alguns de nossos sistemas.",
                        "",
                        "&aClique para rebecer acesso",
                        "&aao link."
                )
                .make();
    }

    private ItemStack buildFactionIcon(FactionUser user) {
        FactionUserRelation relation = user.getRelation();

        return new ItemBuilder(Material.DIAMOND_SWORD)
                .name(relation == null ? "&cVocê não possui facção." : "&eFacção de " + user.getNick())
                .make();
    }

    private ItemStack buildCollectionsIcon() {
        return new ItemBuilder(Material.PAINTING)
                .name("&eColetas especiais")
                .lore(
                        "&7Visualize coletas especiais",
                        "&7realizadas por você durante",
                        "&7sua jornada no Factions!",
                        "",
                        "&aClique para visualizar."
                )
                .build();
    }

}
