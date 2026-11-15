package ruiseki.okcore.network;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import ruiseki.okcore.client.audio.record.RecordData;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.BlockStack;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.SingleCache;

/**
 * Packet with automatic coding and decoding of basic fields annotated with {@link CodecField}.
 *
 * @author rubensworks
 *
 */
public abstract class PacketCodec extends PacketBase {

    private static Map<Class<?>, ICodecAction> codecActions = Maps.newHashMap();
    static {
        codecActions.put(ExtendedBuffer.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                if (object == null) {
                    output.writeInt(-1);
                    return;
                }

                ExtendedBuffer buffer = (ExtendedBuffer) object;
                int length = buffer.readableBytes();
                output.writeInt(length);

                if (length > 0) {
                    output.writeBytes(buffer, buffer.readerIndex(), length);
                }
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                int length = input.readInt();
                if (length < 0) {
                    return null;
                }
                if (length == 0) {
                    return new ExtendedBuffer(Unpooled.EMPTY_BUFFER);
                }
                return new ExtendedBuffer(input.readBytes(length));
            }
        });
        codecActions.put(ResourceLocation.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                output.writeResourceLocation((ResourceLocation) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readResourceLocation();
            }
        });
        codecActions.put(UUID.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                output.writeString(((UUID) object).toString());
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return UUID.fromString(input.readString());
            }
        });
        codecActions.put(RecordData.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                ((RecordData) object).toNetwork(output);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return RecordData.fromNetwork(input);
            }
        });

        codecActions.put(String.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeString((String) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readString();
            }
        });

        codecActions.put(double.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeDouble((Double) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readDouble();
            }
        });

        codecActions.put(Double.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeDouble((Double) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readDouble();
            }
        });

        codecActions.put(int.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeInt((int) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readInt();
            }
        });

        codecActions.put(Integer.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeInt((Integer) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readInt();
            }
        });

        codecActions.put(short.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeShort((Short) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readShort();
            }
        });

        codecActions.put(Short.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeShort((Short) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readShort();
            }
        });

        codecActions.put(boolean.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeBoolean((Boolean) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readBoolean();
            }
        });

        codecActions.put(Boolean.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeBoolean((Boolean) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readBoolean();
            }
        });

        codecActions.put(float.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeFloat((Float) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readFloat();
            }
        });

        codecActions.put(Float.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeFloat((Float) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readFloat();
            }
        });

        codecActions.put(long.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeLong((Long) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readLong();
            }
        });

        codecActions.put(Long.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeLong((Long) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readLong();
            }
        });
        codecActions.put(byte.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeByte((byte) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readByte();
            }
        });

        codecActions.put(Byte.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeByte((Byte) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readByte();
            }
        });

        codecActions.put(char.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeChar((char) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readChar();
            }
        });

        codecActions.put(Character.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeChar((Character) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readChar();
            }
        });

        codecActions.put(Vec3.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                Vec3 v = (Vec3) object;
                output.writeDouble(v.xCoord);
                output.writeDouble(v.yCoord);
                output.writeDouble(v.zCoord);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                double x = input.readDouble();
                double y = input.readDouble();
                double z = input.readDouble();
                return Vec3.createVectorHelper(x, y, z);
            }
        });

        codecActions.put(Map.class, new ICodecAction() {

            // Packet structure:
            // Map length (int)
            // --- end if length == 0
            // Key class name (UTF)
            // Value class name (UTF)
            // for length
            // key
            // value

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                Map<?, ?> map = (Map<?, ?>) object;
                int size = map.size();

                output.writeInt(size);
                if (size == 0) return;

                Map.Entry<?, ?> firstEntry = map.entrySet()
                    .iterator()
                    .next();

                Object firstKey = firstEntry.getKey();
                Object firstValue = firstEntry.getValue();

                if (firstKey == null || firstValue == null) {
                    throw new IllegalArgumentException("Map keys and values cannot be null");
                }

                Class<?> keyClass = firstKey.getClass();
                Class<?> valueClass = firstValue.getClass();

                ICodecAction keyAction = getAction(keyClass);
                ICodecAction valueAction = getAction(valueClass);

                output.writeString(keyClass.getName());
                output.writeString(valueClass.getName());

                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();

                    if (key == null || value == null) {
                        throw new IllegalArgumentException("Map keys and values cannot be null");
                    }

                    if (key.getClass() != keyClass || value.getClass() != valueClass) {
                        throw new IllegalArgumentException("Map entries must have consistent key and value types");
                    }

                    keyAction.encode(key, output);
                    valueAction.encode(value, output);
                }
            }

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object decode(ExtendedBuffer input) {
                Map map = Maps.newHashMap();
                int size = input.readInt();

                if (size < 0) {
                    throw new IllegalStateException("Invalid map size: " + size);
                }

                if (size == 0) {
                    return map;
                }
                try {
                    ICodecAction keyAction = getAction(Class.forName(input.readString()));
                    ICodecAction valueAction = getAction(Class.forName(input.readString()));
                    for (int i = 0; i < size; i++) {
                        Object key = keyAction.decode(input);
                        Object value = valueAction.decode(input);
                        map.put(key, value);
                    }
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Failed to resolve Map codec class", e);
                }
                return map;
            }
        });

        codecActions.put(NBTBase.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                NBTBase nbt = (NBTBase) object;
                NBTTagCompound wrapper = new NBTTagCompound();
                if (nbt != null) {
                    wrapper.setTag("value", nbt);
                }
                output.writeNBTTagCompoundToBuffer(wrapper);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                try {
                    NBTTagCompound wrapper = input.readNBTTagCompoundFromBuffer();
                    if (wrapper != null && wrapper.hasKey("value")) {
                        return wrapper.getTag("value");
                    }
                    return null;
                } catch (IOException ioexception) {
                    throw new EncoderException(ioexception);
                }
            }
        });

        codecActions.put(NBTTagCompound.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                output.writeNBTTagCompoundToBuffer((NBTTagCompound) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                try {
                    return input.readNBTTagCompoundFromBuffer();
                } catch (IOException ioexception) {
                    throw new EncoderException(ioexception);
                }
            }
        });

        codecActions.put(ItemStack.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                output.writeItemStackToBuffer((ItemStack) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                try {
                    return input.readItemStackFromBuffer();
                } catch (IOException ioexception) {
                    throw new EncoderException(ioexception);
                }
            }
        });

        codecActions.put(BlockStack.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                output.writeBlockStack((BlockStack) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return input.readBlockStack();
            }
        });

        codecActions.put(FluidStack.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                output.writeFluidStack((FluidStack) object);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                try {
                    return input.readFluidStack();
                } catch (IOException ioexception) {
                    throw new EncoderException(ioexception);
                }
            }
        });

        codecActions.put(Item.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeInt(Item.getIdFromItem((Item) object));
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return Item.getItemById(input.readInt());
            }
        });

        codecActions.put(ForgeDirection.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeInt(((ForgeDirection) object).ordinal());
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return ForgeDirection.values()[input.readInt()];
            }
        });

        codecActions.put(BlockPos.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                output.writeLong(((BlockPos) object).toLong());
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return BlockPos.fromLong(input.readLong());
            }
        });
        codecActions.put(DimPos.class, new PacketCodec.ICodecAction() {

            @Override
            public void encode(Object o, ExtendedBuffer extendedBuffer) throws IOException {
                DimPos dimPos = (DimPos) o;
                extendedBuffer.writeInt(dimPos.getDimensionId());
                extendedBuffer.writeLong(
                    dimPos.getBlockPos()
                        .toLong());
            }

            @Override
            public Object decode(ExtendedBuffer extendedBuffer) {
                int dim = extendedBuffer.readInt();
                return DimPos.of(dim, BlockPos.fromLong(extendedBuffer.readLong()));
            }
        });
        codecActions.put(List.class, new ICodecAction() {

            // Packet structure:
            // List length (int)
            // --- end if length == 0
            // Value class name (UTF)
            // index + value
            // -1

            @Override
            public void encode(Object object, ExtendedBuffer output) throws IOException {
                List<?> list = (List<?>) object;
                int size = list.size();

                output.writeInt(size);

                if (size == 0) return;

                Class<?> valueClass = null;
                ICodecAction valueAction = null;

                for (Object value : list) {
                    if (value == null) continue;

                    if (valueClass == null) {
                        valueClass = value.getClass();
                        valueAction = getAction(valueClass);
                    } else if (value.getClass() != valueClass) {
                        throw new IllegalArgumentException("List elements must have the same runtime type");
                    }
                }

                if (valueAction == null) {
                    output.writeString("__noclass");
                    return;
                }

                output.writeString(valueClass.getName());

                for (int i = 0; i < size; i++) {
                    Object value = list.get(i);

                    if (value == null) continue;

                    output.writeInt(i);
                    valueAction.encode(value, output);
                }

                output.writeInt(-1);
            }

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object decode(ExtendedBuffer input) {
                int size = input.readInt();

                if (size < 0) {
                    throw new IllegalStateException("Invalid list size: " + size);
                }

                if (size == 0) {
                    return Collections.emptyList();
                }

                List list = Lists.newArrayListWithExpectedSize(size);
                try {
                    String className = input.readString();

                    if (className.equals("__noclass")) {
                        for (int i = 0; i < size; i++) {
                            list.add(null);
                        }

                        return list;
                    }

                    ICodecAction valueAction = getAction(Class.forName(className));

                    int currentLength = 0;
                    int previousIndex = -1;

                    while (true) {
                        int index = input.readInt();

                        if (index == -1) break;

                        if (index <= previousIndex || index >= size) {
                            throw new IllegalStateException("Invalid list index: " + index + ", size: " + size);
                        }

                        while (currentLength < index) {
                            list.add(null);
                            currentLength++;
                        }

                        list.add(valueAction.decode(input));

                        currentLength++;
                        previousIndex = index;
                    }
                    while (currentLength < size) {
                        list.add(null);
                        currentLength++;
                    }
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Failed to resolve List codec class", e);
                }
                return list;
            }
        });

        codecActions.put(ChunkCoordinates.class, new ICodecAction() {

            @Override
            public void encode(Object object, ExtendedBuffer output) {
                ChunkCoordinates coords = (ChunkCoordinates) object;
                output.writeInt(coords.posX);
                output.writeInt(coords.posY);
                output.writeInt(coords.posZ);
            }

            @Override
            public Object decode(ExtendedBuffer input) {
                return new ChunkCoordinates(input.readInt(), input.readInt(), input.readInt());
            }
        });
    }

    public static void addCodedAction(Class<?> clazz, ICodecAction action) {
        codecActions.put(clazz, action);
    }

    public static boolean hasCodecAction(Class<?> clazz) {
        return codecActions.get(clazz) != null;
    }

    protected SingleCache<Void, List<Field>> fieldCache = new SingleCache<Void, List<Field>>(
        new SingleCache.ICacheUpdater<Void, List<Field>>() {

            @Override
            public List<Field> getNewValue(Void key) {
                List<Field> fieldList = Lists.newLinkedList();

                Class<?> clazz = PacketCodec.this.getClass();
                for (; clazz != PacketCodec.class && clazz != null; clazz = clazz.getSuperclass()) {
                    Field[] fields = clazz.getDeclaredFields();

                    // Sort this because the Java API tells us that getDeclaredFields()
                    // does not deterministically define the order of the fields in the array.
                    // Otherwise we might get nasty class cast exceptions when running in SMP.
                    Arrays.sort(fields, new Comparator<Field>() {

                        @Override
                        public int compare(Field o1, Field o2) {
                            return o1.getName()
                                .compareTo(o2.getName());
                        }

                    });

                    for (final Field field : fields) {
                        if (field.isAnnotationPresent(CodecField.class)) {
                            fieldList.add(field);
                        }
                    }
                }

                return fieldList;
            }

            @Override
            public boolean isKeyEqual(Void cacheKey, Void newKey) {
                return true;
            }

        });

    @Nullable
    protected static ICodecAction getActionSuper(Class<?> clazz) {
        if (ClassUtils.isPrimitiveWrapper(clazz)) {
            clazz = ClassUtils.wrapperToPrimitive(clazz);
        }
        ICodecAction action = codecActions.get(clazz);
        if (action == null) {
            for (Class<?> iface : clazz.getInterfaces()) {
                action = codecActions.get(iface);
                if (action != null) {
                    return action;
                }
            }
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getActionSuper(superClass);
            } else {
                return null;
            }
        }
        return action;
    }

    public static ICodecAction getAction(Class<?> clazz) {
        ICodecAction action = getActionSuper(clazz);
        if (action == null) {
            System.err.println("No ICodecAction was found for " + clazz + ". You should add one in PacketCodec.");
        }
        return action;
    }

    private void loopCodecFields(ICodecRunnable runnable) {
        for (Field field : fieldCache.get(null)) {
            ICodecAction action = getAction(field.getType());

            if (action == null) {
                throw new IllegalStateException(
                    "Missing codec for " + field.getType()
                        .getName());
            }

            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                runnable.run(field, action);
            } catch (Exception e) {
                throw new IllegalStateException(
                    "Failed to process packet " + getClass().getName()
                        + ", field="
                        + field.getName()
                        + ", type="
                        + field.getType()
                            .getName(),
                    e);
            }
        }
    }

    @Override
    public void encode(final ExtendedBuffer output) {
        loopCodecFields((field, action) -> {
            Object object = null;
            try {
                object = field.get(PacketCodec.this);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            action.encode(object, output);
        });
    }

    @Override
    public void decode(final ExtendedBuffer input) {
        loopCodecFields((field, action) -> {
            Object object = action.decode(input);
            try {
                field.set(PacketCodec.this, object);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public interface ICodecAction {

        /**
         * Encode the given object.
         *
         * @param object The object to encode into the output.
         * @param output The byte array to encode to.
         */
        public void encode(Object object, ExtendedBuffer output) throws IOException;

        /**
         * Decode from the input.
         *
         * @param input The byte array to decode from.
         * @return The object to return after reading it from the input.
         */
        public Object decode(ExtendedBuffer input);

    }

    public interface ICodecRunnable {

        /**
         * Run a type of codec.
         *
         * @param field  The field annotated with {@link CodecField}.
         * @param action The action that must be applied to the field.
         */
        public void run(Field field, ICodecAction action) throws IOException;

    }

}
