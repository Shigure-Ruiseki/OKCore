/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package ruiseki.okcore.fluid;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import org.jetbrains.annotations.NotNull;

/**
 * ItemStacks handled by an {@link IFluidHandler} may change, so this class allows
 * users of the fluid handler to get the container after it has been used.
 */
public interface IFluidHandlerItem extends IFluidHandler {

    /**
     * Get the container currently acted on by this fluid handler.
     * The ItemStack may be different from its initial state, in the case of fluid containers that have different items
     * for their filled and empty states.
     * May be an empty item if the container was drained and is consumable.
     */
    @NotNull
    ItemStack getContainer();

    FluidStack drain(int maxDrain, boolean doDrain);

    FluidStack drain(FluidStack stack, boolean doDrain);

    int fill(FluidStack resource, boolean doFill);

    @Override
    default FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return drain(maxDrain, doDrain);
    }

    @Override
    default FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return drain(resource, doDrain);
    }

    @Override
    default int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return fill(resource, doFill);
    }
}
