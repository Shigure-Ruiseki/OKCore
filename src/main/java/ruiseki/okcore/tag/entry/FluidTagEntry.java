package ruiseki.okcore.tag.entry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.ResourceKey;

@TagData
public class FluidTagEntry extends TagEntry<Fluid> {

    public FluidTagEntry() {
        super(null, WILDCARD);
    }

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
    public String getKey() {
        return "fluid";
    }

    @Override
    public ResourceKey<?> getRegistryKey() {
        return Registries.FLUID;
    }

    @Override
    public TagEntry<Fluid> create(ResourceLocation id, int meta) {
        return new FluidTagEntry(id, meta);
    }

    @Override
    public Fluid to() {
        if (this.id == null) return null;
        return FluidRegistry.getFluid(this.id.getResourcePath());
    }
}
