package ruiseki.okcore.tag;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class Registries {

    public static final ResourceLocation ROOT_REGISTRY_NAME = new ResourceLocation("root");

    public static final ResourceKey<Block> BLOCK = ResourceKey.createRegistryKey(new ResourceLocation("block"));
    public static final ResourceKey<Item> ITEM = ResourceKey.createRegistryKey(new ResourceLocation("item"));
    public static final ResourceKey<Fluid> FLUID = ResourceKey.createRegistryKey(new ResourceLocation("fluid"));
    public static final ResourceKey<Entity> ENTITY_TYPE = ResourceKey
        .createRegistryKey(new ResourceLocation("entity_type"));
}
