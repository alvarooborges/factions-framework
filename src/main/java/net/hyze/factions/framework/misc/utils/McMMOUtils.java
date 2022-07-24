package net.hyze.factions.framework.misc.utils;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.utils.PlayerUtils;
import net.hyze.hyzeskills.HyzeSkillsPlugin;
import net.hyze.hyzeskills.datatypes.player.McMMOPlayer;
import net.hyze.hyzeskills.datatypes.player.PlayerProfile;
import net.hyze.hyzeskills.datatypes.skills.SkillType;
import net.hyze.hyzeskills.tasks.McMMORanksTask;
import net.hyze.hyzeskills.util.player.UserManager;
import org.apache.commons.io.Charsets;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

public class McMMOUtils {

    public static String getLookingAtNotificationMessage(Player player) {
        Player target = PlayerUtils.getLookingAt(player, 1, 3);

        if (target == null || !player.canSee(target) || player == target) {
            return null;
        }

        if (player.hasMetadata("NPC") || target.hasMetadata("NPC")) {
            return null;
        }

        try {
            User targetUser = CoreProvider.Cache.Local.USERS.provide().get(target.getName());

            McMMOPlayer mcPlayer = UserManager.getPlayer(target);

            if (mcPlayer != null && targetUser != null && !target.getGameMode().equals(GameMode.SPECTATOR)) {

                Integer rank = McMMORanksTask.getPlayerRank(target.getName()).get(null);
                StringBuilder builder = new StringBuilder();

                builder.append(targetUser.getHighestGroup().getDisplayTag(targetUser.getNick()))
                        .append(ChatColor.GRAY)
                        .append(" - ")
                        .append(ChatColor.WHITE)
                        .append("Nível: ")
                        .append(ChatColor.YELLOW)
                        .append(mcPlayer.getPowerLevel());

                if (rank != null) {
                    builder.append(ChatColor.WHITE)
                            .append(" Ranking: ")
                            .append(ChatColor.YELLOW)
                            .append(rank)
                            .append("º");
                }

                return builder.toString();
            }
        } catch (Exception ignore) {

        }

        return null;
    }

    public static boolean hasAnyLevel(String nick, int lvl, SkillType type, SkillType... types) {
        PlayerProfile profile = HyzeSkillsPlugin.getDatabaseManager().loadPlayerProfile(
                nick,
                UUID.nameUUIDFromBytes(("OfflinePlayer:" + nick).getBytes(Charsets.UTF_8)),
                false
        );

        if (profile == null) {
            return false;
        }

        return Stream.concat(Stream.of(type), Arrays.stream(types))
                .anyMatch(t -> profile.getSkillLevel(t) >= lvl);
    }

}
