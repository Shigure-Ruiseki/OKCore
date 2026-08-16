package ruiseki.okcore.fluid.handler;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.FluidHandlerItemCapacityConfig;
import ruiseki.okcore.helper.ItemNBTHelpers;
import ruiseki.okcore.persist.nbt.INBTSerializable;

/**
 * An itemfluid handler with a mutable capacity.
 *
 * @author rubensworks
 */
public class FluidHandlerItemCapacity extends FluidHandlerItemStack
    implements IFluidHandlerItemCapacity, INBTSerializable {

    private final Fluid fluid;
    private final int capacityDefault;

    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public FluidHandlerItemCapacity(ItemStack container, int capacity) {
        this(container, capacity, null);
    }

    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     * @param fluid     The accepted fluid.
     */
    public FluidHandlerItemCapacity(ItemStack container, int capacity, Fluid fluid) {
        super(container, capacity);
        this.fluid = fluid;
        this.capacityDefault = capacity;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid resource) {
        return fluid == null || resource == null || this.fluid == resource;
    }

    @Override
    protected void setFluid(FluidStack fluid) {
        // super.setFluid(fluid); // We override the implementation completely to avoid NBT saving for empty fluids

        if (fluid != null) {
            if (!this.container.hasTagCompound()) {
                this.container.setTagCompound(new NBTTagCompound());
            }

            NBTTagCompound fluidTag = new NBTTagCompound();
            fluid.writeToNBT(fluidTag);
            this.container.getTagCompound()
                .setTag("Fluid", fluidTag);
        } else {
            if (this.container.hasTagCompound()) {
                this.container.getTagCompound()
                    .removeTag("Fluid");
            }
        }
    }

    @Override
    public void setCapacity(int capacity) {
        NBTTagCompound tag = ItemNBTHelpers.getNBT(getContainer());
        this.capacity = capacity;
        if (this.getCapacity() != this.capacityDefault) {
            tag.setInteger("capacity", capacity);
        } else {
            tag.removeTag("capacity");
        }
    }

    @Override
    public int getCapacity() {
        return ItemNBTHelpers.getInt(container, "capacity", this.capacity);
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        this.capacity = getCapacity(); // Force overriding protected capacity field as soon as possible.
        return super.getFluid();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        return capability == FluidHandlerItemCapacityConfig.CAPABILITY ? LazyOptional.of(() -> this)
            .cast() : super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        FluidStack fluid = this.getFluid();
        if (fluid != null) {
            fluid.writeToNBT(nbt);
        }
        if (this.getCapacity() != this.capacityDefault) {
            nbt.setInteger("capacity", this.getCapacity());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("capacity", Constants.NBT.TAG_INT)) {
            this.setCapacity(nbt.getInteger("capacity"));
        }
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
        this.setFluid(fluid);
    }
}
