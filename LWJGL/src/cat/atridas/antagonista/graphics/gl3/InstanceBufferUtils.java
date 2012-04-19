package cat.atridas.antagonista.graphics.gl3;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT;

/**
 * Constants useful for instanced rendering.
 * 
 * @author Isaac 'Atridas' Serrano Guash
 * @since 0.1
 * 
 */
abstract class InstanceBufferUtils {

  /**
   * Number of floats needed in the matrix buffer.
   * 
   * @since 0.1
   */
  static final int BUFFER_MATRIXES_SIZE = 3 * 16; // 3 matrius de 16 floats
  /**
   * Number of floats needed in the special colors buffer.
   * 
   * @since 0.1
   */
  static final int BUFFER_COLORS_SIZE = 3 * 16; // 3 matrius de 16 floats

  /**
   * In a buffer containing both the matrix buffer and special colors buffer,
   * the offset to the color buffer.
   * 
   * @since 0.1
   */
  static final int COLOR_OFFSET;
  /**
   * In a buffer containing both the matrix buffer and special colors buffer,
   * the size of this buffer.
   * 
   * @since 0.1
   */
  static final int BUFFER_SIZE;

  static {
    int aligment = glGetInteger(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT);

    int k = 0;
    while (aligment * k < BUFFER_MATRIXES_SIZE) {
      k++;
    }
    COLOR_OFFSET = aligment * k;
    BUFFER_SIZE = COLOR_OFFSET + BUFFER_COLORS_SIZE;
  }
}
