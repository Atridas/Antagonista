package cat.atridas.antagonista.graphics.gl3;

import java.util.HashSet;

import cat.atridas.antagonista.graphics.gl.FontManagerGL;

import static org.lwjgl.opengl.GL30.*;

public final class FontManagerGL3 extends FontManagerGL {

  private final HashSet<Integer> generatedVAOs = new HashSet<>();
  private final HashSet<Integer> uninitializedVAOs = new HashSet<>();

  @Override
  protected void activateVAO(int vao, CachedTextInfo cachedBuffers) {
    if(uninitializedVAOs.contains(vao)) {
      vao = glGenVertexArrays();
      glBindVertexArray(vao);
      
      bindVertexAttribs(cachedBuffers);
      
    } else {
      glBindVertexArray(vao);
    }
  }

  @Override
  protected int createVAO() {
    int vao = glGenVertexArrays();
    generatedVAOs.add(vao);
    uninitializedVAOs.add(vao);
    return vao;
  }

  boolean cleaned = false;
  
  @Override
  protected void finalize() {
    assert !cleaned;
    for(int vao : generatedVAOs)
      glDeleteVertexArrays(vao);
    cleaned = true;
  }
}
