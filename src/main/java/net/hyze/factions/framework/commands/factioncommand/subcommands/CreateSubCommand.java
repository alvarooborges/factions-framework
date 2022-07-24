package net.hyze.factions.framework.commands.factioncommand.subcommands;

import com.google.common.base.Joiner;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.FactionCreatedPacket;
import net.hyze.factions.framework.echo.packets.UserJoinedFactionPacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Date;

public class CreateSubCommand extends FactionSubCommand {

    public CreateSubCommand() {
        super("criar");

        registerArgument(new Argument("tag", "A tag da facção"));
        registerArgument(new Argument("nome", "O nome da facção"));
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        if (relation != null) {
            Message.ERROR.send(player, "Você já faz parte de uma facção.");
            return;
        }

        String tag = args[0].toUpperCase();
        String name = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));

        if (tag.length() > FactionsConstants.TAG_MAX_LENGTH || tag.length() < FactionsConstants.TAG_MIN_LENGTH) {
            Message.ERROR.send(player, String.format(
                    "A tag da facção deve conter entre %s e %s caracteres.",
                    FactionsConstants.TAG_MIN_LENGTH,
                    FactionsConstants.TAG_MAX_LENGTH
            ));
            return;
        }

        if (!tag.matches(FactionsConstants.TAG_PATTERN)) {
            Message.ERROR.send(player, "A tag da facção não pode conter caracteres especiais.");
            return;
        }

        if (FactionsConstants.BLOCKED_TAGS.contains(tag)) {
            Message.ERROR.send(player, "Esta tag foi bloqueada. Por favor, escolha outra tag.");
            return;
        }

        if (name.length() < FactionsConstants.NAME_MIN_LENGTH || name.length() > FactionsConstants.NAME_MAX_LENGTH) {
            Message.ERROR.send(
                    player,
                    String.format(
                            "O nome de sua facção deve conter de %s a %s caracteres.",
                            FactionsConstants.NAME_MIN_LENGTH,
                            FactionsConstants.NAME_MAX_LENGTH
                    )
            );
            return;
        }

        if (!name.matches(FactionsConstants.NAME_PATTERN)) {
            Message.ERROR.send(player, "O nome de sua facção não pode conter caracteres especiais.");
            return;
        }

        Faction factionByTag = FactionsProvider.Cache.Local.FACTIONS.provide().get(tag);

        if (factionByTag != null) {
            Message.ERROR.send(player, "Já existe outra facção utilizando a tag \"&f" + tag + "&c\".");
            return;
        }

        boolean anyMatchWithName = FactionsProvider.Cache.Local.FACTIONS.provide().get().stream()
                .anyMatch(fac -> fac.getName().equalsIgnoreCase(name));

        if (anyMatchWithName) {
            Message.ERROR.send(player, "Já existe outra facção utilizando o nome \"&f" + name + "&c\".");
            return;
        }

        Faction faction = FactionsProvider.Repositories.FACTIONS.provide().create(tag, name, 25);

        if (faction == null) {
            Message.ERROR.send(player, "Não foi possivel criar sua facção.");
            return;
        }

        CoreProvider.Redis.ECHO.provide().publish(new FactionCreatedPacket(faction, user.getId()));

        relation = new FactionUserRelation(user.getId(), faction, FactionRole.LEADER, new Date());

        FactionsProvider.Repositories.USERS_RELATIONS.provide().update(relation);

        CoreProvider.Redis.ECHO.provide().publish(new UserJoinedFactionPacket(
                faction, user.getId(), UserJoinedFactionPacket.Reason.FACTION_CREATED
        ));

        Message.SUCCESS.send(player, "Sua facção foi criada com sucesso!");

    }
}
