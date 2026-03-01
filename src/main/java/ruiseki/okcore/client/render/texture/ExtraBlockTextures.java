package ruiseki.okcore.client.render.texture;

import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.helper.TextureHelpers;

public enum ExtraBlockTextures {
    ;

    @SideOnly(Side.CLIENT)
    public static IIcon getMissing() {
        return TextureHelpers.getMissingBlock();
    }
}
