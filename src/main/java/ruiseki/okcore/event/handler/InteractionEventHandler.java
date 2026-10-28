
package ruiseki.okcore.event.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.event.interaction.PlayerInteractEvent;

/**
 * Bridges Minecraft Forge interaction events
 * into the OkCore interaction event API.
 *
 * <p>
 * Cancelable events propagate their cancellation
 * state back to the original Forge event.
 * </p>
 *
 * <p>
 * Client-only interactions without a direct Forge
 * event source must be emitted through dedicated hooks.
 * </p>
 */
public final class InteractionEventHandler {

    public static final InteractionEventHandler INSTANCE = new InteractionEventHandler();

    private InteractionEventHandler() {}

    /**
     * Forwards block and air interactions from Forge
     * into OkCore events.
     */
    @SubscribeEvent
    public void onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent forge) {
        EntityPlayer player = forge.entityPlayer;

        switch (forge.action) {
            case LEFT_CLICK_BLOCK: {
                PlayerInteractEvent.LeftClickBlock event = new PlayerInteractEvent.LeftClickBlock(
                    player,
                    new BlockPos(forge.x, forge.y, forge.z),
                    ForgeDirection.getOrientation(forge.face),
                    PlayerInteractEvent.LeftClickBlock.Action.START);

                if (MinecraftForge.EVENT_BUS.post(event)) {
                    forge.setCanceled(true);
                }

                break;
            }

            case RIGHT_CLICK_BLOCK: {
                PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(
                    player,
                    new BlockPos(forge.x, forge.y, forge.z),
                    ForgeDirection.getOrientation(forge.face));

                event.setUseBlock(forge.useBlock);
                event.setUseItem(forge.useItem);

                boolean canceled = MinecraftForge.EVENT_BUS.post(event);

                if (canceled) {
                    forge.setCanceled(true);
                }

                forge.useBlock = event.getUseBlock();
                forge.useItem = event.getUseItem();

                break;
            }

            case RIGHT_CLICK_AIR: {
                if (player.getCurrentEquippedItem() == null) {
                    if (player.worldObj.isRemote) {
                        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickEmpty(player));
                    }
                    break;
                }

                PlayerInteractEvent.RightClickItem event = new PlayerInteractEvent.RightClickItem(player);
                event.setUseItem(forge.useItem);
                boolean canceled = MinecraftForge.EVENT_BUS.post(event);
                if (canceled) {
                    forge.setCanceled(true);
                }

                forge.useItem = event.getUseItem();
                break;
            }

            default:
                break;
        }
    }

    /**
     * Forwards Forge entity interactions into OkCore.
     */
    @SubscribeEvent
    public void onEntityInteract(net.minecraftforge.event.entity.player.EntityInteractEvent forge) {
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
            forge.entityPlayer,
            forge.target);

        if (MinecraftForge.EVENT_BUS.post(event)) {
            forge.setCanceled(true);
        }
    }
}
