package net.hyze.factions.framework.tasks;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class UnderAttackMessageTask implements Runnable {

    private static final CharSequence TEXT = "Sua facção está sob ataque!";
    private static String MESSAGE = TEXT.toString();

    private int i = 0;
    private final ChatColor red = ChatColor.RED;
    private final ChatColor dark = ChatColor.DARK_RED;

    public static String getMessage() {
        return MESSAGE;
    }

    @Override
    public void run() {

        List<Character> list = Lists.newArrayList(Lists.charactersOf(TEXT));

        if (i >= TEXT.length()) {
            i = 0;
        }

        list.add(i, dark.getChar()); //dark
        list.add(i, ChatColor.COLOR_CHAR);

        list.add(i + 3, red.getChar()); //red
        list.add(i + 3, ChatColor.COLOR_CHAR);

        list.add(0, red.getChar()); //red
        list.add(0, ChatColor.COLOR_CHAR);
        i++;

        MESSAGE = ChatColor.BOLD + list.stream().map(c -> (String) "" + c).collect(Collectors.joining());
    }
}
