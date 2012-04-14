package cat.atridas.antagonista.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public abstract class BufferUtils {

	private static BufferUtilsInstance instance;


	public static ShortBuffer createShortBuffer(int length) {
		return instance.createShortBuffer(length);
	}

	public static FloatBuffer createFloatBuffer(int length) {
		return instance.createFloatBuffer(length);
	}

	public static ByteBuffer createByteBuffer(int length) {
		return instance.createByteBuffer(length);
	}
	
	public static abstract class BufferUtilsInstance {
		
		protected BufferUtilsInstance() {
			instance = this;
		}

		protected abstract ShortBuffer createShortBuffer(int length);
		protected abstract FloatBuffer createFloatBuffer(int length);
		protected abstract ByteBuffer createByteBuffer(int length);
	}
}
