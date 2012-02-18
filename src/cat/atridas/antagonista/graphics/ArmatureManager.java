package cat.atridas.antagonista.graphics;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;

/**
 * TODO
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.3
 *
 */
public class ArmatureManager extends ResourceManager<ArmatureCore> {
  /**
   * Default mesh.
   * @since 0.3
   */
  private ArmatureCore defaultResource;
  
  /**
   * Initializes the manager.
   * 
   * @param _extensionsPriorized Extensions of the mesh files to be loaded.
   * @param _basePath Path where the mesh files will be searched.
   * @see ResourceManager#ResourceManager(String, ArrayList)
   * @since 0.3
   */
  public void init(ArrayList<HashedString> _extensionsPriorized, String _basePath) {
    setExtensions(_extensionsPriorized);
    setBasePath(_basePath);
    
    defaultResource = createNewResource(Utils.DEFAULT);
    defaultResource.loadDefault();
    
    assert !Utils.hasGLErrors();
  }

  @Override
  protected ArmatureCore createNewResource(HashedString name) {
    return new ArmatureCore(name);
  }

  @Override
  public ArmatureCore getDefaultResource() {
    return defaultResource;
  }

}
