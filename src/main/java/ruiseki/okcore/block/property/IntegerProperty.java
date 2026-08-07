package ruiseki.okcore.block.property;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IntegerProperty extends IProperty<Integer> {

    @Override
    default Type getType() {
        return Integer.class;
    }

    static AbstractIntegerProperty construct(String name, int defaultValue, PropertyGetter<Integer> getter,
        PropertySetter<Integer> setter) {
        return new AbstractIntegerProperty(name, defaultValue) {

            @Override
            public Integer getValue(ItemStack s) {
                return getDefaultValue();
            }

            @Override
            public Integer getValue(IBlockAccess w, int x, int y, int z) {
                Integer r = getter.get(w, x, y, z);
                return r != null ? r : getDefaultValue();
            }

            @Override
            public void setValue(World w, int x, int y, int z, Integer v) {
                setter.accept(w, x, y, z, v);
            }
        };
    }

    abstract class AbstractIntegerProperty implements IntegerProperty {

        private String name;
        private int defaultValue;

        public AbstractIntegerProperty(String name, int defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public AbstractIntegerProperty(String name) {
            this(name, 0);
        }

        public AbstractIntegerProperty setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Integer getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    /**
     * Creates an {@code IntegerProperty} that reads and writes directly to Block Metadata.
     *
     * @param name         Property name (e.g., "fill")
     * @param defaultValue The default value if metadata is out of bounds or invalid
     * @param maxMeta      Maximum allowed metadata value (typically between 1 and 15, default is 15)
     * @return A property backed by block metadata
     */
    static MetaIntegerProperty createMeta(String name, int defaultValue, int maxMeta) {
        return new MetaIntegerProperty(name, defaultValue, maxMeta);
    }

    /**
     * Creates an {@code IntegerProperty} that reads and writes directly to Block Metadata,
     * defaulting the maximum metadata value to 15.
     *
     * @param name         Property name (e.g., "fill")
     * @param defaultValue The default value if metadata is out of bounds or invalid
     * @return A property backed by block metadata
     */
    static MetaIntegerProperty createMeta(String name, int defaultValue) {
        return createMeta(name, defaultValue, 15);
    }

    class MetaIntegerProperty extends AbstractIntegerProperty {

        private final int allowedValues;

        public MetaIntegerProperty(String name, int defaultValue, int allowedValues) {
            super(name, defaultValue);
            this.allowedValues = allowedValues;
        }

        public int getAllowedValues() {
            return allowedValues;
        }

        @Override
        public Integer getValue(ItemStack stack) {
            int meta = stack.getItemDamage();
            return meta <= allowedValues ? meta : getDefaultValue();
        }

        @Override
        public Integer getValue(IBlockAccess world, int x, int y, int z) {
            int meta = world.getBlockMetadata(x, y, z);
            return meta <= allowedValues ? meta : getDefaultValue();
        }

        @Override
        public void setValue(World world, int x, int y, int z, Integer value) {
            int meta = (value != null) ? Math.min(value, allowedValues) : getDefaultValue();
            world.setBlockMetadataWithNotify(x, y, z, meta, 3);
        }
    }
}
