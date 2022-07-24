package net.hyze.factions.framework.war.clock.phases.impl;

import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.utils.Title;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.settings.SettingsManager;
import net.hyze.factions.framework.war.clock.phases.AbstractWarPhase;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class WarPhaseEnding extends AbstractWarPhase {

    public static Faction FACTION;

    public WarPhaseEnding() {
        super("Pós-Evento", 60, false);
    }

    @Override
    public void onStart() {

        SettingsManager.setExplosionsStatus(true);
        FactionsProvider.getSettings().setAllyFire(false);

        /**
         * Remove todo mundo de combate.
         */
        Bukkit.getOnlinePlayers().forEach(player -> {
            User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());
            CombatManager.untag(user);
        });

        if (FACTION == null) {
            String message = MessageUtils.translateColorCodes("\n&6&lEVENTO GUERRA\n&eSem vencedores. :S\n ");

            CoreProvider.Redis.ECHO.provide().publish(
                    BroadcastMessagePacket.builder()
                    .groups(Sets.newHashSet(Group.DEFAULT))
                    .components(TextComponent.fromLegacyText(message))
                    .build()
            );
            return;
        }

        String message = MessageUtils.translateColorCodes(String.format("\n&6&lEVENTO GUERRA\n&eVencedores: &f%s&e.\n ", FACTION.getDisplayName()));

        CoreProvider.Redis.ECHO.provide().publish(
                BroadcastMessagePacket.builder()
                .groups(Sets.newHashSet(Group.DEFAULT))
                .components(TextComponent.fromLegacyText(message))
                .build()
        );
        Title.builder().title("&a&lVITÓRIA!").fadeIn(20).fadeOut(20).stay(40).build().send();

    }

    @Override
    public void onMeantime(Integer second) {

    }

    @Override
    public void onEnd() {

    }

}
