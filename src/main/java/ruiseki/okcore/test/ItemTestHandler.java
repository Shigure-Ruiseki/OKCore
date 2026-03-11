package ruiseki.okcore.test;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.item.ItemItemStackHandler;
import ruiseki.okcore.item.capability.CapabilityItemHandler;

public class ItemTestHandler extends ItemItemStackHandler implements ICapabilityProvider {

    public final int size;

    public ItemTestHandler(ItemStack itemStack, int size) {
        super(itemStack);
        this.size = size;
    }

    @Override
    public int getSlots() {
        return size;
    }

    @Override
    protected List<ItemStack> getItemList() {
        ;

        List<ItemStack> stacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            stacks.add(null);
        }

        NBTTagCompound nbt = getItemStack().getTagCompound();
        if (nbt == null || !nbt.hasKey("Items", Constants.NBT.TAG_LIST)) {
            return stacks;
        }

        NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagList.tagCount(); ++i) {

            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");

            if (slot >= 0 && slot < stacks.size()) {

                ItemStack loadedStack = ItemStack.loadItemStackFromNBT(itemTags);

                if (loadedStack != null && itemTags.hasKey("Count", Constants.NBT.TAG_INT)) {
                    loadedStack.stackSize = itemTags.getInteger("Count");
                }

                stacks.set(slot, loadedStack);
            }
        }

        return stacks;
    }

    @Override
    protected void setItemList(List<ItemStack> stacks) {

        NBTTagList list = new NBTTagList();

        for (int i = 0; i < stacks.size(); ++i) {

            ItemStack stack = stacks.get(i);

            if (stack != null) {

                NBTTagCompound itemTag = new NBTTagCompound();

                itemTag.setInteger("Slot", i);

                stack.writeToNBT(itemTag);

                itemTag.setInteger("Count", stack.stackSize);

                list.appendTag(itemTag);
            }
        }

        NBTTagCompound nbt = getItemStack().getTagCompound();

        if (nbt == null) {
            nbt = new NBTTagCompound();
            getItemStack().setTagCompound(nbt);
        }

        nbt.setTag("Items", list);
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable ForgeDirection facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T getCapability(Capability<T> capability, ForgeDirection facing) {
        return hasCapability(capability, facing) ? (T) this : null;
    }
}
