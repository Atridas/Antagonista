package cat.atridas.antagonista.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Effect.TechniqueType;

public abstract class Material extends Resource {
  private static Logger LOGGER = Logger.getLogger(Material.class.getCanonicalName());

  public static final byte[] TEXT_HEADER = "antagonist text".getBytes();
  public static final byte[] BINARY_HEADER = "antagonist binary".getBytes();
  
  public static final Map<byte[], MaterialFileTypes> FILE_TYPES;
  
  protected float specularFactor, specularPower, height;
  private boolean alphaBlend;
  
  protected Texture albedo, normalmap, heightmap;
  
  protected Effect effect;
  
  public Material(HashedString _resourceName) {
    super(_resourceName);
  }
  
  @Override
  public final boolean load(InputStream is, String extension) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading material " + resourceName);
    
    assert "mat".compareToIgnoreCase(extension) == 0;

    try {
      MaterialFileTypes mft = Utils.readHeader(is, FILE_TYPES, MaterialFileTypes.ERROR);
      
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
  
  final void loadDefault() {
    specularFactor = .4f;
    specularPower  = 50.f;
    height         = .2f;
    alphaBlend = false;
    albedo = Core.getCore().getTextureManager().getDefaultResource();
  }

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

  public final boolean isAlphaBlended() {
    return alphaBlend;
  }
  
  public abstract void activate(TechniqueType tt, Quality q, RenderManager rm);
  
  static {
    Map<byte[], MaterialFileTypes> fileTypes = new HashMap<byte[], Material.MaterialFileTypes>();
    fileTypes.put(TEXT_HEADER, MaterialFileTypes.TEXT);
    fileTypes.put(BINARY_HEADER, MaterialFileTypes.BINARY);
    
    FILE_TYPES = Collections.unmodifiableMap(fileTypes);
  }
  
  private static enum MaterialFileTypes {
    TEXT, BINARY, ERROR
  }
}
