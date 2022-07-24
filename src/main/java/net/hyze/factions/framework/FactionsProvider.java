package net.hyze.factions.framework;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import net.hyze.auction.AuctionProvider;
import net.hyze.beacon.BeaconProvider;
import net.hyze.containers.ContainersProvider;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.contracts.Provider;
import net.hyze.core.shared.exceptions.ApplicationAlreadyPreparedException;
import net.hyze.core.shared.exceptions.InvalidApplicationException;
import net.hyze.core.shared.providers.*;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.spigot.misc.playerdata.storage.UserDataRepository;
import net.hyze.economy.EconomyProvider;
import net.hyze.end.EndProvider;
import net.hyze.factions.framework.divinealtar.cache.local.AltarLocalCache;
import net.hyze.factions.framework.divinealtar.storage.DivineAltarRepository;
import net.hyze.factions.framework.faction.cache.local.FactionLocalCache;
import net.hyze.factions.framework.faction.permission.cache.local.FactionPermissionLocalCache;
import net.hyze.factions.framework.faction.permission.storage.FactionPermissionRepository;
import net.hyze.factions.framework.faction.ranking.storage.FactionRankingRepository;
import net.hyze.factions.framework.faction.relation.faction.cache.local.FactionRelationsLocalCache;
import net.hyze.factions.framework.faction.relation.faction.cache.redis.AllyInvitationsRedisCache;
import net.hyze.factions.framework.faction.relation.faction.storage.FactionRelationRepository;
import net.hyze.factions.framework.faction.relation.user.cache.local.FactionUsersRelationsLocalCache;
import net.hyze.factions.framework.faction.relation.user.cache.redis.FactionInvitationsRedisCache;
import net.hyze.factions.framework.faction.relation.user.storage.FactionUserRelationRepository;
import net.hyze.factions.framework.faction.storage.FactionRepository;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.lands.cache.local.LandLocalCache;
import net.hyze.factions.framework.lands.claim.storage.ClaimRepository;
import net.hyze.factions.framework.misc.arena.storage.ArenaRepository;
import net.hyze.factions.framework.misc.crystalamplifier.cache.local.CrystalAmplifierLocalCache;
import net.hyze.factions.framework.misc.crystalamplifier.storage.CrystalAmplifierRepository;
import net.hyze.factions.framework.misc.furnaces.FurnacesLocalCache;
import net.hyze.factions.framework.misc.lostfortress.storage.LostFortressRepository;
import net.hyze.factions.framework.misc.offers.impl.spawners.OfferSpawnerLocalCache;
import net.hyze.factions.framework.misc.offers.storage.OfferRepository;
import net.hyze.factions.framework.misc.tpa.cache.local.TpaAcceptLogLocalCache;
import net.hyze.factions.framework.misc.tpa.storage.TpaLogRepository;
import net.hyze.factions.framework.spawners.cache.local.FactionSpawnerSpawnLocalCache;
import net.hyze.factions.framework.spawners.evolutions.cache.local.EvolutionLocalCache;
import net.hyze.factions.framework.spawners.evolutions.storage.EvolutionRepository;
import net.hyze.factions.framework.spawners.log.cache.local.SpawnerLogLocalCache;
import net.hyze.factions.framework.spawners.log.storage.SpawnerLogRepository;
import net.hyze.factions.framework.spawners.storage.SpawnerRepository;
import net.hyze.factions.framework.user.cache.local.FactionUserLocalCache;
import net.hyze.factions.framework.user.options.cache.redis.UserOptionsRedisCache;
import net.hyze.factions.framework.user.stats.storage.UserStatsRepository;
import net.hyze.homes.HomesProvider;
import net.hyze.hyzeskills.HyzeSkillsProvider;
import net.hyze.kits.KitsProvider;
import net.hyze.market.MarketProvider;
import net.hyze.mysterybox.MysteryBoxProvider;
import net.hyze.personalmail.PersonalMailProvider;
import net.hyze.signshop.SignShopProvider;

import java.util.LinkedList;

public class FactionsProvider {

    public static final LinkedList<Provider<?>> PROVIDERS = Lists.newLinkedList();

    static {
        PROVIDERS.add(Repositories.FACTIONS);
        PROVIDERS.add(Repositories.FACTIONS_RELATIONS);
        PROVIDERS.add(Repositories.USERS_RELATIONS);
        PROVIDERS.add(Repositories.USER_STATS);
        PROVIDERS.add(Repositories.USER_DATA);
        PROVIDERS.add(Repositories.CLAIMS);
        PROVIDERS.add(Repositories.SPAWNERS);
        PROVIDERS.add(Repositories.FACTIONS_PERMISSIONS);
        PROVIDERS.add(Repositories.SPAWNERS_LOG);
        PROVIDERS.add(Repositories.FACTIONS_RANKING);
        PROVIDERS.add(Repositories.OFFERS);
        PROVIDERS.add(Repositories.ARENA);
        PROVIDERS.add(Repositories.ALTAR);
        PROVIDERS.add(Repositories.LOST_FORTRESS);
        PROVIDERS.add(Repositories.CRYSTAL_AMPLIFIER);
        PROVIDERS.add(Repositories.TPA_LOG);
        PROVIDERS.add(Repositories.SPAWNER_EVOLUTIONS);

        PROVIDERS.add(Cache.Local.FACTIONS);
        PROVIDERS.add(Cache.Local.USERS);
        PROVIDERS.add(Cache.Local.FACTIONS_RELATIONS);
        PROVIDERS.add(Cache.Local.USERS_RELATIONS);
        PROVIDERS.add(Cache.Local.LANDS);
        PROVIDERS.add(Cache.Local.FACTIONS_PERMISSIONS);
        PROVIDERS.add(Cache.Local.SPAWNERS_SPAWN);
        PROVIDERS.add(Cache.Local.CRYSTAL_AMPLIFIER);
        PROVIDERS.add(Cache.Local.SPAWNERS_LOG);
        PROVIDERS.add(Cache.Local.OFFER_SPAWNER);
        PROVIDERS.add(Cache.Local.ALTAR);
    }

    private static boolean prepared = false;

    @Getter
    private static Server server;

    @Getter
    private static FactionsSettings settings;

    public static void prepare(@NonNull Server server, @NonNull FactionsSettings settings) throws InvalidApplicationException, ApplicationAlreadyPreparedException {

        if (prepared) {
            throw new ApplicationAlreadyPreparedException("the factions application has already been prepared");
        }

        prepared = true;

        FactionsProvider.server = server;
        FactionsProvider.settings = settings;

        Database.MYSQL_FACTIONS = settings.getMysqlDatabaseProvider();
        Database.MONGO_FACTIONS = settings.getMongoDatabaseProvider();
        Redis.REDIS_FACTIONS = settings.getRedisProvider();

        PROVIDERS.add(Database.MYSQL_FACTIONS);
        PROVIDERS.add(Database.MONGO_FACTIONS);
        PROVIDERS.add(Redis.REDIS_FACTIONS);

        PROVIDERS.forEach(Provider::prepare);

        /*
         * Baixando todos as facções e adicionando no cache local
         */
        Repositories.FACTIONS.provide().fetchAll().forEach(faction -> {
            Cache.Local.FACTIONS.provide().put(faction);

            /*
             * Forçando busca de usuários das facções
             */
            Cache.Local.USERS_RELATIONS.provide().getByFaction(faction);
        });

        /*
         * Baixando relações entre facções
         */
        Repositories.FACTIONS_RELATIONS.provide().fetch()
                .forEach(relation -> Cache.Local.FACTIONS_RELATIONS.provide().put(relation));

        /*
         * Limpa claims temporários.
         *
         * TODOS OS CLAIMS TEMPORARIOS CRIADOS A MAIS DE 60 MINUTOS SERÃO
         * EXCLUIDOS
         */
        Repositories.CLAIMS.provide().deleteExpiredTemporaryClaims();

        /*
         * Baixa claims
         */
        Repositories.CLAIMS.provide().fetch()
                .forEach(claim -> Cache.Local.LANDS.provide().put(claim));

        /*
         * Baixando permissões
         */
        Repositories.FACTIONS_PERMISSIONS.provide().fetch((faction, allyId, value) -> {
            Cache.Local.FACTIONS_PERMISSIONS.provide().putByAlly(faction, allyId, value);
        }, (faction, role, value) -> {
            Cache.Local.FACTIONS_PERMISSIONS.provide().putByRole(faction, role, value);
        }, (faction, userId, value) -> {
            Cache.Local.FACTIONS_PERMISSIONS.provide().putByUser(faction, userId, value);
        });

        settings.getWarZones().forEach((key, value) -> value.forEach(cuboid -> {
            cuboid.getChunks(vector -> {
                Cache.Local.LANDS.provide().put(new Zone(
                        Zone.Type.WAR,
                        key,
                        vector.getBlockX(),
                        vector.getBlockZ()
                ));
            });
        }));

        settings.getProtectedZones().forEach((key, value) -> value.forEach(cuboid -> {
            cuboid.getChunks(vector -> {
                Cache.Local.LANDS.provide().put(new Zone(
                        Zone.Type.PROTECTED,
                        key,
                        vector.getBlockX(),
                        vector.getBlockZ()
                ));
            });
        }));

        settings.getLostfortressZones().forEach((key, value) -> value.forEach(cuboid -> {
            cuboid.getChunks(vector -> {
                Cache.Local.LANDS.provide().put(new Zone(
                        Zone.Type.LOST_FORTRESS,
                        key,
                        vector.getBlockX(),
                        vector.getBlockZ()
                ));
            });
        }));

        settings.getNoClaimZones().forEach((key, value) -> value.forEach(cuboid -> {
            cuboid.getChunks(vector -> {
                Cache.Local.LANDS.provide().put(new Zone(
                        Zone.Type.NEUTRAL,
                        key,
                        vector.getBlockX(),
                        vector.getBlockZ()
                ));
            });
        }));

        EndProvider.prepare(Database.MYSQL_FACTIONS, Redis.REDIS_FACTIONS);
        MysteryBoxProvider.prepare(Database.MYSQL_FACTIONS);
        PersonalMailProvider.prepare(Database.MYSQL_FACTIONS);
        BeaconProvider.prepare(Database.MYSQL_FACTIONS);
        ContainersProvider.prepare(Database.MYSQL_FACTIONS, Redis.REDIS_FACTIONS);
        EconomyProvider.prepare(Database.MYSQL_FACTIONS);
        HomesProvider.prepare(Database.MYSQL_FACTIONS);

        SignShopProvider.prepare(Database.MYSQL_FACTIONS, () -> true);

        MarketProvider.prepare(Database.MYSQL_FACTIONS);
        KitsProvider.prepare(Database.MYSQL_FACTIONS);
        HyzeSkillsProvider.prepare(Database.MYSQL_FACTIONS);

        AuctionProvider.prepare(Database.MYSQL_FACTIONS);

        CoreProvider.setDomainServers(new Server[]{server});

        System.out.printf("Factions definido para %s%n", server.getId());
    }

    public static void shut() {
        PROVIDERS.forEach(Provider::shut);
    }

    public static class Database {

        public static MysqlDatabaseProvider MYSQL_FACTIONS;
        public static MongoDatabaseProvider MONGO_FACTIONS;
    }

    public static class Redis {

        public static RedisProvider REDIS_FACTIONS;
    }

    public static class Repositories {

        public static final MysqlRepositoryProvider<TpaLogRepository> TPA_LOG = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                TpaLogRepository.class
        );

        public static final MysqlRepositoryProvider<FactionRepository> FACTIONS = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                FactionRepository.class
        );

        public static final MysqlRepositoryProvider<FactionUserRelationRepository> USERS_RELATIONS = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                FactionUserRelationRepository.class
        );

        public static final MysqlRepositoryProvider<FactionRelationRepository> FACTIONS_RELATIONS = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                FactionRelationRepository.class
        );

        public static final MysqlRepositoryProvider<UserStatsRepository> USER_STATS = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                UserStatsRepository.class
        );

        public static final MysqlRepositoryProvider<UserDataRepository> USER_DATA = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                UserDataRepository.class
        );

        public static final MysqlRepositoryProvider<ClaimRepository> CLAIMS = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                ClaimRepository.class
        );

        public static final MysqlRepositoryProvider<SpawnerRepository> SPAWNERS = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                SpawnerRepository.class
        );

        public static final MysqlRepositoryProvider<SpawnerLogRepository> SPAWNERS_LOG = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                SpawnerLogRepository.class
        );

        public static final MysqlRepositoryProvider<FactionPermissionRepository> FACTIONS_PERMISSIONS = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                FactionPermissionRepository.class
        );

        public static final MysqlRepositoryProvider<FactionRankingRepository> FACTIONS_RANKING = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                FactionRankingRepository.class
        );

        public static final MysqlRepositoryProvider<OfferRepository> OFFERS = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                OfferRepository.class
        );

        public static final MysqlRepositoryProvider<ArenaRepository> ARENA = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                ArenaRepository.class
        );

        public static MysqlRepositoryProvider<DivineAltarRepository> ALTAR = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                DivineAltarRepository.class
        );

        public static MysqlRepositoryProvider<CrystalAmplifierRepository> CRYSTAL_AMPLIFIER = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                CrystalAmplifierRepository.class
        );

        public static MysqlRepositoryProvider<LostFortressRepository> LOST_FORTRESS = new MysqlRepositoryProvider<>(
                () -> CoreProvider.Database.MYSQL_MAIN,
                LostFortressRepository.class
        );

        public static MysqlRepositoryProvider<EvolutionRepository> SPAWNER_EVOLUTIONS = new MysqlRepositoryProvider<>(
                () -> FactionsProvider.Database.MYSQL_FACTIONS,
                EvolutionRepository.class
        );

    }

    public static class Cache {

        public static class Local {

            public static final LocalCacheProvider<FactionLocalCache> FACTIONS = new LocalCacheProvider<>(
                    new FactionLocalCache()
            );

            public static final LocalCacheProvider<FactionUserLocalCache> USERS = new LocalCacheProvider<>(
                    new FactionUserLocalCache()
            );

            public static final LocalCacheProvider<FactionUsersRelationsLocalCache> USERS_RELATIONS = new LocalCacheProvider<>(
                    new FactionUsersRelationsLocalCache()
            );

            public static final LocalCacheProvider<FactionRelationsLocalCache> FACTIONS_RELATIONS = new LocalCacheProvider<>(
                    new FactionRelationsLocalCache()
            );

            public static final LocalCacheProvider<LandLocalCache> LANDS = new LocalCacheProvider<>(
                    new LandLocalCache()
            );

            public static final LocalCacheProvider<FactionPermissionLocalCache> FACTIONS_PERMISSIONS = new LocalCacheProvider<>(
                    new FactionPermissionLocalCache()
            );

            public static final LocalCacheProvider<FactionSpawnerSpawnLocalCache> SPAWNERS_SPAWN = new LocalCacheProvider<>(
                    new FactionSpawnerSpawnLocalCache()
            );

            public static final LocalCacheProvider<SpawnerLogLocalCache> SPAWNERS_LOG = new LocalCacheProvider<>(
                    new SpawnerLogLocalCache()
            );

            public static final LocalCacheProvider<CrystalAmplifierLocalCache> CRYSTAL_AMPLIFIER = new LocalCacheProvider<>(
                    new CrystalAmplifierLocalCache()
            );

            public static final LocalCacheProvider<OfferSpawnerLocalCache> OFFER_SPAWNER = new LocalCacheProvider<>(
                    new OfferSpawnerLocalCache()
            );

            public static final LocalCacheProvider<AltarLocalCache> ALTAR = new LocalCacheProvider<>(new AltarLocalCache());

            public static final LocalCacheProvider<TpaAcceptLogLocalCache> TPA_LOG = new LocalCacheProvider<>(new TpaAcceptLogLocalCache());

            public static final LocalCacheProvider<EvolutionLocalCache> SPAWNER_EVOLUTIONS = new LocalCacheProvider<>(new EvolutionLocalCache());

            public static final LocalCacheProvider<FurnacesLocalCache> FURNACES = new LocalCacheProvider<>(new FurnacesLocalCache());

        }

        public static class Redis {

            public static final RedisCacheProvider<FactionInvitationsRedisCache> FACTION_INVITATIONS = new RedisCacheProvider<>(
                    new FactionInvitationsRedisCache()
            );

            public static final RedisCacheProvider<AllyInvitationsRedisCache> ALLY_INVITATIONS = new RedisCacheProvider<>(
                    new AllyInvitationsRedisCache()
            );

            public static final RedisCacheProvider<UserOptionsRedisCache> USERS_OPTIONS = new RedisCacheProvider<>(
                    new UserOptionsRedisCache()
            );
        }
    }
}
