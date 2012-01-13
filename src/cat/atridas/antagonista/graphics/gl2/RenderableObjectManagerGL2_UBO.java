package cat.atridas.antagonista.graphics.gl2;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.ARBUniformBufferObject.*;
import static org.lwjgl.opengl.GL15.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.InstanceData;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TechniquePass;

public final class RenderableObjectManagerGL2_UBO extends RenderableObjectManager {

  private static final int BUFFER_SIZE = 3 * 16 + 4*4; // 3 matrius de 16 floats + 4 colors(de 4 floats)
  
  private FloatBuffer buffer = BufferUtils.createFloatBuffer(BUFFER_SIZE);
  private int bufferID = -1;

  public RenderableObjectManagerGL2_UBO() {
    throw new RuntimeException("Not implemented");
  }
  
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
    glBufferData(GL_UNIFORM_BUFFER, BUFFER_SIZE * Utils.FLOAT_SIZE, GL_DYNAMIC_DRAW);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
    
    return !Utils.hasGLErrors();
  }

  @Override
  protected void setInstanceUniforms(InstanceData instanceData) {
    assert !cleaned;
    assert bufferID > 0;
    
    buffer.rewind();
    Utils.matrixToBuffer(instanceData.modelViewProj, buffer);
    Utils.matrixToBuffer(instanceData.modelView, buffer);
    Utils.matrixToBuffer(instanceData.modelViewInvTransp, buffer);

    buffer.put(instanceData.specialColor0.x);
    buffer.put(instanceData.specialColor0.y);
    buffer.put(instanceData.specialColor0.z);
    buffer.put(instanceData.specialColor0.w);

    buffer.put(instanceData.specialColor1.x);
    buffer.put(instanceData.specialColor1.y);
    buffer.put(instanceData.specialColor1.z);
    buffer.put(instanceData.specialColor1.w);

    buffer.put(instanceData.specialColor2.x);
    buffer.put(instanceData.specialColor2.y);
    buffer.put(instanceData.specialColor2.z);
    buffer.put(instanceData.specialColor2.w);

    buffer.put(instanceData.specialColor3.x);
    buffer.put(instanceData.specialColor3.y);
    buffer.put(instanceData.specialColor3.z);
    buffer.put(instanceData.specialColor3.w);
    
    buffer.rewind();
    
    glBindBuffer(GL_UNIFORM_BUFFER, bufferID);
    glBufferData(GL_UNIFORM_BUFFER, buffer, GL_DYNAMIC_DRAW);
    
    glBindBufferRange(
        GL_UNIFORM_BUFFER, 
        TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
        bufferID, 
        0, 
        3 * 16 * Utils.FLOAT_SIZE);
    
    glBindBufferRange(
        GL_UNIFORM_BUFFER, 
        TechniquePass.SPECIAL_COLORS_UNIFORMS_BINDING, 
        bufferID, 
        3 * 16 * Utils.FLOAT_SIZE, 
        4 * 4 * Utils.FLOAT_SIZE);

    assert !Utils.hasGLErrors();
  }

  @Override
  protected void setInstanceUniforms(TechniquePass pass, InstanceData instanceData) {
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
