package uk.firedev.guilds.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.Loggers;
import uk.firedev.guilds.Guilds;

import java.util.function.Consumer;

public class LoadingUtil {

    public static <T> void doOrWarn(@Nullable T object, @NotNull Consumer<T> consumer, @NotNull String message) {
        if (object == null) {
            Loggers.warn(Guilds.INSTANCE.getComponentLogger(), message);
            return;
        }
        consumer.accept(object);
    }

}
