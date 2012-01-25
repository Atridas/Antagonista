package cat.atridas.antagonista.graphics.gl2;

import cat.atridas.antagonista.graphics.gl.FontManagerGL;

public final class FontManagerGL2 extends FontManagerGL {

  int lastVAO = 0;

  @Override
  protected void activateVAO(int vao, CachedTextInfo cachedBuffers) {
    bindVertexAttribs(cachedBuffers);
  }

  @Override
  protected int createVAO() {
    return ++lastVAO;
  }

}
