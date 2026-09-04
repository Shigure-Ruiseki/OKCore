package ruiseki.commoncapabilities;

import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.ingredient.NBTBaseComparator;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.config.IChangedCallback;
import ruiseki.okcore.config.extendedconfig.DummyConfig;
import ruiseki.okcore.nbt.path.NbtParseException;
import ruiseki.okcore.nbt.path.NbtPath;
import ruiseki.okcore.nbt.path.navigate.INbtPathNavigation;
import ruiseki.okcore.nbt.path.navigate.NbtPathNavigationList;

/**
 * A config with general options for this mod.
 *
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    /**
     * The type of this config.
     */
    public static ConfigurableType TYPE = ConfigurableType.DUMMY;

    /**
     * Create a new instance.
     */
    public GeneralConfig() {
        super(CommonCapabilities._instance, true, "general", null);
    }

    @Override
    public void onRegistered() {}

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static class IgnoreNbtPathsForEqualityChangedCallback implements IChangedCallback {

        @Override
        public void onChanged(Object value) {
            List<INbtPathNavigation> navigations = Lists.newArrayList();
            for (String path : (String[]) value) {
                try {
                    navigations.add(
                        NbtPath.parse(path)
                            .asNavigation());
                } catch (NbtParseException e) {
                    CommonCapabilities.clog(Level.ERROR, String.format("Failed to parse NBT path to filter: %s", path));
                }
            }
            ItemMatch.NBT_COMPARATOR = NBTBaseComparator.INSTANCE = new NBTBaseComparator(
                new NbtPathNavigationList(navigations));
        }

        @Override
        public void onRegisteredPostInit(Object value) {

        }
    }
}
