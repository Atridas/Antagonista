package cat.atridas.antagonista.graphics;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.gl2.MeshGL2;
import cat.atridas.antagonista.graphics.gl3.MeshGL3;

/**
 * Manages all mesh cores.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 *
 */
public class MeshManager extends ResourceManager<Mesh> {
  /**
   * Default mesh.
   */
  private Mesh defaultResource;
  
  /**
   * Initializes the manager.
   * 
   * @param _extensionsPriorized Extensions of the mesh files to be loaded.
   * @param _basePath Path where the mesh files will be searched.
   * @see ResourceManager#ResourceManager(String, ArrayList)
   */
  public void init(ArrayList<HashedString> _extensionsPriorized, String _basePath) {
    setExtensions(_extensionsPriorized);
    setBasePath(_basePath);
    
    defaultResource = createNewResource(Utils.DEFAULT);
    defaultResource.loadDefault();
    
    assert !Utils.hasGLErrors();
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
