package ruiseki.okcore.client.model.state;

import static com.gtnewhorizon.gtnhlib.client.model.unbaked.MissingModel.MISSING_MODEL;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.client.model.unbaked.UnbakedModel;

import ruiseki.okcore.block.state.IBlockState;

public class MissingState implements IStateModelMap {

    public static final MissingState MISSING_STATE_MAP = new MissingState();

    @Override
    public @NotNull UnbakedModel selectModel(IBlockState state) {
        return MISSING_MODEL;
    }
}
