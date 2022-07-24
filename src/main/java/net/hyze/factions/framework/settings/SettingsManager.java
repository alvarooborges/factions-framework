package net.hyze.factions.framework.settings;

import net.hyze.core.shared.CoreProvider;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.GlobalChatCommand;
import net.hyze.factions.framework.echo.packets.ExplostionsEnabledTogglePacket;
import net.hyze.factions.framework.echo.packets.GlobalChatTogglePacket;

import java.util.Map;

public class SettingsManager {

    public static void onEnable() {
        
    }

    public static void onLoad() {
        Map<String, String> map = CoreProvider.Cache.Redis.SETTINGS.provide()
                .fetchSettingsByServer(FactionsProvider.getServer());

        {
            if (map.containsKey("global_status")) {
                GlobalChatCommand.STATUS = Boolean.parseBoolean(map.get("global_status"));
            }
        }

        {
            if (map.containsKey("explosions_status")) {
                FactionsProvider.getSettings().setExplosionsEnabled(Boolean.parseBoolean(map.get("explosions_status")));
            }
        }

    }

    public static void setExplosionsStatus(boolean value) {
        CoreProvider.Redis.ECHO.provide().publish(new ExplostionsEnabledTogglePacket(value));

        CoreProvider.Cache.Redis.SETTINGS.provide()
                .setSettingsByServer(FactionsProvider.getServer(), "explosions_status", Boolean.toString(value));
    }

    public static void setGlobalStatus(boolean value) {
        CoreProvider.Redis.ECHO.provide().publish(new GlobalChatTogglePacket(value));

        CoreProvider.Cache.Redis.SETTINGS.provide()
                .setSettingsByServer(FactionsProvider.getServer(), "global_status", Boolean.toString(value));
    }

}
