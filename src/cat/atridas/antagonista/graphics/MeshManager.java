package cat.atridas.antagonista.graphics;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.gl2.MeshGL2;
import cat.atridas.antagonista.graphics.gl3.MeshGL3;

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
    if(Utils.supports(Profile.GL3)) {
      return new MeshGL3(Utils.DEFAULT);
    } else if(Utils.supports(Profile.GL2)) {
      return new MeshGL2(Utils.DEFAULT);
    } else {
      throw new IllegalStateException(
          "Current Profile [" + 
              Core.getCore().getRenderManager().getProfile() + 
                               "] not implemented.");
    }
  }

  @Override
  public Mesh getDefaultResource() {
    return defaultResource;
  }

}
