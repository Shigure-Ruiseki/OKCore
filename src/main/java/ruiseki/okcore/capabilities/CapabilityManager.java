package ruiseki.okcore.capabilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import net.minecraftforge.common.util.EnumHelper;

import org.objectweb.asm.Type;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.discovery.ASMDataTable;

public enum CapabilityManager {

    INSTANCE;

    /**
     * Registers a capability to be consumed by others.
     * APIs who define the capability should call this.
     * To retrieve the Capability instance, use the @CapabilityInject annotation.
     *
     * @param type The Interface to be registered
     */
    public <T> void register(Class<T> type) {
        String name = type.getName();

        Capability<?> cap = providers.computeIfAbsent(name, k -> new Capability<T>(name));

        List<Function<Capability<?>, Object>> list = callbacks.remove(name);
        if (list != null) {
            for (Function<Capability<?>, Object> func : list) {
                func.apply(cap);
            }
        }
        cap.onRegister();
    }

    // INTERNAL
    private final ConcurrentHashMap<String, Capability<?>> providers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Function<Capability<?>, Object>>> callbacks = new ConcurrentHashMap<>();

    public void injectCapabilities(ASMDataTable data) {
        for (ASMDataTable.ASMData entry : data.getAll(CapabilityInject.class.getName())) {
            final String targetClass = entry.getClassName();
            final String targetName = entry.getObjectName();
            Type type = (Type) entry.getAnnotationInfo()
                .get("value");
            if (type == null) {
                FMLLog.getLogger()
                    .warn("Unable to inject capability at {}.{} (Invalid Annotation)", targetClass, targetName);
                continue;
            }
            final String capabilityName = type.getInternalName()
                .replace('/', '.')
                .intern();

            List<Function<Capability<?>, Object>> list = callbacks
                .computeIfAbsent(capabilityName, k -> Lists.newArrayList());

            if (entry.getObjectName()
                .indexOf('(') > 0) {
                list.add(new Function<Capability<?>, Object>() {

                    @Override
                    public Object apply(Capability<?> input) {
                        try {
                            for (Method mtd : Class.forName(targetClass)
                                .getDeclaredMethods()) {
                                if (targetName.equals(mtd.getName() + Type.getMethodDescriptor(mtd))) {
                                    if ((mtd.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
                                        FMLLog.getLogger()
                                            .warn(
                                                "Unable to inject capability {} at {}.{} (Non-Static)",
                                                capabilityName,
                                                targetClass,
                                                targetName);
                                        return null;
                                    }

                                    mtd.setAccessible(true);
                                    mtd.invoke(null, input);
                                    return null;
                                }
                            }
                            FMLLog.getLogger()
                                .warn(
                                    "Unable to inject capability {} at {}.{} (Method Not Found)",
                                    capabilityName,
                                    targetClass,
                                    targetName);
                        } catch (Exception e) {
                            FMLLog.getLogger()
                                .warn(
                                    "Unable to inject capability {} at {}.{}",
                                    capabilityName,
                                    targetClass,
                                    targetName,
                                    e);
                        }
                        return null;
                    }
                });
            } else {
                list.add(new Function<Capability<?>, Object>() {

                    @Override
                    public Object apply(Capability<?> input) {
                        try {
                            Field field = Class.forName(targetClass)
                                .getDeclaredField(targetName);
                            if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
                                FMLLog.getLogger()
                                    .warn(
                                        "Unable to inject capability {} at {}.{} (Non-Static)",
                                        capabilityName,
                                        targetClass,
                                        targetName);
                                return null;
                            }
                            EnumHelper.setFailsafeFieldValue(field, null, input);
                        } catch (Exception e) {
                            FMLLog.getLogger()
                                .warn(
                                    "Unable to inject capability {} at {}.{}",
                                    capabilityName,
                                    targetClass,
                                    targetName,
                                    e);
                        }
                        return null;
                    }
                });
            }
        }
    }
}
