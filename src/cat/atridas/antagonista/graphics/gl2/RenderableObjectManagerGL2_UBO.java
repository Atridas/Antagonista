package cat.atridas.antagonista.graphics.gl2;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.ARBUniformBufferObject.*;
import static org.lwjgl.opengl.GL15.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TechniquePass;

public final class RenderableObjectManagerGL2_UBO extends RenderableObjectManager {

  private static final int BUFFER_SIZE = 3 * 16; // 3 matrius de 16 floats
  
  private FloatBuffer buffer = BufferUtils.createFloatBuffer(BUFFER_SIZE);
  private int bufferID = -1;

  @Override
  public boolean init() {
    assert !cleaned;
    assert bufferID == -1;

    bufferID = glGenBuffers();
    if(bufferID < 0) {
      Utils.hasGLErrors();
      return false;
    }
    glBindBuffer(GL_UNIFORM_BUFFER, bufferID);
    glBufferData(GL_UNIFORM_BUFFER, BUFFER_SIZE, GL_STATIC_DRAW);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
    
    return !Utils.hasGLErrors();
  }

  @Override
  protected void setInstanceUniforms(Matrix4f modelViewProj,
      Matrix4f modelView, Matrix4f modelViewInvTransp) {
    assert !cleaned;
    assert bufferID > 0;
    
    buffer.rewind();
    Utils.matrixToBuffer(modelViewProj, buffer);
    Utils.matrixToBuffer(modelView, buffer);
    Utils.matrixToBuffer(modelViewInvTransp, buffer);
    buffer.rewind();
    
    glBindBuffer(GL_UNIFORM_BUFFER, bufferID);
    glBufferData(GL_UNIFORM_BUFFER, buffer, GL_STATIC_DRAW);
    
    glBindBufferRange(
        GL_UNIFORM_BUFFER, 
        TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
        bufferID, 
        0, 
        4 * 8 * Utils.FLOAT_SIZE);
  }

  @Override
  protected void setInstanceUniforms(TechniquePass pass,
      Matrix4f modelViewProj, Matrix4f modelView, Matrix4f modelViewInvTransp) {
    assert !cleaned;
    assert bufferID > 0;
    // no-no-no-thing!!!!
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    assert bufferID > 0;
    
    glDeleteBuffers(bufferID);
    
    cleaned = true;
  }

}
