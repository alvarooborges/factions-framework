package net.hyze.factions.framework.faction.ranking.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.factions.framework.faction.Faction;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class InsertValuationSpec extends InsertSqlSpec<Void> {

    private final Faction faction;
    private final double value;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "INSERT INTO `ranking_value` (`faction_id`, `value`, `date`) "
                    + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE "
                    + "`value` = ?;"
            );

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            long roundTime = (System.currentTimeMillis() / 1000 / 60 / 60) * 1000 * 60 * 60;

            statement.setInt(1, this.faction.getId());
            statement.setDouble(2, this.value);
            statement.setDate(3, new Date(roundTime));
            statement.setDouble(4, this.value);

            return statement;
        };
    }

}
