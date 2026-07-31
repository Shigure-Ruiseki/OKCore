package ruiseki.okcore.guide.button;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import ruiseki.okcore.Reference;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.helper.GuiHelpers;

public class ButtonBack extends ButtonBase {

    public ButtonBack(int id, int x, int y, GuiBase guiBase, Consumer<ButtonBase> consumer) {
        super(id, x, y, guiBase, consumer);
        width = 18;
        height = 10;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager()
                .bindTexture(new ResourceLocation(Reference.PREFIX_GUI + "guide/book_colored.png"));
            if (GuiHelpers.isMouseBetween(mouseX, mouseY, xPosition, yPosition, width, height)) {
                this.drawTexturedModalRect(xPosition, yPosition + 1, 70, 201, 18, 10);
                guiBase.drawHoveringText(getHoveringText(), mouseX, mouseY, Minecraft.getMinecraft().fontRenderer);
            } else {
                this.drawTexturedModalRect(xPosition, yPosition, 94, 201, 18, 10);
            }
            GL11.glDisable(GL11.GL_BLEND);
            RenderHelper.disableStandardItemLighting();
        }
    }

    public List<String> getHoveringText() {
        ArrayList<String> list = new ArrayList<String>();
        String s = StatCollector.translateToLocal("button.back.name");
        list.add(s);
        return list;
    }
}
