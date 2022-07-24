package net.hyze.factions.framework.commands.factioncommand;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.subcommands.*;
import net.hyze.factions.framework.commands.factioncommand.subcommands.adminsubcommand.AdminSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.claim.ClaimSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.claim.ProtectSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.permissionsubcommand.PermissionSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.pointssubcommand.PointsSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.rankingsubcommand.RankingSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.relationsubcommand.RelationSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.setspawnsubcommand.SetSpawnSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionCommand extends CustomCommand {

    @Getter
    private static final Multimap<FactionSubCommand.CommandRelationType, String> COMMANDS_RELATIONS_MAP = ArrayListMultimap.create();

    public FactionCommand() {
        super("faction", CommandRestriction.IN_GAME, "f");

        registerSubCommand(new CreateSubCommand(), "Crie uma fação.");
        registerSubCommand(new InviteSubCommand(), "Convide um jogador.");
        registerSubCommand(new BaseSubCommand(), "Vá até a home da facção.");
        registerSubCommand(new MembersSubCommand(), "Veja a lista de membros.");
        registerSubCommand(new KickSubCommand(), "Expulse um jogador.");
        registerSubCommand(new InfoSubCommand(), "Veja informações de uma facção.");
        registerSubCommand(new ProfileSubCommand(), "Veja informações de um jogador.");
        registerSubCommand(new AcceptSubCommand(), "Aceite um convite de facção.");
        registerSubCommand(new PromoteSubCommand(), "Promova um jogador.");
        registerSubCommand(new DemoteSubCommand(), "Rebaixe um jogador.");
        registerSubCommand(new LeaveSubCommand(), "Saia da sua facção.");
        registerSubCommand(new MapSubCommand(), "Veja o mapa de terras.");
        registerSubCommand(new AbandonSubCommand(), "Abandone terras");
        registerSubCommand(new SpawnersSubCommand(), "Gerencie os geradores.");
        registerSubCommand(new TransferSubCommand(), "Transfira sua facção");
        registerSubCommand(new DisbandSubCommand(), "Desfaça sua facção.");
        if (FactionsProvider.getSettings().isAllowAlly()) {
            registerSubCommand(new RelationSubCommand(), "Gerencie as relações.");
        }
        registerSubCommand(new SeeChunkCommand(), "Mostra o limites de suas terras.");
        if (FactionsProvider.getSettings().isAllowAlly()) {
            registerSubCommand(new AllySubCommand());
        }
        registerSubCommand(new PermissionSubCommand(), "Gerencie as permissões.");
        registerSubCommand(new AdminSubCommand());
        registerSubCommand(new BeaconSubCommand());
        registerSubCommand(new SetBaseSubCommand(), "Defina o spawn da base de sua Fac.");
        registerSubCommand(new SetSpawnSubCommand(), "Defina o spawn dos geradores de sua Fac.");
        if (FactionsProvider.getSettings().isAllowRankCommand()) {
            registerSubCommand(new RankingSubCommand(), "Ranking de facções");
        }
        registerSubCommand(new ClaimSubCommand(), "Domine terras.");
        registerSubCommand(new ProtectSubCommand(), "Domine terras temporárias.");
        registerSubCommand(new ContestSubCommand(), "Conteste terras.");
        registerSubCommand(new MenuSubCommand(), "Veja o menu de fações.");

        registerSubCommand(new FlySubCommand(), "Utilize para voar.");
        registerSubCommand(new FurySwordSubCommand());
        registerSubCommand(new PointsSubCommand());

        registerSubCommand(new HelpSubCommand());

        if (FactionsProvider.getSettings().isBankEnabled()) {
            registerSubCommand(new BankSubCommand(), "Abra o banco de sua facção.");
        }
    }

    /*

     */

    public void registerRelationType(FactionSubCommand subCommand) {
        COMMANDS_RELATIONS_MAP.put(
                subCommand.getCommandRelationType(),
                this.getUsageWithoutSlash(subCommand.getLabel(), subCommand.getDescription0())
        );
    }

    public static Multimap<FactionSubCommand.CommandRelationType, String> getCommandsRelationsTypesMap() {
        return COMMANDS_RELATIONS_MAP;
    }

    /*

     */

    @Override
    public void registerSubCommand(CustomCommand subCommand, String description) {
        super.registerSubCommand(subCommand, description);

        if (subCommand instanceof FactionSubCommand) {
            FactionSubCommand factionSubCommand = (FactionSubCommand) subCommand;

            if (factionSubCommand.getCommandRelationType() != null) {
                registerRelationType(factionSubCommand);
            }
        }
    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {
        Player player = (Player) sender;
        player.performCommand("f menu");
    }
}
