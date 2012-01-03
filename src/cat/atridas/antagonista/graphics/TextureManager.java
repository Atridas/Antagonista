package cat.atridas.antagonista.graphics;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.gl.TextureGL;

public class TextureManager extends ResourceManager<Texture> {

  private final ArrayList<String> extensionsPriorized = new ArrayList<>();
  private String basePath;
  private Texture defaultResource;
  
  public void init(ArrayList<String> _extensionsPriorized, String _basePath) {
    extensionsPriorized.addAll(_extensionsPriorized);
    basePath = _basePath;
    
    {
      Utils.supportOrException(Profile.GL2, "Needs OpenGL, GL ES not yet suported");
      defaultResource = new TextureGL(Utils.DEFAULT);
    }
    
    defaultResource.loadDefault();
    
    assert !Utils.hasGLErrors();
  }
  
  @Override
  protected String getBasePath() {
    return basePath;
  }

  @Override
  protected ArrayList<String> getExtensionsPriorized() {
    return extensionsPriorized;
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
