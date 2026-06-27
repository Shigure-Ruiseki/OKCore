package ruiseki.okcore.tag;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class Registries {

    public static final ResourceLocation ROOT_REGISTRY_NAME = new ResourceLocation("root");
    public static final ResourceKey<ResourceKey<Block>> BLOCK = ResourceKey
        .createRegistryKey(new ResourceLocation("block"));
    public static final ResourceKey<ResourceKey<Item>> ITEM = ResourceKey
        .createRegistryKey(new ResourceLocation("item"));
    public static final ResourceKey<ResourceKey<Item>> FLUID = ResourceKey
        .createRegistryKey(new ResourceLocation("fluid"));
    public static final ResourceKey<ResourceKey<Item>> ENTITY_TYPE = ResourceKey
        .createRegistryKey(new ResourceLocation("entity_type"));
}
