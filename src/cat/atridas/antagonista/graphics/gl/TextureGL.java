package cat.atridas.antagonista.graphics.gl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.opengl.LoadableImageData;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.util.glu.GLU.gluBuild2DMipmaps;

/**
 * Desktop OpenGL implementation of the Texture class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public final class TextureGL extends Texture {
  private static Logger LOGGER = Logger.getLogger(TextureGL.class.getCanonicalName());
  
  private int width;
  private int height;
  private int bpp;
  
  private boolean hasAlpha;

  /**
   * Builds an uninitialized texture.
   * 
   * @param resourceName
   * @see Texture#Texture(HashedString)
   * @since 0.1
   */
  public TextureGL(HashedString resourceName) {
    super(resourceName);
    
    minFilter = magFilter = GL_NEAREST;
  }
  
  @Override
  public boolean load(InputStream is, HashedString extension) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading texture '" + resourceName + "'");
    
    LoadableImageData loader = ImageDataFactory.getImageDataFor(extension.toString().toUpperCase());
    ByteBuffer bb;
    try {
      bb = loader.loadImage(is);
    } catch (IOException e) {
      LOGGER.warning("Error loading texture");
      LOGGER.warning(Utils.logExceptionStringAndStack(e));
      return false;
    }
    
    width  = loader.getWidth();
    height = loader.getHeight();
    bpp    = loader.getDepth();
    hasAlpha = bpp == 32;

    int max = glGetInteger(GL_MAX_TEXTURE_SIZE);

    if ((width > max) || (height > max)) {
      LOGGER.warning("Attempt to allocate a texture to big for the current hardware");
      return false;
    }

    int glFormat = hasAlpha ? GL_RGBA : GL_RGB;
    
    id = glGenTextures();
    activate(0);
    
    if(Utils.supports(Profile.GL3)) {
      
      glTexImage2D(GL_TEXTURE_2D, 
          0,               //mipmap level
          glFormat, 
          width, height, 
          0,               //Border
          glFormat, 
          GL_UNSIGNED_BYTE,            
          bb);
      
      glGenerateMipmap(GL_TEXTURE_2D);
    } else {
      gluBuild2DMipmaps(GL_TEXTURE_2D, 
          glFormat, 
          width, height, 
          glFormat, 
          GL_UNSIGNED_BYTE,            
          bb);
    }

    setMagFilter(Quality.MID);
    setMinFilter(Quality.MID);
    noTexture();
    
    assert !Utils.hasGLErrors();
    
    return true;
  }
  
  @Override
  public void loadDefault() {
    
    ByteBuffer bb = BufferUtils.createByteBuffer(256 * 256 * 3);
    
    byte[] baux = new byte[3];
    for(int i = 0; i < 256; ++i) {
      for(int j = 0; j < 256; ++j) {
        baux[0] = (byte)((i     % 16) * 16);
        baux[1] = (byte)((j     % 16) * 16);
        baux[2] = (byte)(((i+j) % 16) * 16);
        bb.put(baux);
      }
    }
    bb.rewind();
    
    width = 256;
    height = 256;
    hasAlpha = false;
    bpp = 24;
    
    assert !Utils.hasGLErrors();

    int glFormat = GL_RGB;
    id = glGenTextures();
    assert !Utils.hasGLErrors();
    activate(0);
    
    assert !Utils.hasGLErrors();
    
    if(GLContext.getCapabilities().OpenGL30) {
      
      glTexImage2D(GL_TEXTURE_2D, 
          0,               //mipmap level
          glFormat, 
          width, height, 
          0,               //Border
          glFormat, 
          GL_UNSIGNED_BYTE,            
          bb);
      
      assert !Utils.hasGLErrors();
      
      glGenerateMipmap(GL_TEXTURE_2D);
      
      assert !Utils.hasGLErrors();
    } else {
      gluBuild2DMipmaps(GL_TEXTURE_2D, 
          glFormat, 
          width, height, 
          glFormat, 
          GL_UNSIGNED_BYTE,            
          bb);
      
      assert !Utils.hasGLErrors();
    }

    setMagFilter(Quality.MID);
    setMinFilter(Quality.MID);
    
    assert !Utils.hasGLErrors();
    
    noTexture();
  }

  @Override
  protected int getTarget() {
    return GL_TEXTURE_2D;
  }

  @Override
  protected boolean isMipMapped() {
    return true;
  }
  
  
  public void activate(int _unit) {
    assert !cleaned;
    glActiveTexture(GL_TEXTURE0 + _unit);
    glBindTexture(getTarget(), id);
    
    glTexParameteri(getTarget(), GL_TEXTURE_MIN_FILTER, minFilter);
    glTexParameteri(getTarget(), GL_TEXTURE_MAG_FILTER, magFilter);
  }
  
  public void noTexture() {
    glBindTexture(GL_TEXTURE_2D, 0);
  }
  
  public int getMinParameter(Quality quality) {
    switch(quality) {
    case NONE:
    case LOW:
      if(isMipMapped()) {
        return GL_NEAREST_MIPMAP_NEAREST;
      } else {
        return GL_NEAREST;
      }
    case MID:
      if(isMipMapped()) {
        return GL_NEAREST_MIPMAP_LINEAR;
      } else {
        return GL_NEAREST;
      }
    case HIGH:
      if(isMipMapped()) {
        return GL_LINEAR_MIPMAP_NEAREST;
      } else {
        return GL_LINEAR;
      }
    case ULTRA:
      if(isMipMapped()) {
        return GL_LINEAR_MIPMAP_LINEAR;
      } else {
        return GL_LINEAR;
      }
    default:
      LOGGER.severe("Unknown Filter passed: " + quality);
      throw new RuntimeException();
      //return GL_NEAREST;
    }
  }
  
  public int getMagParameter(Quality quality) {
    switch(quality) {
    case NONE:
    case LOW:
    case MID:
      return GL_NEAREST;
    case HIGH:
    case ULTRA:
      return GL_LINEAR;
    default:
      LOGGER.severe("Unknown Filter passed: " + quality);
      throw new RuntimeException();
      //return GL_NEAREST;
    }
  }

  @Override
  public int getRAMBytesEstimation() {
    return 4 * 4;
  }

  @Override
  public int getVRAMBytesEstimation() {
    return width * height * bpp / 8;
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    glDeleteTextures(getTarget());
    cleaned = true;
  }
}
