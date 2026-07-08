package ruiseki.okcore.block.property;

import net.minecraft.world.IBlockAccess;

@FunctionalInterface
public interface PropertySetter<T> {

    void accept(IBlockAccess world, int x, int y, int z, T value);
}
