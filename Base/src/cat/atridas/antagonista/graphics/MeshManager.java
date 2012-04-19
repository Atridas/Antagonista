package cat.atridas.antagonista.graphics;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;

/**
 * Manages all mesh cores.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public abstract class MeshManager extends ResourceManager<Mesh> {
  /**
   * Default mesh.
   */
  private Mesh defaultResource;

  /**
   * Initializes the manager.
   * 
   * @param _extensionsPriorized
   *          Extensions of the mesh files to be loaded.
   * @param _basePath
   *          Path where the mesh files will be searched.
   * @see ResourceManager#ResourceManager(String, ArrayList)
   */
  public void init(ArrayList<HashedString> _extensionsPriorized,
      String _basePath) {
    setExtensions(_extensionsPriorized);
    setBasePath(_basePath);

    defaultResource = createNewResource(Utils.DEFAULT);
    defaultResource.loadDefault();

    assert !Utils.hasGLErrors();
  }

  @Override
  public Mesh getDefaultResource() {
    return defaultResource;
  }

}
