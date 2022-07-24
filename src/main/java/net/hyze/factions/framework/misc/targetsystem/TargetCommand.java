package net.hyze.factions.framework.misc.targetsystem;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.argument.impl.NickArgument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TargetCommand extends CustomCommand implements GroupCommandRestrictable, Listener {

    //target id - staff id
    private final Int2IntMap targets = new Int2IntOpenHashMap();

    public TargetCommand() {
        super("target", CommandRestriction.IN_GAME);

        registerArgument(new NickArgument("nick", "Nick do jogador.", true));
    }

    @Override
    public Group getGroup() {
        return Group.MODERATOR;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("clear")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                player.showPlayer(onlinePlayer);
            }

            targets.values().removeIf(integer -> integer == user.getId());

            Message.INFO.send(player, "Alvos limpos com sucesso.");

            return;
        }

        User target = CoreProvider.Cache.Local.USERS.provide().get(args[0]);
        if (target == null) {
            Message.ERROR.send(sender, "Usuário inválido.");
            return;
        }

        int added = targets.put(target.getId().intValue(), user.getId().intValue());
        if (added != targets.defaultReturnValue()) {
            Message.ERROR.send(sender, "Este usuário já é um alvo.");
        } else {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().equals(target.getNick())) {
                    continue;
                }

                player.hidePlayer(onlinePlayer);
            }

            Message.SUCCESS.send(sender, "Alvo selecionado com sucesso!");
        }
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && event.getDamager() instanceof Player) {

            Player damager = (Player) event.getDamager();

            User damagerUser = CoreProvider.Cache.Local.USERS.provide().get(damager.getName());

            int staffId = targets.get(damagerUser.getId().intValue());

            if (staffId != targets.defaultReturnValue()) {

                User user = CoreProvider.Cache.Local.USERS.provide().get(staffId);

                Player player = Bukkit.getPlayer(user.getNick());
                if (player != null) {
                    player.showPlayer(((Player) event.getEntity()));
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        targets.remove(
                CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName()).getId().intValue()
        );
    }
}
