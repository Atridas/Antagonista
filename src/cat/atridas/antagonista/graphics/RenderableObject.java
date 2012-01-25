package cat.atridas.antagonista.graphics;

import javax.vecmath.Matrix4f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;

/**
 * Encapsulates a renderable object instance.
 * 
 * @author Isaac 'Atridas' Serrano Guash
 * @since 0.1
 *
 */
public final class RenderableObject {
  /**
   * Name of this renderable object.
   * @since 0.1
   */
  private final HashedString name;
  
  /**
   * Transformation in world coordinate system of this renderable object.
   * @since 0.1
   */
  private final Transformation trans = new Transformation();
  /**
   * Mesh core of this renderable object.
   * @since 0.1
   */
  private final Mesh mesh;
  
  /**
   * Indicates if this renderable object must be rendered or not.
   * @since 0.1
   */
  private boolean visible = true;
  /**
   * Indicates if this renderable object has been culled by some culling system.
   * @since 0.1
   */
  private boolean culled  = false;
  
  /**
   * Checks if this object has been activated by the aplication.
   * 
   * @return <code>true</code> if the renderer should render this object.
   * @since 0.1
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * May be used by anyone to activate or deactivate rendering.
   * 
   * @param visible
   * @since 0.1
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Checks if the culling system has culled out this object and should not be rendered.
   * 
   * @return <code>true</code> if the renderer should not render this object.
   * @since 0.1
   */
  public boolean isCulled() {
    return culled;
  }

  /**
   * Used by te culling system to indicate if this object is visible from the current camera or not.
   * 
   * @param culled if this object must be culled or not.
   * @since 0.1
   */
  public void setCulled(boolean culled) {
    this.culled = culled;
  }

  /**
   * Gets the name of this RenderableObject.
   * 
   * @return the name of this RenderableObject.
   * @since 0.1
   */
  public HashedString getName() {
    return name;
  }

  /**
   * Gets the mesh of this RenderableObject.
   * 
   * @return the mesh of this RenderableObject.
   * @since 0.1
   */
  public Mesh getMesh() {
    return mesh;
  }

  /**
   * Constructs an untransformed renderable object.
   * 
   * @param _name name of this object.
   * @param _mesh mesh used by this object.
   * @since 0.1
   */
  public RenderableObject(HashedString _name, Mesh _mesh) {
    name = _name;
    mesh = _mesh;
  }

  /**
   * Gets the transformation of this object.
   * 
   * @param trans_ output parameter.
   * @since 0.1
   */
  public void getTransformation(Transformation trans_) {
    trans_.setTransform(trans);
  }

  /**
   * Sets the transformation of this object.
   * 
   * @param _trans input parameter.
   * @since 0.1
   */
  public void setTransformation(Transformation _trans) {
    trans.setTransform(_trans);
  }

  /**
   * Gets the transformation matrix of this object.
   * 
   * @param trans_ output parameter.
   * @since 0.1
   */
  public void getTransformation(Matrix4f matrix_) {
    trans.getMatrix(matrix_);
  }

  /**
   * Sets the transformation matrix of this object.
   * 
   * @param _trans input parameter.
   * @since 0.1
   */
  public void setTransformation(Matrix4f _trans) {
    trans.setTransform(_trans);
  }
}
