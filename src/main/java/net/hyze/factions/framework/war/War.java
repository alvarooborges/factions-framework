package net.hyze.factions.framework.war;

import com.google.common.collect.Sets;
import net.hyze.factions.framework.war.clock.WarClock;

import java.util.Set;

public class War {

    public static final Set<String> COMMAND_PRE_START_WHITELIST = Sets.newHashSet(/*"/echest",*/ "/spawn", "/lobby", "/.", "/c");
    public static final Set<String> COMMAND_LATE_GAME_WHITELIST = Sets.newHashSet("/.", "/c");

    public static boolean OPEN = false;
    public static boolean TEST = true;
    public static boolean PAUSE = false;

    public static WarConfig CONFIG;
    public static final WarClock CLOCK = new WarClock();

}
