package cat.atridas.antagonista.graphics;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class SceneData {

  private final Matrix4f viewMatrix = new Matrix4f();
  private final Matrix4f projectionMatrix = new Matrix4f();
  private final Vector3f cameraPosition = new Vector3f();
  

  public final void setPerspective(float fovy, float zNear, float zFar, RenderManager rm) {
    setPerspective(fovy, (float)rm.getWidth() / (float)rm.getHeight(), zNear, zFar);
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
  
  public final void setOrtho(float near, float far, RenderManager rm)
  {
    setOrtho(near,  far, rm, projectionMatrix);
  }
  
  public final void setOrtho(float near, float far, RenderManager rm, Matrix4f matrix)
  {
    setOrtho(0, rm.getWidth(),  0, rm.getHeight(), near,  far, matrix);
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
}
