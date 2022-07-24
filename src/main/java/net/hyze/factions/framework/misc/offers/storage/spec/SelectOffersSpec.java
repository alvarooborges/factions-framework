package net.hyze.factions.framework.misc.offers.storage.spec;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.economy.Currency;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.misc.offers.Offer;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;

@RequiredArgsConstructor
public class SelectOffersSpec extends SelectSqlSpec<LinkedList<Offer>> {

    private final int userId;

    @Override
    public ResultSetExtractor<LinkedList<Offer>> getResultSetExtractor() {
        return (ResultSet result) -> {

            LinkedList<Offer> offers = Lists.newLinkedList();

            while (result.next()) {

                LinkedList<String> offerItems = CoreConstants.GSON.fromJson(result.getString("items"), new TypeToken<LinkedList<String>>() {
                }.getType());

                Offer offer = new Offer(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getTimestamp("expire_time").getTime(),
                        Currency.valueOf(result.getString("currency")),
                        result.getInt("price"),
                        result.getInt("old_price"),
                        offerItems
                );

                offers.add(offer);

            }

            return offers;

        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "SELECT * FROM `%s` WHERE `user_id`=?;";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(
                            query,
                            FactionsConstants.Databases.Mysql.Tables.OFFERS_TABLE_NAME
                    )
            );

            statement.setInt(1, this.userId);
            return statement;
        };
    }

}
