package uk.firedev.guilds.database;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.database.Database;
import uk.firedev.guilds.Guilds;
import uk.firedev.guilds.database.types.YamlDatabase;

public class DataManager {

    private static final DataManager instance = new DataManager();

    private Database database;

    private DataManager() {}

    public static @NotNull DataManager getInstance() {
        return instance;
    }

    public void init(@NotNull Guilds plugin) {
        this.database = new YamlDatabase(plugin);
        // TODO eventually check main config for database type.
    }

    public boolean databaseEnabled() {
        return database != null;
    }

    public @NotNull Database getDatabase() {
        if (database == null) {
            throw new IllegalStateException("Database has not been initialized!");
        }
        return database;
    }

}
