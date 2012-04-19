package cat.atridas.antagonista.graphics.gl3;

import java.util.HashSet;

import cat.atridas.antagonista.graphics.gl.FontManagerGL;

import static org.lwjgl.opengl.GL30.*;

/**
 * OpenGL 3.3 implementation of the FontManager class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 * 
 */
public final class FontManagerGL3 extends FontManagerGL {

  /**
   * Vertex Array Objects that had been generated and need deletion.
   * 
   * @since 0.1
   */
  private final HashSet<Integer> generatedVAOs = new HashSet<>();
  /**
   * Vertex Array Objects that had been generated not yet initialized.
   * 
   * @since 0.1
   */
  private final HashSet<Integer> uninitializedVAOs = new HashSet<>();

  @Override
  protected void activateVAO(int vao, CachedTextInfo cachedBuffers) {
    if (uninitializedVAOs.contains(vao)) {
      // vao = glGenVertexArrays();
      glBindVertexArray(vao);

      bindVertexAttribs(cachedBuffers);

      uninitializedVAOs.remove(vao);

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

  /**
   * Contains information concerning if this object had been cleared.
   * 
   * @since 0.1
   */
  boolean cleaned = false;

  @Override
  protected void finalize() {
    assert !cleaned;
    for (int vao : generatedVAOs)
      glDeleteVertexArrays(vao);
    cleaned = true;
  }
}
