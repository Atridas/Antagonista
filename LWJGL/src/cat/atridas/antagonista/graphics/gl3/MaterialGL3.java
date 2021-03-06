package cat.atridas.antagonista.graphics.gl3;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.TechniquePass;

/**
 * OpenGL 3.3 implementation of the Material class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public final class MaterialGL3 extends Material {
  
  /**
   * Auxiliar buffer used to pass material information to the OpenGL driver.
   * @since 0.1 
   */
  private ByteBuffer bb = BufferUtils.createByteBuffer(3 * Float.SIZE);
  /**
   * Uniform Buffer identifier for this material. 
   */
  private int bufferId = -1;

  /**
   * Builds a blank, uninitialized material.
   * @param _resourceName name of the material.
   * @since 0.1
   * @see Material#Material(HashedString)
   */
  public MaterialGL3(HashedString _resourceName) {
    super(_resourceName);
  }
  
  @Override
  public void setUpUniforms(RenderManager rm) {
    if(bufferId < 0) {
      bufferId  = glGenBuffers();

      glBindBuffer(GL_UNIFORM_BUFFER, bufferId);
      glBufferData(GL_UNIFORM_BUFFER, 3 * Utils.FLOAT_SIZE, GL_DYNAMIC_DRAW);
    }
    
    bb.clear();
    bb.putFloat(specularFactor);
    bb.putFloat(specularPower);
    bb.putFloat(height);
    bb.flip();
    
    glBindBuffer(GL_UNIFORM_BUFFER, bufferId);
    glBufferSubData(GL_UNIFORM_BUFFER, 0, bb);
    
    glBindBufferRange(GL_UNIFORM_BUFFER, TechniquePass.BASIC_MATERIAL_UNIFORMS_BINDING,
          bufferId, 0, 3 * Float.SIZE);
    
    if(albedo != null)
      albedo.activate(TechniquePass.ALBEDO_TEXTURE_UNIT);
    if(normalmap != null)
      normalmap.activate(TechniquePass.NORMALMAP_TEXTURE_UNIT);
    if(heightmap != null)
      heightmap.activate(TechniquePass.HEIGHTMAP_TEXTURE_UNIT);

    assert !Utils.hasGLErrors();
  }

  @Override
  public void setUpUniforms(TechniquePass pass, RenderManager rm) {
    // --
  }
  
  @Override
  public void cleanUp() {
    assert !cleaned;
    
    glDeleteBuffers(bufferId);
    bufferId = -1;
    cleaned = true;
  }
}
