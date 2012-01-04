package cat.atridas.antagonista.graphics.gl;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBUniformBufferObject;
import org.lwjgl.opengl.GLContext;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.TechniquePass;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

public class MaterialGL extends Material {

  private ByteBuffer bb = BufferUtils.createByteBuffer(3 * Float.SIZE); 
  private int bufferId = -1;
  
  private static final boolean GL_ARB_uniform_buffer_object, GL3;

  static {
    GL3 = Core.getCore().getRenderManager().getProfile().supports(Profile.GL3);
    GL_ARB_uniform_buffer_object = GLContext.getCapabilities().GL_ARB_uniform_buffer_object;
  } 
  
  public MaterialGL(HashedString _resourceName) {
    super(_resourceName);
  }

  @Override
  public void setUpUniforms(RenderManager rm) {
    if(GL3 || GL_ARB_uniform_buffer_object) {

      if(bufferId < 0) {
        bufferId  = glGenBuffers();
      }
      
      bb.rewind();
      bb.putFloat(specularFactor);
      bb.putFloat(specularPower);
      bb.putFloat(height);
      bb.rewind();
      
      glBindBuffer(GL31.GL_UNIFORM_BUFFER, bufferId);
      glBufferData(GL31.GL_UNIFORM_BUFFER, bb, GL_DYNAMIC_DRAW);
      
      if(GL3) {
        GL30.glBindBufferRange(GL31.GL_UNIFORM_BUFFER, TechniquePass.BASIC_MATERIAL_UNIFORMS_BINDING,
            bufferId, 0, 3 * Float.SIZE);
      } else {
        ARBUniformBufferObject.glBindBufferRange(GL31.GL_UNIFORM_BUFFER, 
                        TechniquePass.BASIC_MATERIAL_UNIFORMS_BINDING,
                        bufferId, 0, 3 * Float.SIZE);
      }
      
    }
    
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
    if(!(GL3 || GL_ARB_uniform_buffer_object)) {

      glUniform1f(pass.getSpecularFactorUniform(), specularFactor);
      glUniform1f(pass.getSpecularGlossinessUniform(), specularPower);
      glUniform1f(pass.getHeightUniform(), height);
      
    }
    assert !Utils.hasGLErrors();
  }
  
  @Override
  public void cleanUp() {
    assert !cleaned;
    cleaned = true;
  }

}
