package cat.atridas.antagonista.graphics;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.Material;

/**
 * Material Manager.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public abstract class MaterialManager extends ResourceManager<Material> {

  /**
   * Default material.
   * 
   * @since 0.1
   */
  private Material defaultResource;

  /**
   * Initializes the manager.
   * 
   * @param _extensionsPriorized
   *          Extensions of the material files to be loaded.
   * @param _basePath
   *          Path where the material files will be searched.
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
  public Material getDefaultResource() {
    return defaultResource;
  }

}
