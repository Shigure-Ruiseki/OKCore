package ruiseki.okcore.block.property;

import net.minecraft.world.IBlockAccess;

@FunctionalInterface
public interface PropertyGetter<T> {

    T get(IBlockAccess world, int x, int y, int z);
}
