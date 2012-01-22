package cat.atridas.antagonista.graphics;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.gl2.MaterialGL2;
import cat.atridas.antagonista.graphics.gl3.MaterialGL3;

public class MaterialManager extends ResourceManager<Material> {

  private Material defaultResource;
  
  public void init(ArrayList<HashedString> _extensionsPriorized, String _basePath) {
    setExtensions(_extensionsPriorized);
    setBasePath(_basePath);
    
    

    defaultResource = createNewResource(Utils.DEFAULT);
    
    defaultResource.loadDefault();
    
    assert !Utils.hasGLErrors();
  }

  @Override
  protected Material createNewResource(HashedString name) {
    if(Utils.supports(Profile.GL3)) {
      return new MaterialGL3(name);
    } else if(Utils.supports(Profile.GL2)) {
      return new MaterialGL2(name);
    } else {
      throw new IllegalStateException(
          "Current Profile [" + 
              Core.getCore().getRenderManager().getProfile() + 
                               "] not implemented.");
    }
  }

  @Override
  public Material getDefaultResource() {
    return defaultResource;
  }

}
