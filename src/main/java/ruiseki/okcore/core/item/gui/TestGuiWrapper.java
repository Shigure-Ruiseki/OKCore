package ruiseki.okcore.core.item.gui;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.helper.NBTHelpers;
import ruiseki.okcore.item.handler.IItemHandler;

public class TestGuiWrapper {

    private static final String CONTENTS_UUID_TAG = "contentsUuid";

    @Nullable
    private ItemStack stack;
    private Runnable inventorySlotChangeHandler = () -> {};

    @Nullable
    private InventoryHandler handler;

    public TestGuiWrapper(ItemStack stack) {
        this.stack = stack;
    }

    public void setInventorySlotChangeHandler(Runnable slotChangeHandler) {
        inventorySlotChangeHandler = slotChangeHandler;
    }

    public IItemHandler getItemHandler() {
        if (handler == null) {
            handler = new InventoryHandler(54, getBackpackContentsNbt(), () -> {
                this.markContentsDirty();
                this.inventorySlotChangeHandler.run();
            }, 64);
        }
        return handler;
    }

    private NBTTagCompound getBackpackContentsNbt() {
        return TestGuiStorage.getInstance(OKCore._instance)
            .getOrCreateContents(getOrCreateContentsUuid());
    }

    private void markContentsDirty() {
        TestGuiStorage.getInstance(OKCore._instance)
            .markDirty();
    }

    private UUID getOrCreateContentsUuid() {
        Optional<UUID> contentsUuid = getContentsUuid();
        if (contentsUuid.isPresent()) {
            return contentsUuid.get();
        }
        UUID newUuid = UUID.randomUUID();
        setContentsUuid(newUuid);
        migrateBackpackContents(newUuid);
        return newUuid;
    }

    public Optional<UUID> getContentsUuid() {
        return NBTHelpers.getUniqueId(stack, CONTENTS_UUID_TAG);
    }

    public void setContentsUuid(UUID storageUuid) {
        NBTHelpers.setUniqueId(stack, CONTENTS_UUID_TAG, storageUuid);
    }

    public void removeContentsUuid() {
        getContentsUuid().ifPresent(TestGuiStorage.getInstance(OKCore._instance)::removeContents);
        removeContentsUUIDTag();
    }

    public void removeContentsUUIDTag() {
        NBTHelpers.removeTag(stack, CONTENTS_UUID_TAG);
    }

    private void migrateBackpackContents(UUID newUuid) {
        migrateNbtTag(newUuid, InventoryHandler.INVENTORY_TAG);
    }

    private void migrateNbtTag(UUID newUuid, String key) {
        NBTHelpers.getCompoundTag(stack, key)
            .ifPresent(nbt -> {
                TestGuiStorage.getInstance(OKCore._instance)
                    .getOrCreateContents(newUuid)
                    .setTag(key, nbt);
                markContentsDirty();
                NBTHelpers.removeTag(stack, key);
            });
    }
}
