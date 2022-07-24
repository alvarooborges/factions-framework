package net.hyze.factions.framework.misc.offers.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.misc.offers.Offer;
import net.hyze.factions.framework.misc.offers.OfferLogType;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class InsertOfferLogSpec extends InsertSqlSpec<Void> {

    private final OfferLogType logType;
    private final int userId;
    private final Offer offer;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        String query = String.format("INSERT INTO `%s` (`created_at`, `type`, `user_id`, `offer_name`, `offer_expire_time`, `offer_currency`, `offer_price`, `offer_old_price`, `offer_items`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);",
                FactionsConstants.Databases.Mysql.Tables.OFFERS_LOG_TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);


            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            statement.setString(2, this.logType.name());
            statement.setInt(3, this.userId);
            statement.setString(4, this.offer.getName());
            statement.setTimestamp(5, new Timestamp(this.offer.getExpireTime()));
            statement.setString(6, this.offer.getCurrency().name());
            statement.setInt(7, this.offer.getPrice());
            statement.setInt(8, this.offer.getOldPrice());
            statement.setString(9, CoreConstants.GSON.toJson(this.offer.getOfferItems()));

            return statement;
        };
    }
}
