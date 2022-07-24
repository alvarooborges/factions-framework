package net.hyze.factions.framework.divinealtar.power;

import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.altar.AltarProperties;
import net.hyze.factions.framework.divinealtar.inventory.AltarInventory;

public interface Power {

    /**
     * Id do poder.
     *
     * @return
     */
    String getId();

    /**
     * Nome do poder.
     *
     * @return
     */
    String getName();

    /**
     * Nome da gema.
     *
     * @return
     */
    String getGemName();

    /**
     * Icone do poder.
     *
     * @return
     */
    ItemBuilder getIcon();

    /**
     * Descrição do poder.
     *
     * @return
     */
    String[] getDescription();

    /**
     * Tempo de recarga do poder.
     *
     * @return
     */
    Long rechargeTime();

    /**
     * Tempo de duração do poder.
     *
     * @return
     */
    Long activeTime();

    /**
     * Retorna a moeda que o poder quer para ativar.
     *
     * @return
     */
    PowerCurrency getCurrency();

    /**
     * Retorna o preço para ativar o altar.
     *
     * @return
     */
    Integer getPrice();

    /**
     * Dispara quando o jogador ativa o poder.
     *
     * @param user
     */
    default void onActivate(User user) {

    }

    /**
     * Estado do poder.
     *
     * @param altarInventory
     * @return
     */
    default PowerState getPowerState(AltarInventory altarInventory) {
        PowerInstance instance = PowerInstance.getById(getId());
        AltarProperties altarProperties = altarInventory.getAltarProperties();

        if (altarProperties.getActivePowers().containsKey(instance)) {

            if (System.currentTimeMillis() < altarProperties.getActivePowers().get(instance)) {
                return PowerState.ACTIVE;
            }

            altarProperties.getActivePowers().remove(instance);
            altarProperties.getActiveCooldowns().put(instance, System.currentTimeMillis() + rechargeTime());

        }

        if (altarProperties.getActiveCooldowns().containsKey(instance)) {

            if (System.currentTimeMillis() < altarProperties.getActiveCooldowns().get(instance)) {
                return PowerState.IN_COOLDOWN;
            }

            altarProperties.getActiveCooldowns().remove(instance);
            FactionsProvider.Repositories.ALTAR.provide().update(altarInventory);

        }

        if (getCurrency().equals(PowerCurrency.CASH)) {
            return PowerState.AVAILABLE;
        }

        if (altarProperties.hasBalance(instance, getPrice())) {
            return PowerState.AVAILABLE;
        }

        return PowerState.WITHOUT_BALANCE;
    }

}
