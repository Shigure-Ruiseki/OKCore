package ruiseki.okcore.fluid.handler;

import static org.junit.Assert.*;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.junit.Test;

public class FluidHandlerItemStackTest {

    private FluidStack water(int amount) {
        return new FluidStack(FluidRegistry.WATER, amount);
    }

    @Test
    public void fillSimulationRespectsCapacityWithoutCreatingNbtOrChangingInput() {
        ItemStack container = new ItemStack(new Item());
        FluidHandlerItemStack handler = new FluidHandlerItemStack(container, 1000);
        FluidStack input = water(1500);
        assertEquals(1000, handler.fill(input, false));
        assertFalse(container.hasTagCompound());
        assertNull(handler.getFluid());
        assertEquals(1500, input.amount);
        assertEquals(1000, handler.fill(input, true));
        assertEquals(1000, handler.getFluid().amount);
        assertEquals(1500, input.amount);
        assertEquals(0, handler.fill(water(1), true));
    }

    @Test
    public void rejectsEmptyResourcesAndMultipleContainers() {
        FluidHandlerItemStack handler = new FluidHandlerItemStack(new ItemStack(new Item(), 2), 1000);
        assertEquals(0, handler.fill(water(100), true));
        assertNull(handler.drain(100, true));
        handler = new FluidHandlerItemStack(new ItemStack(new Item()), 1000);
        assertEquals(0, handler.fill(null, true));
        assertEquals(0, handler.fill(water(0), true));
        assertEquals(0, handler.fill(water(-1), true));
        assertNull(handler.drain(0, true));
        assertNull(handler.drain(-1, true));
        assertNull(handler.drain((FluidStack) null, true));
    }

    @Test
    public void drainSimulationAndPartialDrainPreserveStateAndRequest() {
        FluidHandlerItemStack handler = new FluidHandlerItemStack(new ItemStack(new Item()), 1000);
        handler.fill(water(800), true);
        FluidStack request = water(300);
        assertEquals(300, handler.drain(request, false).amount);
        assertEquals(800, handler.getFluid().amount);
        assertEquals(300, request.amount);
        assertEquals(300, handler.drain(request, true).amount);
        assertEquals(500, handler.getFluid().amount);
        assertEquals(300, request.amount);
    }

    @Test
    public void completeDrainRemovesOnlyFluidTagAndKeepsContainer() {
        ItemStack container = new ItemStack(new Item());
        container.setTagCompound(new NBTTagCompound());
        container.getTagCompound()
            .setString("owner", "test");
        FluidHandlerItemStack handler = new FluidHandlerItemStack(container, 1000);
        handler.fill(water(250), true);
        assertEquals(250, handler.drain(Integer.MAX_VALUE, true).amount);
        assertNull(handler.getFluid());
        assertFalse(
            container.getTagCompound()
                .hasKey("Fluid"));
        assertEquals(
            "test",
            container.getTagCompound()
                .getString("owner"));
        assertSame(container, handler.getContainer());
    }

    @Test
    public void rejectsDifferentFluidAndDifferentTags() {
        FluidHandlerItemStack handler = new FluidHandlerItemStack(new ItemStack(new Item()), 1000);
        handler.fill(water(500), true);
        FluidStack lava = new FluidStack(FluidRegistry.LAVA, 100);
        assertEquals(0, handler.fill(lava, true));
        assertNull(handler.drain(lava, true));
        FluidStack tagged = water(100);
        tagged.tag = new NBTTagCompound();
        tagged.tag.setString("variant", "different");
        assertEquals(0, handler.fill(tagged, true));
        assertNull(handler.drain(tagged, true));
        assertEquals(500, handler.getFluid().amount);
    }

    @Test
    public void negativeCapacityAndInvalidTanksAreEmpty() {
        FluidHandlerItemStack handler = new FluidHandlerItemStack(new ItemStack(new Item()), -1);
        assertEquals(0, handler.getTanks());
        assertEquals(0, handler.fill(water(1), true));
        handler = new FluidHandlerItemStack(new ItemStack(new Item()), 1000);
        assertEquals(1, handler.getTanks());
        assertEquals(1000, handler.getTankCapacity(0));
        for (int index : new int[] { -1, 1, Integer.MAX_VALUE }) {
            assertEquals(0, handler.getTankCapacity(index));
            assertNull(handler.getFluidInTank(index));
            assertFalse(handler.isFluidValid(index, water(1)));
        }
        assertFalse(handler.isFluidValid(0, water(0)));
    }

    @Test
    public void consumableAndSwapOnlyReplaceContainerOnActualCompleteDrain() {
        ItemStack container = new ItemStack(new Item());
        FluidHandlerItemStack consumable = new FluidHandlerItemStack.Consumable(container, 1000);
        consumable.fill(water(100), true);
        consumable.drain(100, false);
        assertSame(container, consumable.getContainer());
        consumable.drain(100, true);
        assertNull(consumable.getContainer());

        ItemStack empty = new ItemStack(new Item());
        FluidHandlerItemStack swap = new FluidHandlerItemStack.SwapEmpty(new ItemStack(new Item()), empty, 1000);
        swap.fill(water(100), true);
        swap.drain(100, true);
        assertNotSame(empty, swap.getContainer());
        assertSame(
            empty.getItem(),
            swap.getContainer()
                .getItem());
        assertEquals(1, empty.stackSize);
    }
}
