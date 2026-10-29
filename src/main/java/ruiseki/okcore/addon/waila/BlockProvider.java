
package ruiseki.okcore.addon.waila;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

/**
 * Bridges OKCore tooltip and NBT interfaces to WAILA.
 *
 * <p>
 * Block and tile providers can contribute independently to the
 * header, body, and footer.
 *
 * <p>
 * For the displayed item stack, a non-null tile result takes
 * precedence over the block result.
 *
 * <p>
 * NBT callbacks are handled separately on the server.
 *
 * @see IWailaBlockInfoProvider
 * @see IWailaTileInfoProvider
 * @see IWailaNBTProvider
 */
public final class BlockProvider implements IWailaDataProvider {

    public static final BlockProvider INSTANCE = new BlockProvider();

    private BlockProvider() {}

    /**
     * Sends the WAILA registration request through Forge IMC.
     *
     * <p>
     * Call this during the appropriate mod initialization stage.
     */
    public static void init() {
        FMLInterModComms.sendMessage("Waila", "register", BlockProvider.class.getCanonicalName() + ".load");
    }

    /**
     * Registers all supported WAILA callbacks.
     *
     * <p>
     * Registration uses OKCore interfaces to support implementations
     * from other mods without referring to their concrete classes.
     *
     * @param registrar the WAILA registrar
     */
    public static void load(IWailaRegistrar registrar) {
        registrar.registerStackProvider(INSTANCE, IWailaBlockInfoProvider.class);
        registrar.registerHeadProvider(INSTANCE, IWailaBlockInfoProvider.class);
        registrar.registerBodyProvider(INSTANCE, IWailaBlockInfoProvider.class);
        registrar.registerTailProvider(INSTANCE, IWailaBlockInfoProvider.class);

        registrar.registerNBTProvider(INSTANCE, IWailaNBTProvider.class);
    }

    /**
     * Resolves the item stack displayed by WAILA.
     *
     * <p>
     * Tile entities take precedence. If the tile returns {@code null},
     * the block provider is consulted.
     *
     * @return the custom stack, or {@code null} if neither provides one
     */
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tile = accessor.getTileEntity();

        if (tile instanceof IWailaTileInfoProvider provider) {
            ItemStack stack = provider.getWailaStack(accessor, config);
            if (stack != null) {
                return stack;
            }
        }

        Block block = accessor.getBlock();

        if (block instanceof IWailaBlockInfoProvider provider) {
            return provider.getWailaStack(accessor, config);
        }

        return null;
    }

    /**
     * Collects header lines from both block and tile providers.
     *
     * @return the modified tooltip
     */
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        TileEntity tile = accessor.getTileEntity();

        if (block instanceof IWailaBlockInfoProvider provider) {
            provider.getWailaHead(itemStack, currenttip, accessor, config);
        }

        if (tile instanceof IWailaTileInfoProvider provider) {
            provider.getWailaHead(itemStack, currenttip, accessor, config);
        }

        return currenttip;
    }

    /**
     * Collects body lines from both block and tile providers.
     *
     * <p>
     * Block information is appended first, followed by tile information.
     * Legacy {@code getWailaInfo()} implementations are supported through
     * the interface's default delegation method.
     *
     * @return the modified tooltip
     */
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        TileEntity tile = accessor.getTileEntity();

        if (block instanceof IWailaBlockInfoProvider provider) {
            provider.getWailaBody(currenttip, itemStack, accessor, config);
        }

        if (tile instanceof IWailaTileInfoProvider provider) {
            provider.getWailaBody(currenttip, itemStack, accessor, config);
        }

        return currenttip;
    }

    /**
     * Collects footer lines from both block and tile providers.
     *
     * @return the modified tooltip
     */
    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        TileEntity tile = accessor.getTileEntity();

        if (block instanceof IWailaBlockInfoProvider provider) {
            provider.getWailaTail(itemStack, currenttip, accessor, config);
        }

        if (tile instanceof IWailaTileInfoProvider provider) {
            provider.getWailaTail(itemStack, currenttip, accessor, config);
        }

        return currenttip;
    }

    /**
     * Collects additional server-side NBT data.
     *
     * @return the supplied NBT compound after provider modifications
     */
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x,
        int y, int z) {
        if (tile instanceof IWailaNBTProvider provider) {
            provider.getWailaNBTData(player, tile, tag, world, x, y, z);
        }

        return tag;
    }
}
