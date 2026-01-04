package uk.firedev.guilds.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.util.Loggers;
import uk.firedev.guilds.Guilds;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LoadingUtil {

    public static <T> void doOrWarn(@Nullable T object, @NotNull Consumer<T> consumer, @NotNull String message) {
        if (object == null) {
            Loggers.warn(Guilds.getInstance().getComponentLogger(), message);
            return;
        }
        consumer.accept(object);
    }

    @SafeVarargs
    public static <T> List<T> mergeLists(@NotNull List<T>... lists) {
        List<T> finalList = new ArrayList<>();
        for (List<T> list : lists) {
            finalList.addAll(list);
        }
        return finalList;
    }

}
