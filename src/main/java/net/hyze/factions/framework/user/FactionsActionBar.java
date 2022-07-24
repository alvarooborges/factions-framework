package net.hyze.factions.framework.user;

import net.hyze.core.shared.misc.utils.DateUtils;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.notification.actionbar.ActionBarNotification;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.misc.utils.McMMOUtils;

import java.util.Calendar;
import java.util.Date;

public class FactionsActionBar extends ActionBarNotification {

    private FactionUser factionUser;
    boolean underAttackColorToggle = false;

    public FactionsActionBar(FactionUser factionUser) {
        super(factionUser.getPlayer());
        this.factionUser = factionUser;
    }

    @Override
    public String get() {

        boolean inCombat = CombatManager.isTagged(factionUser.getHandle());

        if (!inCombat) {
            String mcMMMONotificationMessage = McMMOUtils.getLookingAtNotificationMessage(factionUser.getPlayer());

            if (mcMMMONotificationMessage != null) {
                return mcMMMONotificationMessage;
            }
        }

        FactionUserRelation relation = FactionUserUtils.getRelation(factionUser);

        if (relation != null) {
            Faction faction = relation.getFaction();

            if (faction.isUnderAttack()) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(faction.getUnderAttackAt());
                calendar.add(Calendar.MINUTE, 5);

                String message = String.format(
                        "Sua facção está sob ataque (%s)!",
                        DateUtils.diffToDigitalStr(new Date(), calendar.getTime(), true)
                );

                if (underAttackColorToggle) {
                    message = "&c&l\u26A0 &4" + message + " &c&l\u26A0";
                } else {
                    message = "&4&l\u26A0 &c" + message + " &4&l\u26A0";
                }

                underAttackColorToggle = !underAttackColorToggle;

                return message;
            }
        }

        return null;
    }
}
