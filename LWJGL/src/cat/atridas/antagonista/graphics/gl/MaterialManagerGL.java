package cat.atridas.antagonista.graphics.gl;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.MaterialManager;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.gl2.MaterialGL2;
import cat.atridas.antagonista.graphics.gl3.MaterialGL3;

/**
 * Material Manager.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public final class MaterialManagerGL extends MaterialManager {

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
    super.init(_extensionsPriorized, _basePath);
  }

  @Override
  protected Material createNewResource(HashedString name) {
    if (Utils.supports(Profile.GL3)) {
      return new MaterialGL3(name);
    } else if (Utils.supports(Profile.GL2)) {
      return new MaterialGL2(name);
    } else {
      throw new IllegalStateException("Current Profile ["
          + Core.getCore().getRenderManager().getProfile()
          + "] not implemented.");
    }
  }

}
