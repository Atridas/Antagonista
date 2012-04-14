package cat.atridas.antagonista.graphics.gl;

import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Mesh;
import cat.atridas.antagonista.graphics.MeshManager;
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
public class MeshManagerGL extends MeshManager {

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

}
