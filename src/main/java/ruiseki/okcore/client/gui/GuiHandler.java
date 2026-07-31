package ruiseki.okcore.client.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * The handler class that will map Containers to GUI's (1.7.10 Version).
 *
 * @author rubensworks
 *
 */
public class GuiHandler implements IGuiHandler {

    private final ModBase mod;
    private Map<Integer, Class<? extends Container>> containers = Maps.newHashMap();
    private Map<Integer, Class<? extends GuiScreen>> guis = Maps.newHashMap();
    private Map<Integer, GuiType> types = Maps.newHashMap();

    // Two temporary data maps to avoid collisions in singleplayer worlds
    private Map<GuiType, Object> tempDataHolderClient = Maps.newHashMap();
    private Map<GuiType, Object> tempDataHolderServer = Maps.newHashMap();

    public GuiHandler(ModBase mod) {
        this.mod = mod;
    }

    public ModBase getMod() {
        return this.mod;
    }

    /**
     * Register a new GUI.
     *
     * @param guiProvider A provider of GUI data.
     * @param type        The GUI type.
     */
    public void registerGUI(IGuiContainerProvider guiProvider, GuiType type) {
        containers.put(guiProvider.getGuiID(), guiProvider.getContainer());
        if (MinecraftHelpers.isClientSide()) {
            guis.put(guiProvider.getGuiID(), guiProvider.getGui());
        }
        types.put(guiProvider.getGuiID(), type);
    }

    /**
     * Set a temporary object for showing gui's of a certain type.
     * This temporary object will be removed once a gui/container is opened once.
     *
     * @param guiType The type of gui.
     * @param data    The data for this gui type.
     * @param <O>     The type of temporary data.
     */
    public <O> void setTemporaryData(GuiType<O> guiType, O data) {
        if (MinecraftHelpers.isClientSide()) {
            tempDataHolderClient.put(guiType, data);
        } else {
            tempDataHolderServer.put(guiType, data);
        }
    }

    /**
     * Clear the temporary item index for item containers.
     */
    private <O> void clearTemporaryData(GuiType<O> guiType) {
        setTemporaryData(guiType, null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <O> O getTemporaryData(GuiType<O> guiType) throws IllegalArgumentException {
        O data = (O) (MinecraftHelpers.isClientSide() ? tempDataHolderClient.get(guiType)
            : tempDataHolderServer.get(guiType));

        if (guiType.isCarriesData() && data == null) {
            throw new IllegalArgumentException("Invalid GUI data.");
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Class<? extends Container> containerClass = containers.get(id);
        if (containerClass == null) {
            return null; // Possible with client-only GUI's like books.
        }
        GuiType guiType = types.get(id);
        Object data;
        try {
            data = getTemporaryData(guiType);
        } catch (IllegalArgumentException e) {
            getMod().log(Level.WARN, "Invalid GUI data.");
            return null;
        }
        return guiType.getContainerConstructor()
            .getServerGuiElement(id, player, world, x, y, z, containerClass, data);
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Class<? extends GuiScreen> guiClass = guis.get(id);
        GuiType guiType = types.get(id);
        Object data;
        try {
            data = getTemporaryData(guiType);
        } catch (IllegalArgumentException e) {
            getMod().log(Level.WARN, "Invalid GUI data.");
            return null;
        }

        Object guiElement = guiType.getGuiConstructor()
            .getClientGuiElement(id, player, world, x, y, z, guiClass, data);

        clearTemporaryData(guiType);

        return guiElement;
    }

    /**
     * Types of GUIs.
     *
     * @author rubensworks
     */
    public static class GuiType<O> {

        private final boolean carriesData;
        private IContainerConstructor<O> containerConstructor = null;
        private IGuiConstructor<O> guiConstructor = null;

        private GuiType(boolean carriesData) {
            this.carriesData = carriesData;
        }

        public boolean isCarriesData() {
            return this.carriesData;
        }

        public void setContainerConstructor(IContainerConstructor<O> containerConstructor) {
            if (this.containerConstructor != null) {
                throw new IllegalStateException("The container constructor was already set!");
            }
            this.containerConstructor = containerConstructor;
        }

        public void setGuiConstructor(IGuiConstructor<O> guiConstructor) {
            if (this.guiConstructor != null) {
                throw new IllegalStateException("The gui constructor was already set!");
            }
            this.guiConstructor = guiConstructor;
        }

        public IContainerConstructor<O> getContainerConstructor() {
            return this.containerConstructor;
        }

        @SideOnly(Side.CLIENT)
        public IGuiConstructor<O> getGuiConstructor() {
            return this.guiConstructor;
        }

        public static <O> GuiType<O> create(boolean carriesData) {
            return new GuiType<O>(carriesData);
        }

        /**
         * A block.
         */
        public static final GuiType<Void> BLOCK = GuiType.create(false);
        /**
         * A block with a tile entity.
         */
        public static final GuiType<Void> TILE = GuiType.create(false);
        /**
         * An item (Integer represents item slot ID).
         */
        public static final GuiType<Integer> ITEM = GuiType.create(true);

        static {
            // Container Constructors (Server & Client)
            BLOCK.setContainerConstructor(new IContainerConstructor<Void>() {

                @Override
                public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
                    Class<? extends Container> containerClass, Void data) {
                    try {
                        Constructor<? extends Container> containerConstructor = containerClass
                            .getConstructor(InventoryPlayer.class);
                        return containerConstructor.newInstance(player.inventory);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                        | NoSuchMethodException e) {
                        try {
                            Constructor<? extends Container> containerConstructor = containerClass
                                .getConstructor(InventoryPlayer.class, World.class, int.class, int.class, int.class);
                            return containerConstructor.newInstance(player.inventory, world, x, y, z);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                            | NoSuchMethodException e2) {
                            e2.printStackTrace();
                        }
                    }
                    return null;
                }
            });

            TILE.setContainerConstructor(new IContainerConstructor<Void>() {

                @Override
                public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
                    Class<? extends Container> containerClass, Void data) {
                    try {
                        TileEntity tileEntity = world.getTileEntity(x, y, z);
                        Constructor<? extends Container> containerConstructor = containerClass
                            .getConstructor(InventoryPlayer.class, tileEntity.getClass());
                        return containerConstructor.newInstance(player.inventory, tileEntity);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                        | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            ITEM.setContainerConstructor(new IContainerConstructor<Integer>() {

                @Override
                public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
                    Class<? extends Container> containerClass, Integer data) {
                    try {
                        Constructor<? extends Container> containerConstructor = containerClass
                            .getConstructor(EntityPlayer.class, int.class);
                        return containerConstructor.newInstance(player, data);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                        | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            // Client GUI Constructors (Only initialized on Client Side)
            if (MinecraftHelpers.isClientSide()) {
                BLOCK.setGuiConstructor(new IGuiConstructor<Void>() {

                    @Override
                    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
                        Class<? extends GuiScreen> guiClass, Void data) {
                        try {
                            Constructor<? extends GuiScreen> guiConstructor = guiClass
                                .getConstructor(InventoryPlayer.class);
                            return guiConstructor.newInstance(player.inventory);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                            | NoSuchMethodException e) {
                            try {
                                Constructor<? extends GuiScreen> guiConstructor = guiClass.getConstructor(
                                    InventoryPlayer.class,
                                    World.class,
                                    int.class,
                                    int.class,
                                    int.class);
                                return guiConstructor.newInstance(player.inventory, world, x, y, z);
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                                | NoSuchMethodException e2) {
                                e2.printStackTrace();
                            }
                        }
                        return null;
                    }
                });

                TILE.setGuiConstructor(new IGuiConstructor<Void>() {

                    @Override
                    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
                        Class<? extends GuiScreen> guiClass, Void data) {
                        try {
                            TileEntity tileEntity = world.getTileEntity(x, y, z);
                            Constructor<? extends GuiScreen> guiConstructor = guiClass
                                .getConstructor(InventoryPlayer.class, tileEntity.getClass());
                            return guiConstructor.newInstance(player.inventory, tileEntity);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                            | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });

                ITEM.setGuiConstructor(new IGuiConstructor<Integer>() {

                    @Override
                    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
                        Class<? extends GuiScreen> guiClass, Integer data) {
                        try {
                            Constructor<? extends GuiScreen> guiConstructor = guiClass
                                .getConstructor(EntityPlayer.class, int.class);
                            return guiConstructor.newInstance(player, data);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                            | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            }
        }
    }

    public static interface IContainerConstructor<O> {

        public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
            Class<? extends Container> containerClass, O data);
    }

    public static interface IGuiConstructor<O> {

        public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
            Class<? extends GuiScreen> guiClass, O data);
    }
}
