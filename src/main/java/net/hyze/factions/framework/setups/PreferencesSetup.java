package net.hyze.factions.framework.setups;

import dev.utils.shared.setup.SetupException;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.spigot.misc.preference.CorePreference;
import net.hyze.core.spigot.misc.preference.PreferenceIcon;
import net.hyze.core.spigot.misc.preference.PreferenceInventoryRegistry;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsCustomPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PreferencesSetup<T extends FactionsCustomPlugin> extends FactionsSetup<T> {

    @Override
    public void enable(T plugin) throws SetupException {

        PreferenceInventoryRegistry.registry(
                CorePreference.TPA.name(),
                new PreferenceIcon(
                        "Pedidos de TPA",
                        new String[]{"Desabilite os pedidos de teleporte."},
                        new ItemStack(Material.ENDER_PEARL)
                ),
                PreferenceStatus.ON
        );

        PreferenceInventoryRegistry.registry(
                FactionsConstants.UserPreference.CHAT_LOCAL,
                new PreferenceIcon(
                        "Chat Local",
                        new String[]{"Desabilite as mensagens do chat local."},
                        new ItemStack(Material.PAPER)
                ),
                PreferenceStatus.ON
        );

        PreferenceInventoryRegistry.registry(
                FactionsConstants.UserPreference.CHAT_GLOBAL,
                new PreferenceIcon(
                        "Chat Global",
                        new String[]{"Desabilite as mensagens do chat global."},
                        new ItemStack(Material.PAPER)
                ),
                PreferenceStatus.ON
        );

        /*
        PreferenceInventoryRegistry.registry(
                FactionsConstants.UserPreference.MINING_DROPS,
                new PreferenceIcon(
                        "Drops da Mina",
                        new String[]{
                                "Habilite para dropar itens dos blocos",
                                "quebrados na mina.",
                                "",
                                "* Válido também para o mundo", "de escavação."
                        },
                        new ItemStack(Material.STONE_PICKAXE)
                ),
                PreferenceStatus.ON
        );
         */


        PreferenceInventoryRegistry.registry(
                FactionsConstants.UserPreference.SPAWNER_EFFECT,
                new PreferenceIcon(
                        "Efeitos dos Geradores",
                        new String[]{
                                "Desabilite os efeitos visuais",
                                "dos geradores."
                        },
                        new ItemStack(Material.MOB_SPAWNER)
                ),
                PreferenceStatus.ON
        );

//        PreferenceInventoryRegistry.registry(
//                FactionsConstants.UserPreference.PARTY_INVITE,
//                new PreferenceIcon(
//                        "Pedidos de Party",
//                        new String[]{"Desabilite os pedidos de party."},
//                        new ItemStack(Material.IRON_SWORD)
//                ),
//                PreferenceStatus.ON
//        );
    }
}
