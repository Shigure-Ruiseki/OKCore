package ruiseki.okcore.block.property;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface BooleanProperty extends IProperty<Boolean> {

    @Override
    default Type getType() {
        return Boolean.class;
    }

    static AbstractBooleanProperty construct(String name, boolean defaultValue, PropertyGetter<Boolean> getter,
        PropertySetter<Boolean> setter) {
        return new AbstractBooleanProperty(name, defaultValue) {

            @Override
            public Boolean getValue(ItemStack s) {
                return getDefaultValue();
            }

            @Override
            public Boolean getValue(IBlockAccess w, int x, int y, int z) {
                Boolean r = getter.get(w, x, y, z);
                return r != null ? r : getDefaultValue();
            }

            @Override
            public void setValue(World w, int x, int y, int z, Boolean v) {
                setter.accept(w, x, y, z, v);
            }
        };
    }

    abstract class AbstractBooleanProperty implements BooleanProperty {

        private String name;
        private boolean defaultValue;

        public AbstractBooleanProperty(String name, boolean defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public AbstractBooleanProperty(String name) {
            this(name, false);
        }

        public AbstractBooleanProperty setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Boolean getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
