package ruiseki.commoncapabilities.modcompat.vanilla;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyStorage;
import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.Reference;
import ruiseki.commoncapabilities.api.capability.block.BlockCapabilities;
import ruiseki.commoncapabilities.api.capability.block.IBlockCapabilityConstructor;
import ruiseki.commoncapabilities.api.capability.block.IBlockCapabilityProvider;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import ruiseki.commoncapabilities.api.capability.temperature.ITemperature;
import ruiseki.commoncapabilities.api.capability.work.IWorker;
import ruiseki.commoncapabilities.capability.recipehandler.RecipeHandlerConfig;
import ruiseki.commoncapabilities.capability.temperature.TemperatureConfig;
import ruiseki.commoncapabilities.capability.worker.WorkerConfig;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.energystorage.CoFHEnergyWrapper;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.energystorage.VanillaEntityItemEnergyStorage;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.energystorage.VanillaEntityItemFrameEnergyStorage;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.fluidhandler.VanillaEntityItemFluidHandler;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.fluidhandler.VanillaEntityItemFrameFluidHandler;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.itemhandler.VanillaEntityItemFrameItemHandler;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.itemhandler.VanillaEntityItemItemHandler;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.recipehandler.VanillaCraftingTableRecipeHandler;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.recipehandler.VanillaFurnaceRecipeHandler;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.temperature.VanillaFurnaceTemperature;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.temperature.VanillaUniversalBucketTemperature;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.work.VanillaBrewingStandWorker;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.work.VanillaFurnaceWorker;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.capability.wrapper.FluidBucketWrapper;
import ruiseki.okcore.fluid.capability.wrapper.FluidContainerWrapper;
import ruiseki.okcore.fluid.capability.wrapper.FluidHandlerWrapper;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.capability.wrapper.InventoryHandlerWrapper;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.modcompat.IModCompat;
import ruiseki.okcore.modcompat.capabilities.CapabilityConstructorRegistry;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.DefaultSidedCapabilityProvider;
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
            registry.registerInheritableItem(
                ItemBucket.class,
                new ICapabilityConstructor<ITemperature, ItemBucket, ItemStack>() {

                    @Override
                    public Capability<ITemperature> getCapability() {
                        return TemperatureConfig.CAPABILITY;
                    }

                    @Override
                    public ICapabilityProvider createProvider(ItemBucket hostType, ItemStack host) {
                        return new DefaultCapabilityProvider<>(this, new VanillaUniversalBucketTemperature(host));
                    }
                });
            registry.registerInheritableItem(
                IFluidContainerItem.class,
                new ICapabilityConstructor<ITemperature, IFluidContainerItem, ItemStack>() {

                    @Override
                    public Capability<ITemperature> getCapability() {
                        return TemperatureConfig.CAPABILITY;
                    }

                    @Override
                    public ICapabilityProvider createProvider(IFluidContainerItem hostType, ItemStack host) {
                        return new DefaultCapabilityProvider<>(this, new VanillaUniversalBucketTemperature(host));
                    }
                });
            registry.registerInheritableItem(
                IFluidHandlerItem.class,
                new ICapabilityConstructor<ITemperature, IFluidHandlerItem, ItemStack>() {

                    @Override
                    public Capability<ITemperature> getCapability() {
                        return TemperatureConfig.CAPABILITY;
                    }

                    @Override
                    public ICapabilityProvider createProvider(IFluidHandlerItem hostType, ItemStack host) {
                        return new DefaultCapabilityProvider<>(this, new VanillaUniversalBucketTemperature(host));
                    }
                });

            // ItemHandler
            registry
                .registerEntity(EntityItem.class, new ICapabilityConstructor<IItemHandler, EntityItem, EntityItem>() {

                    @Override
                    public Capability<IItemHandler> getCapability() {
                        return CapabilityItemHandler.ITEM_HANDLER;
                    }

                    @Override
                    public ICapabilityProvider createProvider(EntityItem hostType, final EntityItem host) {
                        return new ICapabilityProvider() {

                            @Override
                            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                                @Nullable ForgeDirection facing) {
                                return capability == CapabilityItemHandler.ITEM_HANDLER
                                    && CapabilityHelpers.getCapability(host.getEntityItem(), capability, facing)
                                        .isPresent()
                                            ? LazyOptional.of(() -> new VanillaEntityItemItemHandler(host, facing))
                                                .cast()
                                            : LazyOptional.empty();
                            }
                        };
                    }
                });
            registry.registerEntity(
                EntityItemFrame.class,
                new ICapabilityConstructor<IItemHandler, EntityItemFrame, EntityItemFrame>() {

                    @Override
                    public Capability<IItemHandler> getCapability() {
                        return CapabilityItemHandler.ITEM_HANDLER;
                    }

                    @Override
                    public ICapabilityProvider createProvider(EntityItemFrame hostType, final EntityItemFrame host) {
                        return new ICapabilityProvider() {

                            @Override
                            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                                @Nullable ForgeDirection facing) {
                                return capability == CapabilityItemHandler.ITEM_HANDLER
                                    && CapabilityHelpers.getCapability(host.getDisplayedItem(), capability, facing)
                                        .isPresent()
                                            ? LazyOptional.of(() -> new VanillaEntityItemFrameItemHandler(host, facing))
                                                .cast()
                                            : LazyOptional.empty();
                            }
                        };
                    }
                });
            registry.registerInheritableTile(
                IInventory.class,
                new ICapabilityConstructor<IItemHandler, TileEntity, TileEntity>() {

                    @Override
                    public Capability<IItemHandler> getCapability() {
                        return CapabilityItemHandler.ITEM_HANDLER;
                    }

                    @Override
                    public @NotNull ICapabilityProvider createProvider(TileEntity hostType, TileEntity host) {
                        return new DefaultSidedCapabilityProvider<>(
                            DefaultSidedCapabilityProvider.forAllSides(
                                getCapability(),
                                side -> new InventoryHandlerWrapper((IInventory) hostType, side)));
                    }
                });

            // FluidHandler
            registry
                .registerEntity(EntityItem.class, new ICapabilityConstructor<IFluidHandler, EntityItem, EntityItem>() {

                    @Override
                    public Capability<IFluidHandler> getCapability() {
                        return CapabilityFluidHandler.FLUID_HANDLER;
                    }

                    @Override
                    public ICapabilityProvider createProvider(EntityItem hostType, final EntityItem host) {
                        return new ICapabilityProvider() {

                            @Override
                            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                                @Nullable ForgeDirection facing) {
                                return capability == CapabilityFluidHandler.FLUID_HANDLER
                                    && CapabilityHelpers.getCapability(host.getEntityItem(), capability, facing)
                                        .isPresent()
                                            ? LazyOptional.of(() -> new VanillaEntityItemFluidHandler(host, facing))
                                                .cast()
                                            : LazyOptional.empty();
                            }
                        };
                    }
                });
            registry.registerEntity(
                EntityItemFrame.class,
                new ICapabilityConstructor<IFluidHandler, EntityItemFrame, EntityItemFrame>() {

                    @Override
                    public Capability<IFluidHandler> getCapability() {
                        return CapabilityFluidHandler.FLUID_HANDLER;
                    }

                    @Override
                    public ICapabilityProvider createProvider(EntityItemFrame hostType, final EntityItemFrame host) {
                        return new ICapabilityProvider() {

                            @Override
                            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                                @Nullable ForgeDirection facing) {
                                return capability == CapabilityFluidHandler.FLUID_HANDLER
                                    && CapabilityHelpers.getCapability(host.getDisplayedItem(), capability, facing)
                                        .isPresent()
                                            ? LazyOptional
                                                .of(() -> new VanillaEntityItemFrameFluidHandler(host, facing))
                                                .cast()
                                            : LazyOptional.empty();
                            }
                        };
                    }
                });
            registry.registerInheritableItem(
                ItemBucket.class,
                new ICapabilityConstructor<IFluidHandlerItem, ItemBucket, ItemStack>() {

                    @Override
                    public Capability<IFluidHandlerItem> getCapability() {
                        return CapabilityFluidHandler.FLUID_HANDLER_ITEM;
                    }

                    @Override
                    public @NotNull ICapabilityProvider createProvider(ItemBucket hostType, ItemStack host) {
                        return new DefaultCapabilityProvider<>(this, new FluidBucketWrapper(host));
                    }
                });
            registry.registerItem(
                ItemBucketMilk.class,
                new ICapabilityConstructor<IFluidHandlerItem, ItemBucketMilk, ItemStack>() {

                    @Override
                    public Capability<IFluidHandlerItem> getCapability() {
                        return CapabilityFluidHandler.FLUID_HANDLER_ITEM;
                    }

                    @Override
                    public @NotNull ICapabilityProvider createProvider(ItemBucketMilk hostType, ItemStack host) {
                        return new DefaultCapabilityProvider<>(this, new FluidBucketWrapper(host));
                    }
                });
            registry.registerInheritableItem(
                IFluidContainerItem.class,
                new ICapabilityConstructor<IFluidHandlerItem, IFluidContainerItem, ItemStack>() {

                    @Override
                    public Capability<IFluidHandlerItem> getCapability() {
                        return CapabilityFluidHandler.FLUID_HANDLER_ITEM;
                    }

                    @Override
                    public @NotNull ICapabilityProvider createProvider(IFluidContainerItem hostType, ItemStack host) {
                        return new DefaultCapabilityProvider<>(this, new FluidContainerWrapper(host, hostType));
                    }
                });
            registry.registerInheritableTile(
                net.minecraftforge.fluids.IFluidHandler.class,
                new ICapabilityConstructor<ruiseki.okcore.fluid.handler.IFluidHandler, TileEntity, TileEntity>() {

                    @Override
                    public Capability<ruiseki.okcore.fluid.handler.IFluidHandler> getCapability() {
                        return CapabilityFluidHandler.FLUID_HANDLER;
                    }

                    @Override
                    public @Nullable ICapabilityProvider createProvider(TileEntity hostType, TileEntity host) {
                        if (hostType instanceof ruiseki.okcore.fluid.handler.IFluidHandler) {
                            return ICapabilityProvider.EMPTY;
                        }

                        if (!(hostType instanceof net.minecraftforge.fluids.IFluidHandler forgeHandler)) {
                            return ICapabilityProvider.EMPTY;
                        }

                        return new DefaultSidedCapabilityProvider<>(
                            DefaultSidedCapabilityProvider
                                .forAllSides(getCapability(), side -> new FluidHandlerWrapper(forgeHandler, side)));
                    }
                });

            // EnergyStorage
            registry
                .registerEntity(EntityItem.class, new ICapabilityConstructor<IEnergyStorage, EntityItem, EntityItem>() {

                    @Override
                    public Capability<IEnergyStorage> getCapability() {
                        return CapabilityEnergy.ENERGY;
                    }

                    @Override
                    public ICapabilityProvider createProvider(EntityItem hostType, final EntityItem host) {
                        return new ICapabilityProvider() {

                            @Override
                            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                                @Nullable ForgeDirection facing) {
                                return capability == CapabilityEnergy.ENERGY
                                    && CapabilityHelpers.getCapability(host.getEntityItem(), capability, facing)
                                        .isPresent()
                                            ? LazyOptional.of(() -> new VanillaEntityItemEnergyStorage(host, facing))
                                                .cast()
                                            : LazyOptional.empty();
                            }
                        };
                    }
                });
            registry.registerEntity(
                EntityItemFrame.class,
                new ICapabilityConstructor<IEnergyStorage, EntityItemFrame, EntityItemFrame>() {

                    @Override
                    public Capability<IEnergyStorage> getCapability() {
                        return CapabilityEnergy.ENERGY;
                    }

                    @Override
                    public ICapabilityProvider createProvider(EntityItemFrame hostType, final EntityItemFrame host) {
                        return new ICapabilityProvider() {

                            @Override
                            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                                @Nullable ForgeDirection facing) {
                                return capability == CapabilityEnergy.ENERGY
                                    && CapabilityHelpers.getCapability(host.getDisplayedItem(), capability, facing)
                                        .isPresent()
                                            ? LazyOptional
                                                .of(() -> new VanillaEntityItemFrameEnergyStorage(host, facing))
                                                .cast()
                                            : LazyOptional.empty();
                            }
                        };
                    }
                });
            registry.registerInheritableTile(
                IEnergyConnection.class,
                new ICapabilityConstructor<IEnergyStorage, TileEntity, TileEntity>() {

                    @Override
                    public Capability<IEnergyStorage> getCapability() {
                        return CapabilityEnergy.ENERGY;
                    }

                    @Override
                    public @Nullable ICapabilityProvider createProvider(TileEntity hostType, TileEntity host) {
                        return new DefaultSidedCapabilityProvider<>(
                            DefaultSidedCapabilityProvider
                                .forAllSides(getCapability(), side -> new CoFHEnergyWrapper(host, side)));
                    }
                });

            // RecipeHandler
            registry.registerTile(
                TileEntityFurnace.class,
                new ICapabilityConstructor<IRecipeHandler, TileEntityFurnace, TileEntityFurnace>() {

                    @Override
                    public Capability<IRecipeHandler> getCapability() {
                        return RecipeHandlerConfig.CAPABILITY;
                    }

                    @Override
                    public ICapabilityProvider createProvider(TileEntityFurnace hostType, TileEntityFurnace host) {
                        return new DefaultCapabilityProvider<>(this, VanillaFurnaceRecipeHandler.getInstance());
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
