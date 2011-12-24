package cat.atridas.antagonista.graphics;

import java.awt.Canvas;
import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL12.*;
//import static org.lwjgl.opengl.GL13.*;
//import static org.lwjgl.opengl.GL14.*;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL20.*;
//import static org.lwjgl.opengl.GL21.*;

public final class RenderManager {
	
	private int width, height;

	private final Matrix4f viewMatrix = new Matrix4f();
	private final Matrix4f projectionMatrix = new Matrix4f();
	private final Vector3f cameraPosition = new Vector3f();

	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	public void initDisplay(final int _width, final int _height, final String title, Canvas displayParent) {
		width  = _width;
		height = _height;
		
		try {
			Display.setTitle(title);
			if(displayParent == null)
				Display.setDisplayMode(new DisplayMode(width, height));
			else
				Display.setParent(displayParent);
			Display.create();
		} catch (LWJGLException e) {
			//e.printStackTrace();
			//System.exit(1);
			throw new RuntimeException(e);
		}
	}
	
	public void initGL() {
		glClearColor(1,0,1,0);
		
		glEnable(GL_BLEND);
		
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glEnable(GL_DEPTH_TEST);
		glClearDepth(1);
	}
	
	public void closeDisplay()
	{
		Display.destroy();
	}

	public void initFrame() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void present() {
		Display.update();
	}
	
	
	public void setPerspective(float fovy, float zNear, float zFar) {
	    setPerspective(fovy, (float)width / height, zNear, zFar);
	  }
	  
	  public void setPerspective(float fovy, float aspect, float zNear, float zFar) {
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
	  
	  public void setFrustum(
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
	  
	  public void setOrtho(
	      float left, float right, 
	      float top,  float bottom,
	      float near, float far)
	  {
		  setOrtho(left, right,  top,  bottom, near,  far, projectionMatrix);
	  }
	  
	  public void setOrtho(float near, float far)
	  {
		  setOrtho(near,  far, projectionMatrix);
	  }
	  
	  public void setOrtho(float near, float far, Matrix4f matrix)
	  {
		  setOrtho(0, width,  0, height, near,  far, matrix);
	  }
	  
	  public void setOrtho(
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
	  
	  public void setCamera(Point3f eye, Point3f lookAt, Vector3f up) {
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
	  
	  public void getViewProjectionMatrix(Matrix4f viewProjection) {
	    viewProjection.mul( projectionMatrix );
	    viewProjection.mul( viewMatrix   );
	  }
	  
	  public void getViewMatrix(Matrix4f view) {
	    view.mul( viewMatrix );
	  }
	  
	  public void getProjectionMatrix(Matrix4f projection) {
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
}
