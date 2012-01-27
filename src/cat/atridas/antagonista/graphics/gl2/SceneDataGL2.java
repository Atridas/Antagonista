package cat.atridas.antagonista.graphics.gl2;

import static org.lwjgl.opengl.GL20.*;


import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.gl.RenderManagerGL;

public class SceneDataGL2 extends SceneData {

  
  public SceneDataGL2(RenderManagerGL _rm) {
    super(_rm);
  }
  
  @Override
  public void setUniforms() {
    // --
  }

  @Override
  public void setUniforms(TechniquePass pass) {
    glUniform3f(pass.getAmbientLightColorUniform(), 
        ambientLightColor.x, ambientLightColor.y, ambientLightColor.z);
    glUniform3f(pass.getDirectionalLightColorUniform(), 
        directionalLightColor.x, directionalLightColor.y, directionalLightColor.z);
    glUniform3f(pass.getDirectionalLightDirectionUniform(), 
        directionalLightDirection.x, directionalLightDirection.y, directionalLightDirection.z);
    assert !Utils.hasGLErrors();
  }

}
