package cat.atridas.antagonista.graphics.gl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Font;
import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.ShaderObject;
import cat.atridas.antagonista.graphics.Texture;
import cat.atridas.antagonista.graphics.gl.FontGL.NullFont;

public class FontManagerGL extends FontManager {

  private static final FontGL defaultFont = new NullFont();

  private int vertexBuffer = -1, indexBuffer = -1;
  private int vbLen = 0, ibLen = 0;
  
  private int u_WVPmatrix, a_position, a_texCoord, a_channel, a_page, u_color, u_tex0;
  
  private ShaderObject shader = null;
  
  @Override
  protected final Font getDefaultFont() {
    return defaultFont;
  }

  @Override
  protected final Font createFont(String path, RenderManager rm) throws IOException {
    return new FontGL(path, rm);
  }
  
  public final void printString(Font font, String text, Tuple3f color, Matrix4f WVPmatrix, boolean centered) {
    int len = text.length();
    int buffer1Size = Font.getVertexSize() * len * 4;
    ByteBuffer buffer1 = BufferUtils.createByteBuffer(buffer1Size);
    IntBuffer  buffer2 = BufferUtils.createIntBuffer(len * 6);
    
    Texture tex[] = new Texture[font.numTextures()];
    
    int x = font.fillBuffers(text, buffer1, buffer2, tex);
    
    if(centered) {
      buffer1 = centerText(buffer1, buffer1Size, x);
    }
    
    buffer1.rewind();
    buffer2.rewind();
    
    //Actualitzar el buffer de vertexos
    if(vbLen < buffer1Size) {
      if(vertexBuffer == -1) {
        vertexBuffer = glGenBuffers();
      }
      vbLen = buffer1Size;
      glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
      glBufferData(GL_ARRAY_BUFFER, buffer1, GL_DYNAMIC_DRAW);
      
    } else {
      glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
      glBufferSubData(GL_ARRAY_BUFFER, 0, buffer1);
    }
    
    //Actualitzar el buffer de indexos
    if(ibLen < len * 6) {
      if(indexBuffer == -1) {
        indexBuffer = glGenBuffers();
      }
      ibLen = len * 6;
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer2, GL_DYNAMIC_DRAW);
      
      //printBuffers(buffer1, buffer2);
    } else {
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
      glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, buffer2);
    }
    
    
    if(shader == null) {
      //shader = new ShaderObject(Font.VERTEX_SHADER, Font.FRAGMENT_SHADER_1_TEX);
      shader = Core.getCore().getShaderManager().getShader("text");
      u_WVPmatrix = shader.getUniform("u_WorldViewProj");
      u_tex0      = shader.getUniform("u_page0");
      u_color     = shader.getUniform("u_color");
      a_position  = shader.getAttrib("a_position");
      a_texCoord  = shader.getAttrib("a_texCoord");
      a_channel   = shader.getAttrib("a_channel");
      a_page      = shader.getAttrib("a_page");
    }
    
    for(int i = 0; i < tex.length; ++i) {
      tex[i].activate(Core.getCore().getRenderManager(), i);
    }
    
    shader.activate();

    shader.setUniform(u_WVPmatrix, WVPmatrix);
    shader.setUniform(u_color, color);
    shader.setTextureUniform(u_tex0, 0);
    
    font.setAttributes(shader, a_position, a_texCoord, a_channel, a_page);
    
    glDrawElements(GL_TRIANGLES, len * 6, GL_UNSIGNED_INT, 0);
  }
  
  private ByteBuffer centerText(ByteBuffer buffer1, int buffer1Size, int sizeX) {

    ByteBuffer newBuffer1 = BufferUtils.createByteBuffer(buffer1Size);

    buffer1.rewind();
    
    for(int i = 0; i < buffer1Size/Font.getVertexSize(); i++)
    {
      //vertex 00
      newBuffer1.putInt(buffer1.getInt() - sizeX/2); //x
      newBuffer1.putInt(buffer1.getInt()); //y
        
      newBuffer1.putInt(buffer1.getInt());   //page
  
      newBuffer1.putFloat(buffer1.getFloat());
      newBuffer1.putFloat(buffer1.getFloat());
  
      newBuffer1.put(buffer1.get()); //channel
      newBuffer1.put(buffer1.get()); //channel
      newBuffer1.put(buffer1.get()); //channel
      newBuffer1.put(buffer1.get()); //channel
    }
    return newBuffer1;
}

}
