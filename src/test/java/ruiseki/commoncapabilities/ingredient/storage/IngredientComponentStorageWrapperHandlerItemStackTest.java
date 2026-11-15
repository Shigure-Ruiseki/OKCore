package ruiseki.commoncapabilities.ingredient.storage;

import static org.junit.Assert.*;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.Test;

import ruiseki.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerItemStack.ComponentStorageWrapper;
import ruiseki.okcore.item.handler.ItemStackHandler;

public class IngredientComponentStorageWrapperHandlerItemStackTest {

    private ItemStackHandler largeSlot(int count) {
        ItemStackHandler handler = new ItemStackHandler(1) {

            @Override
            public int getSlotLimit(int slot) {
                return 1024;
            }
        };
        if (count > 0) handler.setStackInSlot(0, new ItemStack(new Item(), count));
        return handler;
    }

    @Test
    public void largeExtractionAccumulatesMultipleBatchesFromInitiallyEmptyBuffer() {
        ItemStackHandler handler = largeSlot(200);
        ComponentStorageWrapper wrapper = new ComponentStorageWrapper(null, handler);
        ItemStack result = wrapper.extract(0, 150, false);
        assertEquals(150, result.stackSize);
        assertEquals(50, handler.getStackInSlot(0).stackSize);
    }

    @Test
    public void simulationReturnsRequestedCountWithoutMutatingOversizedSlot() {
        ItemStackHandler handler = largeSlot(200);
        ComponentStorageWrapper wrapper = new ComponentStorageWrapper(null, handler);
        ItemStack result = wrapper.extract(0, 150, true);
        assertEquals(150, result.stackSize);
        assertEquals(200, handler.getStackInSlot(0).stackSize);
        assertNotSame(handler.getStackInSlot(0), result);
    }

    @Test
    public void largeRequestStopsWhenSlotIsExhausted() {
        ItemStackHandler handler = largeSlot(90);
        ComponentStorageWrapper wrapper = new ComponentStorageWrapper(null, handler);
        assertEquals(90, wrapper.extract(0, 200, false).stackSize);
        assertNull(handler.getStackInSlot(0));
    }

    @Test
    public void oversizedSimulationHandlesEmptyOrPartiallyFilledSlot() {
        ItemStackHandler handler = largeSlot(0);
        ComponentStorageWrapper wrapper = new ComponentStorageWrapper(null, handler);
        assertNull(wrapper.extract(0, 200, true));
        handler.setStackInSlot(0, new ItemStack(new Item(), 20));
        assertEquals(20, wrapper.extract(0, 200, true).stackSize);
        assertEquals(20, handler.getStackInSlot(0).stackSize);
    }
}
