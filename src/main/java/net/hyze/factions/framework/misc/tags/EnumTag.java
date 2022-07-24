package net.hyze.factions.framework.misc.tags;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumTag {
    
    BETA_VIP("&e[beta]"), TAG_1("&7[TAG1]"), TAG_2("&7[TAG2]");
    
    private final String text;

}
