package ruiseki.okcore.block.property;

import static net.minecraftforge.common.util.ForgeDirection.UNKNOWN;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.core.InvalidPropertyTextException;

import ruiseki.okcore.block.IBlockDirection;
import ruiseki.okcore.helper.TileHelpers;

public interface DirectionProperty extends BlockProperty<ForgeDirection> {

    @Override
    default Type getType() {
        return ForgeDirection.class;
    }

    @Override
    default boolean hasTrait(BlockPropertyTrait trait) {
        return switch (trait) {
            case SupportsWorld, WorldMutable, SupportsStacks, StackMutable -> true;
            default -> false;
        };
    }

    @Override
    default JsonElement serialize(ForgeDirection value) {
        return new JsonPrimitive(stringify(value));
    }

    @Override
    default ForgeDirection deserialize(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive()
            .isString() ? parse(element.getAsString()) : UNKNOWN;
    }

    @Override
    default String stringify(ForgeDirection value) {
        return value.name()
            .toLowerCase();
    }

    @Override
    default ForgeDirection parse(String text) throws InvalidPropertyTextException {
        try {
            return ForgeDirection.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPropertyTextException("Invalid ForgeDirection", e);
        }
    }

    static AbstractDirectionProperty facing() {
        return facing(ForgeDirection.SOUTH);
    }

    static AbstractDirectionProperty facing(ForgeDirection defaultValue) {
        return facing(defaultValue, (world, x, y, z) -> {
            if (world.getBlock(x, y, z) instanceof IBlockDirection direction) {
                direction.getDirection(world, x, y, z);
            }
            IBlockDirection direction = TileHelpers.getSafeTile(world, x, y, z, IBlockDirection.class);
            if (direction != null) {
                direction.getDirection(world, x, y, z);
            }
            return null;
        }, (w, x, y, z, v) -> {
            if (w instanceof World world) {
                if (world.getBlock(x, y, z) instanceof IBlockDirection direction) {
                    direction.setDirection(world, x, y, z, v);
                }
                IBlockDirection direction = TileHelpers.getSafeTile(world, x, y, z, IBlockDirection.class);
                if (direction != null) {
                    direction.setDirection(world, x, y, z, v);
                }
            }
        });
    }

    static AbstractDirectionProperty facing(ForgeDirection defaultValue, PropertyGetter<ForgeDirection> getter,
        PropertySetter<ForgeDirection> setter) {
        return new AbstractDirectionProperty("facing", defaultValue) {

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

    abstract class AbstractDirectionProperty implements DirectionProperty {

        private String name;
        private ForgeDirection defaultValue;

        public AbstractDirectionProperty(String name, ForgeDirection defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public AbstractDirectionProperty(String name) {
            this(name, ForgeDirection.SOUTH);
        }

        public AbstractDirectionProperty setName(String name) {
            this.name = name;
            return this;
        }

        public ForgeDirection getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
