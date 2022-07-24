package net.hyze.factions.framework.misc.offers.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.economy.Currency;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.misc.offers.Offer;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;

@RequiredArgsConstructor
public class InsertOfferSpec extends InsertSqlSpec<Offer> {
    
    private final int userId;
    private final String name;
    private final Long expireTime;
    private final Currency currency;
    private final int price;
    private final int oldPrice;
    private final LinkedList<String> offerItems;

    @Override
    public Offer parser(int affectedRows, KeyHolder keyHolder) {
        return new Offer(
                keyHolder.getKey().intValue(),
                this.name,
                this.expireTime,
                this.currency,
                this.price,
                this.oldPrice,
                this.offerItems
        );
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        String query = String.format("INSERT INTO `%s` (`user_id`, `name`, `expire_time`, `currency`, `price`, `old_price`, `items`) VALUES (?, ?, ?, ?, ?, ?, ?);",
                FactionsConstants.Databases.Mysql.Tables.OFFERS_TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, this.userId);
            statement.setString(2, this.name);
            statement.setTimestamp(3, new Timestamp(this.expireTime));
            statement.setString(4, this.currency.name());
            statement.setInt(5, this.price);
            statement.setInt(6, this.oldPrice);
            statement.setString(7, CoreConstants.GSON.toJson(this.offerItems));

            return statement;
        };
    }
}
