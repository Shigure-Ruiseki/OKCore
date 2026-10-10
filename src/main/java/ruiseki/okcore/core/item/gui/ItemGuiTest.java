package ruiseki.okcore.core.item.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.client.mui.gui.component.slot.ModularItemSlot;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.item.ItemBase;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

public class ItemGuiTest extends ItemBase implements IGuiHolder<PlayerInventoryGuiData> {

    public ItemGuiTest() {

    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean flag) {
        super.addInformation(itemStack, entityPlayer, list, flag);
        if (itemStack.hasTagCompound()) {
            list.add(
                itemStack.getTagCompound()
                    .toString());
        }
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ICapabilityProvider() {

            private TestGuiWrapper wrapper = null;

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                @Nullable ForgeDirection side) {
                initCap();

                if (cap == CapabilityTestGuiWrapperConfig.CAPABILITY) {
                    return LazyOptional.of(() -> wrapper)
                        .cast();
                }

                if (cap == CapabilityItemHandler.ITEM_HANDLER) {
                    return LazyOptional.of(() -> wrapper.getItemHandler())
                        .cast();
                }

                return LazyOptional.empty();
            }

            private void initCap() {
                if (wrapper == null) {
                    wrapper = new TestGuiWrapper(stack);
                }
            }
        };
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            GuiFactories.playerInventory()
                .openFromMainHand(player);
        }
        return super.onItemRightClick(itemStackIn, worldIn, player);
    }

    @Override
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(OKCore._instance.getModId(), mainPanel);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager manager, UISettings settings) {

        LazyOptional<TestGuiWrapper> wrapperCap = CapabilityHelpers
            .getCapability(data.getUsedItemStack(), CapabilityTestGuiWrapperConfig.CAPABILITY);

        final int panelHeight = 216;
        final ModularPanel panel = ModularPanel.defaultPanel("golden_bag", 176, panelHeight);
        manager.registerSlotGroup("inv", 54);

        Flow column = Flow.column()
            .marginLeft(7)
            .marginTop(5)
            .coverChildren();

        wrapperCap.ifPresent(wrapper -> {
            IItemHandler handler = wrapper.getItemHandler();
            SlotGroupWidget grid = SlotGroupWidget.builder()
                .row("IIIIIIIII")
                .row("IIIIIIIII")
                .row("IIIIIIIII")
                .row("IIIIIIIII")
                .row("IIIIIIIII")
                .row("IIIIIIIII")
                .key('I', i -> new ItemSlot().slot(new ModularItemSlot(handler, i).slotGroup("inv")))
                .build();

            column.child(grid.marginTop(3))
                .child(
                    SlotGroupWidget.playerInventory(false)
                        .marginTop(12));
            panel.child(column);
        });

        return panel;
    }
}
