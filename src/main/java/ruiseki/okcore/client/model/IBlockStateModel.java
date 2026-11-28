package ruiseki.okcore.client.model;

import com.gtnewhorizon.gtnhlib.api.IBlockModelProvider;
import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;

import ruiseki.okcore.helper.BlockStateHelpers;

public interface IBlockStateModel extends IBlockModelProvider {

    @Override
    default BakedModel getModel(BakedModelQuadContext context) {
        return ModelRegistry.getBakedModel(BlockStateHelpers.getBlockState(context));
    }
}
