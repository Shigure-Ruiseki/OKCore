package ruiseki.okcore.block.property;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;

public interface IProperty<TValue> extends BlockProperty<TValue> {

    @Override
    default boolean hasTrait(BlockPropertyTrait trait) {
        return switch (trait) {
            case SupportsWorld, WorldMutable, SupportsStacks, StackMutable -> true;
            default -> false;
        };
    }

    TValue getDefaultValue();
}
