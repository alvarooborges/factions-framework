package net.hyze.factions.framework.user.stats;

import com.google.common.collect.Sets;
import lombok.*;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.world.location.unserializer.BukkitLocationParser;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import org.bukkit.Location;

import java.security.InvalidParameterException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class UserStats {

    private static Set<AppType> ALLOWED_BACK_APPS = Sets.newHashSet(
            AppType.FACTIONS_WORLD,
            AppType.FACTIONS_END,
            AppType.FACTIONS_SPAWN,
            AppType.FACTIONS_VIP,
            AppType.FACTIONS_TESTS,
            AppType.FACTIONS_MINE,
            AppType.FACTIONS_EXCAVATION
    );

    private final Integer userId;

    private int power;
    private int additionalMaxPower;
    private int civilDeaths;
    private int neutralDeaths;
    private int civilKills;
    private int neutralKills;

    @Setter(AccessLevel.NONE)
    private SerializedLocation backLocation;

    public int get(Field field) {
        switch (field) {
            case POWER:
                return power;
            case ADDITIONAL_MAX_POWER:
                return additionalMaxPower;
            case CIVIL_DEATHS:
                return civilDeaths;
            case NEUTRAL_DEATHS:
                return neutralDeaths;
            case CIVIL_KILLS:
                return civilKills;
            case NEUTRAL_KILLS:
                return neutralKills;
        }

        throw new InvalidParameterException("Invalid stats field.");
    }

    public int increment(Field field) {
        switch (field) {
            case POWER:
                return ++power;
            case ADDITIONAL_MAX_POWER:
                return ++additionalMaxPower;
            case CIVIL_DEATHS:
                return ++civilDeaths;
            case NEUTRAL_DEATHS:
                return ++neutralDeaths;
            case CIVIL_KILLS:
                return ++civilKills;
            case NEUTRAL_KILLS:
                return ++neutralKills;
        }

        throw new InvalidParameterException("Invalid stats field.");
    }

    public int decrement(Field field) {
        switch (field) {
            case POWER:
                return --power;
            case ADDITIONAL_MAX_POWER:
                return --additionalMaxPower;
            case CIVIL_DEATHS:
                return --civilDeaths;
            case NEUTRAL_DEATHS:
                return --neutralDeaths;
            case CIVIL_KILLS:
                return --civilKills;
            case NEUTRAL_KILLS:
                return --neutralKills;
        }

        throw new InvalidParameterException("Invalid stats field.");
    }

    public int getTotalMaxPower() {
        return this.getAdditionalMaxPower() + FactionsProvider.getSettings().getMaxPower();
    }

    public int getTotalKills() {
        return this.getCivilKills() + this.getNeutralKills();
    }

    public int getTotalDeaths() {
        return this.getCivilDeaths() + this.getNeutralDeaths();
    }

    public double getKDR() {
        int totalKills = this.getTotalKills();
        int totalDeaths = this.getTotalDeaths();

        if (totalKills == 0 && totalDeaths == 0) {
            return 0;
        }

        if (totalKills == totalDeaths) {
            return 1;
        }

        if (totalDeaths == 0) {
            return totalKills;
        }

        return totalKills / totalDeaths;
    }

    public void setBackLocation(Location location) {
        String ownAppId = FactionsPlugin.getInstance().getOwnerAppResolver().apply(location);

        if (ownAppId == null) {
            return;
        }

        if (!Objects.equals(CoreProvider.getApp().getId(), ownAppId)) {
            return;
        }

        if (ALLOWED_BACK_APPS.contains(CoreProvider.getApp().getType())) {
            backLocation = BukkitLocationParser.serialize(ownAppId, location);
            FactionsProvider.Repositories.USER_STATS.provide().updateBackLocation(this);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum Field {
        POWER("power"),
        ADDITIONAL_MAX_POWER("additional_max_power"),
        CIVIL_DEATHS("civil_deaths"),
        NEUTRAL_DEATHS("neutral_deaths"),
        CIVIL_KILLS("civil_kills"),
        NEUTRAL_KILLS("neutral_kills");

        private final String columnName;
    }
}
