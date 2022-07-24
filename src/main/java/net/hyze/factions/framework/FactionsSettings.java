package net.hyze.factions.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.*;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.Position;
import net.hyze.core.shared.providers.MongoDatabaseProvider;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.providers.RedisProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import net.hyze.core.spigot.world.location.unserializer.BukkitLocationParser;
import net.hyze.factions.framework.echo.packets.UserSpokePacket;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@Getter
@Builder
public class FactionsSettings {

    @Builder.Default
    private final int maxPower = 3;

    @Builder.Default
    private final int maxAdditionalMaxPower = 7;

    @Builder.Default
    private final long powerUpdateDelay = TimeUnit.SECONDS.toMillis(10);

    // Positions and Locations

    @NonNull
    private final Position shopPosition;

    //
    @Singular("enableBackLocation")
    private final Set<AppType> backLocationEnabledAt;

    @Singular("enableActionBar")
    private final Set<AppType> actionBarEnabledAt;

    @Singular("disableDefaultScoreboard")
    private final Set<AppType> defaultScoreboardDisabledAt;

    private Map<String, Set<WorldCuboid>> warZones;

    private Map<String, Set<WorldCuboid>> lostfortressZones;

    private Map<String, Set<WorldCuboid>> protectedZones;

    private Map<String, Set<WorldCuboid>> noClaimZones;


    private List<UserSpokePacket.Chat> disabledChats;

    private SerializedLocation arenaLocation;

    @NonNull
    private MysqlDatabaseProvider mysqlDatabaseProvider;

    @NonNull
    private MongoDatabaseProvider mongoDatabaseProvider;

    @NonNull
    private RedisProvider redisProvider;

    /**
     * Numero maximo de jogadores por facção
     */
    @NonNull
    private Integer factionMaxMembers;

    /**
     * Preço do claim temporario
     */
    @Builder.Default
    private double temporaryClaimPrice = 100000d;

    /**
     * Tempo em minutos que um claim temporario
     */
    @Builder.Default
    private final int temporaryClaimMinutes = 60;

    /**
     * Limite de claims temporarios
     */
    @Builder.Default
    private final int temporaryClaimLimit = 2;

    /**
     * Tamanho do mapa para worldborder em chunks
     */
    @Builder.Default
    private final int worldSize = 50;

    /**
     * Se o servidor vai ter o sistema de ofertas
     */
    @Setter
    private boolean offerSystemEnabled;


    @Setter
    @Builder.Default
    private boolean globalTablistEnabled = true;

    @NonNull
    private WorldCuboid spawnerProtectedZoneCuboid;

    @Setter
    @Builder.Default
    private boolean syncWorldTime = true;

    @Setter
    @Builder.Default
    private boolean handleQueue = false;

    @Setter
    @Builder.Default
    private Group canUseChatColor = Group.ARCANE;

    @Setter
    @Builder.Default
    private boolean explosionsEnabled = true;

    @Setter
    @Builder.Default
    private boolean allyFire = false;

    @Setter
    @Builder.Default
    private boolean spawnersManagerActive = true;

    @Setter
    @Builder.Default
    private int allyLimit = 1;

    @Setter
    @Builder.Default
    private boolean allowAlly = true;

    @Setter
    @Builder.Default
    private boolean allowRankCommand = true;

    @Setter
    @Builder.Default
    private boolean bankEnabled = false;

    @Setter
    @Builder.Default
    private boolean allowInventoryHolderOutOfLands = true;

    @Builder.Default
    private final SpawnerMode spawnerMode = SpawnerMode.BREAK_COOLDOWN;

    @Builder.Default
    private final List<SpawnerType> enabledSpawners = Lists.newArrayList();

    @Builder.Default
    private final boolean autoRemoveSpawnersEnabled = false;

    @Builder.Default
    private final MinePVPMode minePVPMode = MinePVPMode.AWAYS_DISABLE;

    @Getter
    @RequiredArgsConstructor
    public enum SpawnerMode {

        BREAK_COOLDOWN(true, false),
        UNDER_ATTACK(false, true),
        BOTH(true, true);

        private final boolean breakCooldownEnabled;
        private final boolean underAttackEnabled;

    }

    public enum MinePVPMode {
        AWAYS_DISABLE, AWAYS_ACTIVE, DAY_DISABLE_NIGHT_ACTIVE;

        public boolean isCurrent() {
            return this.equals(FactionsProvider.getSettings().minePVPMode);
        }
    }

    public static class FactionsSettingsBuilder {

        private final Map<String, Set<WorldCuboid>> warZones = Maps.newHashMap();
        private final Map<String, Set<WorldCuboid>> lostfortressZones = Maps.newHashMap();
        private final Map<String, Set<WorldCuboid>> protectedZones = Maps.newHashMap();
        private final Map<String, Set<WorldCuboid>> noClaimZones = Maps.newHashMap();
        private final List<UserSpokePacket.Chat> disabledChats = Lists.newArrayList();
        private SerializedLocation spawnLocation;
        private BiFunction<Player, User, SerializedLocation> respawnLocationBiFunction = (p, u) -> spawnLocation.clone();
        private BiFunction<Player, User, Location> limboLocationBiFunction = (p, u) -> spawnLocation.parser(new BukkitLocationParser());

        public FactionsSettingsBuilder disabledChats(UserSpokePacket.Chat... chats) {
            Stream.of(chats).forEach(chat -> disabledChats.add(chat));
            return this;
        }

        public FactionsSettingsBuilder warZones(@NonNull String appId, @NonNull WorldCuboid... cuboids) {
            Set<WorldCuboid> zones = warZones.getOrDefault(appId, Sets.newHashSet());

            zones.addAll(Sets.newHashSet(cuboids));

            warZones.put(appId, zones);

            return this;
        }

        public FactionsSettingsBuilder lostfortressZones(@NonNull String appId, @NonNull WorldCuboid... cuboids) {
            Set<WorldCuboid> zones = lostfortressZones.getOrDefault(appId, Sets.newHashSet());

            zones.addAll(Sets.newHashSet(cuboids));

            lostfortressZones.put(appId, zones);

            return this;
        }

        public FactionsSettingsBuilder protectedZones(@NonNull String appId, @NonNull WorldCuboid... cuboids) {
            Set<WorldCuboid> zones = protectedZones.getOrDefault(appId, Sets.newHashSet());

            zones.addAll(Sets.newHashSet(cuboids));

            protectedZones.put(appId, zones);

            return this;
        }

        public FactionsSettingsBuilder noClaimZones(@NonNull String appId, @NonNull WorldCuboid... cuboids) {
            Set<WorldCuboid> zones = noClaimZones.getOrDefault(appId, Sets.newHashSet());

            zones.addAll(Sets.newHashSet(cuboids));

            noClaimZones.put(appId, zones);

            return this;
        }
    }
}
