package net.hyze.factions.framework.misc.scoreboard;

import lombok.Getter;
import lombok.Setter;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.scoreboard.bukkit.GroupScoreboard;
import net.hyze.core.spigot.misc.scoreboard.bukkit.IHealthBoard;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.scoreboard.Team;

public class FactionsScoreboard extends GroupScoreboard implements IHealthBoard {

    @Getter
    private final FactionUser handle;

    @Getter
    @Setter
    private boolean lastUnderAttack;

    @Getter
    @Setter
    private int currentFactionsInfoIndex = 0;

    public FactionsScoreboard(FactionUser user) {
        super(user.getPlayer());
        this.handle = user;
    }

    public void registerUser(User user) {
        Group group = user.getHighestGroup();

        Team team = this.scoreboard.getEntryTeam(user.getNick());

        if (team != null) {
            team.removeEntry(user.getNick());
        }

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

        if (relation != null) {
            team = fetchOrCreateTeam(group, relation.getFaction());
        } else {
            team = fetchOrCreateTeam(group);
        }

        team.addEntry(user.getNick());
    }

    @Override
    protected Team fetchOrCreateTeam(Group group) {
        return fetchOrCreateTeam(group, null);
    }

    private Team fetchOrCreateTeam(Group group, Faction faction) {
        Team team;

        String teamName = getName(group, faction);

        if ((team = this.scoreboard.getTeam(teamName)) == null) {
            team = this.scoreboard.registerNewTeam(teamName);

            team.setPrefix(getPrefix(group));
            team.setSuffix(getSuffix(faction));
        }

        return team;
    }

    private String getSuffix(Faction faction) {

        if (faction != null) {
            return MessageUtils.translateColorCodes(String.format(
                    " %s[%s]",
                    ChatColor.GRAY,
                    faction.getTag()
            ).toUpperCase());
        }

        return "";
    }

    protected String getName(Group group, Faction faction) {

        String prefix = GroupScoreboard.getName0(group);

        return String.format(
                "%s-%s",
                prefix,
                faction != null ? faction.getId() : 0
        );
    }
}
