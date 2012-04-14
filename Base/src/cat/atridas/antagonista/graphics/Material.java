package cat.atridas.antagonista.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;

/**
 * Controls a material used in a mesh or other renderable object.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 *
 */
public abstract class Material extends Resource {
  private static Logger LOGGER = Logger.getLogger(Material.class.getCanonicalName());
  
  /**
   * "mat"
   * @since 0.1
   */
  private static final HashedString HS_MAT = new HashedString("mat");
  
  /**
   * Material parameters.
   */
  protected float specularFactor, specularPower, height;
  /**
   * Is the material translucid?
   */
  private boolean alphaBlend;
  
  /**
   * Texture parameters.
   */
  protected Texture albedo, normalmap, heightmap;
  
  /**
   * Effect used in the material renderization.
   */
  protected Effect effect;
  
  /**
   * Builds a blank, uninitialized material.
   * @param _resourceName name of the material.
   * @since 0.1
   * @see Resource#Resource(HashedString)
   */
  public Material(HashedString _resourceName) {
    super(_resourceName);
  }
  
  @Override
  public final boolean load(InputStream is, HashedString extension) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading material " + resourceName);
    
    assert HS_MAT.equals(extension);

    try {
      Utils.CommonFileTypes mft = Utils.readHeader(is, Utils.FILE_TYPES, Utils.CommonFileTypes.ERROR);
      
      switch(mft) {
      case TEXT:
        return loadText(is);
      case BINARY:
        return loadBinary(is);
      case ERROR:
      default:
        LOGGER.warning("Unrecognized header");
        return false;
      }
      
    } catch (IOException e) {
      LOGGER.warning(Utils.logExceptionStringAndStack(e));
      return false;
    }
  }
  
  /**
   * Initializes this material with the default parameters.
   * @since 0.1
   */
  final void loadDefault() {
    specularFactor = .4f;
    specularPower  = 50.f;
    height         = .2f;
    alphaBlend = false;
    albedo = Core.getCore().getTextureManager().getDefaultResource();
    effect = Core.getCore().getEffectManager().getDefaultResource();
  }

  /**
   * Loads a text file.
   * @param is file
   * @return if the resource was correctly loaded.
   * @since 0.1
   */
  private boolean loadText(InputStream is) {
    try {
      String str = Utils.readInputStream(is);
      String[] lines = str.split("\n");
      
      assert lines.length >= 4;
      
      String paramLine = lines[2];
      
      String params[] = paramLine.split(" ");
      assert params.length == 4;
      specularFactor = Float.parseFloat(params[0]);
      specularPower  = Float.parseFloat(params[1]);
      height         = Float.parseFloat(params[2]);
      alphaBlend     = Boolean.parseBoolean(params[3]);
      
      effect = Core.getCore().getEffectManager().getResource(new HashedString(lines[3]));
      
      
      TextureManager tm = Core.getCore().getTextureManager();
      for(int i = 4; i < lines.length; i += 2) {
        String type = lines[i];
        Texture texture = tm.getResource( new HashedString( lines[i+1] ) );
        switch(type) {
        case "albedo":
          albedo = texture;
          break;
        case "normal":
          normalmap = texture;
          break;
        case "height":
          heightmap = texture;
          break;
        default:
          throw new IllegalArgumentException("Unrecognized texture type: " + type);
        }
      }
      
      return true;
    } catch(Exception e) {
      LOGGER.warning("Error loading material file with text format.");
      return false;
    }
  }

  /**
   * Loads a binary file.
   * @param is file
   * @return if the resource was correctly loaded.
   * @since 0.1
   */
  private boolean loadBinary(InputStream is) {
    throw new IllegalStateException("Not yet implemented");
  }
  
  @Override
  public final int getRAMBytesEstimation() {
    return 8 * 4;
  }

  @Override
  public final int getVRAMBytesEstimation() {
    return 0;
  }

  /**
   * Checks if the material is translucid.
   * 
   * @return <code>true</code> if this material needs alpha blending.
   * @since 0.1
   */
  public final boolean isAlphaBlended() {
    return alphaBlend;
  }
  
  /**
   * Finds the effect needed to render this material.
   * 
   * @return the effect.
   * @since 0.1
   */
  public final Effect getEffect() {
    return effect;
  }

  /**
   * Sets the uniforms that need the shader to be binded to be passed to the OpenGL (usually only
   * needed in OpenGL 2.0).
   * 
   * @param pass Shader Program used.
   * @param rm RenderManager
   * @since 0.1
   */
  public abstract void setUpUniforms(TechniquePass pass, RenderManager rm);
  /**
   * Sets the uniforms that don't need the shader to be binded to be passed to the OpenGL 
   * (used usually in OpenGL 3.0 and later).
   * 
   * @param rm RenderManager
   * @since 0.1
   */
  public abstract void setUpUniforms(RenderManager rm);
  
}
