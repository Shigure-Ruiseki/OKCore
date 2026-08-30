package ruiseki.okcore.item;

import java.util.function.Consumer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.item.ItemStackPredicate;

import lombok.Getter;
import lombok.Setter;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.handler.IItemHandler;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class ItemTransfer {

    @Getter
    protected LazyOptional<IItemHandler> sourceCap = LazyOptional.empty();
    @Getter
    protected LazyOptional<IItemHandler> sinkCap = LazyOptional.empty();

    @Setter
    protected int stacksToTransfer = 1;
    @Setter
    protected int maxItemsPerTransfer = 64;
    @Setter
    protected int maxTotalTransferred = Integer.MAX_VALUE;
    @Setter
    protected int maxSinkSlotStackSize = Integer.MAX_VALUE;

    @Getter
    protected int totalItemsTransferred = 0;
    @Getter
    protected int totalStacksTransferred = 0;
    @Getter
    protected int prevItemsTransferred = 0;
    @Getter
    protected int prevStacksTransferred = 0;

    protected int[] sourceSlots, sinkSlots;

    @Setter
    protected ItemStackPredicate filter;

    @Setter
    protected Consumer<ItemStack> rejectedStacks;

    public void source(IItemHandler source) {
        this.sourceCap = source != null ? LazyOptional.of(() -> source) : LazyOptional.empty();
    }

    public void source(LazyOptional<IItemHandler> source) {
        this.sourceCap = source != null ? source : LazyOptional.empty();
    }

    public void source(Object source, ForgeDirection side) {
        this.sourceCap = ItemHelpers.getItemHandler(source, side);
    }

    public void sink(IItemHandler sink) {
        this.sinkCap = sink != null ? LazyOptional.of(() -> sink) : LazyOptional.empty();
    }

    public void sink(LazyOptional<IItemHandler> sink) {
        this.sinkCap = sink != null ? sink : LazyOptional.empty();
    }

    public void sink(Object sink, ForgeDirection side) {
        this.sinkCap = ItemHelpers.getItemHandler(sink, side);
    }

    public void push(Object self, ForgeDirection side, Object target) {
        source(self, side);
        sink(target, side != null ? side.getOpposite() : ForgeDirection.UNKNOWN);
    }

    public void pull(Object self, ForgeDirection side, Object target) {
        source(target, side != null ? side.getOpposite() : ForgeDirection.UNKNOWN);
        sink(self, side);
    }

    public void setSourceSlots(int... sourceSlots) {
        this.sourceSlots = sourceSlots;
    }

    public void setSinkSlots(int... sinkSlots) {
        this.sinkSlots = sinkSlots;
    }

    public int transfer() {
        if (!sourceCap.isPresent() || !sinkCap.isPresent()) return 0;
        if (stacksToTransfer <= 0 || maxItemsPerTransfer <= 0) return 0;

        return sourceCap.map(sourceHandler -> sinkCap.map(sinkHandler -> {

            int itemsTransferred = 0;
            int stacksTransferred = 0;

            int[] sourceIndices = (sourceSlots != null && sourceSlots.length > 0) ? sourceSlots
                : Utils.createSlotArray(sourceHandler.getSlots());
            int[] sinkIndices = (sinkSlots != null && sinkSlots.length > 0) ? sinkSlots
                : Utils.createSlotArray(sinkHandler.getSlots());

            outer: for (int srcSlot : sourceIndices) {
                if (srcSlot < 0 || srcSlot >= sourceHandler.getSlots()) continue;

                ItemStack available = sourceHandler.getStackInSlot(srcSlot);
                if (ItemHelpers.isEmpty(available)) continue;

                if (filter != null && !filter.test(available)) continue;

                int availableCount = available.stackSize;

                while (availableCount > 0) {
                    if (itemsTransferred >= maxTotalTransferred) break outer;
                    if (stacksTransferred >= stacksToTransfer) break outer;

                    int remainingAllowance = maxTotalTransferred - itemsTransferred;
                    int toTransferThisOP = Math.min(remainingAllowance, maxItemsPerTransfer);
                    int toExtract = Math.min(availableCount, toTransferThisOP);

                    ItemStack simulatedExtracted = sourceHandler.extractItem(srcSlot, toExtract, true);
                    if (ItemHelpers.isEmpty(simulatedExtracted)) break;

                    if (filter != null && !filter.test(simulatedExtracted)) break;

                    ItemStack remainder = ItemHelpers.copy(simulatedExtracted);
                    int initialCount = ItemHelpers.getItemStackSize(remainder);

                    for (int dstSlot : sinkIndices) {
                        if (dstSlot < 0 || dstSlot >= sinkHandler.getSlots()) continue;

                        int currentSlotLimit = Math.min(sinkHandler.getSlotLimit(dstSlot), maxSinkSlotStackSize);
                        ItemStack currentInSink = sinkHandler.getStackInSlot(dstSlot);
                        if (!ItemHelpers.isEmpty(currentInSink)) {
                            if (currentInSink.stackSize >= currentSlotLimit) continue;
                        }

                        remainder = sinkHandler.insertItem(dstSlot, remainder, true);
                        if (ItemHelpers.isEmpty(remainder)) break;
                    }

                    int acceptedCount = initialCount - ItemHelpers.getItemStackSize(remainder);
                    if (acceptedCount <= 0) break;

                    ItemStack actualExtracted = sourceHandler.extractItem(srcSlot, acceptedCount, false);
                    if (ItemHelpers.isEmpty(actualExtracted)) break;

                    availableCount -= actualExtracted.stackSize;
                    ItemStack realRemainder = ItemHelpers.copy(actualExtracted);

                    for (int dstSlot : sinkIndices) {
                        realRemainder = sinkHandler.insertItem(dstSlot, realRemainder, false);
                        if (ItemHelpers.isEmpty(realRemainder)) break;
                    }

                    if (!ItemHelpers.isEmpty(realRemainder)) {
                        ItemStack leftOver = sourceHandler.insertItem(srcSlot, realRemainder, false);
                        if (!ItemHelpers.isEmpty(leftOver) && rejectedStacks != null) {
                            rejectedStacks.accept(leftOver);
                        }
                    }

                    int transferred = actualExtracted.stackSize - ItemHelpers.getItemStackSize(realRemainder);
                    if (transferred <= 0) break;

                    itemsTransferred += transferred;
                    stacksTransferred++;
                }
            }

            totalItemsTransferred += itemsTransferred;
            totalStacksTransferred += stacksTransferred;
            prevItemsTransferred = itemsTransferred;
            prevStacksTransferred = stacksTransferred;

            return itemsTransferred;

        })
            .orElse(0))
            .orElse(0);
    }

    private static class Utils {

        static int[] createSlotArray(int size) {
            int[] array = new int[size];
            for (int i = 0; i < size; i++) array[i] = i;
            return array;
        }
    }
}
