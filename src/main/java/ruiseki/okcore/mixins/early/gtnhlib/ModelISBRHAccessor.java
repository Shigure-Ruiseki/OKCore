package ruiseki.okcore.mixins.early.gtnhlib;

import net.minecraftforge.client.IItemRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;

@Mixin(value = ModelISBRH.class, remap = false)
public interface ModelISBRHAccessor {

    @Invoker("applyItemDisplay")
    void applyItemDisplay(BakedModel model, IItemRenderer.ItemRenderType type);
}
