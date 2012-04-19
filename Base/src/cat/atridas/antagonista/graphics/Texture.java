package cat.atridas.antagonista.graphics;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.Resource;

/**
 * Texture encapsulation class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public abstract class Texture extends Resource {

  /**
   * OpenGL identifier of this texture.
   * 
   * @since 0.1
   */
  protected int id;
  /**
   * Filter values to miniaturize of magnify the texture.
   * 
   * @since 0.1
   */
  protected int minFilter, magFilter;

  /**
   * Builds an uninitialized texture.
   * 
   * @param resourceName
   * @see Resource#Resource(HashedString)
   * @since 0.1
   */
  protected Texture(HashedString resourceName) {
    super(resourceName);
  }

  /**
   * Loads the default texture.
   * 
   * @since 0.1
   */
  protected abstract void loadDefault();

  /**
   * Activates this texture in the specified unit.
   * 
   * @param unit
   *          to bind this texture.
   * @since 0.1
   */
  public abstract void activate(int unit);

  /**
   * Unbinds any texture.
   * 
   * @since 0.1
   */
  public abstract void noTexture();

  /**
   * Gets the OpenGL texture type, for example Texture 2D or Cube Texture.
   * 
   * @return the OpenGL texture type.
   * @since 0.1
   */
  protected abstract int getTarget();

  /**
   * Checks if this texture is mipmapped.
   * 
   * @return if this texture is mipmapped.
   * @since 0.1
   */
  protected abstract boolean isMipMapped();

  /**
   * Translates a quality-filter to an OpenGL identifier.
   * 
   * @param quality
   *          of the filter.
   * @return OpenGL filter identifier.
   * @since 0.1
   */
  public abstract int getMinParameter(Quality quality);

  /**
   * Translates a quality-filter to an OpenGL identifier.
   * 
   * @param quality
   *          of the filter.
   * @return OpenGL filter identifier.
   * @since 0.1
   */
  public abstract int getMagParameter(Quality quality);

  /**
   * Sets the filter quality
   * 
   * @param quality
   *          of the filter.
   * @since 0.1
   */
  public final void setMinFilter(Quality quality) {
    assert !cleaned;
    minFilter = getMinParameter(quality);

  }

  /**
   * Sets the filter quality
   * 
   * @param quality
   *          of the filter.
   * @since 0.1
   */
  public final void setMagFilter(Quality quality) {
    assert !cleaned;
    magFilter = getMagParameter(quality);
  }
}
