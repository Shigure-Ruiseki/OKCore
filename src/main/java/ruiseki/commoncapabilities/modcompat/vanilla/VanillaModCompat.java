package ruiseki.commoncapabilities.modcompat.vanilla;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.Reference;
import ruiseki.commoncapabilities.api.capability.block.BlockCapabilities;
import ruiseki.commoncapabilities.api.capability.block.IBlockCapabilityConstructor;
import ruiseki.commoncapabilities.api.capability.block.IBlockCapabilityProvider;
import ruiseki.commoncapabilities.api.capability.temperature.ITemperature;
import ruiseki.commoncapabilities.api.capability.work.IWorker;
import ruiseki.commoncapabilities.capability.recipehandler.RecipeHandlerConfig;
import ruiseki.commoncapabilities.capability.temperature.TemperatureConfig;
import ruiseki.commoncapabilities.capability.worker.WorkerConfig;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.recipehandler.VanillaCraftingTableRecipeHandler;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.temperature.VanillaFurnaceTemperature;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.temperature.VanillaUniversalBucketTemperature;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.work.VanillaBrewingStandWorker;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.work.VanillaFurnaceWorker;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.modcompat.IModCompat;
import ruiseki.okcore.modcompat.capabilities.CapabilityConstructorRegistry;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.ICapabilityConstructor;
import ruiseki.okcore.modcompat.capabilities.SimpleCapabilityConstructor;

public class VanillaModCompat implements IModCompat {

    public VanillaModCompat() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getModID() {
        return Reference.MOD_VANILLA;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "Furnace and Brewing stand capabilities.";
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.INIT) {
            CapabilityConstructorRegistry registry = CommonCapabilities._instance.getCapabilityConstructorRegistry();

            // Worker
            registry
                .registerTile(TileEntityFurnace.class, new SimpleCapabilityConstructor<IWorker, TileEntityFurnace>() {

                    @Override
                    public Capability<IWorker> getCapability() {
                        return WorkerConfig.CAPABILITY;
                    }

                    @Override
                    public ICapabilityProvider createProvider(TileEntityFurnace host) {
                        return new DefaultCapabilityProvider<>(this, new VanillaFurnaceWorker(host));
                    }
                });
            registry.registerTile(
                TileEntityBrewingStand.class,
                new SimpleCapabilityConstructor<IWorker, TileEntityBrewingStand>() {

                    @Override
                    public Capability<IWorker> getCapability() {
                        return WorkerConfig.CAPABILITY;
                    }

                    @Override
                    public ICapabilityProvider createProvider(TileEntityBrewingStand host) {
                        return new DefaultCapabilityProvider<>(this, new VanillaBrewingStandWorker(host));
                    }
                });

            // Temperature
            registry.registerTile(
                TileEntityFurnace.class,
                new SimpleCapabilityConstructor<ITemperature, TileEntityFurnace>() {

                    @Override
                    public Capability<ITemperature> getCapability() {
                        return TemperatureConfig.CAPABILITY;
                    }

                    @Override
                    public ICapabilityProvider createProvider(TileEntityFurnace host) {
                        return new DefaultCapabilityProvider<>(this, new VanillaFurnaceTemperature(host));
                    }
                });
            registry.registerItem(Item.class, new ICapabilityConstructor<ITemperature, Item, ItemStack>() {

                @Override
                public Capability<ITemperature> getCapability() {
                    return TemperatureConfig.CAPABILITY;
                }

                @Override
                public ICapabilityProvider createProvider(Item hostType, ItemStack host) {
                    return new DefaultCapabilityProvider<>(this, new VanillaUniversalBucketTemperature(host));
                }
            });

            BlockCapabilities.getInstance()
                .register(new IBlockCapabilityConstructor() {

                    @Nullable
                    @Override
                    public Block getBlock() {
                        return Blocks.crafting_table;
                    }

                    @Override
                    public IBlockCapabilityProvider createProvider() {
                        return new IBlockCapabilityProvider() {

                            @Override
                            public @NotNull <T> LazyOptional<T> getCapability(@NotNull BlockState blockState,
                                @NotNull Capability<T> capability, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                @Nullable ForgeDirection side) {
                                if (capability == RecipeHandlerConfig.CAPABILITY) {
                                    if (world instanceof World) {
                                        return LazyOptional
                                            .of(() -> new VanillaCraftingTableRecipeHandler((World) world))
                                            .cast();
                                    }
                                }
                                return LazyOptional.empty();
                            }
                        };
                    }
                });
        }
    }
}
