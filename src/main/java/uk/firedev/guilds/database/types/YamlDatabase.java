package uk.firedev.guilds.database.types;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.database.Database;
import uk.firedev.daisylib.database.exceptions.DatabaseLoadException;
import uk.firedev.guilds.Guilds;

import java.sql.SQLException;
import java.util.Map;

public class YamlDatabase extends Database {

    public YamlDatabase(@NotNull Guilds plugin) {
        super(plugin);
    }

    @Override
    public void save() {

    }

    @NotNull
    @Override
    public String getTable() {
        return "";
    }

    @NotNull
    @Override
    public Map<String, String> getColumns() {
        return Map.of();
    }

    @Override
    public long getAutoSaveSeconds() {
        return 0;
    }

    @Override
    public void initConnection() throws DatabaseLoadException {

    }

    @Override
    public boolean addTable(@NotNull String s, @NotNull Map<String, String> map) throws SQLException {
        return false;
    }

    @Override
    public boolean addColumn(@NotNull String s, @NotNull String s1, @NotNull String s2) throws SQLException {
        return false;
    }

}
