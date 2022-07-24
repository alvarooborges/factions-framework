package net.hyze.factions.framework.misc.npc.impl;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.npc.NPCInfo;
import net.hyze.core.spigot.misc.npc.PlayerCustomNPC;
import org.bukkit.Location;

@NPCInfo(id = "npc_dungeon", name = "&b&lDUNGEONS")
public class DungeonNPC extends PlayerCustomNPC {

    public DungeonNPC(Location location) {
        super(
                "eyJ0aW1lc3RhbXAiOjE0OTMxNTgwNDk2NDUsInByb2ZpbGVJZCI6ImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwicHJvZmlsZU5hbWUiOiJNaW5pRGlnZ2VyVGVzdCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzk1Y2IxNjVlYzhmYjNmZmIwZTA3MDg4NTExNDkwODM1Y2YzMzFkODdkZjc4YTVkNDE5OTJkODc2MWVjMzYxIn19fQ==",
                "CX2RIGTGI7Dbp4F/qnMEjDUTdXhHABPRCsMoml9CtLJThmJLIom3hPpRAGELvi740vdzYx1XAghMA9g+VhBUkj9vZMcbB5v4BCyAiQorItw6mAgVcrAd+ngiAW7KIhpKKPfByTVFVY2L8pq2vXcn7m7EWaxjNu45Z9H2alRqU5LJtqZhqIkBp+6z4MZNqrBZzqnY71XcFksMC66YAnY+EYk1tcONfyFxOozCt7kX2mYfwhAz9AWOo4jkS2eVjaByOtlwEEw0ab5iOYpGKAp14Iu+kwX3kk2jr0fo/HyJcFz3jjEo42fcxvaxHcWouzBm8QD3J4T9PEMYnRHIeKIkcwrbo3sSlhEB9ExjhY7KhSBMHzWA7XcJoxwLGVvt4lhVEE/DLV+CpZuKjkhk/wYMJ2zc4/iVRgguFqsAFLArp88J/PGi1KkrlIg15IwmXagmMMiI8M7I18aENp6Q9D46FRDdgHnXtcmJVh6jyI8Zjj2phqayFlBTMzT62doGIQyZMkK+Dd4XgPp2ApByM1AjtN82/K+kRY5vvWLBy6rAzFV5VrLduyYX2iXnXRxhOYpWECa4G0/x8UgTLxot6ZE9IbBlUA3S4HN2xc+iHDUGEbLsqeVDU/uSdKCLRUlkeLvEIpJ0R66jYIEnIgNY5x7qwa2v9b856aIKQvSAlSxCtUc=",
                location
        );
    }

    @Override
    public void handleClick(User user, NPCClickEvent event) {
        event.getClicker().performCommand("dungeon");
    }

}
