package ruiseki.okcore.guide.gui;

import net.minecraft.entity.player.EntityPlayer;

public interface IGuideBookScreen {

    /**
     * @return Width of the usable page area
     */
    int pageWidth();

    /**
     * @return Height of the usable page area
     */
    int pageHeight();

    /**
     * @return Horizontal (x) start of the usable page area
     */
    int pageLeft();

    /**
     * @return Vertical (y) start of the usable page area
     */
    int pageTop();

    /**
     * @return Horizontal center of the page
     */
    int pageXCenter();

    /**
     * @return Vertical center of the page
     */
    int pageYCenter();

    /**
     * @return The current page number
     */
    int currentPage();

    /**
     * @return The total number of pages
     */
    int getPageCount();

    EntityPlayer player();
}
