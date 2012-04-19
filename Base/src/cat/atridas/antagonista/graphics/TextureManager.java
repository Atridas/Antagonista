package cat.atridas.antagonista.graphics;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.Texture;
import cat.atridas.antagonista.graphics.RenderManager.Profile;

/**
 * Manages all textures.
 * 
 * @author Isaac 'Atridas' Serrano Guash
 * @since 0.1
 * 
 */
public abstract class TextureManager extends ResourceManager<Texture> {

  /**
   * Default texture.
   * 
   * @since 0.1
   */
  private Texture defaultResource;

  /**
   * Initializes the manager.
   * 
   * @param _extensions
   *          Extensions of the resources to be loaded.
   * @param _basePath
   *          Path where the resources will be searched.
   * @since 0.1
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
  public Texture getDefaultResource() {
    return defaultResource;
  }

}
