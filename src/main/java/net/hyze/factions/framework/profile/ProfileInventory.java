package net.hyze.factions.framework.profile;

import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;

public class ProfileInventory extends CustomInventory {

    public ProfileInventory(FactionUser user) {
        super(27, "Perfil de " + user.getNick());

        setItem(
                13,
                FactionUtils.getHead(user).make()
        );
    }

}
