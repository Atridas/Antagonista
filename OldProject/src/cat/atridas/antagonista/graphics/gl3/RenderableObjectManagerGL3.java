package cat.atridas.antagonista.graphics.gl3;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;


import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import static org.lwjgl.opengl.GL15.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.InstanceData;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TechniquePass;

/**
 * OpenGL 3.3 implementation of the RenderableObjectManager class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public final class RenderableObjectManagerGL3 extends RenderableObjectManager {

  /**
   * Auxiliar buffer used to pass instance information to the OpenGL driver.
   * @since 0.1 
   */
  private FloatBuffer buffer = BufferUtils.createFloatBuffer(InstanceBufferUtils.BUFFER_SIZE);
  
  /**
   * Auxiliar buffer used to pass bone to the OpenGL driver.
   * @since 0.3 
   */
  private FloatBuffer boneBuffer = BufferUtils.createFloatBuffer(
                                      TechniquePass.ARMATURE_UNIFORMS_BLOCK_SIZE / Utils.FLOAT_SIZE
                                      );
  
  /**
   * Single instance information buffer OpenGL identifier.
   * @since 0.1
   */
  private int bufferID = -1, boneBufferID = -1;

  @Override
  public boolean init() {
    assert !cleaned;
    assert bufferID == -1;

    bufferID = glGenBuffers();
    boneBufferID = glGenBuffers();
    if(bufferID < 0 || boneBufferID < 0) {
      Utils.hasGLErrors();
      return false;
    }
    glBindBuffer(GL_UNIFORM_BUFFER, bufferID);
    glBufferData(GL_UNIFORM_BUFFER, InstanceBufferUtils.BUFFER_SIZE * Utils.FLOAT_SIZE, GL_DYNAMIC_DRAW);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
    
    glBindBuffer(GL_UNIFORM_BUFFER, boneBufferID);
    glBufferData(GL_UNIFORM_BUFFER, TechniquePass.ARMATURE_UNIFORMS_BLOCK_SIZE, GL_DYNAMIC_DRAW);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
    
    return !Utils.hasGLErrors();
  }

  private Matrix4f auxiliarMatrix = new Matrix4f();
  
  @Override
  protected void setInstanceUniforms(InstanceData instanceData) {
    assert !cleaned;
    assert bufferID > 0;
    assert boneBufferID > 0;
    
    buffer.rewind();
    Utils.matrixToBuffer(instanceData.modelViewProj, buffer);
    Utils.matrixToBuffer(instanceData.modelView, buffer);
    Utils.matrixToBuffer(instanceData.modelViewInvTransp, buffer);

    buffer.position(InstanceBufferUtils.COLOR_OFFSET);
    
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
    glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer);
    
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
        InstanceBufferUtils.COLOR_OFFSET, 
        4 *  4 * Utils.FLOAT_SIZE);
    
    assert !Utils.hasGLErrors();
    
    
    if(instanceData.bonePalete != null) {
      int len = instanceData.bonePalete.length;
      if(len > TechniquePass.MAX_BONES) {
        len = TechniquePass.MAX_BONES;
      }
      
      boneBuffer.rewind();
      
      for(int i = 0; i < len; ++i) {
        Utils.matrix34ToBuffer(instanceData.bonePalete[i], boneBuffer);
      }

      boneBuffer.position(TechniquePass.ARMATURE_UNIFORMS_BLOCK_SIZE / (Utils.FLOAT_SIZE * 2));
      

      for(int i = 0; i < len; ++i) {
        auxiliarMatrix.invert(instanceData.bonePalete[i]);
        auxiliarMatrix.transpose();
        Utils.matrix34ToBuffer(auxiliarMatrix, boneBuffer);
      }
      
      boneBuffer.position(0);
      boneBuffer.limit(TechniquePass.ARMATURE_UNIFORMS_BLOCK_SIZE / Utils.FLOAT_SIZE);
      
      glBindBuffer(GL_UNIFORM_BUFFER, boneBufferID);
      glBufferSubData(GL_UNIFORM_BUFFER, 0, boneBuffer);

      
      glBindBufferRange(
          GL_UNIFORM_BUFFER, 
          TechniquePass.ARMATURE_UNIFORMS_BINDING, 
          boneBufferID, 
          0, 
          TechniquePass.ARMATURE_UNIFORMS_BLOCK_SIZE);
    }
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

  @Override
  protected void resetGLState() {
    glBindVertexArray(0);

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
    
  }
}
