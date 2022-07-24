package net.hyze.factions.framework.misc.tablist;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Charsets;
import net.hyze.core.shared.user.User;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.hyze.core.shared.misc.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"user"})
public class InfoData {

    private final User user;
    private PlayerInfoData data;

    public static InfoData fromUser(User user) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + user.getNick()).getBytes(Charsets.UTF_8));
        WrappedGameProfile profile = new WrappedGameProfile(uuid, user.getNick());
        EnumWrappers.NativeGameMode gamemode = EnumWrappers.NativeGameMode.fromBukkit(GameMode.SURVIVAL);

        Player player = Bukkit.getPlayerExact(user.getNick());

        if (player != null && player.isOnline()) {
            gamemode = EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode());
        }

        return new InfoData(user, new PlayerInfoData(profile, RandomUtils.randomInt(120, 180), gamemode, null));
    }
}