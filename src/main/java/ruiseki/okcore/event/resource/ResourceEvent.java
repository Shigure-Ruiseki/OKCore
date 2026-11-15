package ruiseki.okcore.event.resource;

import net.minecraft.client.resources.IResourceManager;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ResourceEvent extends Event {

    private final IResourceManager manager;

    public ResourceEvent(IResourceManager manager) {
        this.manager = manager;
    }

    public IResourceManager getManager() {
        return manager;
    }

    @SideOnly(Side.CLIENT)
    public static class Pre extends ResourceEvent {

        public Pre(IResourceManager manager) {
            super(manager);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class Post extends ResourceEvent {

        public Post(IResourceManager manager) {
            super(manager);
        }
    }
}
