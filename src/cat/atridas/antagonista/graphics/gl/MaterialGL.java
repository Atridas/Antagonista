package cat.atridas.antagonista.graphics.gl;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.Effect.TechniqueType;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.Technique;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

public class MaterialGL extends Material {

  private static ByteBuffer bb = BufferUtils.createByteBuffer(3 * Float.SIZE); 
  private static int glBuffer = -1;
  
  public MaterialGL(HashedString _resourceName) {
    super(_resourceName);
  }

  @Override
  public void activate(TechniqueType tt, Quality q, RenderManager rm) {
    if(Utils.supports(Profile.GL3)) {

      //TODO passar el buffer a la technique
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
      
      glBindBufferRange(GL_UNIFORM_BUFFER, Technique.BASIC_MATERIAL_UNIFORMS_BINDING,
          glBuffer, 0, 3 * Float.SIZE);

      if(albedo != null)
        albedo.activate(Technique.ALBEDO_TEXTURE_UNIT);
      if(normalmap != null)
        normalmap.activate(Technique.NORMALMAP_TEXTURE_UNIT);
      if(heightmap != null)
        heightmap.activate(Technique.HEIGHTMAP_TEXTURE_UNIT);
      
      effect.getTechnique(tt, q).activate(rm);
      
    } else {
      throw new IllegalStateException("OpenGL < 3 not yet implemented");
    }
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    cleaned = true;
  }

}