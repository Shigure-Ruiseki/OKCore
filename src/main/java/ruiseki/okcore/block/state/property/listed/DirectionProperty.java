package ruiseki.okcore.block.state.property.listed;

import net.minecraftforge.common.util.ForgeDirection;

public class DirectionProperty extends EnumProperty<ForgeDirection> {

    protected DirectionProperty(String name) {
        super(name, ForgeDirection.class);
    }

    public static DirectionProperty create(String name) {
        return new DirectionProperty(name);
    }

    public static DirectionProperty create() {
        return new DirectionProperty("facing");
    }
}
