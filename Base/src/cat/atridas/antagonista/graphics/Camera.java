package cat.atridas.antagonista.graphics;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * Encapsulates a Camera.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 *
 */
public interface Camera {
  /**
   * Gets the angle of the camera in the Y vector. Said angle must be in degrees.
   * 
   * @return the field of view angle.
   * @since 0.1
   */
  float getFovY();
  /**
   * Gets the proximity of the near plane.
   * 
   * @return the proximity in game units (meters).
   * @since 0.1
   */
  float getZNear();
  /**
   * Gets the proximity of the far plane.
   * 
   * @return the proximity in game units (meters).
   * @since 0.1
   */
  float getZFar();
  
  /**
   * Returns the camera position, the position the camera looks at, and the up vector of the camera.
   * 
   * @param eye_ camera position.
   * @param lookat_ point to look. 
   * @param up_ approximate up vector.
   * @since 0.1
   */
  void getCameraParams(Point3f eye_, Point3f lookat_, Vector3f up_);
}
