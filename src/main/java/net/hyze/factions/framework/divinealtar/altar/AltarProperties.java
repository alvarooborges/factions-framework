package net.hyze.factions.framework.divinealtar.altar;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.hyze.factions.framework.divinealtar.power.PowerInstance;

import java.util.Map;

public class AltarProperties {

    @Getter
    private final Map<PowerInstance, Integer> gems = Maps.newHashMap();

    @Getter
    private final Map<PowerInstance, Long> activePowers = Maps.newHashMap();

    @Getter
    private final Map<PowerInstance, Long> activeCooldowns = Maps.newHashMap();

    /**
     * Verifica se o altar possuí o valor passado pela variável "value" como
     * saldo.
     *
     * @param power
     * @param value
     * @return
     */
    public boolean hasBalance(PowerInstance power, Integer value) {
        return getBalance(power) >= value;
    }

    /**
     * Retorna o saldo em gemas do poder.
     *
     * @param power
     * @return
     */
    public Integer getBalance(PowerInstance power) {
        return this.gems.getOrDefault(power, 0);
    }

    /**
     * Subtrai o valor (em gemas) passado.
     *
     * @param power
     * @param value
     */
    public void subtractBalance(PowerInstance power, Integer value) {
        int result = this.gems.put(power, getBalance(power) - value);
        
        if(result == 0){
            this.gems.remove(power);
        }
    }

    /**
     * Adiciona o valor (em gemas) passado.
     *
     * @param power
     * @param value
     */
    public void addBalance(PowerInstance power, Integer value) {
        this.gems.put(power, getBalance(power) + value);
    }

}
