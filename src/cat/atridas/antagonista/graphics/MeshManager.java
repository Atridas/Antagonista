package cat.atridas.antagonista.graphics;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.gl.MeshGL;

public class MeshManager extends ResourceManager<Mesh> {

  private final ArrayList<String> extensionsPriorized = new ArrayList<>();
  private String basePath;
  private Mesh defaultResource;
  
  public void init(ArrayList<String> _extensionsPriorized, String _basePath) {
    extensionsPriorized.addAll(_extensionsPriorized);
    basePath = _basePath;
    
    defaultResource = createNewResource(Utils.DEFAULT);
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
  protected Mesh createNewResource(HashedString name) {
    Utils.supportOrException(Profile.GL2, "Needs OpenGL, GL ES not yet suported");
    return new MeshGL(name);
  }

  @Override
  public Mesh getDefaultResource() {
    return defaultResource;
  }

}
