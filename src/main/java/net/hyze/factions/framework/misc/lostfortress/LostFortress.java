package net.hyze.factions.framework.misc.lostfortress;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LostFortress {

    private Map<Integer, UserInfo> users = Maps.newHashMap();
    private Map<Integer, ArrayList<String>> items = Maps.newHashMap();

    private ArrayList<String> tips = Lists.newArrayList();

    private FactionInfo firstFaction;
    private UserInfo firstPlayer;

    public void setFirstFaction(Faction faction) {
        this.firstFaction = new FactionInfo(faction);
    }

    public void setFirstPlayer(FactionUser faction) {
        this.firstPlayer = new UserInfo(faction);
    }

    public void addTip(String string) {
        this.tips.add(string);
    }

    public void logItemStack(FactionUser user, ItemStack itemStack) {
        UserInfo userInfo = this.users.getOrDefault(user.getId(), new UserInfo(user));

        if (!this.users.containsKey(user.getId())) {
            this.users.put(user.getId(), userInfo);
        }

        ArrayList<String> currentItems = this.items.getOrDefault(user.getId(), Lists.newArrayList());

        if (!this.items.containsKey(user.getId())) {
            this.items.put(user.getId(), currentItems);
        }

        currentItems.add(InventoryUtils.serializeContents(new ItemStack[]{itemStack}));
    }

    @Getter
    public class FactionInfo {

        private final String tag;
        private final String displayName;

        public FactionInfo(Faction faction) {
            this.tag = faction.getTag();
            this.displayName = faction.getDisplayName();
        }
    }

    @Getter
    @AllArgsConstructor
    public class UserInfo {

        private final int id;
        private final String displayName;
        private final String factionTag;

        public UserInfo(FactionUser user) {
            this.id = user.getId();
            this.displayName = user.getHandle().getHighestGroup().getDisplayTag(user.getNick());

            FactionUserRelation relation = user.getRelation();

            if (relation == null) {
                this.factionTag = null;
                return;
            }

            this.factionTag = relation.getFaction().getTag();
        }
    }

}
