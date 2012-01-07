package cat.atridas.antagonista.graphics.gl3;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import static org.lwjgl.opengl.GL15.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TechniquePass;

public final class RenderableObjectManagerGL3 extends RenderableObjectManager {

  private static final int BUFFER_SIZE = 3 * 16; // 3 matrius de 16 floats
  
  private FloatBuffer buffer = BufferUtils.createFloatBuffer(BUFFER_SIZE);
  private int bufferID = -1;

  @Override
  public boolean init() {
    assert !cleaned;
    assert bufferID == -1;

    bufferID = GL15.glGenBuffers();
    if(bufferID < 0) {
      Utils.hasGLErrors();
      return false;
    }
    GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, bufferID);
    GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, BUFFER_SIZE, GL15.GL_STATIC_DRAW);
    GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
    
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
    
    GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, bufferID);
    GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    
    GL30.glBindBufferRange(
        GL31.GL_UNIFORM_BUFFER, 
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
