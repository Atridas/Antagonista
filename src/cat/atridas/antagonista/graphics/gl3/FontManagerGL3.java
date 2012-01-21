package cat.atridas.antagonista.graphics.gl3;

import cat.atridas.antagonista.graphics.gl.FontManagerGL;

import static org.lwjgl.opengl.GL30.*;

public final class FontManagerGL3 extends FontManagerGL {
  
  private int vao = 0;

  @Override
  protected void activateVAO() {
    if(vao == 0) {
      vao = glGenVertexArrays();
      glBindVertexArray(vao);
      
      bindVertexAttribs();
      
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
