package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.block.BlockDoorBase;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.item.ItemDoorMetadata;

/**
 * Config for doors.
 *
 * @author josephcsible
 * @see ExtendedConfig
 */
public abstract class BlockDoorConfig extends BlockConfig {

    /**
     * Make a new instance.
     *
     * @param mod            The mod instance.
     * @param enabled        If this should is enabled.
     * @param namedId        The unique name ID for the configurable.
     * @param comment        The comment to add in the config file for this configurable.
     * @param elementFactory Function factory to create the Block instance.
     */
    public BlockDoorConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<BlockConfig, Block> elementFactory) {
        super(mod, enabled, namedId, comment, elementFactory);
        if (MinecraftHelpers.isClientSide()) MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public Class<? extends Item> getItemBlockClass() {
        return ItemDoorMetadata.class;
    }

    @Override
    public @NotNull Item getItemInstance() {
        return ((BlockDoorBase) getInstance()).item;
    }
}
