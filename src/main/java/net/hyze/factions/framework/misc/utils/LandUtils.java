package net.hyze.factions.framework.misc.utils;

import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.hyze.core.shared.CoreProvider;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.events.LandOverrideGetEvent;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.Land;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LandUtils {

    public static boolean is(@NonNull String appId, int x, int z, @NonNull Zone.Type... types) {
        Zone zone = FactionsProvider.Cache.Local.LANDS.provide().get(appId, x, z, Zone.class);
        return zone != null && Arrays.asList(types).contains(zone.getType());
    }

    public static boolean is(Land land, Zone.Type... types) {
        return is(CoreProvider.getApp().getId(), land.getChunkX(), land.getChunkZ(), types);
    }

    public static boolean is(int x, int z, Zone.Type... types) {
        return is(CoreProvider.getApp().getId(), x, z, types);
    }

    public static boolean is(@NonNull Location location, Zone.Type... types) {
        return is(location.getChunk().getX(), location.getChunk().getZ(), types);
    }

    public static boolean canBuildAt(@NonNull FactionUser user, @NonNull String appId, int x, int z) {

        if (user.getOptions().isAdminModeEnabled()) {
            return true;
        }

        Land land = FactionsProvider.Cache.Local.LANDS.provide().get(appId, x, z);

        if (land == null) {
            return true;
        }

        if (land instanceof Zone) {
            return ((Zone) land).getType().isBuildEnabled();
        }

        if (land instanceof Claim) {
            Claim claim = (Claim) land;

            if (claim.getFaction() == null) {
                return true;
            }

            if (claim.isContested()) {
                return canUseContestedClaim(user, claim);
            }

            return FactionPermission.BUILD.allows(claim.getFaction(), user);
        }

        return false;
    }

    public static boolean canBuildAt(FactionUser user, int x, int z) {
        return canBuildAt(user, CoreProvider.getApp().getId(), x, z);
    }

    public static boolean canBuildAt(FactionUser user, @NonNull Location location) {
        return canBuildAt(user, location.getChunk().getX(), location.getChunk().getZ());
    }

    public static boolean canAccessContainerAt(FactionUser user, @NonNull Location location) {
        return canAccessContainerAt(user, location.getChunk().getX(), location.getChunk().getZ());
    }

    public static boolean canAccessContainerAt(FactionUser user, int x, int z) {
        return canAccessContainerAt(user, CoreProvider.getApp().getId(), x, z);
    }

    public static boolean canAccessContainerAt(@NonNull FactionUser user, @NonNull String appId, int x, int z) {

        if (user.getOptions().isAdminModeEnabled()) {
            return true;
        }

        Land land = FactionsProvider.Cache.Local.LANDS.provide().get(appId, x, z);

        if (land == null) {
            return true;
        }

        if (land instanceof Claim) {
            Claim claim = (Claim) land;

            if (claim.getFaction() == null) {
                return true;
            }

            if (claim.isContested()) {
                return canUseContestedClaim(user, claim);
            }

            return FactionPermission.ACCESS_CONTAINERS.allows(claim.getFaction(), user);
        }

        if (land instanceof Zone) {
            Zone zone = (Zone) land;

            return zone.getType().isBuildEnabled();
        }

        return false;
    }

    public static boolean canAccessBeaconAt(FactionUser user, @NonNull Location location) {
        return canAccessBeaconAt(user, location.getChunk().getX(), location.getChunk().getZ());
    }

    public static boolean canAccessBeaconAt(FactionUser user, int x, int z) {
        return canAccessBeaconAt(user, CoreProvider.getApp().getId(), x, z);
    }

    public static boolean canAccessBeaconAt(@NonNull FactionUser user, @NonNull String appId, int x, int z) {

        if (user.getOptions().isAdminModeEnabled()) {
            return true;
        }

        Land land = FactionsProvider.Cache.Local.LANDS.provide().get(appId, x, z);

        if (land == null) {
            return true;
        }

        if (land instanceof Claim) {
            Claim claim = (Claim) land;

            if (claim.isContested()) {
                return canUseContestedClaim(user, claim);
            }

            return FactionPermission.ACCESS_BEACON.allows(claim.getFaction(), user);
        }

        if (land instanceof Zone) {
            Zone zone = (Zone) land;

            return zone.getType().isBuildEnabled();
        }

        return false;
    }

    public static boolean canActivateRedstoneAt(FactionUser user, @NonNull Location location) {
        return canActivateRedstoneAt(user, location.getChunk().getX(), location.getChunk().getZ());
    }

    public static boolean canActivateRedstoneAt(FactionUser user, int x, int z) {
        return canActivateRedstoneAt(user, CoreProvider.getApp().getId(), x, z);
    }

    public static boolean canActivateRedstoneAt(@NonNull FactionUser user, @NonNull String appId, int x, int z) {

        if (user.getOptions().isAdminModeEnabled()) {
            return true;
        }

        Land land = FactionsProvider.Cache.Local.LANDS.provide().get(appId, x, z);

        if (land == null) {
            return true;
        }

        if (land instanceof Claim) {
            Claim claim = (Claim) land;

            if (claim.isContested()) {
                return canUseContestedClaim(user, claim);
            }

            return FactionPermission.ACTIVATE_REDSTONE.allows(claim.getFaction(), user);
        }

        if (land instanceof Zone) {
            Zone zone = (Zone) land;

            return zone.getType().isBuildEnabled();
        }

        return false;
    }

    public static boolean canUseContestedClaim(FactionUser user, Claim claim) {

        if (user.getOptions().isAdminModeEnabled()) {
            return true;
        }

        if (claim.isContested()) {
            FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

            if (relation == null) {
                return false;
            }

            return relation.getFaction().equals(claim.getContestant()) || FactionUtils.isAlly(relation.getFaction(), claim.getContestant());
        }

        return false;
    }

    public static Set<FactionUser> getUsersInsideClaims(Faction... factions) {
        Set<Claim> claims = Sets.newHashSet();

        for (Faction faction : factions) {
            claims.addAll(FactionsProvider.Cache.Local.LANDS.provide().get(faction));
        }

        return getUsersInsideLands(claims.stream().toArray(Claim[]::new));
    }

    public static Set<FactionUser> getUsersInsideLands(Land... lands) {

        World world = Bukkit.getWorld("world");

        if (world == null) {
            return Sets.newHashSet();
        }

        CraftWorld craftWorld = (CraftWorld) world;

        if (craftWorld.getHandle() == null) {
            return Collections.emptySet();
        }

        return Stream.of(lands)
                .filter(Objects::nonNull)
                .map(land -> craftWorld.getHandle().getChunkIfLoaded(land.getChunkX(), land.getChunkZ()))
                .filter(Objects::nonNull)
                .map(craftChunk -> craftChunk.bukkitChunk)
                .filter(Objects::nonNull)
                .map(Chunk::getEntities)
                .flatMap(Stream::of)
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .map(FactionsProvider.Cache.Local.USERS.provide()::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static Claim getClaim(Location location) {
        return getClaim(CoreProvider.getApp().getId(), location);
    }
    
    public static Claim getClaim(String appId, Location location) {
        return FactionsProvider.Cache.Local.LANDS.provide().get(appId, location.getBlockX() >> 4, location.getBlockZ() >> 4, Claim.class);
    }

    public static Zone getZone(Location location) {

        LandOverrideGetEvent event = new LandOverrideGetEvent(location);

        Bukkit.getServer().getPluginManager().callEvent(event);

        Land land = event.getResult();

        if (land != null) {
            return event.getResult();
        }

        return FactionsProvider.Cache.Local.LANDS.provide().get(location.getBlockX() >> 4, location.getBlockZ() >> 4, Zone.class);
    }

    public static Set<Claim> getPermanentClaims(Faction faction) {
        return FactionsProvider.Cache.Local.LANDS.provide().get(faction).stream()
                .filter(claim -> !claim.isTemporary())
                .collect(Collectors.toSet());
    }

    public static Set<Claim> getTemporaryClaims(Faction faction) {
        return FactionsProvider.Cache.Local.LANDS.provide().get(faction).stream()
                .filter(Claim::isTemporary)
                .collect(Collectors.toSet());
    }
}
