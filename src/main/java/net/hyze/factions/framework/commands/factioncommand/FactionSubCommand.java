package net.hyze.factions.framework.commands.factioncommand;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.commands.argument.impl.NickArgument;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class FactionSubCommand extends CustomCommand {

    private final FactionRole rank;

    public FactionSubCommand(String name, String... aliases) {
        this(name, null, aliases);
    }

    public FactionSubCommand(String name, FactionRole role, String... aliases) {
        super(name, CommandRestriction.IN_GAME, aliases);

        this.rank = role;
    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(handle);

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUserId(user.getId());

        if (rank != null) {
            if (relation == null) {
                Message.ERROR.send(sender, "Você precisa fazer parte de uma facção.");
                return;
            }

            if (rank.ordinal() > relation.getRole().ordinal()) {
                Message.ERROR.send(sender, String.format("Você precisa ter o rank %s.", rank.getDisplayName()));
                return;
            }
        }

        onCommand((Player) sender, user, relation, args);
    }

    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        onCommand(player, user, args);
    }

    public void onCommand(Player player, FactionUser user, String[] args) {
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player) || args.length <= 0) {
            return ImmutableList.of();
        }

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(sender.getName());

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUserId(user.getId());

        if (relation != null) {

            if (this.getArguments().size() >= args.length) {
                int index = args.length - 1;
                String token = args[index];

                if (!token.isEmpty()) {
                    Argument argument = this.getArguments().get(index);

                    if (argument instanceof NickArgument) {

                        Set<FactionUserRelation> relations = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByFaction(relation.getFaction());

                        Set<String> nicks = relations.stream()
                                .map(FactionUserRelation::getUser)
                                .map(FactionUser::getHandle)
                                .map(User::getNick)
                                .filter(nick -> StringUtil.startsWithIgnoreCase(nick, args[index]))
                                .collect(Collectors.toSet());

                        return StringUtil.copyPartialMatches(args[index], nicks, new ArrayList(nicks.size()));
                    }
                }
            }
        }

        return ImmutableList.of();
    }

    public CommandRelationType getCommandRelationType() {
        return null;
    }

    @RequiredArgsConstructor
    @Getter
    public enum CommandRelationType {

        FACTION_TO_PLAYER("Relação facção - jogador"),
        PLAYER_TO_FACTION("Relação jogador - facção"),
        FACTION_TO_ADM("Relação facção - administração"),
        MISC("Outros");

        private final String displayName;

        public static CommandRelationType getByOrdinal(int ordinal) {
            for (CommandRelationType commandRelationType : values()) {
                if (commandRelationType.ordinal() == ordinal) {
                    return commandRelationType;
                }
            }

            return null;
        }

    }

}
