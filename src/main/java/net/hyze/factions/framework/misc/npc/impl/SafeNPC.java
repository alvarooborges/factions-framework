package net.hyze.factions.framework.misc.npc.impl;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.npc.NPCInfo;
import net.hyze.core.spigot.misc.npc.PlayerCustomNPC;
import org.bukkit.Location;

@NPCInfo(id = "npc_safe", name = "&b&lCOFRES")
public class SafeNPC extends PlayerCustomNPC {

    public SafeNPC(Location location) {
        super(
                "eyJ0aW1lc3RhbXAiOjE1NjE4MTY5OTc3NjYsInByb2ZpbGVJZCI6IjE0MTVjOTk0M2JiYzRiMWJhMmNlMzZiN2JkMmMyM2Q1IiwicHJvZmlsZU5hbWUiOiJTZUthQm8iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzY0ZDJiOTEzMWUwODUxOTE5Y2FiYzcyYTJkYjk1MWEyNjIxMTJmNDU1YTIwMzA2NTgwMjNkZWUxYTIwNGU0MmEifX19",
                "ZD/9MwBOCSuEq+mbUQX/YBhIweat4Dpu9c9UQgICHXpLg3I1zuIonmA+hPcKQfyDIxBIZHRKKrEPZ5Oaa9ewoCftDiiy2uaF3Ejvh3WEcx6whG/Y1Di1wbWgUsGV0B/Igb9fSzJDPsPo6NMiEkhPYjocoCeHD9mf0ZHVp/oRpQzFBcnXDTzVyZnMDDOcvc1Cx5Z7pcOfrAX9loaI3FuEe7CfWZC2ep39NKMtarjnU9X0622Uk732N5gse6BmtNcHswniQ3P9fb+CCpiigbQeNlwRAF6RlNVe2+zDkCla4WspFrkeGdH1lMG870h25sII9aOo2ZUIclR2t+yRypCe7MeTRkFVQ8OdiyEzjbqgo+IEbynhste57Dtu45eDjaDIPxTVXOnHE9HwUaEuBBgXGCMFJ5bx5V301rgbZKy/1BifEnw9uMzam1waDCQW4RXw46cFxQApNxvs5zPxJEKmtBzhrpSuG5OCf15FsoqLfbHUVJADYav6iKEXSY4bS0habNypjeRmK7QIlYSAqZFqsyEKpkFRE2FFX2cgKHCzNSk4ut5w/XoNvgJt+1Z/jpcIO0Mrj7TW+MWfSEg6OT7s9SmVnn38mqpl5+JU+lVRnh0GnYuYPrjCIVkcEj0A/9DPE6vKjYvz+5wb5ELJQ/mO6eYGYioqK9NT0sdKXBCdnYk=",
                location
        );

    }

    @Override
    public void handleClick(User user, NPCClickEvent event) {
        event.getClicker().performCommand("cofre");
    }

}
