package net.hyze.factions.framework.lands;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.LongHashMap;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public enum LandState {

    YOUR_POSITION("Sua Posição", (byte) 122, ChatColor.YELLOW),
    FREE_LAND("Zona Livre", (byte) 25, ChatColor.GRAY),
    YOUR_CLAIM("Sua Facção", (byte) 47, ChatColor.DARK_GRAY),
    ALLY_CLAIM("Terra Aliada", (byte) 78, ChatColor.GREEN),
    TEMPORARY_CLAIM("Terra Temporária", (byte) 54, ChatColor.BLUE),
    NEUTRAL_CLAIM("Terra Neutra", (byte) 34, ChatColor.WHITE),
    CONTESTED_CLAIM("Terra Contestada", (byte) 18, ChatColor.RED),
    UNDER_ATTACK_CLAIM("Terra Sob Ataque", (byte) 82, ChatColor.LIGHT_PURPLE),
    WAR_ZONE("Zona de Guerra", (byte) 19, ChatColor.DARK_RED),
    PROTECTED_ZONE("Zona Protegida", (byte) 62, ChatColor.GOLD),
    NEUTRAL_ZONE("Zona Neutra", (byte) 98, ChatColor.DARK_PURPLE),
    LOST_FORTRESS("Base Perdida", (byte) 34, ChatColor.WHITE),
    VOID_ZONE("Fora do Mapa", (byte) 29, ChatColor.BLACK);

    public static final LongHashMap<Date> UNDER_ATTACK_CHUNK = new LongHashMap<Date>() {
        @Override
        public Date getEntry(long l) {
            Date date = super.getEntry(l);

            if (date != null && date.getTime() + TimeUnit.MINUTES.toMillis(5) < System.currentTimeMillis()) {
                remove(l);
                return null;
            }

            return date;
        }

        @Override
        public boolean contains(long l) {
            return this.getEntry(l) != null;
        }
    };

    private final String name;
    private final byte mapColor;
    private final ChatColor chatColor;

    public static LandState get(@NonNull FactionUser user, Land land) {
        if (land == null) {
            return LandState.FREE_LAND;
        }

        if (land instanceof Claim) {
            Claim claim = (Claim) land;

            if (claim.isTemporary()) {
                return LandState.TEMPORARY_CLAIM;
            }

            if (claim.isContested()) {
                return LandState.CONTESTED_CLAIM;
            }

            if (claim.getFaction() != null && claim.getFaction().isUnderAttack()) {

                long hash = LongHash.toLong(land.getChunkX(), land.getChunkZ());

                if (UNDER_ATTACK_CHUNK.getEntry(hash) != null) {
                    return LandState.UNDER_ATTACK_CLAIM;
                }
            }

            FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(user);

            if (relation != null) {
                if (Objects.equals(claim.getFaction(), relation.getFaction())) {
                    return LandState.YOUR_CLAIM;
                }

                if (FactionUtils.isAlly(claim.getFaction(), relation.getFaction())) {
                    return LandState.ALLY_CLAIM;
                }
            }

            return LandState.NEUTRAL_CLAIM;
        }

        if (land instanceof Zone) {
            Zone zone = (Zone) land;
            switch (zone.getType()) {
                case PROTECTED:
                    return LandState.PROTECTED_ZONE;
                case WAR:
                    return LandState.WAR_ZONE;
                case NEUTRAL:
                    return LandState.NEUTRAL_ZONE;
                case LOST_FORTRESS:
                    return LandState.LOST_FORTRESS;
            }
        }

        return LandState.VOID_ZONE;
    }

    public static String getTitle(@NonNull FactionUser user, Land land) {
        LandState state = LandState.get(user, land);

        if (!(land instanceof Claim)) {
            return state.getChatColor() + state.getName();
        }

        Claim claim = (Claim) land;
        Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(claim.getFactionId());

        if (claim.isContested()) {
            return String.format(
                    "%s[%s] &8&m[%s]",
                    LandState.CONTESTED_CLAIM.getChatColor(),
                    claim.getContestant().getTag(),
                    faction.getTag()
            );

        } else if (claim.isTemporary()) {
            return String.format(
                    "%s[%s] %s",
                    LandState.TEMPORARY_CLAIM.getChatColor(),
                    faction.getTag(),
                    faction.getName()
            );
        }

        ChatColor color;

        switch (state) {
            case NEUTRAL_CLAIM:
                color = ChatColor.WHITE;
                break;
            case ALLY_CLAIM:
                color = ChatColor.GREEN;
                break;
            default:
                color = ChatColor.YELLOW;
        }

        return String.format(
                "%s[%s] %s",
                color,
                faction.getTag(),
                faction.getName()
        );
    }
}
