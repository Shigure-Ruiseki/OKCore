package ruiseki.okcore.block;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.network.ExtendedBuffer;

public interface IBlockGui {

    default void writeExtraGuiData(ExtendedBuffer packetBuffer, World world, EntityPlayer player, BlockPos blockPos,
        MovingObjectPosition rayTraceResult) {

    }

    static boolean onBlockActivatedHook(IBlockGui block, IBlockContainerProvider blockContainerProvider,
        BlockState blockState, World world, BlockPos blockPos, EntityPlayer player,
        MovingObjectPosition rayTraceResult) {
        if (player.isSneaking()) {
            return false;
        }

        if (!world.isRemote) {
            IGuiConstructor containerProvider = blockContainerProvider.get(blockState, world, blockPos);
            if (containerProvider != null) {
                PlayerHelpers.openGui(
                    (EntityPlayerMP) player,
                    containerProvider,
                    packetBuffer -> block.writeExtraGuiData(packetBuffer, world, player, blockPos, rayTraceResult));
            }
        }

        return true;
    }

    default IGuiConstructor getGuiProvider(BlockState blockState, World world, BlockPos blockPos) {
        return null;
    }

    @FunctionalInterface
    interface IBlockContainerProvider {

        @Nullable
        IGuiConstructor get(BlockState blockState, World world, BlockPos blockPos);
    }
}
