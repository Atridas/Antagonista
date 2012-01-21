package cat.atridas.antagonista.graphics.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Font;
import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.Texture;

public abstract class FontManagerGL extends FontManager {

  protected int vertexBuffer = -1, indexBuffer = -1;
  private int vbLen = 0, ibLen = 0;
  
  //private ShaderObject shader = null;
  private TechniquePass pass;
  
  private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
  private Matrix4f    aligmentMatrix = new Matrix4f();
  private Matrix4f    finalWVP       = new Matrix4f();
  private Vector3f    translation    = new Vector3f();
  
  @Override
  public final void printString(Font font, String text, Tuple3f color, Matrix4f WVPmatrix, TextAligment aligment, RenderManager rm) {
    int len = text.length();
    int buffer1Size = Font.getVertexSize() * len * 4;
    ByteBuffer buffer1 = BufferUtils.createByteBuffer(buffer1Size);
    IntBuffer  buffer2 = BufferUtils.createIntBuffer(len * 6);
    
    Texture tex[] = new Texture[font.numTextures()];
    
    int x = font.fillBuffers(text, buffer1, buffer2, tex);

    translation.z = 0;
    
    switch(aligment) {
    case TOP_LEFT:
    case TOP_RIGHT:
    case TOP_CENTER:
      translation.y = 0;
      break;
    case BOTTOM_LEFT:
    case BOTTOM_RIGHT:
    case BOTTOM_CENTER:
      translation.y = -font.getLineHeight();
      break;
    case MID_LEFT:
    case MID_RIGHT:
    case MID_CENTER:
      translation.y = -font.getLineHeight() / 2.f;
      break;
    }
    
    switch(aligment) {
    case TOP_LEFT:
    case MID_LEFT:
    case BOTTOM_LEFT:
      translation.x = 0;
      break;
    case TOP_RIGHT:
    case MID_RIGHT:
    case BOTTOM_RIGHT:
      translation.x = -x;
      break;
    case TOP_CENTER:
    case BOTTOM_CENTER:
    case MID_CENTER:
      translation.x = -x / 2.f;
      break;
    }

    aligmentMatrix.setIdentity();
    aligmentMatrix.setTranslation(translation);
    
    
    finalWVP.mul(WVPmatrix, aligmentMatrix);
    
    buffer1.rewind();
    buffer2.rewind();
    
    rm.noVertexArray();
    
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
    
    
    if(pass == null) {
      //shader = new ShaderObject(Font.VERTEX_SHADER, Font.FRAGMENT_SHADER_1_TEX);
      pass = Core.getCore().getEffectManager().getFontPass();
    }
    
    for(int i = 0; i < tex.length; ++i) {
      tex[i].activate(i);
    }
    
    //shader.activate();

    //shader.setUniform(u_WVPmatrix, WVPmatrix);
    //shader.setUniform(u_color, color);
    //shader.setTextureUniform(u_tex0, 0);
    
    //font.setAttributes(shader, a_position, a_texCoord, a_channel, a_page);
    
    activateVAO();
    
    pass.activate(rm);
    
    matrixBuffer.clear();
    Utils.matrixToBuffer(finalWVP, matrixBuffer);
    matrixBuffer.flip();
    
    glUniformMatrix4(pass.getModelViewProjectionUniform(), false, matrixBuffer);
    glVertexAttrib3f(TechniquePass.FONT_COLOR_ATTRIBUTE, color.x, color.y, color.z);
    
    glDrawElements(GL_TRIANGLES, len * 6, GL_UNSIGNED_INT, 0);
  }
  
  protected abstract void activateVAO();
  
  protected final void bindVertexAttribs() {


    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

    glEnableVertexAttribArray(TechniquePass.FONT_POSITION_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.FONT_TEX_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.FONT_CHANNEL_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.FONT_PAGE_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.FONT_COLOR_ATTRIBUTE);

    glVertexAttribPointer(TechniquePass.FONT_POSITION_ATTRIBUTE, 2, 
        GL_INT, false, Font.VERTEX_STRIDE, Font.POSITION_OFFSET);
    
    glVertexAttribPointer(TechniquePass.FONT_PAGE_ATTRIBUTE, 1, 
        GL_INT, false, Font.VERTEX_STRIDE, Font.PAGE_OFFSET);
    
    glVertexAttribPointer(TechniquePass.FONT_TEX_ATTRIBUTE, 2, 
        GL_FLOAT, false, Font.VERTEX_STRIDE, Font.TEXCOORDS_OFFSET);
    
    glVertexAttribPointer(TechniquePass.FONT_CHANNEL_ATTRIBUTE, 4, 
        GL_BYTE, true, Font.VERTEX_STRIDE, Font.CHANNEL_OFFSET);
  }

}
