package net.hyze.factions.framework.misc.utils;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.echo.packets.SendMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.misc.utils.BannerAlphabet;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.FactionDefineBasePacket;
import net.hyze.factions.framework.echo.packets.permission.FactionAllyPermissionUpdatedPacket;
import net.hyze.factions.framework.echo.packets.permission.FactionRolePermissionUpdatedPacket;
import net.hyze.factions.framework.echo.packets.permission.FactionUserPermissionUpdatedPacket;
import net.hyze.factions.framework.events.FactionSetBaseEvent;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.user.stats.UserStats;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FactionUtils {

    public static String formatKDR(double kdr) {
        return new DecimalFormat("#0.00").format(kdr);
    }

    public static int getMaxPower(@NonNull Faction faction) {
        Set<FactionUserRelation> relations = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByFaction(faction);

        return FactionsProvider.Cache.Local.USERS.provide()
                .getAllByIds(relations.stream().map(FactionUserRelation::getUserId).collect(Collectors.toSet()))
                .values()
                .stream()
                .map(FactionUser::getStats)
                .map(stats -> stats.getAdditionalMaxPower() + FactionsProvider.getSettings().getMaxPower())
                .mapToInt(Integer::intValue).sum();
    }

    public static int getPower(@NonNull Faction faction) {
        Set<FactionUserRelation> relations = FactionsProvider.Cache.Local.USERS_RELATIONS.provide()
                .getByFaction(faction);

        int size = relations.size();

        relations = relations.stream()
                .filter(relation -> {
                    // if (size >= FactionsProvider.getSettings().getFactionMaxMembers() / 2) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -20);

                    // Só conta poder de jogadores com mais de 20min
                    return relation.getSince().before(calendar.getTime());
                    //}

                    //return true;
                })
                .collect(Collectors.toSet());

        return FactionsProvider.Cache.Local.USERS.provide()
                .getAllByIds(
                        relations.stream()
                                .map(FactionUserRelation::getUserId)
                                .collect(Collectors.toSet())
                )
                .values()
                .stream()
                .map(FactionUser::getStats)
                .map(UserStats::getPower)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public static Integer countClaims(@NonNull Faction faction) {
        return FactionsProvider.Cache.Local.LANDS.provide()
                .get(faction)
                .size();
    }

    public static boolean isAlly(@NonNull User handle, @NonNull Faction faction) {
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(handle);

        if (relation == null) {
            return false;
        }

        return isAlly(relation.getFaction(), faction);
    }

    public static boolean isMember(@NonNull User handle, @NonNull Faction faction) {
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(handle);

        return relation != null && relation.getFaction().equals(faction);
    }

    public static boolean isAlly(@NonNull Faction factionA, @NonNull Faction factionB) {
        if (!FactionsProvider.getSettings().isAllowAlly()) {
            return false;
        }

        Set<Faction> allies = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(factionA, FactionRelation.Type.ALLY);

        return allies.stream()
                .anyMatch(ally -> ally.equals(factionB));
    }

    public static boolean isAlly(@NonNull FactionUser userA, @NonNull FactionUser userB) {
        if (!FactionsProvider.getSettings().isAllowAlly()) {
            return false;
        }

        FactionUserRelation relationA = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(userA);
        FactionUserRelation relationB = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(userB);

        return relationA != null && relationB != null && isAlly(relationA.getFaction(), relationB.getFaction());
    }

    public static boolean isNeutral(@NonNull Faction factionA, @NonNull Faction factionB) {
        return !isAlly(factionA, factionB);
    }

    public static boolean isSame(@NonNull FactionUser userA, @NonNull FactionUser userB) {
        FactionUserRelation relationA = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(userA);
        FactionUserRelation relationB = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(userB);

        return relationA != null && relationB != null && Objects.equals(relationA.getFaction(), relationB.getFaction());
    }

    public static boolean isHostile(@NonNull FactionUser userA, @NonNull FactionUser userB) {
        if (userA.equals(userB)) {
            return false;
        }

        FactionUserRelation relationA = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(userA);
        FactionUserRelation relationB = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(userB);

        if (relationA == null || relationB == null) {
            return true;
        }

        if (relationA.getFaction().equals(relationB.getFaction())) {
            return false;
        }

        if (isAlly(relationA.getFaction(), relationB.getFaction())) {
            return FactionsProvider.getSettings().isAllyFire();
        }

        return true;
    }

    public static FactionUser getLeader(@NonNull Faction faction) {
        Set<FactionUserRelation> users = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByFaction(faction);

        FactionUserRelation leaderRelation = users.stream().filter(relation -> relation.getRole() == FactionRole.LEADER).findFirst().get();

        return FactionsProvider.Cache.Local.USERS.provide().get(leaderRelation.getUserId());
    }

    public static Set<FactionUser> getUsers(@NonNull Faction faction) {
        Set<FactionUserRelation> relations = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByFaction(faction);

        return relations.stream().map(FactionUserRelation::getUser).collect(Collectors.toSet());
    }

    public static Set<FactionUser> getUsers(@NonNull Faction faction, FactionRole... roles) {
        return getUsers(faction, false, roles);
    }

    public static Set<FactionUser> getUsers(@NonNull Faction faction, boolean onlineOnly, FactionRole... roles) {
        return getUsers(faction, onlineOnly, false, roles);
    }

    public static void broadcast(Faction faction, String message, FactionRole... roles) {
        broadcast(faction, TextComponent.fromLegacyText(message), false, roles);
    }

    public static void broadcast(Faction faction, BaseComponent[] components, FactionRole... roles) {
        broadcast(faction, components, false, roles);
    }

    public static void broadcast(Faction faction, BaseComponent[] components, boolean local, FactionRole... roles) {
        if (local) {
            getUsers(faction, true, true, roles).stream()
                    .map(FactionUser::getPlayer)
                    .filter(Objects::nonNull)
                    .filter(Player::isOnline)
                    .forEach(player -> player.sendMessage(components));
        } else {
            CoreProvider.Redis.ECHO.provide().publish(new SendMessagePacket(
                    getUsers(faction).stream().map(FactionUser::getId).collect(Collectors.toSet()),
                    components
            ));
        }
    }

    public static Set<FactionUser> getUsers(@NonNull Faction faction, boolean onlineOnly, boolean inAppOnly, FactionRole... roles) {
        Set<FactionUserRelation> relations;

        if (onlineOnly) {
            relations = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByFaction(faction);
        } else {
            relations = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByFaction(faction);
        }

        if (roles == null || roles.length == 0) {
            roles = FactionRole.values();
        }

        final List<FactionRole> roleList = Arrays.asList(roles);

        relations = relations.stream()
                .filter(relation -> roleList.contains(relation.getRole()))
                .collect(Collectors.toSet());

        Set<FactionUser> out = relations.stream()
                .map(relation -> FactionsProvider.Cache.Local.USERS.provide().get(relation.getUserId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!onlineOnly) {
            return out;
        } else if (inAppOnly) {
            return out.stream()
                    .filter(user -> {
                        Player player = Bukkit.getPlayerExact(user.getNick());

                        return player != null && player.isOnline();
                    })
                    .collect(Collectors.toSet());
        }

        Set<User> handles = out.stream().map(FactionUser::getHandle).collect(Collectors.toSet());

        Map<User, AppStatus> statuses = CoreProvider.Cache.Redis.USERS_STATUS.provide().getBukkitApp(handles);

        return statuses.entrySet().stream()
                .filter(entry -> {
                    AppStatus status = entry.getValue();

                    return status != null && status.getServer() == FactionsProvider.getServer();
                })
                .map(entry -> FactionsProvider.Cache.Local.USERS.provide().get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static ItemBuilder getHead(@NonNull FactionUser user) {
        ItemBuilder builder = ItemBuilder.of(HeadTexture.getPlayerHead(user.getNick()), true);

        UserStats stats = user.getStats();
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

        Group group = user.getHandle().getHighestGroup();

        builder.name(group.getDisplayTag() + " " + user.getNick());

//        builder.lore("&fCoins: &7" + Currency.COINS.format(EconomyAPI.get(user.getHandle(), Currency.COINS)));
        if (relation != null) {
            builder.lore("&fFacção: &7" + relation.getFaction().getDisplayName());
            builder.lore("&fCargo: &7" + relation.getRole().getDisplayName());
        } else {
            builder.lore("&fFacção: &7Sem facção");
        }

        builder.lore("");
        builder.lore("&fPoder: &a" + String.format("%s/%s", stats.getPower(), stats.getTotalMaxPower()));
        builder.lore("&fKDR: &7" + FactionUtils.formatKDR(stats.getKDR()));
        builder.lore("");
        builder.lore("&a▲ Abates:");
        builder.lore(" &fCivil: &7" + stats.getCivilKills());
        builder.lore(" &fNeutro: &7" + stats.getNeutralKills());
        builder.lore(" &fTotal: &7" + stats.getTotalKills());
        builder.lore("");
        builder.lore("&c▼ Mortes:");
        builder.lore(" &fCivil: &7" + stats.getCivilDeaths());
        builder.lore(" &fNeutro: &7" + stats.getNeutralDeaths());
        builder.lore(" &fTotal: &7" + stats.getTotalDeaths());

        return builder;
    }

    public static ItemBuilder getBanner(@NonNull Faction faction) {
        return getBanner(faction, (Faction) null);
    }

    public static ItemBuilder getBanner(@NonNull Faction faction, FactionUser user) {
        if (user != null) {
            FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

            if (relation != null) {
                return getBanner(faction, relation.getFaction());
            }
        }

        return getBanner(faction, (Faction) null);
    }

    public static ItemBuilder getBanner(@NonNull Faction faction, Faction other) {
        ItemBuilder builder = new ItemBuilder(Material.BANNER)
                .flags(ItemFlag.HIDE_POTION_EFFECTS);

        BannerAlphabet bannerAlphabet = BannerAlphabet.getBanner(faction.getTag().charAt(0));

        DyeColor backgroundColor = DyeColor.SILVER;
        String colorCode = "&7";

        if (bannerAlphabet != null) {

            if (other != null && Objects.equals(faction, other)) {
                backgroundColor = DyeColor.ORANGE;
                colorCode = "&6";
            } else if (other != null && isAlly(faction, other)) {
                backgroundColor = DyeColor.LIME;
                colorCode = "&a";
            }
//            else if (other != null && isEnemy(faction, other)) {
//                backgroundColor = DyeColor.RED;
//                colorCode = "&c";
//            }

            builder.patterns(bannerAlphabet.buildPatterns(backgroundColor, DyeColor.BLACK));
        }

        builder.dyeColor(backgroundColor);

        builder.name(String.format("%s[%s] %s", colorCode, faction.getTag(), faction.getName()));

        return builder;
    }

    public static boolean canTeleportTo(FactionUser user, Location location) {
        if (user.getHandle().hasStrictGroup(Group.MODERATOR)) {
            return true;
        }

        /**
         * Utilizado para bloquear o /back.
         */
        /*
        if (AppType.FACTIONS_SPAWN.isCurrent() || AppType.FACTIONS_VIP.isCurrent()) {
            return false;
        }
         */

        Zone zone = LandUtils.getZone(location);

        //zone.getType() != Zone.Type.WAR && zone.getType() != Zone.Type.VOID
        if (zone != null && zone.getType() != Zone.Type.NEUTRAL) {
            return false;
        }

        Claim claim = LandUtils.getClaim(location);

        if (claim == null) {
            return true;
        }

        return FactionPermission.PERSONAL_HOME.allows(claim.getFaction(), user);
    }

    public static boolean setFactionHome(FactionUser user, Faction faction, SerializedLocation location) {
        FactionSetBaseEvent factionSetBaseEvent = new FactionSetBaseEvent(user, location);

        Bukkit.getServer().getPluginManager().callEvent(factionSetBaseEvent);

        if (factionSetBaseEvent.isCancelled()) {
            return false;
        }

        faction.setHome(location);

        FactionsProvider.Repositories.FACTIONS.provide().update(faction);

        CoreProvider.Redis.ECHO.provide().publish(new FactionDefineBasePacket(faction, user.getId(), location));

        return true;
    }

    public static ItemBuilder getInformationIcon(Faction faction, FactionUser user) {
        ItemBuilder builder = FactionUtils.getBanner(faction, user)
                .name("&e" + faction.getStrippedDisplayName());


        for (String info : getInfos(faction)) {
            builder.lore(info);
        }

        return builder;
    }

    public static String[] getInfos(Faction faction) {
        Set<Claim> permanentClaims = LandUtils.getPermanentClaims(faction);

        Set<BaseComponent[]> onlineUsers = FactionUtils.getUsers(faction, true)
                .stream()
                .map(FactionUserUtils::getChatComponents)
                .collect(Collectors.toSet());

        Set<FactionUser> users = FactionUtils.getUsers(faction);

        /*
        double kdr = users.stream()
                .mapToDouble(u -> u.getStats().getKDR())
                .sum();

        int totalKills = users.stream()
                .mapToInt(u -> u.getStats().getTotalKills())
                .sum();

        int totalDeaths = users.stream()
                .mapToInt(u -> u.getStats().getTotalDeaths())
                .sum();

        int civilDeaths = users.stream()
                .mapToInt(u -> u.getStats().getCivilDeaths())
                .sum();

        int neutralDeaths = users.stream()
                .mapToInt(u -> u.getStats().getNeutralDeaths())
                .sum();

        int civilKills = users.stream()
                .mapToInt(u -> u.getStats().getCivilKills())
                .sum();

        int neutralKills = users.stream()
                .mapToInt(u -> u.getStats().getNeutralKills())
                .sum();

         */

        User leader = FactionUtils.getLeader(faction).getHandle();

        String allyName = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(faction, FactionRelation.Type.ALLY)
                .stream()
                .map(Faction::getDisplayName)
                .map(name -> ChatColor.GRAY + MessageUtils.stripColor(name))
                .findFirst()
                .orElse(null); //como só tem um aliado...

        List<String> infos = Lists.newLinkedList();

        infos.add(" ");
        infos.add("&fLíder: " + leader.getHighestGroup().getColor() + leader.getNick());

        appendUsersByRole(infos, users, FactionRole.CAPTAIN);
        appendUsersByRole(infos, users, FactionRole.MEMBER);
        appendUsersByRole(infos, users, FactionRole.RECRUIT);

        infos.add(" ");
        infos.add("&eInformações gerais");
        infos.add("&fAliança: &7" + (allyName == null ? "Nenhum" : allyName));
        infos.add(String.format("&fIntegrantes: &7%s/%s", onlineUsers.size(), users.size()));
        infos.add("&fPoder: &a" + String.format("%s/%s", FactionUtils.getPower(faction), FactionUtils.getMaxPower(faction)));
        infos.add(String.format("&fTerras: &a%s", permanentClaims.size()));

//        infos.add("&fKDR: &a" + FactionUtils.formatKDR(kdr));
//        infos.add("");
//        infos.add("&a▲ Abates:");
//        infos.add(" &fCivil: &7" + civilKills);
//        infos.add(" &fNeutro: &7" + neutralKills);
//        infos.add(" &fTotal: &7" + totalKills);
//        infos.add("");
//        infos.add("&c▼ Mortes:");
//        infos.add(" &fCivil: &7" + civilDeaths);
//        infos.add(" &fNeutro: &7" + neutralDeaths);
//        infos.add(" &fTotal: &7" + totalDeaths);

        return infos.toArray(new String[0]);
    }

    private static void appendUsersByRole(List<String> infos, Set<FactionUser> users, FactionRole role) {
        List<FactionUser> usersByRole = users.stream()
                .filter(factionUser -> factionUser.getRelation().getRole() == role)
                .collect(Collectors.toList());

        String prefix = ChatColor.WHITE + role.getDisplayPluralName() + ": &7";

        if (usersByRole.isEmpty()) {
            infos.add(prefix + "&7Nenhum");
            return;
        }

        int size = usersByRole.size();

        StringBuilder firstLine = new StringBuilder();
        StringBuilder secondLine = new StringBuilder();

        AtomicInteger index = new AtomicInteger(1);

        Consumer<FactionUser> consumer = targetUser -> {
            String colorAndDisplayName = targetUser.getHandle().getHighestGroup().getColor() + targetUser.getNick();

            if (size > 3 && index.getAndIncrement() > 3) {
                secondLine.append(colorAndDisplayName)
                        .append(targetUser.isOnline() ? ChatColor.GREEN : ChatColor.RED)
                        .append(" • ");
            } else {
                firstLine.append(colorAndDisplayName)
                        .append(targetUser.isOnline() ? ChatColor.GREEN : ChatColor.RED)
                        .append(" • ");
            }

        };

        usersByRole.forEach(consumer);

        infos.add(prefix + firstLine);
        if (secondLine.length() != 0) {
            infos.add(secondLine.toString());
        }
    }

    public static void updateUserPermission(FactionUserPermissionUpdatedPacket packet) {
        if (packet.getValue() > -1) {
            FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().putByUser(packet.getFaction(), packet.getUserId(), packet.getValue());
        } else {
            FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().removeByUser(packet.getFaction(), packet.getUserId());
        }
    }

    public static void updateRolePermission(FactionRolePermissionUpdatedPacket packet) {
        if (packet.getValue() > -1) {
            FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().putByRole(packet.getFaction(), packet.getRole(), packet.getValue());
        } else {
            FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().removeByRole(packet.getFaction(), packet.getRole());
        }
    }

    public static void updateAllyPermission(FactionAllyPermissionUpdatedPacket packet) {
        if (packet.getValue() > -1) {
            FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().putByAlly(packet.getFaction(), packet.getAllyId(), packet.getValue());
        } else {
            FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().removeByAlly(packet.getFaction(), packet.getAllyId());
        }
    }
}
