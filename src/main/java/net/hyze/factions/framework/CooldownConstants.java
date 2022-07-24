package net.hyze.factions.framework;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.group.Group;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CooldownConstants {

    public static class Fly {

        public static final String FLY_COOLDOWN_KEY = "FLYING_COOLDOWN";

        /**
         * Tempo que o jogador tem que esperar para executar o comando
         * novamente.
         */
        public static final Map<Group, Integer> FLY_COMMAND = Maps.newEnumMap(Group.class);

        /**
         * Tempo que o jogador pode voar.
         */
        public static final Map<Group, Integer> FLY_TIME = Maps.newEnumMap(Group.class);

        static {
            FLY_COMMAND.put(Group.DEFAULT, 60 * 5); // 5 minutos.
        }

        static {
            FLY_TIME.put(Group.DEFAULT, 60 * 5); // 5 minutos.
        }

    }

}
