package net.hyze.factions.framework.faction;

import lombok.*;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.bank.Bank;

import java.util.Calendar;
import java.util.Date;

@Getter
@ToString(of = {"id", "tag", "name"})
@EqualsAndHashCode(of = "id")
public class Faction {

    private final Integer id;

    @Setter
    @NonNull
    private String tag;

    @Setter
    @NonNull
    private String name;

    @Setter
    private int maxMembers;

    private final Date createdAt;

    @Setter
    private SerializedLocation home;

    @Setter
    private Date underAttackAt;

    @Getter
    @Setter
    private int points;

    @Getter
    @Setter
    private Integer leaguePosition;

    @Getter
    @Setter
    private Bank bank;

    public Faction(Integer id, String tag, String name, int maxMembers, Date createdAt, Date underAttackAt, int points) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.maxMembers = maxMembers;
        this.createdAt = createdAt;
        this.underAttackAt = underAttackAt;
        this.points = points;
    }

    public Faction(Integer id, String tag, String name, int maxMembers, Date createdAt, SerializedLocation home, Date underAttackAt, int points) {
        this(id, tag, name, maxMembers, createdAt, underAttackAt, points);
        this.home = home;
    }

    public String getDisplayName() {
        return MessageUtils.translateColorCodes(String.format(
                "&7[%s] %s", tag.toUpperCase(), name
        ));
    }

    public String getStrippedDisplayName() {
        return String.format("[%s] %s", tag.toUpperCase(), name);
    }

    public boolean isUnderAttack() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);

        return underAttackAt != null && underAttackAt.after(calendar.getTime());
    }

    public Bank getBank() {
        if (!FactionsProvider.getSettings().isBankEnabled()) {
            return null;
        }

        if (this.bank == null) {
            this.bank = new Bank(0);
        }

        return this.bank;
    }
}
