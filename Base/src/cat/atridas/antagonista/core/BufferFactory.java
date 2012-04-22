package cat.atridas.antagonista.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Class used to instantiate Buffers.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.5
 * 
 */
public final class BufferFactory {

  /**
   * Global instance of a implementation.
   * 
   * @since 0.5
   */
  private static BufferFactoryInstance instance;

  /**
   * Creates a buffer of <code>length</code> of shorts.
   * 
   * @param length
   *          number of shorts this buffer will be able to contain.
   * @return a newly created buffer.
   * @since 0.5
   */
  public static ShortBuffer createShortBuffer(int length) {
    return instance.createShortBuffer(length);
  }

  /**
   * Creates a buffer of <code>length</code> of floats.
   * 
   * @param length
   *          number of shorts this buffer will be able to contain.
   * @return a newly created buffer.
   * @since 0.5
   */
  public static FloatBuffer createFloatBuffer(int length) {
    return instance.createFloatBuffer(length);
  }

  /**
   * Creates a buffer of <code>length</code> of bytes.
   * 
   * @param length
   *          number of shorts this buffer will be able to contain.
   * @return a newly created buffer.
   * @since 0.5
   */
  public static ByteBuffer createByteBuffer(int length) {
    return instance.createByteBuffer(length);
  }

  /**
   * Implement this class to provide the engine a way to instantiate buffers.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.5
   * 
   */
  public static abstract class BufferFactoryInstance {

    /**
     * The constructor registers the instance as a global object, and so it will
     * be used from now on as a buffer factory.
     * 
     * @since 0.5
     */
    protected BufferFactoryInstance() {
      instance = this;
    }

    /**
     * Creates a buffer of <code>length</code> of shorts.
     * 
     * @param length
     *          number of shorts this buffer will be able to contain.
     * @return a newly created buffer.
     * @since 0.5
     */
    protected abstract ShortBuffer createShortBuffer(int length);

    /**
     * Creates a buffer of <code>length</code> of floats.
     * 
     * @param length
     *          number of shorts this buffer will be able to contain.
     * @return a newly created buffer.
     * @since 0.5
     */
    protected abstract FloatBuffer createFloatBuffer(int length);

    /**
     * Creates a buffer of <code>length</code> of bytes.
     * 
     * @param length
     *          number of shorts this buffer will be able to contain.
     * @return a newly created buffer.
     * @since 0.5
     */
    protected abstract ByteBuffer createByteBuffer(int length);
  }
}
