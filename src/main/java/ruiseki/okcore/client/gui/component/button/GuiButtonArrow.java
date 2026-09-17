package ruiseki.okcore.client.gui.component.button;

import lombok.Getter;
import ruiseki.okcore.client.gui.image.Image;
import ruiseki.okcore.client.gui.image.Images;

/**
 * A button with an arrow in a certain direction.
 *
 * @author rubensworks
 */
public class GuiButtonArrow extends GuiButtonExtended {

    @Getter
    private final GuiButtonArrow.Direction direction;
    private final Image[] directionImages;

    /**
     * Make a new instance.
     *
     * @param x         X
     * @param y         Y
     * @param direction The direction of the arrow to draw.
     */
    public GuiButtonArrow(int x, int y, OnPress onPress, GuiButtonArrow.Direction direction) {
        super(x, y, direction.width, direction.height, "", onPress, true);
        this.direction = direction;
        this.directionImages = getDirectionImage(direction);
    }

    protected static Image[] getDirectionImage(GuiButtonArrow.Direction direction) {
        if (direction == Direction.NORTH) {
            return Images.BUTTON_ARROW_UP;
        } else if (direction == Direction.EAST) {
            return Images.BUTTON_ARROW_RIGHT;
        } else if (direction == Direction.SOUTH) {
            return Images.BUTTON_ARROW_DOWN;
        } else if (direction == Direction.WEST) {
            return Images.BUTTON_ARROW_LEFT;
        }
        return Images.BUTTON_ARROW_UP;
    }

    @Override
    protected void drawBackground() {
        directionImages[getYImage()].draw(this, getX(), getY());
    }

    @Override
    protected void drawButtonInner(int mouseX, int mouseY, boolean mouseOver) {

    }

    public enum Direction {

        NORTH(15, 10),
        EAST(10, 15),
        SOUTH(15, 10),
        WEST(10, 15);

        @Getter
        private final int width, height;

        private Direction(int width, int height) {
            this.width = width;
            this.height = height;
        }

    }

}
