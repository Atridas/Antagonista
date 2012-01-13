package cat.atridas.antagonista.graphics.gl3;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT;

abstract class InstanceBufferUtils {

  static final int BUFFER_MATRIXES_SIZE = 3 * 16; // 3 matrius de 16 floats
  static final int BUFFER_COLORS_SIZE = 3 * 16; // 3 matrius de 16 floats
  
  static final int COLOR_OFFSET;
  static final int BUFFER_SIZE; 


  static {
    int aligment = glGetInteger(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT);
    
    int k = 0;
    while(aligment * k < BUFFER_MATRIXES_SIZE) {
      k++;
    }
    COLOR_OFFSET = aligment * k;
    BUFFER_SIZE  = COLOR_OFFSET + BUFFER_COLORS_SIZE;
  }
}
