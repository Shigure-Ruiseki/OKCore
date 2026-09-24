package ruiseki.okcore.inventory.container;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.inventory.ClickType;
import ruiseki.okcore.inventory.IValueNotifiable;
import ruiseki.okcore.inventory.IValueNotifier;
import ruiseki.okcore.inventory.container.button.IContainerButtonAction;
import ruiseki.okcore.inventory.container.button.IContainerButtonClickAcceptorServer;
import ruiseki.okcore.inventory.slot.SlotArmor;
import ruiseki.okcore.inventory.slot.SlotExtended;
import ruiseki.okcore.network.packet.PacketValueNotify;

public abstract class ContainerExtended extends Container
    implements IValueNotifier, IValueNotifiable, IContainerButtonClickAcceptorServer<ContainerExtended> {

    protected static final int ITEMBOX = 18;

    private final Map<String, IContainerButtonAction<ContainerExtended>> buttonActions = Maps.newHashMap();
    private final Map<Integer, NBTTagCompound> values = Maps.newHashMap();
    private final List<SyncedGuiVariable<?>> syncedGuiVariables = Lists.newArrayList();
    private int nextValueId = 0;
    private IValueNotifiable guiValueListener = null;

    protected ContainerType<?> containerType;
    private IInventory playerIInventory;
    protected final EntityPlayer player;
    protected int offsetX = 0;
    protected int offsetY = 0;

    /* The current drag mode (0 : evenly split, 1 : one item by slot, 2 : not used ?) */
    private int dragMode = -1;
    /** The current drag event (0 : start, 1 : add slot : 2 : end) */
    private int dragEvent;
    /** The list of slots where the itemstack holds will be distributed */
    private final Set<Slot> dragSlots = Sets.newHashSet();

    /**
     * Make a new ContainerExtended.
     *
     * @param type      The container type.
     * @param inventory The player inventory.
     */
    public ContainerExtended(@Nullable ContainerType<?> type, InventoryPlayer inventory) {
        this.containerType = type;
        this.playerIInventory = inventory;
        this.player = inventory.player;
    }

    public ContainerType<?> getType() {
        if (this.containerType == null) {
            throw new UnsupportedOperationException("Unable to construct this menu by type");
        } else {
            return this.containerType;
        }
    }

    @Override
    public ContainerType<?> getValueNotifiableType() {
        return getType();
    }

    protected static void checkContainerSize(IInventory inventory, int size) {
        int i = inventory.getSizeInventory();
        if (i < size) {
            throw new IllegalArgumentException("Inventory size " + i + " is smaller than expected " + size);
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!MinecraftHelpers.isClientSide()) {
            for (SyncedGuiVariable<?> syncedGuiVariable : this.syncedGuiVariables) {
                syncedGuiVariable.detectAndSendChanges();
            }
        }
    }

    /**
     * Set the listener that will be triggered when a value in this container is updated by the server.
     *
     * @param listener The listener that will be triggered.
     */
    public void setGuiValueListener(IValueNotifiable listener) {
        this.guiValueListener = listener;
    }

    @Override
    public void addCraftingToCrafters(ICrafting listener) {
        super.addCraftingToCrafters(listener);
        if (!MinecraftHelpers.isClientSide()) {
            initializeValues();
        }
    }

    /**
     * This is the place to initialize values server-side so that they can be sent to the client for the first time.
     * This is only called on the server.
     */
    protected void initializeValues() {

    }

    protected Slot createNewSlot(IInventory inventory, int index, int x, int y) {
        return new Slot(inventory, index, x, y);
    }

    public static void setSlotPosX(Slot slot, int newValue) {
        slot.xDisplayPosition = newValue;
    }

    public static void setSlotPosY(Slot slot, int newValue) {
        slot.yDisplayPosition = newValue;
    }

    @Override
    protected Slot addSlotToContainer(Slot slot) {
        setSlotPosX(slot, slot.xDisplayPosition + offsetX);
        setSlotPosY(slot, slot.yDisplayPosition + offsetY);
        return super.addSlotToContainer(slot);
    }

    protected void addInventory(IInventory inventory, int indexOffset, int offsetX, int offsetY, int rows, int cols) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                // Slot params: id, x-coord, y-coord (coords are relative to gui box)
                addSlotToContainer(
                    createNewSlot(inventory, x + y * cols + indexOffset, offsetX + x * ITEMBOX, offsetY + y * ITEMBOX));
            }
        }
    }

    /**
     * Add player inventory and hotbar to the GUI.
     */
    protected void addPlayerInventory(InventoryPlayer inventory, int offsetX, int offsetY) {
        int rows = 3;
        int cols = 9;

        // Player hotbar
        addInventory(inventory, 0, offsetX, offsetY + 58, 1, cols);

        // Player inventory
        addInventory(inventory, cols, offsetX, offsetY, rows, cols);
    }

    /**
     * Add player armor inventory to the GUI.
     */
    protected void addPlayerArmorInventory(InventoryPlayer inventory, int offsetX, int offsetY) {
        for (int y = 0; y < 4; y++) {
            addSlotToContainer(
                new SlotArmor(inventory, 4 * 9 + (3 - y), offsetX, offsetY + y * ITEMBOX, inventory.player, y));
        }
    }

    protected abstract int getSizeInventory();

    protected int getSlotStart(int originSlot, int slotStart, boolean reverse) {
        return slotStart;
    }

    protected int getSlotRange(int originSlot, int slotRange, boolean reverse) {
        return slotRange;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack stack = ItemHelpers.EMPTY;
        Slot slot = this.inventorySlots.get(slotID);
        int slots = getSizeInventory();

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack()
                .copy();
            stack = stackInSlot.copy();

            if (slotID < slots) { // Click in tile -> player inventory
                if (!mergeItemStack(
                    stackInSlot,
                    getSlotStart(slotID, slots, true),
                    getSlotRange(slotID, this.inventorySlots.size(), true),
                    true)) {
                    return ItemHelpers.EMPTY;
                }
            } else if (!mergeItemStack(
                stackInSlot,
                getSlotStart(slotID, 0, false),
                getSlotRange(slotID, slots, false),
                false)) { // Click in player inventory -> tile
                    return ItemHelpers.EMPTY;
                }

            if (stackInSlot.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (stackInSlot.stackSize == stack.stackSize) {
                return ItemHelpers.EMPTY;
            }

            slot.onPickupFromSlot(player, stackInSlot);
        }

        return stack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int slotStart, int slotRange, boolean reverse) {
        boolean successful = false;
        int slotIndex = slotStart;
        int maxStack = stack.getMaxStackSize();

        if (reverse) {
            slotIndex = slotRange - 1;
        }

        Slot slot;
        ItemStack existingStack;

        if (stack.isStackable()) {
            while (stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart)) {
                slot = this.inventorySlots.get(slotIndex);
                int maxSlotSize = Math.min(slot.getSlotStackLimit(), maxStack);
                existingStack = !ItemHelpers.isEmpty(slot.getStack()) ? slot.getStack()
                    .copy() : ItemHelpers.EMPTY;

                if (slot.isItemValid(stack) && !ItemHelpers.isEmpty(existingStack)
                    && existingStack.getItem() == stack.getItem()
                    && (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage())
                    && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
                    int existingSize = existingStack.stackSize + stack.stackSize;
                    if (existingSize <= maxSlotSize) {
                        stack.stackSize = 0;
                        existingStack.stackSize = existingSize;
                        slot.putStack(existingStack);
                        successful = true;
                    } else if (existingStack.stackSize < maxSlotSize) {
                        stack.stackSize -= maxSlotSize - existingStack.stackSize;
                        existingStack.stackSize = maxSlotSize;
                        slot.putStack(existingStack);
                        successful = true;
                    }
                }

                if (reverse) {
                    --slotIndex;
                } else {
                    ++slotIndex;
                }
            }
        }

        if (stack.stackSize > 0) {
            if (reverse) {
                slotIndex = slotRange - 1;
            } else {
                slotIndex = slotStart;
            }

            while (stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart)) {
                slot = (Slot) this.inventorySlots.get(slotIndex);
                existingStack = slot.getStack();

                if (slot.isItemValid(stack) && existingStack == null) {
                    int placedAmount = Math.min(stack.stackSize, slot.getSlotStackLimit());
                    ItemStack toPut = stack.copy();
                    toPut.stackSize = placedAmount;
                    slot.putStack(toPut);
                    stack.stackSize -= placedAmount;
                    successful = true;
                }

                if (reverse) {
                    --slotIndex;
                } else {
                    ++slotIndex;
                }
            }
        }

        return successful;
    }

    /**
     * Get the inventory of the player for which this container is instantiated.
     *
     * @return The player inventory.
     */
    public IInventory getPlayerIInventory() {
        return playerIInventory;
    }

    @Override
    public final ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {
        return this.slotClick(slotId, clickedButton, ClickType.fromNumber(mode), player);
    }

    public ItemStack slotClick(int slotId, int clickedButton, ClickType clickType, EntityPlayer player) {
        Slot slot = slotId < 0 ? null : (Slot) this.inventorySlots.get(slotId);
        InventoryPlayer inventoryplayer = player.inventory;

        if (clickType == ClickType.QUICK_CRAFT) {
            int previousDragEvent = this.dragEvent;
            this.dragEvent = func_94532_c(clickedButton);

            if ((previousDragEvent != 1 || this.dragEvent != 2) && previousDragEvent != this.dragEvent) {
                this.func_94533_d();
            } else if (inventoryplayer.getItemStack() == null) {
                this.func_94533_d();
            } else if (this.dragEvent == 0) {
                this.dragMode = func_94529_b(clickedButton);

                if (func_94528_d(this.dragMode)) {
                    this.dragEvent = 1;
                    this.dragSlots.clear();
                } else {
                    this.func_94533_d();
                }
            } else if (this.dragEvent == 1) {
                Slot dragTargetSlot = (Slot) this.inventorySlots.get(slotId);
                ItemStack heldStack = inventoryplayer.getItemStack();

                if (dragTargetSlot != null && func_94527_a(dragTargetSlot, heldStack, true)
                    && dragTargetSlot.isItemValid(heldStack)
                    && (this.dragMode == 2 || heldStack.stackSize > this.dragSlots.size())
                    && this.canDragIntoSlot(dragTargetSlot)) {
                    this.dragSlots.add(dragTargetSlot);
                }
            } else if (this.dragEvent == 2) {
                if (!this.dragSlots.isEmpty()) {
                    ItemStack originalHeld = inventoryplayer.getItemStack()
                        .copy();
                    int remainingCount = inventoryplayer.getItemStack().stackSize;
                    int phantomCount = 0;

                    for (Slot targetSlot : this.dragSlots) {
                        ItemStack currentHeld = inventoryplayer.getItemStack();

                        if (targetSlot != null && func_94527_a(targetSlot, currentHeld, true)
                            && targetSlot.isItemValid(currentHeld)
                            && (this.dragMode == 2 || currentHeld.stackSize >= this.dragSlots.size())
                            && this.canDragIntoSlot(targetSlot)) {
                            ItemStack calculatedStack = originalHeld.copy();
                            int currentSlotCount = targetSlot.getHasStack() ? targetSlot.getStack().stackSize : 0;
                            func_94525_a(this.dragSlots, this.dragMode, calculatedStack, currentSlotCount);
                            int maxAllowed = Math
                                .min(calculatedStack.getMaxStackSize(), targetSlot.getSlotStackLimit());

                            if (calculatedStack.stackSize > maxAllowed) {
                                calculatedStack.stackSize = maxAllowed;
                            }

                            remainingCount -= calculatedStack.stackSize - currentSlotCount;
                            targetSlot.putStack(calculatedStack);

                            // --- Added ---
                            if (targetSlot instanceof SlotExtended && ((SlotExtended) targetSlot).isPhantom()) {
                                phantomCount += calculatedStack.stackSize - currentSlotCount;
                            }
                        }
                    }

                    originalHeld.stackSize = remainingCount + phantomCount; // Changed
                    if (originalHeld.stackSize <= 0) {
                        inventoryplayer.setItemStack(null);
                    } else {
                        inventoryplayer.setItemStack(originalHeld);
                    }
                }

                this.func_94533_d();
            } else {
                this.func_94533_d();
            }
            return null;
        } else if (this.dragEvent != 0) {
            this.func_94533_d();
            return null;
        } else if (slot instanceof SlotExtended && ((SlotExtended) slot).isPhantom()) {// Phantom slot logic added
            return slotClickPhantom(slot, clickedButton, clickType, player);
        } else {
            // All other cases are delegated to the original code
            return super.slotClick(slotId, clickedButton, clickType.toNumber(), player);
        }
    }

    private ItemStack slotClickPhantom(Slot slot, int mouseButton, ClickType clickType, EntityPlayer player) {
        ItemStack stack = null;

        if (mouseButton == 2) {
            if (((SlotExtended) slot).isAdjustable()) {
                slot.putStack(null);
            }
        } else if (mouseButton == 0 || mouseButton == 1) {
            InventoryPlayer playerInv = player.inventory;
            slot.onSlotChanged();
            ItemStack stackSlot = slot.getStack();
            ItemStack stackHeld = playerInv.getItemStack();

            if (stackSlot != null) {
                stack = stackSlot.copy();
            }

            if (stackSlot == null) {
                if (stackHeld != null && slot.isItemValid(stackHeld)) {
                    fillPhantomSlot(slot, stackHeld, mouseButton, clickType);
                }
            } else if (stackHeld == null) {
                adjustPhantomSlot(slot, mouseButton, clickType);
                slot.onPickupFromSlot(player, playerInv.getItemStack());
            } else if (slot.isItemValid(stackHeld)) {
                if (ItemStack.areItemStacksEqual(stackSlot, stackHeld)
                    && ItemStack.areItemStackTagsEqual(stackSlot, stackHeld)) {
                    adjustPhantomSlot(slot, mouseButton, clickType);
                } else {
                    fillPhantomSlot(slot, stackHeld, mouseButton, clickType);
                }
            }
        }
        return stack;
    }

    protected void adjustPhantomSlot(Slot slot, int mouseButton, ClickType clickType) {
        if (!((SlotExtended) slot).isAdjustable()) {
            return;
        }
        ItemStack stackSlot = slot.getStack();
        if (stackSlot == null) return;

        int stackSize;
        if (clickType == ClickType.QUICK_MOVE) {
            stackSize = mouseButton == 0 ? (stackSlot.stackSize + 1) / 2 : stackSlot.stackSize * 2;
        } else {
            stackSize = mouseButton == 0 ? stackSlot.stackSize - 1 : stackSlot.stackSize + 1;
        }

        if (stackSize > slot.getSlotStackLimit()) {
            stackSize = slot.getSlotStackLimit();
        }

        stackSlot.stackSize = stackSize;

        if (stackSlot.stackSize <= 0) {
            slot.putStack(null);
        }
    }

    protected void fillPhantomSlot(Slot slot, ItemStack stackHeld, int mouseButton, ClickType clickType) {
        if (!((SlotExtended) slot).isAdjustable()) {
            return;
        }
        int stackSize = mouseButton == 0 ? stackHeld.stackSize : 1;
        if (stackSize > slot.getSlotStackLimit()) {
            stackSize = slot.getSlotStackLimit();
        }
        ItemStack phantomStack = stackHeld.copy();
        phantomStack.stackSize = stackSize;

        slot.putStack(phantomStack);
    }

    @Override
    protected void func_94533_d() {
        super.func_94533_d();
        this.dragEvent = 0;
        this.dragSlots.clear();
    }

    @Override
    public void putButtonAction(String buttonId, IContainerButtonAction<ContainerExtended> action) {
        buttonActions.put(buttonId, action);
    }

    @Override
    public boolean onButtonClick(String buttonId) {
        IContainerButtonAction<ContainerExtended> action;
        if ((action = buttonActions.get(buttonId)) != null) {
            action.onAction(buttonId, this);
            return true;
        }
        return false;
    }

    protected int getNextValueId() {
        return nextValueId++;
    }

    @Override
    public void setValue(int valueId, NBTTagCompound value) {
        if (!values.containsKey(valueId) || !values.get(valueId)
            .equals(value)) {
            if (!player.worldObj.isRemote) { // server -> client
                OKCore._instance.getPacketHandler()
                    .sendToPlayer(new PacketValueNotify(getType(), valueId, value), (EntityPlayerMP) player);
            } else { // client -> server
                OKCore._instance.getPacketHandler()
                    .sendToServer(new PacketValueNotify(getType(), valueId, value));
            }
            values.put(valueId, value);
        }
    }

    @Override
    public NBTTagCompound getValue(int valueId) {
        return values.get(valueId);
    }

    @Override
    public Set<Integer> getValueIds() {
        return values.keySet();
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        values.put(valueId, value);
        if (guiValueListener != null) {
            guiValueListener.onUpdate(valueId, value);
        }
    }

    /**
     * Register the given variable for automatically sychronizing between client and server.
     *
     * This method should be called in the constructor of a container,
     * and the resulting supplier should be stored.
     * This resulting supplier can be called at any time by the client to lookup values.
     *
     * @param clazz               The class of the variable to sync.
     * @param serverValueSupplier A supplier for the server-side variable value.
     * @param <T>                 The variable type.
     * @return A supplier that can be called for retrieving the value.
     */
    public <T> Supplier<T> registerSyncedVariable(Class<T> clazz, Supplier<T> serverValueSupplier) {
        SyncedGuiVariable<T> variable = new SyncedGuiVariable<>(this, clazz, serverValueSupplier);
        this.syncedGuiVariables.add(variable);
        return variable;
    }
}
