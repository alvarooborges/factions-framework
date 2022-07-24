package net.hyze.factions.framework;

import com.google.common.collect.Sets;

import java.util.Set;

public class FactionsConstants {

    public static final boolean SEASON_ENABLED = false;

    public static final int TAG_MAX_LENGTH = 3;
    public static final int TAG_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 20;
    public static final int NAME_MIN_LENGTH = 5;

    public static final String NBT_ITEM_GROUP = "nbt_item_group";

    public static final Set<String> BLOCKED_TAGS = Sets.newHashSet(
            "VTC", "FDP", "ASS", "SEX", "VIK", "VKI", "VGK", "VIG", "VKS", "VSK",
            "VQG", "GKV", "VIQ", "VIS", "STA", "EQU", "MOD", "AJD", "ADM", "GER",
            "YOT", "YUT", "VKK", "PAU", "PAL", "SKY", "CU"
    );

    public static final String TAG_PATTERN = String.format("[a-zA-Z0-9]{%s,%s}", TAG_MIN_LENGTH, TAG_MAX_LENGTH);

    public static final String NAME_PATTERN = String.format("[a-zA-Z0-9 ]{%s,%s}", NAME_MIN_LENGTH, NAME_MAX_LENGTH);

    public static class Databases {

        public static class Mysql {

            public static class Tables {

                public static final String FACTIONS_TABLE_NAME = "factions";
                public static final String FACTIONS_SEASON_LOG_TABLE_NAME = "faction_season_log";
                public static final String ARENA_KILLS_TABLE_NAME = "arena_kills";
                public static final String FACTION_PERMISSIONS_TABLE_NAME = "faction_permissions";
                public static final String FACTION_USERS_TABLE_NAME = "faction_users";
                public static final String FACTIONS_RELATIONS_TABLE_NAME = "factions_relations";
                public static final String CLAIMS_TABLE_NAME = "claims";
                public static final String SPAWNERS_TABLE_NAME = "spawners";

                public static final String USER_STATS_TABLE_NAME = "user_stats";
                public static final String USER_PROFILE_TABLE_NAME = "user_profile";

                public static final String OFFERS_TABLE_NAME = "offers";
                public static final String OFFERS_LOG_TABLE_NAME = "offers_log";

            }
        }
    }

    public static class UserPreference {

        public static final String MINING_DROPS = "MINING_DROPS";
        public static final String SPAWNER_EFFECT = "SPAWNER_EFFECT";
        public static final String CHAT_LOCAL = "CHAT_LOCAL";
        public static final String CHAT_GLOBAL = "CHAT_GLOBAL";
        public static final String PARTY_INVITE = "PARTY_INVITE";

    }
}
