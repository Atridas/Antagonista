package cat.atridas.antagonista.graphics.gl3;

import java.nio.FloatBuffer;


import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import static org.lwjgl.opengl.GL15.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.InstanceData;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TechniquePass;

public final class RenderableObjectManagerGL3 extends RenderableObjectManager {

  private static final int BUFFER_MATRIXES_SIZE = 3 * 16; // 3 matrius de 16 floats
  private static final int BUFFER_COLORS_SIZE = 3 * 16; // 3 matrius de 16 floats
  
  private static final int COLOR_OFFSET;
  private static final int BUFFER_SIZE; 
  
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

    buffer.position(COLOR_OFFSET);
    
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
    

    assert !Utils.hasGLErrors();
    
    //int bufferSize = glGetBufferParameter(GL_UNIFORM_BUFFER, GL_BUFFER_SIZE);
    
    
    glBindBufferRange(
        GL_UNIFORM_BUFFER, 
        TechniquePass.SPECIAL_COLORS_UNIFORMS_BINDING, 
        bufferID, 
        COLOR_OFFSET, 
        4 *  4 * Utils.FLOAT_SIZE);
    
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

  static {
    int aligment = glGetInteger(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT);
    
    int k = 0;
    while(aligment * k < BUFFER_MATRIXES_SIZE) {
      k++;
    }
    COLOR_OFFSET = aligment * k;
    BUFFER_SIZE  = COLOR_OFFSET + BUFFER_COLORS_SIZE;
  }
}
