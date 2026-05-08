package ruiseki.okcore.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datacomponent.core.DataComponent;
import ruiseki.okcore.datacomponent.core.DataComponentType;
import ruiseki.okcore.datacomponent.registry.DataComponentRegistry;

/**
 * Utility class for safe interaction with {@link DataComponent}.
 * Provides automatic resource management (closing/pooling) for ItemStack data.
 */
public class DataComponentHelpers {

    /**
     * Retrieves a value from an ItemStack's DataComponent map.
     * The component instance is automatically released/closed after the operation.
     *
     * @param stack  The ItemStack to read from.
     * @param mapper A function to extract the desired value from the DataComponent.
     * @param <T>    The type of the value to return.
     * @return The extracted value, or null if the stack is empty or invalid.
     */
    @Nullable
    public static <T> T get(ItemStack stack, Function<DataComponent, T> mapper) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }

        try (DataComponent component = DataComponentRegistry.getComponentMap(stack)) {
            return mapper.apply(component);
        }
    }

    /**
     * Retrieves a value from an ItemStack's DataComponent map.
     * The component instance is automatically released/closed after the operation.
     *
     * @param stack The ItemStack to read from.
     * @param <T>   The type of the value to return.
     * @return The extracted value, or null if the stack is empty or invalid.
     */
    @Nullable
    public static <T> T get(ItemStack stack, DataComponentType<T> componentType) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }

        try (DataComponent component = DataComponentRegistry.getComponentMap(stack)) {
            return component.get(componentType);
        }
    }

    /**
     * Performs an action or modification on an ItemStack's DataComponent map.
     * Use this for setting values or executing logic that requires the component context.
     *
     * @param stack  The ItemStack to modify.
     * @param action A consumer that performs operations on the DataComponent.
     */
    public static void set(ItemStack stack, Consumer<DataComponent> action) {
        if (stack == null || stack.getItem() == null) {
            return;
        }

        try (DataComponent component = DataComponentRegistry.getComponentMap(stack)) {
            action.accept(component);
        }
    }

    /**
     * Checks if a specific component exists for the given ItemStack.
     * Automatically handles resource cleanup.
     *
     * @param stack The ItemStack to check.
     * @param name  The name of the component.
     * @return true if the component exists.
     */
    public static boolean has(ItemStack stack, String name) {
        if (stack == null || stack.getItem() == null) return false;

        try (DataComponent component = DataComponentRegistry.getComponentMap(stack)) {
            return component.get(name) != null;
        }
    }

    /**
     * Retrieves the DataComponent map WITHOUT closing it.
     * **CRITICAL**: The caller is responsible for calling {@code .close()}
     * manually. Failure to do so will result in memory leaks or pooled instance corruption.
     *
     * @param stack The ItemStack to fetch data for.
     * @return The DataComponent instance.
     */
    public static DataComponent get(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }
        return DataComponentRegistry.getComponentMap(stack);
    }
}
