package uk.firedev.guilds.database.serialize;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DatabaseSerializable<T> {

    @NotNull String serialize();

}
