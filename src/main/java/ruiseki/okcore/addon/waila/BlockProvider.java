package ruiseki.okcore.addon.waila;

import java.util.List;

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

public class BlockProvider implements IWailaDataProvider {

    public static void init() {
        String callback = BlockProvider.class.getCanonicalName() + ".load";
        FMLInterModComms.sendMessage("Waila", "register", callback);
    }

    public static final BlockProvider INSTANCE = new BlockProvider();

    public static void load(IWailaRegistrar registrar) {
        registrar.registerStackProvider(INSTANCE, IWailaBlockInfoProvider.class);
        registrar.registerHeadProvider(INSTANCE, IWailaBlockInfoProvider.class);
        registrar.registerBodyProvider(INSTANCE, IWailaBlockInfoProvider.class);
        registrar.registerTailProvider(INSTANCE, IWailaBlockInfoProvider.class);

        registrar.registerNBTProvider(INSTANCE, IWailaNBTProvider.class);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof IWailaTileInfoProvider provider) {
            return provider.getWailaStack(accessor, config);
        }
        if (accessor.getBlock() instanceof IWailaBlockInfoProvider provider) {
            return provider.getWailaStack(accessor, config);
        }
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof IWailaTileInfoProvider provider) {
            provider.getWailaHead(itemStack, currenttip, accessor, config);
            return currenttip;
        }

        if (accessor.getBlock() instanceof IWailaBlockInfoProvider provider) {
            provider.getWailaHead(itemStack, currenttip, accessor, config);
            return currenttip;
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof IWailaTileInfoProvider info) {
            info.getWailaBody(currenttip, itemStack, accessor, config);
            return currenttip;
        }

        if (accessor.getBlock() instanceof IWailaBlockInfoProvider info) {
            info.getWailaBody(currenttip, itemStack, accessor, config);
            return currenttip;
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof IWailaTileInfoProvider provider) {
            provider.getWailaTail(itemStack, currenttip, accessor, config);
            return currenttip;
        }

        if (accessor.getBlock() instanceof IWailaBlockInfoProvider provider) {
            provider.getWailaTail(itemStack, currenttip, accessor, config);
            return currenttip;
        }
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x,
        int y, int z) {
        if (tile instanceof IWailaNBTProvider te) {
            te.getWailaNBTData(player, tile, tag, world, x, y, z);
        }
        return tag;
    }
}
