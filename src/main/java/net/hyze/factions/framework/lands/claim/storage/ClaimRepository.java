package net.hyze.factions.framework.lands.claim.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.claim.storage.specs.*;

import java.util.Set;

public class ClaimRepository extends MysqlRepository {

    public ClaimRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void delete(Claim claim) {
        query(new DeleteClaimSpec(claim));
    }

    public boolean insert(Claim claim) {
        return query(new InsertClaimSpec(claim));
    }

    public void update(Claim claim) {
        query(new UpdateClaimSpec(claim));
    }

    public Set<Claim> fetch() {
        return query(new SelectClaimsSpec());
    }

    public void deleteExpiredTemporaryClaims() {
        query(new DeleteExpiredTemporaryClaimsSpec());
    }
}
