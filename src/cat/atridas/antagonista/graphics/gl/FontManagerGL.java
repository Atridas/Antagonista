package cat.atridas.antagonista.graphics.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;

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
  
  @Override
  protected final void printString(
      ByteBuffer _vertexBuffer, ShortBuffer _indexBuffer,
      Texture[] _tex, int indexLen,
      Matrix4f WVPMatrix, Color3f color,
      RenderManager rm) {
    
    
    rm.noVertexArray();
    
    //Actualitzar el buffer de vertexos
    if(vbLen < _vertexBuffer.capacity()) {
      if(vertexBuffer == -1) {
        vertexBuffer = glGenBuffers();
      }
      vbLen = _vertexBuffer.capacity();
      glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
      glBufferData(GL_ARRAY_BUFFER, _vertexBuffer, GL_DYNAMIC_DRAW);
      
    } else {
      glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
      glBufferSubData(GL_ARRAY_BUFFER, 0, _vertexBuffer);
    }
    
    //Actualitzar el buffer de indexos
    if(ibLen < _indexBuffer.capacity()) {
      if(indexBuffer == -1) {
        indexBuffer = glGenBuffers();
      }
      ibLen = _indexBuffer.capacity();
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, _indexBuffer, GL_DYNAMIC_DRAW);
      
      //printBuffers(buffer1, buffer2);
    } else {
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
      glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, _indexBuffer);
    }
    
    
    if(pass == null) {
      //shader = new ShaderObject(Font.VERTEX_SHADER, Font.FRAGMENT_SHADER_1_TEX);
      pass = Core.getCore().getEffectManager().getFontPass();
    }
    
    for(int i = 0; i < _tex.length; ++i) {
      _tex[i].activate(i);
    }
    
    //shader.activate();

    //shader.setUniform(u_WVPmatrix, WVPmatrix);
    //shader.setUniform(u_color, color);
    //shader.setTextureUniform(u_tex0, 0);
    
    //font.setAttributes(shader, a_position, a_texCoord, a_channel, a_page);
    
    activateVAO();
    
    pass.activate(rm);
    
    matrixBuffer.clear();
    Utils.matrixToBuffer(WVPMatrix, matrixBuffer);
    matrixBuffer.flip();
    
    glUniformMatrix4(pass.getModelViewProjectionUniform(), false, matrixBuffer);
    glVertexAttrib3f(TechniquePass.FONT_COLOR_ATTRIBUTE, color.x, color.y, color.z);
    
    glDrawElements(GL_TRIANGLES, indexLen, GL_UNSIGNED_SHORT, 0);
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
