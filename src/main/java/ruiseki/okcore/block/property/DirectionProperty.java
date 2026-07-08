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
        return new AbstractDirectionProperty("facing", defaultValue) {

            @Override
            public ForgeDirection getValue(ItemStack stack) {
                return getDefaultValue();
            }

            @Override
            public ForgeDirection getValue(IBlockAccess world, int x, int y, int z) {
                if (world.getBlock(x, y, z) instanceof IBlockDirection direction) {
                    return direction.getDirection(world, x, y, z);
                }

                IBlockDirection orientable = TileHelpers.getSafeTile(world, x, y, z, IBlockDirection.class);
                return orientable != null ? orientable.getDirection(world, x, y, z) : getDefaultValue();
            }

            @Override
            public void setValue(World world, int x, int y, int z, ForgeDirection value) {
                if (world.getBlock(x, y, z) instanceof IBlockDirection direction) {
                    direction.setDirection(world, x, y, z, value);
                    return;
                }

                IBlockDirection orientable = TileHelpers.getSafeTile(world, x, y, z, IBlockDirection.class);
                if (orientable != null) orientable.setDirection(world, x, y, z, value);
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
