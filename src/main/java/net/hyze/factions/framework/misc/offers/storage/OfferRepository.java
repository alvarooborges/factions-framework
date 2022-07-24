package net.hyze.factions.framework.misc.offers.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.economy.Currency;
import net.hyze.factions.framework.misc.offers.Offer;
import net.hyze.factions.framework.misc.offers.OfferLogType;
import net.hyze.factions.framework.misc.offers.storage.spec.DeleteOfferSpec;
import net.hyze.factions.framework.misc.offers.storage.spec.InsertOfferLogSpec;
import net.hyze.factions.framework.misc.offers.storage.spec.InsertOfferSpec;
import net.hyze.factions.framework.misc.offers.storage.spec.SelectOffersSpec;

import java.util.LinkedList;

public class OfferRepository extends MysqlRepository {

    public OfferRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public Offer insert(int userId, String name, Long expireTime, Currency currency, int price, int oldPrice, LinkedList<String> offerItems) {
        return query(new InsertOfferSpec(userId, name, expireTime, currency, price, oldPrice, offerItems));
    }

    public LinkedList<Offer> get(int userId) {
        return query(new SelectOffersSpec(userId));
    }

    public void delete(int offerId) {
        query(new DeleteOfferSpec(offerId));
    }

    public void log(OfferLogType type, int userId, Offer offer) {
        query(new InsertOfferLogSpec(type, userId, offer));
    }

}
