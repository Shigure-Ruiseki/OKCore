package ruiseki.okcore.block.property;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.block.IBlockDirection;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.tileentity.TileEntityOK;

public interface DirectionProperty extends IEnumProperty<ForgeDirection> {

    @Override
    default Class<ForgeDirection> getEnumClass() {
        return ForgeDirection.class;
    }

    static AbstractDirectionProperty facing() {
        return facing(ForgeDirection.SOUTH);
    }

    static AbstractDirectionProperty facing(ForgeDirection defaultValue) {
        return facing(defaultValue, (world, x, y, z) -> {
            if (world.getBlock(x, y, z) instanceof IBlockDirection direction) {
                return direction.getDirection(world, x, y, z);
            }

            IBlockDirection direction = TileHelpers.getSafeTile(world, x, y, z, IBlockDirection.class);
            if (direction != null) return direction.getDirection(world, x, y, z);

            TileEntityOK tileOK = TileHelpers.getSafeTile(world, x, y, z, TileEntityOK.class);
            if (tileOK != null) return tileOK.getRotation();

            return null;
        }, (world, x, y, z, v) -> {
            if (world.getBlock(x, y, z) instanceof IBlockDirection direction) {
                direction.setDirection(world, x, y, z, v);
            }

            IBlockDirection direction = TileHelpers.getSafeTile(world, x, y, z, IBlockDirection.class);
            if (direction != null) direction.setDirection(world, x, y, z, v);

            TileEntityOK tileOK = TileHelpers.getSafeTile(world, x, y, z, TileEntityOK.class);
            if (tileOK != null) tileOK.setRotation(v);
        });
    }

    static AbstractDirectionProperty facing(ForgeDirection defaultValue, PropertyGetter<ForgeDirection> getter,
        PropertySetter<ForgeDirection> setter) {
        return construct("facing", defaultValue, getter, setter);
    }

    static AbstractDirectionProperty construct(String name, ForgeDirection defaultValue,
        PropertyGetter<ForgeDirection> getter, PropertySetter<ForgeDirection> setter) {
        return new AbstractDirectionProperty(name, defaultValue) {

            @Override
            public ForgeDirection getValue(ItemStack s) {
                return getDefaultValue();
            }

            @Override
            public ForgeDirection getValue(IBlockAccess w, int x, int y, int z) {
                ForgeDirection r = getter.get(w, x, y, z);
                return r != null ? r : getDefaultValue();
            }

            @Override
            public void setValue(World w, int x, int y, int z, ForgeDirection v) {
                setter.accept(w, x, y, z, v);
            }
        };
    }

    abstract class AbstractDirectionProperty extends AbstractEnumProperty<ForgeDirection> implements DirectionProperty {

        public AbstractDirectionProperty(String name, ForgeDirection defaultValue) {
            super(name, ForgeDirection.class, defaultValue);
        }

        public AbstractDirectionProperty(String name) {
            this(name, ForgeDirection.SOUTH);
        }

        @Override
        public AbstractDirectionProperty setName(String name) {
            super.setName(name);
            return this;
        }
    }
}
