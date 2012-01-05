package cat.atridas.antagonista.graphics.gl;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.TechniquePass;

import static org.lwjgl.opengl.GL20.*;

public final class MaterialGL2 extends Material {

  public MaterialGL2(HashedString _resourceName) {
    super(_resourceName);
  }
  @Override
  public void setUpUniforms(RenderManager rm) {
    
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
    glUniform1f(pass.getSpecularFactorUniform(), specularFactor);
    glUniform1f(pass.getSpecularGlossinessUniform(), specularPower);
    glUniform1f(pass.getHeightUniform(), height);
      
    assert !Utils.hasGLErrors();
  }
  
  @Override
  public void cleanUp() {
    assert !cleaned;
    cleaned = true;
  }

}
