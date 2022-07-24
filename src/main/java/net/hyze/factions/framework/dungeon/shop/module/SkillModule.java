package net.hyze.factions.framework.dungeon.shop.module;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.shop.module.AbstractModule;
import net.hyze.hyzeskills.datatypes.player.McMMOPlayer;
import net.hyze.hyzeskills.datatypes.skills.SkillType;
import net.hyze.hyzeskills.util.player.UserManager;

@RequiredArgsConstructor
public class SkillModule extends AbstractModule {

    private final SkillType type;

    private final int level;

    @Override
    public State state(User user) {
        McMMOPlayer player = UserManager.getPlayer(user.getNick());
        if (player != null) {
            if (player.getSkillLevel(type) >= level) {
                return State.SUCCESS;
            }
        }

        return State.ERROR;
    }

    public String[] addLore(User user, State state) {
        if (state == State.ERROR) {
            return new String[]{
                String.format("&cVocê precisa do nivel %d de %s.", level, type.getName())
            };
        }

        return super.addLore(user, state);
    }
}
