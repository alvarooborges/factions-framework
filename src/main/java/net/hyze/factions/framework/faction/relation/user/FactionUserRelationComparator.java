package net.hyze.factions.framework.faction.relation.user;


import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.group.GroupComparator;
import net.hyze.core.shared.user.User;

import java.util.Collection;
import java.util.Comparator;

@RequiredArgsConstructor
public class FactionUserRelationComparator implements Comparator<FactionUserRelation> {

    private final Collection<User> onlineUsers;

    @Override
    public int compare(FactionUserRelation r1, FactionUserRelation r2) {
        boolean r1IsOnline = onlineUsers.contains(r2.getUser().getHandle());
        boolean r2IsOnline = onlineUsers.contains(r1.getUser().getHandle());

        Group r1Group = r1.getUser().getHandle().getHighestGroup();
        Group r2Group = r2.getUser().getHandle().getHighestGroup();

        GroupComparator groupComparator = new GroupComparator();

        int roleResult = r2.getRole().compareTo(r1.getRole());

        if (r1IsOnline || r2IsOnline) {

            if (r1IsOnline && r2IsOnline) {

                if (roleResult != 0) {
                    return roleResult;
                }

                int groupResult = groupComparator.compare(r2Group, r1Group);

                if (groupResult != 0) {
                    return groupResult;
                }

                return r2.getRole().compareTo(r1.getRole());
            }

            return Boolean.compare(r1IsOnline, r2IsOnline);
        }

        if (roleResult != 0) {
            return roleResult;
        }

        return groupComparator.compare(r2Group, r1Group) - 5;
    }
}
