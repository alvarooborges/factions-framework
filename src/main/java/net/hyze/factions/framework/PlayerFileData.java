package net.hyze.factions.framework;

import net.hyze.core.spigot.misc.playerdata.storage.events.UserDataLoadEvent;
import net.hyze.core.spigot.misc.playerdata.storage.events.UserDataPreLoadEvent;
import net.hyze.core.spigot.misc.playerdata.storage.events.UserDataPreSaveEvent;
import net.hyze.core.spigot.misc.playerdata.storage.events.UserDataSaveEvent;
import net.hyze.factions.framework.user.FactionUser;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.IPlayerFileData;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;

import java.util.Optional;

public class PlayerFileData implements IPlayerFileData {

    @Override
    public void save(EntityHuman entityHuman) {

        if (!FactionsPlugin.getInstance().isEnabled()) {
            return;
        }

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(entityHuman.getName());

        if (!user.isOnline()) {
            return;
        }

        UserDataPreSaveEvent preSaveEvent = new UserDataPreSaveEvent(user.getHandle());
        Bukkit.getPluginManager().callEvent(preSaveEvent);

        if (preSaveEvent.isCancelled()) {
            return;
        }

        //  Printer.INFO.coloredPrint(String.format("&eSalvando usuário %s...", user.getHandle().getNick()));

        // long start = System.currentTimeMillis();

        NBTTagCompound compound = new NBTTagCompound();

        entityHuman.e(compound);

        compound.remove("FallDistance");
        compound.remove("Riding");
        compound.remove("SpawnX");
        compound.remove("SpawnY");
        compound.remove("SpawnZ");
        compound.remove("SpawnForced");
        compound.remove("EnderItems");
        compound.remove("SpawnWorld");
        compound.remove("Sleeping");
        compound.remove("SleepTimer");

        Bukkit.getPluginManager().callEvent(new UserDataSaveEvent(user.getHandle(), compound));

        /*boolean success =*/ FactionsProvider.Repositories.USER_DATA.provide().update(user.getHandle(), compound);

        /*
        Printer.INFO.coloredPrint(String.format(
                "&eUsuário %s salvo in %sms: %s",
                user.getHandle().getNick(),
                System.currentTimeMillis() - start,
                success
        ));
         */
    }

    @Override
    public NBTTagCompound load(EntityHuman entityHuman) {
        if (!FactionsPlugin.getInstance().isEnabled()) {
            return null;
        }

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(entityHuman.getName());

        if (user == null) {
            return null;
        }

        UserDataPreLoadEvent preloadEvent = new UserDataPreLoadEvent(user.getHandle());
        Bukkit.getPluginManager().callEvent(preloadEvent);

        if (preloadEvent.isCancelled()) {
            return preloadEvent.getCompound();
        }

        NBTTagCompound compound = Optional.ofNullable(FactionsProvider.Repositories.USER_DATA.provide().fetch(user.getHandle()))
                .orElse(new NBTTagCompound());

        Bukkit.getPluginManager().callEvent(new UserDataLoadEvent(user.getHandle(), compound));

        entityHuman.f(compound);

        return compound;
    }

    @Override
    public String[] getSeenPlayers() {
        return new String[0];
    }
}
