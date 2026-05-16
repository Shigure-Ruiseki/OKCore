package ruiseki.okcore.data;

import java.util.Map;

import net.minecraft.client.resources.IResourceManager;

public interface IGlobalResourceManager {

    Map<String, IResourceManager> okcore$getDomainResourceManagers();
}
