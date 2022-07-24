package net.hyze.factions.framework.war.clock.phases.impl;

import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.factions.framework.echo.packets.WarTogglePacket;
import net.hyze.factions.framework.war.War;
import net.hyze.factions.framework.war.clock.phases.AbstractWarPhase;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class WarPhaseAnnounce extends AbstractWarPhase {

    public WarPhaseAnnounce() {
        super("Anúncio", 120, false);
    }

    @Override
    public void onStart() {

        World world = Bukkit.getWorld(War.CONFIG.getSpawn().getWorldName());
        WorldBorder border = world.getWorldBorder();

        border.setCenter(0.5, 0.5);
        border.setSize(160);

        War.OPEN = true;

        CoreProvider.Redis.ECHO.provide().publish(
                new WarTogglePacket(War.OPEN)
        );

    }

    @Override
    public void onMeantime(Integer second) {

        switch (second) {
            case 119:
            case 60:
            case 30:
            case 15:
                String message = MessageUtils.translateColorCodes("\n&6&lEVENTO GUERRA!"
                        + "\n&eUtilize &f/guerra &epara participar."
                        + String.format("\n&eO evento começa em &f%s&e.", TimeCode.toText(((long) second) * 1000L, 5))
                        + "\n ");

                CoreProvider.Redis.ECHO.provide().publish(
                        BroadcastMessagePacket.builder()
                                .groups(Sets.newHashSet(Group.DEFAULT))
                                .components(TextComponent.fromLegacyText(message))
                                .build()
                );
                break;
        }

    }

    @Override
    public void onEnd() {

        War.OPEN = false;

        CoreProvider.Redis.ECHO.provide().publish(
                new WarTogglePacket(War.OPEN)
        );

    }

}
