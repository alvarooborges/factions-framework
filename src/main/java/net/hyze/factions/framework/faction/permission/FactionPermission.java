package net.hyze.factions.framework.faction.permission;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;

import java.util.Set;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum FactionPermission {

    BUILD(1, "Alterar terrenos", ItemBuilder.of(Material.WOOD_PICKAXE), new String[]{
            "Permite que o jogador coloque",
            "ou quebre blocos dentro de",
            "terras da sua facção."
    }),
    ACCESS_CONTAINERS(2, "Acessar Contêiners", ItemBuilder.of(Material.CHEST), new String[]{
            "Permite que o jogador interaja com",
            "contêiners nas terras de sua facção.",
            "",
            "&eIsso inclui: &fEjetores, liberadores,",
            "&7suporte de poções, fornalhas, baús",
            "&7e mesas de encantamento."
    }),
    ACCESS_BEACON(4, "Modificar Sinalizadores", ItemBuilder.of(Material.BEACON), new String[]{
            "Permite que o jogador acesse",
            "ou quebre Sinalizadores que",
            "estiverem em terras da sua ",
            "facção."
    }, null),
    ACTIVATE_REDSTONE(8, "Utilizar Redstone", ItemBuilder.of(Material.REDSTONE), new String[]{
            "Permite que o jogador interaja com",
            "circuitos de redstone nas terras",
            "de sua facção.",
            "",
            "&eIsso inclui: &7Comparadores de redstone,",
            "&7liberadores, ejetores, botões, portas,",
            "&7placas de supressão e repetidores."
    }),
    WITHDRAW_SPAWNERS(16, "Coletar Geradores", ItemBuilder.of(Material.MOB_SPAWNER), new String[]{
            "Permite que o jogador colete",
            "os geradores que estiverem",
            "armazenados no /f geradores."
    }),
    PERSONAL_HOME(32, "Homes na facção", ItemBuilder.of(Material.MYCEL), new String[]{
            "Permite que um jogador defina",
            "e vá até homes nas",
            "terras da sua facção."
    }),
    TPACCEPT(64, "Aceitar Pedidos de Teleporte", ItemBuilder.of(Material.ENDER_PEARL), new String[]{
            "Permite que o jogador aceite pedidos",
            "de teletransporte de jogadores de",
            "outras facções dentro das terras",
            "da sua facção."
    }),
    EXPLOSIONS(128, "Explosões", ItemBuilder.of(Material.TNT), new String[]{
            "Permite que o jogador crie",
            "explosões dentro de terras",
            "da sua facção."
    }, null),
    COMMAND_CLAIM(256, "Dominar Terras", ItemBuilder.of(Material.GRASS), new String[]{
            "Permite que o jogador domine",
            "novas terras para a sua",
            "facção."
    }, true),
    COMMAND_ABANDON(512, "Abandonar Terras", ItemBuilder.of(Material.DIRT), new String[]{
            "Permite que o jogador abandone",
            "terras da sua facção."
    }, true),
    COMMAND_KICK(1024, "Expulsar Jogadores", ItemBuilder.of(Material.FLINT_AND_STEEL), new String[]{
            "Permite que o jogador expulse",
            "membros da sua facção."
    }, true),
    COMMAND_INVITE(2048, "Recrutar Novos Jogadores", ItemBuilder.of(Material.GOLD_HELMET), new String[]{
            "Permite que o jogador convide",
            "novos jogadores para a sua", "facção."
    }, true),
    COMMAND_BASE(4096, "Base da Facção", ItemBuilder.of(Material.IRON_DOOR), new String[]{
            "Permite que o jogador acesse",
            "a base da sua facção através",
            "do comando /f base."
    }, true),
    COMMAND_SPAWNER_SET_SPAWN(8192, "Definir Spawn dos Geradores", ItemBuilder.of(Material.MONSTER_EGG, (byte) 54), new String[]{
            "Permite que o jogador defina ou",
            "altere a localização de spawn",
            "dos geradores."
    }, true),
    COMMAND_SET_BASE(16384, "Definir Base da Facção", ItemBuilder.of(Material.DARK_OAK_DOOR_ITEM), new String[]{
            "Permite que o jogador defina",
            "a base da sua facção através",
            "do comando /f setbase."
    }, true),
    DEPOSIT_PLACED_SPAWNERS(32768, "Remover Geradores", ItemBuilder.of(HeadTexture.ARROW_WHITE_DOWN.getHead()), new String[]{
            "Permite que o jogador remova",
            "os geradores que estiverem",
            "colocados nas terras da fação."
    }, null),
    BREAK_PLACED_SPAWNERS(65536, "Quebrar Geradores Colocados", ItemBuilder.of(Material.DIAMOND_PICKAXE), new String[]{
            "Permite que o jogador quebre",
            "os geradores que estiverem",
            "colocados nas terras da fação."
    });

    private final int bit;
    private final String name;
    private final ItemBuilder icon;
    private final String[] description;

    /**
     * se true a permissão só pode ser configurada
     * por cargo.
     * se null a permissão é desativada.
     * se false a permissão poderá ser configurada
     * por cargo e individualmente.
     */
    private Boolean roleOnly = false;

    public ItemBuilder getIcon() {
        return icon.clone();
    }

    public boolean allows(@NonNull Faction faction, @NonNull FactionUser user) {
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user);

        /*
         * Jogador sem facção ou facção sem aliança
         */
        if (relation == null || (!relation.getFaction().equals(faction) && !FactionUtils.isAlly(relation.getFaction(), faction))) {
            switch (this) {
                case TPACCEPT:
                case EXPLOSIONS:
                    return true;
                default:
                    return false;
            }
        }

        /*
         * Jogador faz parte de facção
         */
        if (relation.getFaction().equals(faction)) {
            Integer value = FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().getByUser(relation);

            if (value != null) {
                return (value & this.getBit()) == this.getBit();
            }

            return FactionPermission.this.allows(relation.getFaction(), relation.getRole());
        }

        /*
         * Jogador faz parte de uma facção aliada
         */
        return allows(faction, relation.getFaction());
    }

    public boolean allows(@NonNull Faction faction, @NonNull FactionRole role) {

        if (role == FactionRole.LEADER) {
            return true;
        }

        Integer value = FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().getByRole(faction, role);

        if (value != null) {
            return (value & this.bit) == this.bit;
        }

        value = getDefaultRoleValue(role);

        return (value & this.bit) == this.bit;
    }

    public boolean allows(@NonNull Faction faction, @NonNull Faction ally) {
        if (FactionUtils.isAlly(faction, ally)) {
            Integer value = FactionsProvider.Cache.Local.FACTIONS_PERMISSIONS.provide().getByAlly(faction, ally);

            if (value != null) {
                return (value & this.bit) == this.bit;
            }

            value = getDefaultAllyValue();

            return (value & this.bit) == this.bit;
        }

        return false;
    }

    public static int getDefaultRoleValue(FactionRole role) {
        Set<FactionPermission> permissions = Sets.newHashSet(ACTIVATE_REDSTONE);

        if (role.isSameOrHigher(FactionRole.MEMBER)) {
            permissions.add(ACCESS_CONTAINERS);
            permissions.add(PERSONAL_HOME);
            permissions.add(COMMAND_BASE);
            permissions.add(ACTIVATE_REDSTONE);
            permissions.add(BUILD);
            permissions.add(TPACCEPT);
            permissions.add(BREAK_PLACED_SPAWNERS);
        }

        if (role.isSameOrHigher(FactionRole.CAPTAIN)) {
            permissions.add(WITHDRAW_SPAWNERS);
            permissions.add(ACCESS_BEACON);
            permissions.add(COMMAND_KICK);
            permissions.add(COMMAND_INVITE);
            permissions.add(COMMAND_CLAIM);
            permissions.add(COMMAND_ABANDON);
        }

        return permissions.stream()
                .filter(perm -> perm.getRoleOnly() != null)
                .map((perm) -> perm.bit)
                .reduce(0, (accumulator, bit) -> accumulator | bit);
    }

    public static int getDefaultAllyValue() {
        return ACTIVATE_REDSTONE.getBit() | TPACCEPT.getBit();
    }
}
