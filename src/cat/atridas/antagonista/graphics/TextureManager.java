package cat.atridas.antagonista.graphics;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.gl.TextureGL;

public class TextureManager extends ResourceManager<Texture> {

  private Texture defaultResource;
  
  public void init(ArrayList<HashedString> _extensionsPriorized, String _basePath) {
    setExtensions(_extensionsPriorized);
    setBasePath(_basePath);
    
    defaultResource = createNewResource(Utils.DEFAULT);
    
    defaultResource.loadDefault();
    
    assert !Utils.hasGLErrors();
  }

  @Override
  protected Texture createNewResource(HashedString name) {
    Utils.supportOrException(Profile.GL2, "Needs OpenGL, GL ES not yet suported");
    return new TextureGL(name);
  }

  @Override
  public Texture getDefaultResource() {
    return defaultResource;
  }
  
  
  
}
