package ruiseki.commoncapabilities.api.capability.block;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * The general registry for capabilities of blocks.
 * This is used to register capabilities to blocks AND to lookup capabilities of blocks.
 * 
 * @author rubensworks
 */
public class BlockCapabilities implements IBlockCapabilityProvider {

    private List<IBlockCapabilityConstructor> capabilityConstructors = Lists.newLinkedList();

    private final Map<Block, IBlockCapabilityProvider[]> providers = Maps.newIdentityHashMap();

    private static final BlockCapabilities INSTANCE = new BlockCapabilities();

    private BlockCapabilities() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static BlockCapabilities getInstance() {
        return INSTANCE;
    }

    /**
     * Register a capability provider for the given block or for all blocks.
     * This MUST be called after the given block is registered, otherwise the block instance may be null.
     *
     * @param block              The block the capability provider applies to,
     *                           null if it applies to all blocks.
     *                           Only use null if absolutely necessary, as this will reduce performance during lookup.
     * @param capabilityProvider The capability provider
     */
    protected void register(@Nullable Block block, @Nonnull IBlockCapabilityProvider capabilityProvider) {
        IBlockCapabilityProvider[] providers = this.providers.get(block);
        if (providers == null) {
            providers = new IBlockCapabilityProvider[0];
        }
        providers = ArrayUtils.add(providers, capabilityProvider);
        this.providers.put(block, providers);
    }

    /**
     * Register a block capability provider constructor.
     * This will make sure that the constructor is only called AFTER all blocks have been registered.
     * So this method can be called at any time.
     *
     * @param capabilityConstructor A constructor for a block capability provider.
     */
    public void register(@Nonnull IBlockCapabilityConstructor capabilityConstructor) {
        if (this.capabilityConstructors != null) {
            this.capabilityConstructors.add(capabilityConstructor);
        } else {
            register(capabilityConstructor.getBlock(), capabilityConstructor.createProvider());
        }
    }

    /**
     * Initialize all registered capability constructors after blocks are registered.
     * Call this during PostInit or via Event in 1.7.10.
     */
    public void initConstructors() {
        if (this.capabilityConstructors != null) {
            for (IBlockCapabilityConstructor capabilityConstructor : this.capabilityConstructors) {
                register(capabilityConstructor.getBlock(), capabilityConstructor.createProvider());
            }
            this.capabilityConstructors = null;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull BlockState blockState, @Nonnull Capability<T> capability,
        @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable ForgeDirection side) {
        IBlockCapabilityProvider[] blockProviders = this.providers.get(blockState.getBlock());
        if (blockProviders != null) {
            for (IBlockCapabilityProvider provider : blockProviders) {
                LazyOptional<T> result = provider.getCapability(blockState, capability, world, pos, side);
                if (result.isPresent()) {
                    return result;
                }
            }
        }

        IBlockCapabilityProvider[] globalProviders = this.providers.get(null);
        if (globalProviders != null) {
            for (IBlockCapabilityProvider provider : globalProviders) {
                LazyOptional<T> result = provider.getCapability(blockState, capability, world, pos, side);
                if (result.isPresent()) {
                    return result;
                }
            }
        }

        return LazyOptional.empty();
    }
}
