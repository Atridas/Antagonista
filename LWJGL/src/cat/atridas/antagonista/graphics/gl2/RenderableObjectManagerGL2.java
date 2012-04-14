package cat.atridas.antagonista.graphics.gl2;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.InstanceData;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TechniquePass;

/**
 * OpenGL 2.1 implementation of the RenderableObjectManager class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public final class RenderableObjectManagerGL2 extends RenderableObjectManager {

  /**
   * Auxiliar buffer used to pass bone to the OpenGL driver.
   * @since 0.3 
   */
  private FloatBuffer boneBuffer = BufferUtils.createFloatBuffer(
                                      TechniquePass.ARMATURE_UNIFORMS_BLOCK_SIZE / (Utils.FLOAT_SIZE * 2)
                                      );
  
  /**
   * Buffer size needed to store one instance.
   * @since 0.1
   */
  private static final int BASIC_INSTANCE_BUFFER_SIZE = 16; // 3 matrius de 16 floats
  
  /**
   * Auxiliar buffer to pass information to the OpenGL driver.
   * @since 0.1
   */
  private FloatBuffer buffer = BufferUtils.createFloatBuffer(BASIC_INSTANCE_BUFFER_SIZE);

  @Override
  public boolean init() {
    assert !cleaned;
    // no-no-no-thing!!!!
    
    return !Utils.hasGLErrors();
  }

  @Override
  protected void setInstanceUniforms(InstanceData instanceData) {
    assert !cleaned;
    // no-no-no-thing!!!!
  }

  private Matrix4f auxiliarMatrix = new Matrix4f();
  @Override
  protected void setInstanceUniforms(TechniquePass pass, InstanceData instanceData) {
    assert !cleaned;

    //TODO jugar amb les marques i tal per fer-ho m�s optim, potser

    if(pass.hasBasicInstanceUniforms()) {
      buffer.rewind();
      Utils.matrixToBuffer(instanceData.modelViewProj, buffer);
      buffer.rewind();
      glUniformMatrix4(pass.getModelViewProjectionUniform(), false, buffer);
  
      buffer.rewind();
      Utils.matrixToBuffer(instanceData.modelView, buffer);
      buffer.rewind();
      glUniformMatrix4(pass.getModelViewUniform(), false, buffer);
  
      buffer.rewind();
      Utils.matrixToBuffer(instanceData.modelViewInvTransp, buffer);
      buffer.rewind();
      glUniformMatrix4(pass.getModelViewITUniform(), false, buffer);
    }
    
    if(pass.hasSpecialColorsUniforms()) {
      glUniform4f(     pass.getSpecialColor0Uniform(), 
                  instanceData.specialColor0.x, 
                  instanceData.specialColor0.y,  
                  instanceData.specialColor0.z,
                  instanceData.specialColor0.w);
      glUniform4f(     pass.getSpecialColor1Uniform(), 
                  instanceData.specialColor1.x, 
                  instanceData.specialColor1.y,  
                  instanceData.specialColor1.z,
                  instanceData.specialColor1.w);
      glUniform4f(     pass.getSpecialColor2Uniform(), 
                  instanceData.specialColor2.x, 
                  instanceData.specialColor2.y,  
                  instanceData.specialColor2.z,
                  instanceData.specialColor2.w);
      glUniform4f(     pass.getSpecialColor3Uniform(), 
                  instanceData.specialColor3.x, 
                  instanceData.specialColor3.y,  
                  instanceData.specialColor3.z,
                  instanceData.specialColor3.w);
    }
    
    if( instanceData.bonePalete != null ) {
      int len = instanceData.bonePalete.length;
      if(len > TechniquePass.MAX_BONES) {
        len = TechniquePass.MAX_BONES;
      }
      
      boneBuffer.rewind();
      
      for(int i = 0; i < len; ++i) {
        Utils.matrix34ToBuffer(instanceData.bonePalete[i], boneBuffer);
      }

      boneBuffer.position(0);
      boneBuffer.limit(len * 12);

      glUniformMatrix4x3( pass.getBoneMatrixPalete() , true, boneBuffer);

      boneBuffer.rewind();
      for(int i = 0; i < len; ++i) {
        auxiliarMatrix.invert(instanceData.bonePalete[i]);
        auxiliarMatrix.transpose();
        Utils.matrix34ToBuffer(auxiliarMatrix, boneBuffer);
      }
      
      boneBuffer.position(0);
      boneBuffer.limit(len * 12);
      
      glUniformMatrix4x3( pass.getBoneMatrixPaleteIT() , true, boneBuffer);
    }
    
    assert !Utils.hasGLErrors();
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    // no-no-no-thing!!!!
    
    cleaned = true;
  }

  @Override
  protected void resetGLState() {

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.NORMAL_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.TANGENT_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.BITANGENT_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.UV_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);
  }

}
