package ruiseki.okcore.block.property;

import net.minecraft.world.World;

@FunctionalInterface
public interface PropertySetter<T> {

    void accept(World world, int x, int y, int z, T value);
}
