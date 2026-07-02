package ruiseki.okcore.data;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.datastructure.Resource;

public interface DataManager {

    Set<String> getNamespaces();

    Map<ResourceLocation, Resource> listResources(String type, Predicate<ResourceLocation> filter);

}
