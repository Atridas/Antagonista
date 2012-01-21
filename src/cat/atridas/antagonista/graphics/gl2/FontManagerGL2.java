package cat.atridas.antagonista.graphics.gl2;

import cat.atridas.antagonista.graphics.gl.FontManagerGL;

public final class FontManagerGL2 extends FontManagerGL {

  @Override
  protected void activateVAO() {
    bindVertexAttribs();
  }

}
