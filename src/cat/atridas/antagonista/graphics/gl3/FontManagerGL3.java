package cat.atridas.antagonista.graphics.gl3;

import cat.atridas.antagonista.graphics.Font;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.gl.FontManagerGL;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class FontManagerGL3 extends FontManagerGL {
  
  private int vao = 0;

  @Override
  protected void activateVAO() {
    if(vao == 0) {
      vao = glGenVertexArrays();
      glBindVertexArray(vao);

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
      
      glVertexAttribPointer(TechniquePass.FONT_CHANNEL_ATTRIBUTE, 2, 
          GL_FLOAT, false, Font.VERTEX_STRIDE, Font.CHANNEL_OFFSET);
      
    } else {
      glBindVertexArray(vao);
    }
  }

  boolean cleaned = false;
  
  @Override
  protected void finalize() {
    assert !cleaned;
    glDeleteVertexArrays(vao);
  }
}
