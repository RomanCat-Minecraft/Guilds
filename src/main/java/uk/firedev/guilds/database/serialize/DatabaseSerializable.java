package uk.firedev.guilds.database.serialize;

import org.jetbrains.annotations.NotNull;

public interface DatabaseSerializable<T> {

    @NotNull String serialize();

}
