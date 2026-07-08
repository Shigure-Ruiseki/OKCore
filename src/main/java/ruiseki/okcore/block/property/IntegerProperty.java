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
}
