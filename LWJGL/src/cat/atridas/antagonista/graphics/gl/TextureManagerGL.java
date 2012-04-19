package cat.atridas.antagonista.graphics.gl;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.Texture;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.graphics.RenderManager.Profile;

/**
 * Manages all textures.
 * 
 * @author Isaac 'Atridas' Serrano Guash
 * @since 0.1
 * 
 */
public class TextureManagerGL extends TextureManager {

  @Override
  protected Texture createNewResource(HashedString name) {
    Utils.supportOrException(Profile.GL2,
        "Needs OpenGL, GL ES not yet suported");
    return new TextureGL(name);
  }

}
