package ruiseki.okcore.block.property;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;

public class UnlistedProperty<TValue> implements IProperty<TValue> {

    private final String name;
    private final Class<TValue> type;
    private final TValue defaultValue;

    public UnlistedProperty(String name, Class<TValue> type, TValue defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public UnlistedProperty(String name, Class<TValue> type) {
        this(name, type, null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public TValue getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean hasTrait(BlockPropertyTrait trait) {
        return trait == BlockPropertyTrait.SupportsWorld || trait == BlockPropertyTrait.SupportsStacks;
    }

    @Override
    public TValue getValue(IBlockAccess world, int x, int y, int z) {
        return getDefaultValue();
    }

    @Override
    public TValue getValue(ItemStack stack) {
        return getDefaultValue();
    }
}
