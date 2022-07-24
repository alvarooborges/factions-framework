package net.hyze.factions.framework.faction.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.faction.Faction;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;

@RequiredArgsConstructor
public class UpdateFactionSpec extends UpdateSqlSpec<Boolean> {

    private final Faction faction;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows > 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = "UPDATE `factions` SET `home` = ?, `under_attack_at` = ?, `points` = ? WHERE id = ?;";

            PreparedStatement statement = connection.prepareStatement(query);

            if (faction.getHome() != null) {
                statement.setString(1, faction.getHome().toString());
            } else {
                statement.setNull(1, Types.VARCHAR);
            }

            if (faction.getUnderAttackAt() != null) {
                statement.setTimestamp(2, new Timestamp(faction.getUnderAttackAt().getTime()));
            } else {
                statement.setNull(2, Types.TIMESTAMP);
            }

            statement.setInt(3, faction.getPoints());
            
            statement.setInt(4, faction.getId());

            return statement;
        };
    }
}
