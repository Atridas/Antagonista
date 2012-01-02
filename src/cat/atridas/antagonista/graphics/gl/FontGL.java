package cat.atridas.antagonista.graphics.gl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

import cat.atridas.antagonista.deprecated.ShaderObject;
import cat.atridas.antagonista.graphics.Font;
import cat.atridas.antagonista.graphics.Texture;

class FontGL extends Font {
  
  FontGL(String path) throws IOException {
    super(path);
  }

  public void setAttributes(
      ShaderObject shader,
      int position,
      int texCoord,
      int channel,
      int page)
  {
    int vsize = getVertexSize();
    shader.setAttribBufferedPointer(
        position, 2, GL_INT, false, vsize, 0);

    shader.setAttribBufferedPointer(
        page, 1, GL_INT, false, vsize, Integer.SIZE / 8 * 2);

    shader.setAttribBufferedPointer(
        texCoord, 2, GL_FLOAT, false, vsize, Integer.SIZE / 8 * 3);

    shader.setAttribBufferedPointer(
        channel, 4, GL_BYTE, false, vsize, Integer.SIZE / 8 * 3
                                              + Float.SIZE   / 8 * 2);
    
  }
  
  private FontGL() {}
  
  static final class NullFont extends FontGL {

    public NullFont() {super();}
    public int fillBuffers(CharSequence characters, ByteBuffer vertexBuffer, IntBuffer indexBuffer, Texture[] textures) {return 0;}
    
  }
}
