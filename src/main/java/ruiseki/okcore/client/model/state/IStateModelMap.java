package ruiseki.okcore.client.model.state;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.client.model.unbaked.UnbakedModel;

import ruiseki.okcore.block.state.IBlockState;

public interface IStateModelMap {

    /// @param state The blockstate properties as strings
    /// @return The appropriate model, or [MissingModel#MISSING_MODEL] if none match
    @NotNull
    UnbakedModel selectModel(IBlockState state);
}
