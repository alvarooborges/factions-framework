package net.hyze.factions.framework.misc.offers.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.DeleteSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class DeleteOfferSpec extends DeleteSqlSpec<Void> {

    private final int id;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "DELETE FROM `%s` WHERE `id` = ?;";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(
                            query,
                            FactionsConstants.Databases.Mysql.Tables.OFFERS_TABLE_NAME
                    )
            );

            statement.setInt(1, this.id);

            return statement;
        };
    }

}
