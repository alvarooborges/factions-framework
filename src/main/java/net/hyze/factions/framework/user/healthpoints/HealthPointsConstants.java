package net.hyze.factions.framework.user.healthpoints;

import net.hyze.hyzeskills.datatypes.skills.SkillType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class HealthPointsConstants {

    public static final double DEFAULT_HEARTS = 3.0;

    // INT 1 - NÍVEL . SKILL E NÍVEL REQUERIDO
    public static final Map<Integer, Map<SkillType, Integer>> GOALS_MAP = new HashMap<Integer, Map<SkillType, Integer>>() {{

        put(0, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.MINING, 2);
        }});

        put(1, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.EXCAVATION, 2);
        }});

        put(2, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.MINING, 4);
        }});

        put(3, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.HERBALISM, 2);
        }});

        put(4, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.EXCAVATION, 4);
            put(SkillType.HERBALISM, 4);
        }});

        put(5, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.SWORDS, 2);
        }});

        put(6, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.MINING, 7);
            put(SkillType.AXES, 2);
        }});

        put(7, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.EXCAVATION, 7);
            put(SkillType.ALCHEMY, 2);
        }});

        put(8, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.HERBALISM, 7);
            put(SkillType.SWORDS, 4);
        }});

        put(9, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.MINING, 10);
            put(SkillType.EXCAVATION, 10);
            put(SkillType.HERBALISM, 10);
        }});

        put(10, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.AXES, 4);
            put(SkillType.ACROBATICS, 2);
        }});

        put(11, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.SWORDS, 7);
            put(SkillType.ALCHEMY, 4);
        }});

        put(12, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.ACROBATICS, 4);
        }});

        put(13, new EnumMap<SkillType, Integer>(SkillType.class) {{
            put(SkillType.SWORDS, 10);
            put(SkillType.AXES, 7);
        }});
    }};
}
