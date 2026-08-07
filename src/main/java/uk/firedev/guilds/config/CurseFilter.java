package uk.firedev.guilds.config;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import uk.firedev.chatchannels.libs.daisylib.config.BasicConfig;
import uk.firedev.chatchannels.libs.daisylib.config.ConfigBase;
import uk.firedev.guilds.Guilds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurseFilter extends BasicConfig {

    private static final CurseFilter instance = new CurseFilter();

    private List<Pattern> curses = null;

    private CurseFilter() {
        super("curses.yml", "curses.yml", Guilds.get());
    }

    @Override
    public void reload(@NonNull File configFile) {
        super.reload(configFile);
        loadCurses();
    }

    public static @NotNull CurseFilter get() {
        return instance;
    }

    public boolean isEnabled() {
        return getConfig().getBoolean("enabled");
    }

    public @NotNull String getReplacement() {
        return getConfig().getString("replacement", "****");
    }

    public List<Pattern> getCurses() {
        return curses == null ? new ArrayList<>() : curses;
    }

    private void loadCurses() {
        if (curses == null) {
            curses = new ArrayList<>();
        } else {
            curses.clear();
        }
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
