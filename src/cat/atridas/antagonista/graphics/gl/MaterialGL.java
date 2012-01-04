package cat.atridas.antagonista.graphics.gl;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBUniformBufferObject;
import org.lwjgl.opengl.GLContext;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.TechniquePass;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

public class MaterialGL extends Material {

  private ByteBuffer bb = BufferUtils.createByteBuffer(3 * Float.SIZE); 
  private int glBuffer = -1;
  
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

      if(glBuffer < 0) {
        glBuffer  = glGenBuffers();
      }
      
      bb.rewind();
      bb.putFloat(specularFactor);
      bb.putFloat(specularPower);
      bb.putFloat(height);
      bb.rewind();
      
      glBindBuffer(GL_UNIFORM_BUFFER, glBuffer);
      glBufferData(GL_UNIFORM_BUFFER, bb, GL_DYNAMIC_DRAW);
      
      if(GL3) {
        glBindBufferRange(GL_UNIFORM_BUFFER, TechniquePass.BASIC_MATERIAL_UNIFORMS_BINDING,
            glBuffer, 0, 3 * Float.SIZE);
      } else {
        ARBUniformBufferObject.glBindBufferRange(GL_UNIFORM_BUFFER, 
                        TechniquePass.BASIC_MATERIAL_UNIFORMS_BINDING,
                        glBuffer, 0, 3 * Float.SIZE);
      }
      
    }
    
    if(albedo != null)
      albedo.activate(TechniquePass.ALBEDO_TEXTURE_UNIT);
    if(normalmap != null)
      normalmap.activate(TechniquePass.NORMALMAP_TEXTURE_UNIT);
    if(heightmap != null)
      heightmap.activate(TechniquePass.HEIGHTMAP_TEXTURE_UNIT);
  }

  @Override
  public void setUpUniforms(TechniquePass pass, RenderManager rm) {
    if(!(GL3 || GL_ARB_uniform_buffer_object)) {

      glUniform1f(pass.getSpecularFactorUniform(), specularFactor);
      glUniform1f(pass.getSpecularGlossinessUniform(), specularPower);
      glUniform1f(pass.getHeightUniform(), height);
      
    }
  }
  
  @Override
  public void cleanUp() {
    assert !cleaned;
    cleaned = true;
  }

}
