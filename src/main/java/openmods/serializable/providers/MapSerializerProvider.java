package openmods.serializable.providers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import net.minecraft.network.PacketBuffer;
import openmods.reflection.TypeUtils;
import openmods.serializable.IGenericSerializerProvider;
import openmods.serializable.SerializerRegistry;
import openmods.utils.io.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class MapSerializerProvider implements IGenericSerializerProvider {

	@Override
	public IStreamSerializer<?> getSerializer(Type type) {
		TypeToken<?> typeToken = TypeToken.of(type);

		if (TypeUtils.MAP_TOKEN.isAssignableFrom(typeToken)) {
			final TypeToken<?> keyType = typeToken.resolveType(TypeUtils.MAP_KEY_PARAM);
			final TypeToken<?> valueType = typeToken.resolveType(TypeUtils.MAP_VALUE_PARAM);

			final IStreamSerializer<Object> keySerializer = getSerializer(keyType);
			final IStreamSerializer<Object> valueSerializer = getSerializer(valueType);

			return new IStreamSerializer<Map<Object, Object>>() {

				@Override
				public Map<Object, Object> readFromStream(PacketBuffer input) throws IOException {
					final int length = input.readVarIntFromBuffer();

					Map<Object, Object> result = Maps.newHashMap();

					if (length > 0) {
						final int nullBitsSize = StreamUtils.bitsToBytes(length * 2);
						final byte[] nullBits = StreamUtils.readBytes(input, nullBitsSize);
						final InputBitStream nullBitStream = InputBitStream.create(nullBits);

						for (int i = 0; i < length; i++) {
							Object key = null;
							if (nullBitStream.readBit()) key = keySerializer.readFromStream(input);

							Object value = null;
							if (nullBitStream.readBit()) value = valueSerializer.readFromStream(input);

							result.put(key, value);
						}
					}

					return result;
				}

				@Override
				public void writeToStream(Map<Object, Object> o, PacketBuffer output) throws IOException {
					final int length = o.size();
					output.writeVarIntToBuffer(length);

					if (length > 0) {
							final OutputBitStream nullBitsStream = OutputBitStream.create(output);

						List<Map.Entry<Object, Object>> entries = ImmutableList.copyOf(o.entrySet());

						for (Map.Entry<Object, Object> e : entries) {
							nullBitsStream.writeBit(e.getKey() != null);
							nullBitsStream.writeBit(e.getValue() != null);
						}

						nullBitsStream.flush();

						for (Map.Entry<Object, Object> e : entries) {
							writeValue(e.getKey(), keySerializer, output);
							writeValue(e.getValue(), valueSerializer, output);
						}
					}
				}

				private void writeValue(Object value, IStreamSerializer<Object> serializer, PacketBuffer output) throws IOException {
					if (value != null) serializer.writeToStream(value, output);
				}
			};
		}

		return null;
	}

	private static IStreamSerializer<Object> getSerializer(final TypeToken<?> type) {
		final IStreamSerializer<Object> keySerializer = SerializerRegistry.instance.findSerializer(type.getType());
		Preconditions.checkNotNull(keySerializer, "Can't find serializer for %s", type);
		return keySerializer;
	}

}
