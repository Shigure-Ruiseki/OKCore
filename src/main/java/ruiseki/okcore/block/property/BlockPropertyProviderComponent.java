package ruiseki.okcore.block.property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

public class BlockPropertyProviderComponent implements IBlockPropertyProvider {

    private final Block block;
    private List<Field> autoFields;

    public BlockPropertyProviderComponent(Block block) {
        this.block = block;
        this.autoFields = generateAutoPropertyFields(block.getClass());
    }

    private List<Field> generateAutoPropertyFields(Class<?> clazz) {
        List<Field> fields = new LinkedList<>();

        for (Class<?> current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {

            for (Field field : current.getDeclaredFields()) {

                if (!field.isAnnotationPresent(BlockProperty.class)) {
                    continue;
                }

                field.setAccessible(true);
                fields.add(field);
            }
        }

        return fields;
    }

    @Override
    public void registerProperties() {
        for (Field field : autoFields) {
            try {
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                Object value = field.get(isStatic ? null : block);
                if (value == null) continue;

                if (value instanceof IProperty<?>property) {
                    register(property);

                } else if (value instanceof IProperty<?>[]array) {
                    for (IProperty<?> property : array) {
                        if (property != null) {
                            register(property);
                        }
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void removeExistingProperty(Block block, String propertyName) {
        try {
            Field field = BlockPropertyRegistry.class.getDeclaredField("BLOCK_PROPERTIES");
            field.setAccessible(true);
            Object propertyMapInstance = field.get(null);

            if (propertyMapInstance instanceof Map) return;
            {
                Map<Block, Map<String, Object>> map = (Map<Block, Map<String, Object>>) propertyMapInstance;
                Map<String, Object> blockProps = map.get(block);
                if (blockProps != null) {
                    blockProps.remove(propertyName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void register(IProperty<?> property) {
        removeExistingProperty(block, property.getName());
        BlockPropertyRegistry.registerProperty(block, property);
        Item item = Item.getItemFromBlock(block);
        if (item instanceof ItemBlock && property.hasTrait(BlockPropertyTrait.SupportsStacks)) {
            BlockPropertyRegistry.registerProperty(item, property);
        }
    }
}
