package btw.community.abbyread.sbtt.util;

import btw.world.util.data.DataEntry;
import btw.world.util.data.component.DataComponents;
import net.minecraft.src.NBTTagCompound;

import java.lang.reflect.Constructor;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registers a persistent per-player data entry to track cumulative
 * hemp seed attempts across sessions.
 * <p>
 * This uses reflection because the PlayerDataEntry constructor
 * is package-private within BTW’s internal data system.
 */
public class ModPlayerDataEntries {

    public static final DataEntry.PlayerDataEntry<Integer> SEED_ATTEMPTS;

    static {
        try {
            // Use raw constructor type — generics are erased at runtime
            Constructor<?> ctor = DataEntry.PlayerDataEntry.class.getDeclaredConstructor(
                    String.class,
                    Supplier.class,
                    boolean.class,
                    Function.class,
                    BiConsumer.class
            );

            ctor.setAccessible(true);

            // Create a new PlayerDataEntry instance reflectively
            Object rawEntry = ctor.newInstance(
                    "SeedAttempts",
                    (Supplier<Integer>) () -> 0,
                    false,
                    (Function<NBTTagCompound, Integer>) tag -> tag.getInteger("SeedAttempts"),
                    (BiConsumer<NBTTagCompound, Integer>) (tag, val) -> tag.setInteger("SeedAttempts", val)
            );

            // Explicitly cast from Object to the specific subclass
            @SuppressWarnings("unchecked")
            DataEntry.PlayerDataEntry<Integer> entry =
                    (DataEntry.PlayerDataEntry<Integer>) rawEntry;

            // Register the entry as a player data component
            SEED_ATTEMPTS = (DataEntry.PlayerDataEntry<Integer>)
                    entry.addComponent(new DataComponents.PlayerDataComponent()).register();

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SEED_ATTEMPTS DataEntry", e);
        }
    }
}
