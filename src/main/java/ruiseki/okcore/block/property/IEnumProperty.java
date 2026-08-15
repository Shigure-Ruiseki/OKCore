package ruiseki.okcore.block.property;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.gtnewhorizon.gtnhlib.blockstate.core.InvalidPropertyTextException;

public interface IEnumProperty<E extends Enum<E>> extends IProperty<E> {

    Class<E> getEnumClass();

    @Override
    default Type getType() {
        return getEnumClass();
    }

    @Override
    default JsonElement serialize(E value) {
        return new JsonPrimitive(stringify(value));
    }

    @Override
    default E deserialize(JsonElement element) {
        if (element != null && element.isJsonPrimitive()
            && element.getAsJsonPrimitive()
                .isString()) {
            try {
                return parse(element.getAsString());
            } catch (InvalidPropertyTextException e) {
                return getDefaultValue();
            }
        }
        return getDefaultValue();
    }

    @Override
    default String stringify(E value) {
        return value.name()
            .toLowerCase();
    }

    @Override
    default E parse(String text) throws InvalidPropertyTextException {
        try {
            return Enum.valueOf(getEnumClass(), text.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPropertyTextException("Invalid enum value: " + text, e);
        }
    }

    static <E extends Enum<E>> AbstractEnumProperty<E> construct(String name, Class<E> enumClass, E defaultValue,
        PropertyGetter<E> getter, PropertySetter<E> setter) {
        return new AbstractEnumProperty<>(name, enumClass, defaultValue) {

            @Override
            public E getValue(ItemStack stack) {
                return getDefaultValue();
            }

            @Override
            public E getValue(IBlockAccess world, int x, int y, int z) {
                E val = getter.get(world, x, y, z);
                return val != null ? val : getDefaultValue();
            }

            @Override
            public void setValue(World world, int x, int y, int z, E value) {
                setter.accept(world, x, y, z, value);
            }
        };
    }

    abstract class AbstractEnumProperty<E extends Enum<E>> implements IEnumProperty<E> {

        private String name;
        private final Class<E> enumClass;
        private final E defaultValue;

        public AbstractEnumProperty(String name, Class<E> enumClass, E defaultValue) {
            this.name = name;
            this.enumClass = enumClass;
            this.defaultValue = defaultValue;
        }

        @Override
        public String getName() {
            return name;
        }

        public AbstractEnumProperty<E> setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Class<E> getEnumClass() {
            return enumClass;
        }

        @Override
        public E getDefaultValue() {
            return defaultValue;
        }
    }
}
