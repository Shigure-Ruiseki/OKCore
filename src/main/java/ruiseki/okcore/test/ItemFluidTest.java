package ruiseki.okcore.test;

import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.item.IItemToggle;
import ruiseki.okcore.item.ItemOK;
import ruiseki.okcore.network.packet.PacketSyncCursorStack;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagEntry;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;

public class ItemFluidTest extends ItemOK implements IItemToggle {

    public ItemFluidTest() {
        super();
        setTextureName("stick");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null) {
            ForgeDirection direction = ForgeDirection.getOrientation(side);

            return FluidHelpers.getFluidHandler(te, direction)
                .map(handler -> {
                    if (player.isSneaking()) {
                        int fill = handler.fill(direction, new FluidStack(FluidRegistry.WATER, 1000), true);
                        return fill > 0;
                    }

                    FluidStack drain = handler.drain(direction, new FluidStack(FluidRegistry.WATER, 1000), true);
                    return drain.amount > 0;
                })
                .orElse(false);
        }
        return false;
    }

    @Override
    public void toggle(EntityPlayer player, ItemStack slotStack) {
        ItemStack cursorStack = player.inventory.getItemStack();
        if (cursorStack == null) return;

        LazyOptional<IFluidHandlerItem> cap = FluidHelpers.getFluidHandler(cursorStack);
        if (cap.isPresent()) {
            cap.ifPresent(handler -> {
                boolean isShift = GuiScreen.isShiftKeyDown();

                if (isShift) {
                    FluidStack current = FluidHelpers.getFluidContained(cursorStack);
                    Fluid fluidToAdd = (current != null) ? current.getFluid() : FluidRegistry.WATER;
                    FluidStack stackToAdd = new FluidStack(fluidToAdd, 1000);

                    int filled = handler.fill(stackToAdd, true);
                    if (filled > 0 && !player.worldObj.isRemote) {
                        player.addChatComponentMessage(
                            new ChatComponentText("§aFill " + filled + "mB " + fluidToAdd.getLocalizedName()));
                    }
                } else {
                    FluidStack drained = handler.drain(1000, true);
                    if (drained != null && !player.worldObj.isRemote) {
                        player.addChatComponentMessage(
                            new ChatComponentText(
                                "§cDrain " + drained.amount
                                    + "mB "
                                    + drained.getFluid()
                                        .getLocalizedName()));
                    } else if (!player.worldObj.isRemote) {
                        player.addChatComponentMessage(new ChatComponentText("§cEmpty!"));
                    }
                }

                if (player instanceof EntityPlayerMP playerMP) {
                    playerMP.inventory.setItemStack(cursorStack);
                    OKCore.instance.getPacketHandler()
                        .sendToPlayer(new PacketSyncCursorStack(cursorStack), playerMP);
                }
            });
        }
    }

    @Override
    public boolean needsShiftClick(ItemStack stack) {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        TagKey<Item> dustTagKey = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "dusts"));

        Set<TagEntry> entries = TagManager.getManager()
            .getEntries(dustTagKey);

        list.add("§6Items in #forge:dusts:");
        if (entries.isEmpty()) {
            list.add(" §7(Empty Tag)");
        } else {
            for (TagEntry entry : entries) {
                String itemId = entry.id()
                    .toString();
                int meta = entry.meta();
                list.add(" §7- " + itemId + (meta == TagEntry.WILDCARD ? ":*" : ":" + meta));
            }
        }
    }
}
