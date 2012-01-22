package cat.atridas.antagonista.graphics;

import java.awt.Canvas;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public abstract class RenderManager {
  private static Logger LOGGER = Logger.getLogger(RenderManager.class.getCanonicalName());
	
	protected int width;
  protected int height;
  
  private boolean forwardCompatible = true;

	public final int getWidth() {
		return width;
	}
	public final int getHeight() {
		return height;
	}
	public final float getAspectRatio() {
	  return (float) width / (float) height;
	}
	public final boolean isForwardCompatible() {
	  return forwardCompatible;
	}
	
	public final void initDisplay(final int _width, final int _height, final String title, Canvas displayParent) {
		width  = _width;
		height = _height;
		
		//TODO
		PixelFormat pf = new PixelFormat().withDepthBits(24).withBitsPerPixel(32).withAlphaBits(8);
		ContextAttribs ca = new ContextAttribs(4, 2).withForwardCompatible(forwardCompatible);//.withDebug(true);
		
		// ? ca.withDebug(true);
		
		
		try {
			Display.setTitle(title);
			if(displayParent == null)
				Display.setDisplayMode(new DisplayMode(width, height));
			else
				Display.setParent(displayParent);
			Display.create(pf, ca);
		} catch (LWJGLException e) {
			//e.printStackTrace();
			//System.exit(1);
			throw new RuntimeException(e);
		}
	}
	
	public abstract Profile getProfile();
	
	public abstract void initGL();
	
	public final void closeDisplay()
	{
		Display.destroy();
	}

	public abstract void initFrame();
	
	public final void present() {
	  assert !hasGLErrors();
	  hasGLErrors(); //si no hi ha asserts, els imprimim igualment.
	  
	  
		Display.update();
	}
	
	public abstract void activateShader(int shader);

  public abstract void setDepthTest(boolean enable);
  public abstract void setZWrite(boolean enable);
  public abstract void setDepthTest(DepthFunction function);

  public abstract void setAlphaBlend(boolean enable);
  public abstract void setAlphaBlend(BlendOperation operation);
  public abstract void setAlphaBlend(BlendOperationSeparate operation);
  public abstract void setAlphaBlend(boolean enable, int renderTarget);
  public abstract void setAlphaBlend(BlendOperation operation, int renderTarget);
  public abstract void setAlphaBlend(BlendOperationSeparate operation, int renderTarget);
	
  public abstract void noVertexArray();
  
  public abstract SceneData getSceneData();

  public abstract boolean hasGLErrors();
  public abstract void clearSilentlyGLErrors();

  public abstract int getMaxInstancesBasic();
  public abstract int getMaxInstancesWithBones();
  public abstract int getMaxInstancesWithColors();
  
  public static enum Profile {
    GL2         (2,false),
    GL3         (3,false),
    GL4         (4,false),
    GLES2       (2,true);
    
    private final int glVersion;
    private final boolean gles;

    private Profile(int _glVersion, boolean _gles) {
      glVersion = _glVersion;
      gles = _gles;
    }
    
    public static Profile getFromString(String str) {
      switch(str) {
      case "GL2":
        return GL2;
      case "GL3":
        return GL3;
      case "GL4":
        return GL4;

      case "GLES2":
        return GLES2;
        
      default:
        throw new IllegalArgumentException();
      }
    }
    
    public boolean supports(Profile other) {
      if(glVersion < other.glVersion)
        return false;
      if(gles != other.gles)
        return false;
      
      return true;
    }
    
    public void supportOrException(Profile other, String functionality) {
      if(!supports(other)) {
        LOGGER.severe("Functionality " + functionality + " needs a context of " + other.toString() + " or greater.");
        throw new RuntimeException();
      }
    }
    
    @Override
    public String toString() {
      switch (this) {
      case GLES2:
        return "OpenGL ES 2.0";
      case GL4:
        return "OpenGL 4.2";
      case GL3:
        return "OpenGL 3.3";
      case GL2:
        return "OpenGL 2.1";
      default:
        throw new IllegalStateException();
      }
    }
  }
  

  public static enum DepthFunction {
    LESS, GREATER, EQUAL, NOTEQUAL, LEQUAL, GEQUAL, ALWAYS, NEVER;
    
    public static DepthFunction getFromString(String str) {
      switch(str.toUpperCase()) {
      case "LESS":
        return LESS;
      case "GREATER":
        return GREATER;
      case "EQUAL":
        return EQUAL;
      case "NOTEQUAL":
        return NOTEQUAL;
      case "LEQUAL":
        return LEQUAL;
      case "GEQUAL":
        return GEQUAL;
      case "ALWAYS":
        return ALWAYS;
      case "NEVER":
        return NEVER;
      default:
        throw new IllegalArgumentException(str);
      }
    }
  }
  
  public static enum BlendOperator {
    ZERO, ONE, 
    SRC_COLOR, SRC_ALPHA, DST_ALPHA, DST_COLOR, 
    SRC_ALPHA_SATURATE, CONSTANT_COLOR, CONSTANT_ALPHA,
    ONE_MINUS_SRC_COLOR, ONE_MINUS_SRC_ALPHA,
    ONE_MINUS_DST_COLOR, ONE_MINUS_DST_ALPHA, 
    ONE_MINUS_CONSTANT_COLOR, ONE_MINUS_CONSTANT_ALPHA,
    SRC1_ALPHA, ONE_MINUS_SRC1_ALPHA;
    
    public static BlendOperator getFromString(String str) {
      switch(str) {
      case "ZERO":
        return ZERO;
      case "ONE":
        return ONE;
      case "SRC_COLOR":
        return SRC_COLOR;
      case "SRC_ALPHA":
        return SRC_ALPHA;
      case "DST_ALPHA":
        return DST_ALPHA;
      case "DST_COLOR":
        return DST_COLOR;
      case "SRC_ALPHA_SATURATE":
        return SRC_ALPHA_SATURATE;
      case "CONSTANT_COLOR":
        return CONSTANT_COLOR;
      case "CONSTANT_ALPHA":
        return CONSTANT_ALPHA;
      case "ONE_MINUS_SRC_COLOR":
        return ONE_MINUS_SRC_COLOR;
      case "ONE_MINUS_SRC_ALPHA":
        return ONE_MINUS_SRC_ALPHA;
      case "ONE_MINUS_DST_COLOR":
        return ONE_MINUS_DST_COLOR;
      case "ONE_MINUS_DST_ALPHA":
        return ONE_MINUS_DST_ALPHA;
      case "ONE_MINUS_CONSTANT_COLOR":
        return ONE_MINUS_CONSTANT_COLOR;
      case "ONE_MINUS_CONSTANT_ALPHA":
        return ONE_MINUS_CONSTANT_ALPHA;
      case "SRC1_ALPHA":
        return SRC1_ALPHA;
      case "ONE_MINUS_SRC1_ALPHA":
        return ONE_MINUS_SRC1_ALPHA;
      default:
        throw new IllegalArgumentException();
      }
    }
  }
  
  public static final class BlendOperation {
    public BlendOperator src, dst;
  }
  
  public static final class BlendOperationSeparate {
    public BlendOperation color, alpha;
  }
}
