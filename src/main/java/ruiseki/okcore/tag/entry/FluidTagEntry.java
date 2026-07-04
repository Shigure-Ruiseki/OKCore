package ruiseki.okcore.tag.entry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidTagEntry extends TagEntry<Fluid> {

    public FluidTagEntry(Fluid fluid) {
        super((fluid != null) ? new ResourceLocation(fluid.getName()) : null, WILDCARD);
    }

    public FluidTagEntry(FluidStack stack) {
        this(stack.getFluid());
    }

    public FluidTagEntry(ResourceLocation id, int meta) {
        super(id, meta);
    }

    @Override
    public Class<Fluid> getType() {
        return Fluid.class;
    }

    @Override
    public Fluid to() {
        if (this.id == null) return null;
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
