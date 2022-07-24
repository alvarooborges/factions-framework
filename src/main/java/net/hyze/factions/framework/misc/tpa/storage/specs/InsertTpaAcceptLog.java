package net.hyze.factions.framework.misc.tpa.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.world.location.unserializer.BukkitLocationParser;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import org.bukkit.Location;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;

@RequiredArgsConstructor
public class InsertTpaAcceptLog extends UpdateSqlSpec<Void> {

    private final User target;
    private final User requester;
    private final Claim claim;
    private final Location location;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return con -> {
            String query = "INSERT INTO `tpaccept_log` " +
                    "(`faction_id`, `target_user_id`, `requester_user_id`, `target_tag`, `requester_tag`, " +
                    "`app_id`, `world_name`, `x`, `y`, `z`, `date`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, claim.getFactionId());
            statement.setInt(2, target.getId());
            statement.setInt(3, requester.getId());

            String targetTag = getTag(target);
            if (targetTag != null) {
                statement.setString(4, targetTag);
            } else {
                statement.setNull(4, Types.VARCHAR);
            }

            String requesterTag = getTag(requester);
            if (requesterTag != null) {
                statement.setString(5, requesterTag);
            } else {
                statement.setNull(5, Types.VARCHAR);
            }

            SerializedLocation serializedLocation = BukkitLocationParser.serialize(location);

            statement.setString(6, serializedLocation.getAppId());
            statement.setString(7, serializedLocation.getWorldName());
            statement.setDouble(8, serializedLocation.getX());
            statement.setDouble(9, serializedLocation.getY());
            statement.setDouble(10, serializedLocation.getZ());
            statement.setTimestamp(11, new Timestamp(System.currentTimeMillis()));

            return statement;
        };
    }

    private String getTag(User user) {
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

        if (relation == null) {
            return null;
        }

        return String.format(
                "%s%s",
                relation.getRole().getSymbol(),
                relation.getFaction().getTag()
        );
    }

}
