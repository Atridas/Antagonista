package cat.atridas.antagonista.graphics;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.Utils;

/**
 * Encapsulates all global scene information.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 *
 */
public abstract class SceneData {

  /**
   * Quick access to the Render Manager.
   * @since 0.1
   */
  protected final RenderManager rm;
  /**
   * Matrix transformation from world space to camera space.
   * @since 0.1
   */
  private final Matrix4f viewMatrix = new Matrix4f();
  /**
   * Matrix transformation from view space to homogeneous space.
   * @since 0.1
   */
  private final Matrix4f projectionMatrix = new Matrix4f();
  
  private final Matrix4f inverseVPMatrix = new Matrix4f();
  private boolean inverseVPMatrixUpdated = false;
  
  /**
   * Distance to the near/far planes.
   * @since 0.2
   */
  private float near, far;
  
  private final Point3f cameraPosition  = new Point3f();
  private final Vector3f cameraUpVector  = new Vector3f();
  private final Vector3f cameraDirection = new Vector3f();

  protected final Color3f  ambientLightColor         = new Color3f();
  protected final Color3f  directionalLightColor     = new Color3f();
  protected final Vector3f directionalLightDirection = new Vector3f();

  /**
   * Sets the current scene uniforms.
   * @since 0.1
   */
  public abstract void setUniforms();

  /**
   * Sets the current scene uniforms.
   * @since 0.1
   */
  public abstract void setUniforms(TechniquePass pass);
  
  /**
   * Default constructor.
   * 
   * @param _rm Render Manager reference.
   * @since 0.1
   */
  protected SceneData(RenderManager _rm) {
    rm = _rm;
  }
  
  /**
   * Sets the current ambient light color.
   * @param _color
   * @since 0.1
   */
  public final void setAmbientLight(Color3f _color) {
    ambientLightColor.set(_color);
  }
  
  /**
   * Sets a unique directional light parameters.
   * 
   * @param _direction of the light.
   * @param _color of the light
   * @since 0.1
   */
  public final void setDirectionalLight(Vector3f _direction, Color3f _color) {
    directionalLightColor.set(_color);
    directionalLightDirection.set(_direction);
    if(Math.abs(directionalLightDirection.lengthSquared() - 1.f) > Utils.EPSILON ) {
      directionalLightDirection.normalize();
    }
  }

  /**
   * Gets the ambient light color.
   * @param color_ output paramenter
   * @since 0.1
   */
  public final void getAmbientLight(Color3f color_) {
    color_.set(ambientLightColor);
  }
  
  /**
   * Gets the unique directional light parameters.
   * 
   * @param direction_ output paramenter
   * @param color_ output paramenter
   * @since 0.1
   */
  public final void getDirectionalLight(Vector3f direction_, Color3f color_) {
    direction_.set(directionalLightColor);
    color_.set(directionalLightDirection);
  }
  
  /**
   * Sets a perspective projection.
   * 
   * @param fovy angle (in degrees) of the camera in the y plane.
   * @param zNear depth plane
   * @param zFar depth plane
   * @since 0.1
   * @see #setPerspective(float, float, float, float)
   */
  public final void setPerspective(float fovy, float zNear, float zFar) {
    setPerspective(fovy, (float)rm.getWidth() / (float)rm.getHeight(), zNear, zFar);
  }

  /**
   * Sets a perspective projection.
   * 
   * @param fovy angle (in degrees) of the camera in the y plane.
   * @param aspect ratio of width/height pixels.
   * @param zNear depth plane
   * @param zFar depth plane
   * @since 0.1
   * @see #setPerspective(float, float, float, float)
   */
  public final void setPerspective(float fovy, float aspect, float zNear, float zFar) {
    float f = (float)(1.0 / Math.tan((fovy/2.) * (Math.PI/180.f)));
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
        
    inverseVPMatrixUpdated = false;
    near = zNear;
    far = zFar;
  }
  
  /**
   * Sets a frustum projection.
   * 
   * @param _left plane
   * @param _right plane
   * @param _bottom plane
   * @param _top plane
   * @param _near plane
   * @param _far plane
   * @since 0.1
   */
  public final void setFrustum(
      float _left, float _right, 
      float _bottom, float _top, 
      float _near, float _far) 
  {
    projectionMatrix.setColumn(0, 
        2*_near / (_right-_left), 
        0.0f, 
        0.0f, 
        0.0f);
    projectionMatrix.setColumn(1, 
        0.0f, 
        2*_near / (_top-_bottom), 
        0.0f, 
        0.0f);
    projectionMatrix.setColumn(2, 
        (_right+_left) / (_right-_left), 
        (_top+_bottom) / (_top-_bottom), 
        -(_far+_near) / (_far-_near), 
        -1.0f);
    projectionMatrix.setColumn(3, 
        0.0f, 
        0.0f, 
        -2*_near * _far / (_far-_near), 
        0.0f);

    inverseVPMatrixUpdated = false;
    near = _near;
    far = _far;
  }
  
  /**
   * Orthogonal projection.
   * 
   * @param _left plane
   * @param _right plane
   * @param _top plane
   * @param _bottom plane
   * @param _near plane
   * @param _far plane
   * @since 0.1
   */
  public final void setOrtho(
      float _left, float _right, 
      float _top,  float _bottom,
      float _near, float _far)
  {
    getOrtho(_left, _right,  _top,  _bottom, _near,  _far, projectionMatrix);
    inverseVPMatrixUpdated = false;
    near = _near;
    far = _far;
  }
  
  /**
   * Set an orthogonal projection going from 0 to the current screen width an heights.
   * 
   * @param _near plane
   * @param _far plane
   * @since 0.1
   * @see #setOrtho(float, float, float, float, float, float)
   */
  public final void setOrtho(float _near, float _far)
  {
    getOrtho(near,  far, projectionMatrix);
    inverseVPMatrixUpdated = false;
    near = _near;
    far = _far;
  }
  
  /**
   * Saves an orthogonal projection similar to {@link #setOrtho(float, float)} into a matrix.
   * 
   * @param near plane
   * @param far plane
   * @param matrix_ output parameter.
   * @since 0.1
   * @see #setOrtho(float, float)
   * @see #getOrtho(float, float, float, float, float, float, Matrix4f)
   */
  public final void getOrtho(float near, float far, Matrix4f matrix_)
  {
    getOrtho(0, rm.getWidth(),  0, rm.getHeight(), near,  far, matrix_);
  }
  
  /**
   * Saves an orthogonal projection into a matrix.
   * 
   * @param left plane
   * @param right plane
   * @param top plane
   * @param bottom plane
   * @param near plane
   * @param far plane
   * @param matrix output parameter.
   * @since 0.1
   * @see #setOrtho(float, float, float, float, float, float)
   */
  public static final void getOrtho(
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
  
  /**
   * Sets the basic camera parameters.
   * 
   * @param eye camera position.
   * @param lookAt Point the camera looks at.
   * @param up camera up vector.
   * @since 0.1
   */
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

    cameraUpVector.set(ydir);
    cameraDirection.set(zdir);

    viewMatrix.setColumn(0, xdir.x, xdir.y, xdir.z, 0.0f);
    viewMatrix.setColumn(1, ydir.x, ydir.y, ydir.z, 0.0f);
    viewMatrix.setColumn(2, zdir.x, zdir.y, zdir.z, 0.0f);
    viewMatrix.setColumn(3, cameraPosition.x, cameraPosition.y, cameraPosition.z, 1.0f);
    
    viewMatrix.invert();
    inverseVPMatrixUpdated = false;
  }
  
  /**
   * Gets the camera parameters from this camera.
   * 
   * @param camera to save.
   * @since 0.1
   * @see #setCamera(Point3f, Point3f, Vector3f)
   * @see #setPerspective(float, float, float)
   */
  public final void setCamera(Camera camera) {
    Point3f eye    = new Point3f();
    Point3f lookAt = new Point3f();
    Vector3f up    = new Vector3f();
    
    camera.getCameraParams(eye, lookAt, up);
    setCamera(eye, lookAt, up);
    
    setPerspective(camera.getFovY(), camera.getZNear(), camera.getZFar());
  }
  
  /**
   * Gets the current camera position.
   * 
   * @param position_ output parameter
   * @since 0.1
   */
  public final void getCameraPosition(Point3f position_) {
    position_.set(cameraPosition);
  }
  
  /**
   * Gets the current camera up vector.
   * 
   * @param upVector_ output parameter
   * @since 0.1
   */
  public final void getCameraUpVector(Vector3f upVector_) {
    upVector_.set(cameraUpVector);
  }
  
  /**
   * Gets the current camera direction.
   * 
   * @param direction_ output parameter.
   * @since 0.1
   */
  public final void getCameraPositionDirection(Vector3f direction_) {
    direction_.set(cameraDirection);
  }
  
  /**
   * Gets the transformation matrix that transforms from world space to homogeneous space.
   * 
   * @param viewProjection_ output parameter.
   * @since 0.1
   */
  public final void getViewProjectionMatrix(Matrix4f viewProjection_) {
    viewProjection_.set( projectionMatrix );
    viewProjection_.mul( viewMatrix   );
  }
  
  /**
   * Gets the transformation matrix that transforms from world space to view space.
   * 
   * @param view_ output parameter.
   * @since 0.1
   */
  public final void getViewMatrix(Matrix4f view_) {
    view_.set( viewMatrix );
  }
  
  /**
   * Gets the transformation matrix that transforms from view space to homogeneous space.
   * 
   * @param projection_ output parameter.
   * @since 0.1
   */
  public final void getProjectionMatrix(Matrix4f projection_) {
    projection_.set( projectionMatrix );
  }
  
  /**
   * Gets the inverse matrix that transforms from homogeneus space to world space.
   * 
   * @param inverseVPMatrix_ output parameter.
   * @since 0.2
   */
  public final void getInverseViewProjectionMatrix(Matrix4f inverseVPMatrix_) {
    if(!inverseVPMatrixUpdated) {
      //inverseVPMatrix.setIdentity();
      getViewProjectionMatrix(inverseVPMatrix);
      inverseVPMatrix.invert();
      inverseVPMatrixUpdated = true;
    }
    inverseVPMatrix_.mul( inverseVPMatrix );
  }
  
  private final ThreadLocal<Point4f> aux_getFarPlanePoint = new ThreadLocal<Point4f>() {
                                                                        @Override
                                                                        protected Point4f initialValue() {
                                                                          return new Point4f();
                                                                        }
                                                                      };
  public final void getFarPlanePoint(Point2f windowPoint, Point3f worldPoint_) {
    if(!inverseVPMatrixUpdated) {
      //inverseVPMatrix.setIdentity();
      getViewProjectionMatrix(inverseVPMatrix);
      //getProjectionMatrix(inverseVPMatrix);
      inverseVPMatrix.invert();
      inverseVPMatrixUpdated = true;
    }
    
    Point4f homogeneusPoint = aux_getFarPlanePoint.get();
    
    homogeneusPoint.set(0,0,-far,1);
    projectionMatrix.transform(homogeneusPoint);
    

    homogeneusPoint.x = windowPoint.x / rm.getWidth();
    homogeneusPoint.y = windowPoint.y / rm.getHeight();

    homogeneusPoint.x *= 2; homogeneusPoint.x -= 1;
    homogeneusPoint.y *= 2; homogeneusPoint.y -= 1;

    homogeneusPoint.x *= homogeneusPoint.w;
    homogeneusPoint.y *= homogeneusPoint.w;
    
    homogeneusPoint.z = homogeneusPoint.z;
    homogeneusPoint.w = far;
    
    inverseVPMatrix.transform(homogeneusPoint);

    worldPoint_.x = homogeneusPoint.x;
    worldPoint_.y = homogeneusPoint.y;
    worldPoint_.z = homogeneusPoint.z;
  }
}
