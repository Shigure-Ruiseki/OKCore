package ruiseki.okcore.tag.entry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidTagEntry extends TagEntry<Fluid> {

    public FluidTagEntry(ResourceLocation id, int meta) {
        super(id, meta);
    }

    @Override
    public Class<Fluid> getType() {
        return Fluid.class;
    }

    @Override
    public Fluid get() {
        return FluidRegistry.getFluid(this.id.getResourcePath());
    }

    public static class Serializer implements ITagEntrySerializer<Fluid, FluidTagEntry> {

        public static final FluidTagEntry.Serializer INSTANCE = new FluidTagEntry.Serializer();

        @Override
        public String getKey() {
            return "fluid";
        }

        @Override
        public FluidTagEntry read(ResourceLocation id, int meta) {
            return new FluidTagEntry(id, meta);
        }
    }
}
