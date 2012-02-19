package cat.atridas.antagonista.graphics.animation;

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
public class AnimationManager extends ResourceManager<Animation> {
  /**
   * Default mesh.
   * @since 0.3
   */
  private Animation defaultResource;
  
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
  protected Animation createNewResource(HashedString name) {
    return new Animation(name);
  }

  @Override
  public Animation getDefaultResource() {
    return defaultResource;
  }

}
