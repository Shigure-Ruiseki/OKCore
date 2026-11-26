package ruiseki.okcore.client.model;

import com.gtnewhorizon.gtnhlib.api.IBlockModelProvider;
import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;

import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.helper.BlockStateHelpers;

public interface IBlockStateModel extends IBlockModelProvider {

    @Override
    default BakedModel getModel(BakedModelQuadContext context) {
        if (context instanceof BakedModelQuadContext.World world) {
            IBlockState state = this.getWorldState(world);
        }
        return null;
    }

    default IBlockState getWorldState(BakedModelQuadContext.World context) {
        return BlockStateHelpers.getBlockState(context.getWorld(), context.getX(), context.getY(), context.getZ());
    }

}
