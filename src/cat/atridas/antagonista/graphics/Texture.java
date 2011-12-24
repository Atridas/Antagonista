package cat.atridas.antagonista.graphics;

import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
//import static org.lwjgl.opengl.GL14.*;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL20.*;
//import static org.lwjgl.opengl.GL21.*;

public abstract class Texture {
  
  protected int id;
  private int minFilter, magFilter;
  
  protected Texture() {}

  
  public final void activate(RenderManager _rm, int unit) {
    assert !finalized;
    glActiveTexture(GL_TEXTURE0 + unit);
    glBindTexture(getTarget(), id);
    
    glTexParameteri(getTarget(), GL_TEXTURE_MIN_FILTER, minFilter);
    glTexParameteri(getTarget(), GL_TEXTURE_MAG_FILTER, magFilter);
  }
  
  public static void noTexture() {
    glBindTexture(GL_TEXTURE_2D, 0);
  }
  
  public static enum FilterQuality {LOW, MID, HIGH, ULTRA}
  
  protected abstract int getTarget();
  protected abstract boolean isMipMapped();
  
  protected final int getMinParameter(FilterQuality quality) {
    switch(quality) {
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
      //logger.warning("Unknown Filter passed: " + quality);
      return GL_NEAREST;
    }
  }
  
  protected final int getMagParameter(FilterQuality quality) {
    switch(quality) {
    case LOW:
    case MID:
      return GL_NEAREST;
    case HIGH:
    case ULTRA:
      return GL_LINEAR;
    default:
      //logger.warning("Unknown Filter passed: " + quality);
      return GL_NEAREST;
    }
  }
  
  public final void setMinFilter(FilterQuality quality) {
    assert !finalized;
    minFilter = getMinParameter(quality);
    
  }
  
  public final void setMagFilter(FilterQuality quality) {
    assert !finalized;
    magFilter = getMagParameter(quality);
  }
  
  private volatile boolean finalized = false;
  public void cleanUp() {
    assert !finalized;
    
    glDeleteTextures(id);
    finalized = true;
  }
  
  @Override
  public void finalize() {
    if(!finalized)
      cleanUp();
  }
  
  /*public static class TextureProxy extends Texture {
    
    private int target;
    
    protected TextureProxy(Texture original) {
      super(original.rm);
      id = original.id;
      target = original.getTarget();
    }

    @Override
    protected int getTarget() {
      return target;
    }
    
  }*/
}
