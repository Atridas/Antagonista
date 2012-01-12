package cat.atridas.antagonista.graphics.gl2;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TechniquePass;

public final class RenderableObjectManagerGL2 extends RenderableObjectManager {

  private static final int BUFFER_SIZE = 16; // 3 matrius de 16 floats
  
  private FloatBuffer buffer = BufferUtils.createFloatBuffer(BUFFER_SIZE);

  @Override
  public boolean init() {
    assert !cleaned;
    // no-no-no-thing!!!!
    
    return !Utils.hasGLErrors();
  }

  @Override
  protected void setInstanceUniforms(Matrix4f modelViewProj,
      Matrix4f modelView, Matrix4f modelViewInvTransp) {
    assert !cleaned;
    // no-no-no-thing!!!!
  }

  @Override
  protected void setInstanceUniforms(TechniquePass pass,
      Matrix4f modelViewProj, Matrix4f modelView, Matrix4f modelViewInvTransp) {
    assert !cleaned;

    //TODO jugar amb les marques i tal per fer-ho més optim, potser

    buffer.rewind();
    Utils.matrixToBuffer(modelViewProj, buffer);
    buffer.rewind();
    glUniformMatrix4(pass.getModelViewProjectionUniform(), false, buffer);

    buffer.rewind();
    Utils.matrixToBuffer(modelView, buffer);
    buffer.rewind();
    glUniformMatrix4(pass.getModelViewUniform(), false, buffer);

    buffer.rewind();
    Utils.matrixToBuffer(modelViewInvTransp, buffer);
    buffer.rewind();
    glUniformMatrix4(pass.getModelViewITUniform(), false, buffer);

  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    // no-no-no-thing!!!!
    
    cleaned = true;
  }

}
