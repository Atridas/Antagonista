package cat.atridas.antagonista.graphics;

import java.awt.Canvas;
import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public abstract class RenderManager {
	
	private int width, height;

	private final Matrix4f viewMatrix = new Matrix4f();
	private final Matrix4f projectionMatrix = new Matrix4f();
	private final Vector3f cameraPosition = new Vector3f();

	public final int getWidth() {
		return width;
	}
	public final int getHeight() {
		return height;
	}
	
	public final void initDisplay(final int _width, final int _height, final String title, Canvas displayParent) {
		width  = _width;
		height = _height;
		
		//TODO
		PixelFormat pf = new PixelFormat().withDepthBits(24).withBitsPerPixel(32).withAlphaBits(8);
		ContextAttribs ca = new ContextAttribs(2, 1).withForwardCompatible(true);
		
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
		Display.update();
	}
	
	public abstract void activateShader(int shader);

  public abstract void setDepthTest(boolean enable);
  public abstract void setDepthTest(DepthFunction function);

  public abstract void setAlphaBlend(boolean enable);
  public abstract void setAlphaBlend(BlendOperation operation);
  public abstract void setAlphaBlend(BlendOperationSeparate operation);
  public abstract void setAlphaBlend(boolean enable, int renderTarget);
  public abstract void setAlphaBlend(BlendOperation operation, int renderTarget);
  public abstract void setAlphaBlend(BlendOperationSeparate operation, int renderTarget);
	
	
	public final void setPerspective(float fovy, float zNear, float zFar) {
    setPerspective(fovy, (float)width / height, zNear, zFar);
  }
	  
  public final void setPerspective(float fovy, float aspect, float zNear, float zFar) {
    float f = (float)(1.0 / Math.tan(fovy/2.));
    projectionMatrix.setColumn(0, 
        f/aspect,
        0,
        0,
        0);
    projectionMatrix.setColumn(1, 
        0,
        f,
        0,
        0);
    projectionMatrix.setColumn(2, 
        0,
        0,
        (zFar+zNear)/(zNear-zFar),
        -1);
    projectionMatrix.setColumn(3, 
        0,
        0,
        2*zFar*zNear / (zNear-zFar),
        0);
        
  }
  
  public final void setFrustum(
      float left, float right, 
      float bottom, float top, 
      float near, float far) 
  {
    projectionMatrix.setColumn(0, 
        2*near / (right-left), 
        0.0f, 
        0.0f, 
        0.0f);
    projectionMatrix.setColumn(1, 
        0.0f, 
        2*near / (top-bottom), 
        0.0f, 
        0.0f);
    projectionMatrix.setColumn(2, 
        (right+left) / (right-left), 
        (top+bottom) / (top-bottom), 
        -(far+near) / (far-near), 
        -1.0f);
    projectionMatrix.setColumn(3, 
        0.0f, 
        0.0f, 
        -2*near * far / (far-near), 
        0.0f);
  }
  
  public final void setOrtho(
      float left, float right, 
      float top,  float bottom,
      float near, float far)
  {
	  setOrtho(left, right,  top,  bottom, near,  far, projectionMatrix);
  }
  
  public final void setOrtho(float near, float far)
  {
	  setOrtho(near,  far, projectionMatrix);
  }
  
  public final void setOrtho(float near, float far, Matrix4f matrix)
  {
	  setOrtho(0, width,  0, height, near,  far, matrix);
  }
  
  public final void setOrtho(
      float left, float right, 
      float top,  float bottom,
      float near, float far,
      Matrix4f matrix)
  {
	  matrix.setColumn(0, 
        2 / (right-left),
        0,
        0,
        0);
	  matrix.setColumn(1, 
        0,
        2 / (top-bottom),
        0,
        0);
	  matrix.setColumn(2, 
        0,
        0,
        -2 / (far-near),
        0);
	  matrix.setColumn(3, 
        -(right + left)/ (right-left),
        -(top + bottom)/ (top-bottom),
        -(far + near  )/ (far-near  ),
        1);
  }
  
  public final void setCamera(Point3f eye, Point3f lookAt, Vector3f up) {
    cameraPosition.set(eye);
    
    Vector3f zdir = new Vector3f(eye);
    zdir.sub(lookAt);
    Vector3f xdir = new Vector3f();
    xdir.cross(up, zdir);
    Vector3f ydir = new Vector3f();
    ydir.cross(zdir, xdir);

    xdir.normalize();
    ydir.normalize();
    zdir.normalize();

    viewMatrix.setColumn(0, xdir.x, xdir.y, xdir.z, 0.0f);
    viewMatrix.setColumn(1, ydir.x, ydir.y, ydir.z, 0.0f);
    viewMatrix.setColumn(2, zdir.x, zdir.y, zdir.z, 0.0f);
    viewMatrix.setColumn(3, cameraPosition.x, cameraPosition.y, cameraPosition.z, 1.0f);
    
    viewMatrix.invert();
  }
  
  public final void getViewProjectionMatrix(Matrix4f viewProjection) {
    viewProjection.mul( projectionMatrix );
    viewProjection.mul( viewMatrix   );
  }
  
  public final void getViewMatrix(Matrix4f view) {
    view.mul( viewMatrix );
  }
  
  public final void getProjectionMatrix(Matrix4f projection) {
    projection.mul( projectionMatrix );
  }
  
  public static void matrixToBuffer(Matrix4f in, FloatBuffer out) {
    out.rewind();
    float f[] = new float[4];
    for(int i = 0; i < 4; ++i) {
      in.getColumn(i, f);
      out.put(f);
    }
    out.rewind();
  }
  
  public static enum Profile {
    GL2,
    GL3,
    GL4,
    GLES2;
    
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
    
    public boolean supports(Profile profile) {
      switch (this) {
      case GLES2:
        return profile == GLES2;
      case GL4:
        return profile != GLES2;
      case GL3:
        return profile == GL2 || profile == GL3;
      case GL2:
        return profile == GL2;
      default:
        throw new IllegalStateException();
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
      switch(str) {
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
        throw new IllegalArgumentException();
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
