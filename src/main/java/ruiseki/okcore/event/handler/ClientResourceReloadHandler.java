package ruiseki.okcore.event.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;

import ruiseki.okcore.event.resource.ResourceEvent;

public class ClientResourceReloadHandler implements IResourceManagerReloadListener {

    public static final ClientResourceReloadHandler INSTANCE = new ClientResourceReloadHandler();

    private ClientResourceReloadHandler() {}

    public static void register() {
        IResourceManager manager = Minecraft.getMinecraft()
            .getResourceManager();

        if (manager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) manager).registerReloadListener(INSTANCE);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager manager) {
        MinecraftForge.EVENT_BUS.post(new ResourceEvent.Pre(manager));
        MinecraftForge.EVENT_BUS.post(new ResourceEvent.Post(manager));
    }
}
