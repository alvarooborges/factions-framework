package net.hyze.factions.framework.divinealtar;

public class AltarConstants {

    public static final Long COOLDOWN = 5L * 60L * 60000L;

    public static final String METADATA_TYPE_KEY = "altar_identifier";

    public static class Databases {

        public static class Mysql {

            public static class Tables {

                public static final String ALTAR_TABLE_NAME = "altar";
                public static final String ALTAR_COOLDOWN_TABLE_NAME = "altar_cooldown";

            }
        }
    }

}
