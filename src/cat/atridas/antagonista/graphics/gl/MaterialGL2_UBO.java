package cat.atridas.antagonista.graphics.gl;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.ARBUniformBufferObject.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.TechniquePass;

public final class MaterialGL2_UBO extends Material {
  
  private ByteBuffer bb = BufferUtils.createByteBuffer(3 * Float.SIZE); 
  private int bufferId = -1;

  public MaterialGL2_UBO(HashedString _resourceName) {
    super(_resourceName);
  }
  
  @Override
  public void setUpUniforms(RenderManager rm) {
    if(bufferId < 0) {
      bufferId  = glGenBuffers();
    }
    
    bb.rewind();
    bb.putFloat(specularFactor);
    bb.putFloat(specularPower);
    bb.putFloat(height);
    bb.rewind();
    
    glBindBuffer(GL_UNIFORM_BUFFER, bufferId);
    glBufferData(GL_UNIFORM_BUFFER, bb, GL_DYNAMIC_DRAW);
    
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
