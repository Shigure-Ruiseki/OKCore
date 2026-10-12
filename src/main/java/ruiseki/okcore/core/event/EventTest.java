package ruiseki.okcore.core.event;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import ruiseki.okcore.core.item.gui.CapabilityGuiTestWrapperConfig;
import ruiseki.okcore.core.item.gui.GuiTestWrapper;
import ruiseki.okcore.core.item.gui.ItemGuiTest;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.inventory.PlayerExtendedInventoryIterator;

import java.util.List;

@EventBusSubscriber
public class EventTest {

    @SubscribeEvent
    public static void onPlayerPickup(EntityItemPickupEvent event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj.isRemote) return;
        if (event.item == null || event.item.isDead) {
            event.setCanceled(true);
            return;
        }

        ItemStack entityStack = event.item.getEntityItem();
        if (entityStack == null || entityStack.stackSize <= 0) {
            event.item.setDead();
            event.setCanceled(true);
            return;
        }

        ItemStack stack = entityStack.copy();
        int initialSize = stack.stackSize;

        stack = attemptPickup(player, stack);

        if (event.item.isDead) {
            event.setCanceled(true);
            return;
        }

        if (ItemHelpers.isEmpty(stack)) {
            event.item.setDead();
            event.setCanceled(true);

            World world = event.item.worldObj;

            world.playSoundEffect(
                event.item.posX,
                event.item.posY,
                event.item.posZ,
                "random.pop",
                0.2F,
                ((player.getRNG()
                    .nextFloat()
                    - player.getRNG()
                        .nextFloat())
                    * 0.7F + 1.0F) * 2.0F);
        } else if (stack.stackSize != initialSize) {
            event.item.setEntityItemStack(stack.copy());
            event.setCanceled(true);
        }
    }
    public static ItemStack attemptPickup(EntityPlayer player, ItemStack pickupStack) {
        if (player == null || ItemHelpers.isEmpty(pickupStack)) {
            return pickupStack;
        }

        GuiTestWrapper openWrapper = getOpenGuiWrapper(player);

        if (openWrapper != null) {
            return openWrapper.getInventoryHandler()
                .insertItem(pickupStack, false);
        }

        final ItemStack[] remaining = { pickupStack };

        PlayerExtendedInventoryIterator iterator =
            new PlayerExtendedInventoryIterator(player);

        iterator.forEachRemaining(stack -> {
            if (ItemHelpers.isEmpty(remaining[0])) {
                return;
            }

            GuiTestWrapper wrapper = CapabilityHelpers
                .getCapability(stack, CapabilityGuiTestWrapperConfig.CAPABILITY)
                .getOrNull();

            if (wrapper == null) {
                return;
            }

            remaining[0] = wrapper.getInventoryHandler()
                .insertItem(remaining[0], false);
        });

        return ItemHelpers.isEmpty(remaining[0])
            ? ItemHelpers.EMPTY
            : remaining[0];
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        if (player.worldObj.isRemote || player.isDead) return;

        tick(player);
    }

    public static void tick(EntityPlayer player) {
        PlayerExtendedInventoryIterator iterator = new PlayerExtendedInventoryIterator(player);
        iterator.forEachRemaining(stack -> {
            GuiTestWrapper wrapper = CapabilityHelpers
                .getCapability(stack, CapabilityGuiTestWrapperConfig.CAPABILITY)
                .getOrNull();
            if (wrapper == null) return;

            World world = player.worldObj;
            double radius = 2.5D;
            AxisAlignedBB area = AxisAlignedBB.getBoundingBox(
                player.posX - radius,
                player.posY - radius,
                player.posZ - radius,
                player.posX + radius,
                player.posY + radius,
                player.posZ + radius
            );

            List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, area);
            for (EntityItem entityItem : items) {
                if (entityItem.isDead) {
                    continue;
                }

                double dx = player.posX - entityItem.posX;
                double dy = (player.boundingBox.minY + 0.1D) - entityItem.posY;
                double dz = player.posZ - entityItem.posZ;

                double distanceSq = dx * dx + dy * dy + dz * dz;
                if (distanceSq < 0.01D) {
                    entityItem.delayBeforeCanPickup = 0;
                    continue;
                }

                double distance = Math.sqrt(distanceSq);
                double speed = 0.25D;

                entityItem.motionX = dx / distance * speed;
                entityItem.motionY = dy / distance * speed;
                entityItem.motionZ = dz / distance * speed;

                entityItem.velocityChanged = true;
            }
        });
    }

    @Nullable
    private static GuiTestWrapper getOpenGuiWrapper(EntityPlayer player) {
        if (player == null || player.openContainer == null) {
            return null;
        }

        if (!(player.openContainer instanceof ItemGuiTest.GuiTestContainer container)) {
            return null;
        }

        return container.cap.getOrNull();
    }
}
