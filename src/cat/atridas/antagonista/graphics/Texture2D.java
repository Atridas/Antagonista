package cat.atridas.antagonista.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.util.glu.GLU.*;

import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.opengl.LoadableImageData;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;

public final class Texture2D extends Texture {
  
  public final int width;
  public final int height;
  
  public final boolean hasAlpha;

  public Texture2D(String resourceName) throws IOException {
    this(resourceName, resourceName.substring(resourceName.lastIndexOf('.')+1).toUpperCase(), false);
  }

  public Texture2D(String resourceName, boolean greyscale) throws IOException {
    this(resourceName, resourceName.substring(resourceName.lastIndexOf('.')+1).toUpperCase(), greyscale);
  }

  public Texture2D(String resourceName, String format) throws IOException {
    this(resourceName, format, false);
  }
  
  public Texture2D(String resourceName, String format, boolean greyscale) throws IOException {
    
    InputStream is = Utils.findInputStream(resourceName);
    
    LoadableImageData loader = ImageDataFactory.getImageDataFor(format);
    ByteBuffer bb = loader.loadImage(is);
    
    width = loader.getWidth();
    height = loader.getHeight();
    hasAlpha = loader.getDepth() == 32;

    IntBuffer temp = BufferUtils.createIntBuffer(16);
    glGetInteger(GL_MAX_TEXTURE_SIZE, temp);
    int max = temp.get(0);

    if ((width > max) || (height > max)) {
      throw new IOException("Attempt to allocate a texture to big for the current hardware");
    }

    int glFormat = hasAlpha ? GL_RGBA : GL_RGB;
    //int componentCount = hasAlpha ? 4 : 3;
    
    

    /*while(bb.hasRemaining()) {
      int b = bb.get() & 0xFF; 
      System.out.print(b);
      System.out.print("," );
      b = bb.get() & 0xFF; 
      System.out.print(b);
      System.out.print("," );
      b = bb.get() & 0xFF; 
      System.out.print(b);
      System.out.print("," );
      if(hasAlpha) {
        b = bb.get() & 0xFF; 
        System.out.print(b);
      }
      System.out.print("\n" );
    }
    bb.rewind();*/
    
    
    
    id = glGenTextures();
    
    RenderManager rm = Core.getCore().getRenderManager(); 
    activate(rm, 0);
    
    if(GLContext.getCapabilities().OpenGL30) {
      
      glTexImage2D(GL_TEXTURE_2D, 
          0,               //mipmap level
          (greyscale)? GL_LUMINANCE : glFormat, 
          width, height, 
          0,               //Border
          glFormat, 
          GL_UNSIGNED_BYTE,            
          bb);
      
      glGenerateMipmap(GL_TEXTURE_2D);
    } else {
      gluBuild2DMipmaps(GL_TEXTURE_2D, 
          (greyscale)? GL_LUMINANCE : glFormat, 
          width, height, 
          glFormat, 
          GL_UNSIGNED_BYTE,            
          bb);
    }

    setMagFilter(FilterQuality.MID);
    setMinFilter(FilterQuality.MID);
  }
  
  public Texture2D() {
    
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

    int glFormat = GL_RGB;
    id = glGenTextures();
    

    RenderManager rm = Core.getCore().getRenderManager(); 
    activate(rm, 0);
    
    if(GLContext.getCapabilities().OpenGL30) {
      
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

    setMagFilter(FilterQuality.MID);
    setMinFilter(FilterQuality.MID);
  }

  @Override
  protected int getTarget() {
    return GL_TEXTURE_2D;
  }

  @Override
  protected boolean isMipMapped() {
    return true;
  }
}
