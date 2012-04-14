package cat.atridas.antagonista.graphics.gl2;

import cat.atridas.antagonista.graphics.gl.FontManagerGL;

/**
 * OpenGL 2.1 implementation of the FontManager class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public final class FontManagerGL2 extends FontManagerGL {

  /**
   * Last created identifier.
   * @since 0.1
   */
  private int lastVAO = 0;

  @Override
  protected void activateVAO(int vao, CachedTextInfo cachedBuffers) {
    bindVertexAttribs(cachedBuffers);
  }

  @Override
  protected int createVAO() {
    return ++lastVAO;
  }

}
