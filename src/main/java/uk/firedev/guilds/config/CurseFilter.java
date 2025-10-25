package uk.firedev.guilds.config;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.guilds.Guilds;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurseFilter extends ConfigBase {

    private static final CurseFilter instance = new CurseFilter();

    private final List<Pattern> curses = new ArrayList<>();

    private CurseFilter() {
        super("curses.yml", "curses.yml", Guilds.getInstance());
    }

    @Override
    public void reload() {
        super.reload();
        loadCurses();
    }

    public static @NotNull CurseFilter getInstance() {
        return instance;
    }

    public boolean isEnabled() {
        return getConfig().getBoolean("enabled");
    }

    public @NotNull String getReplacement() {
        return getConfig().getString("replacement", "****");
    }

    public List<Pattern> getCurses() {
        return curses;
    }

    private void loadCurses() {
        curses.clear();
        getConfig().getStringList("curses").forEach(curse ->
            curses.add(Pattern.compile("(?i)\\b" + Pattern.quote(curse) + "\\b"))
        );
    }

    public boolean containsCurses(@NotNull String string) {
        if (!isEnabled()) {
            return false;
        }
        return getCurses().stream().anyMatch(pattern -> pattern.matcher(string).find());
    }

    public @NotNull String filter(@NotNull String string) {
        if (!isEnabled()) {
            return string;
        }
        String replacement = Matcher.quoteReplacement(getReplacement());
        for (Pattern pattern : curses) {
            string = pattern.matcher(string).replaceAll(replacement);
        }
        return string;
    }

}
