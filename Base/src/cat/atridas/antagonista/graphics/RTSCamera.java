package cat.atridas.antagonista.graphics;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * Camera emuling the classic Real Time Strategy camera.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 * 
 */
public class RTSCamera implements Camera {

  /**
   * Field of view in vertical. The measure is in degrees.
   * 
   * @since 0.1
   */
  private float fovY = 30;
  /**
   * Depth plane.
   * 
   * @since 0.1
   */
  private float zNear = 0.5f, zFar = 40;
  /**
   * Camera position.
   * 
   * @since 0.1
   */
  private final Point3f eye = new Point3f();
  /**
   * Where is the camera looking at.
   * 
   * @since 0.1
   */
  private final Point3f lookat = new Point3f(0, 0, 0);
  /**
   * Camera up vector.
   * 
   * @since 0.1
   */
  private final Vector3f up = new Vector3f(0, 0, 1);

  /**
   * Direction of the camera when the user tries to move it to the "right".
   * 
   * @since 0.1
   */
  private Vector3f rightDisplacement = new Vector3f();
  /**
   * Direction of the camera when the user tries to move it to the "front".
   * 
   * @since 0.1
   */
  private Vector3f upDisplacement = new Vector3f();

  /**
   * <p>
   * Spherical angles, in degrees.
   * </p>
   * <p>
   * A yaw of 0 means that the camera is looking to the positive Y axis, and 90
   * degrees the camera looks to negative X angle.
   * </p>
   * <p>
   * A pitch of 0 means the camera is looking paralel to the floor (X-Y) plane,
   * and 90 degrees the camera is looking directly at it. Pitch should never be
   * 0 or 90 exactly.
   * </p>
   * 
   * @since 0.1
   */
  private float yaw = 0, pitch = 80;

  /**
   * Values that cap the pitch of the camera.
   * 
   * @since 0.1
   * @see #pitch
   */
  private float minPitch = -85, maxPitch = 85;

  /**
   * Distance, in meters (engine units) from the lookat point to the camera
   * position.
   * 
   * @since 0.1
   */
  private float distance = 5;

  /**
   * Values that cap the distance.
   * 
   * @since 0.1
   * @see #distance
   */
  private float minDistance = 2, maxDistance = 5;

  private final Vector3f v3Aux1 = new Vector3f();
  private final AxisAngle4f aaAux1 = new AxisAngle4f();
  private final Matrix3f m3Aux1 = new Matrix3f();
  {
    updateVariables();
  }

  /**
   * Modifies the minimum camera distance.
   * 
   * @param _minDistance
   *          minimum distance to the lookat point.
   * @since 0.1
   * @see #setDistance(float)
   * @see #addDistance(float)
   */
  public void setMinDistance(float _minDistance) {
    minDistance = _minDistance;
  }

  /**
   * Modifies the maximum camera distance.
   * 
   * @param _maxDistance
   *          maximum distance to the lookat point.
   * @since 0.1
   * @see #setDistance(float)
   * @see #addDistance(float)
   */
  public void setMaxDistance(float _maxDistance) {
    maxDistance = _maxDistance;
  }

  /**
   * Gets the current minimum distance to the lookat point.
   * 
   * @return the current minimum distance to the lookat point.
   * @since 0.1
   * @see #setMinDistance(float)
   */
  public float getMinDistance() {
    return minDistance;
  }

  /**
   * Gets the current maximum distance to the lookat point.
   * 
   * @return the current maximum distance to the lookat point.
   * @since 0.1
   * @see #setMaxDistance(float)
   */
  public float getMaxDistance() {
    return maxDistance;
  }

  /**
   * Sets the yaw (in degrees) of the camera.
   * 
   * @param _yaw
   *          new yaw of the camera.
   * @since 0.1
   * @see #addYaw(float)
   */
  public void setYaw(float _yaw) {
    yaw = _yaw;
    updateVariables();
  }

  /**
   * Sets the pitch (in degrees) of the camera.
   * 
   * @param _pitch
   *          the new pitch of the camera.
   * @since 0.1
   * @see #addPitch(float)
   */
  public void setPitch(float _pitch) {
    pitch = _pitch;
    updateVariables();
  }

  /**
   * Sets the new distance to the lookat point.
   * 
   * @param _distance
   *          the new distance to the lookat point.
   * @since 0.1
   * @see #addDistance(float)
   */
  public void setDistance(float _distance) {
    distance = _distance;
    updateVariables();
  }

  /**
   * Adds some degrees to the camera yaw.
   * 
   * @param _yaw
   *          diff value to add to the camera yaw.
   * @since 0.1
   * @see #setYaw(float)
   */
  public void addYaw(float _yaw) {
    float nyaw = yaw + _yaw;
    while (nyaw > 360) {
      nyaw -= 360;
    }
    while (nyaw < 0) {
      nyaw += 360;
    }
    setYaw(nyaw);
  }

  /**
   * Adds some degrees to the camera pitch.
   * 
   * @param _pitch
   *          diff value to add to the camera pitch.
   * @since 0.1
   * @see #setPitch(float)
   */
  public void addPitch(float _pitch) {
    float npitch = pitch + _pitch;
    if (npitch > maxPitch) {
      npitch = maxPitch;
    }
    if (npitch < minPitch) {
      npitch = minPitch;
    }
    setPitch(npitch);
  }

  /**
   * Adds some distance in meters (or engine units) to the distance from the
   * camera to the look at point.
   * 
   * @param _distance
   *          diff value to ne new distance.
   * @since 0.1
   * @see #setDistance(float)
   */
  public void addDistance(float _distance) {
    float ndistance = distance + _distance;
    if (ndistance > maxDistance) {
      ndistance = maxDistance;
    }
    if (ndistance < minDistance) {
      ndistance = minDistance;
    }
    setDistance(ndistance);
  }

  /**
   * Moves the camera to the right a specified distance, in meters (or engine
   * units).
   * 
   * @param distance
   *          to move.
   * @since 0.1
   */
  public void moveRight(float distance) {
    assert rightDisplacement.z == 0;

    v3Aux1.scale(distance, rightDisplacement);
    lookat.add(v3Aux1);
    eye.add(v3Aux1);
  }

  /**
   * Moves the camera to the front a specified distance, in meters (or engine
   * units).
   * 
   * @param distance
   *          to move.
   * @since 0.1
   */
  public void moveUp(float distance) {
    assert upDisplacement.z == 0;

    v3Aux1.scale(distance, upDisplacement);
    lookat.add(v3Aux1);
    eye.add(v3Aux1);
  }

  /**
   * Sets the z-plane where the lookat point will be.
   * 
   * @param z
   *          plane.
   * @since 0.1
   * @see #addZLookAt(float)
   */
  public void setZLookAt(float z) {
    float dist = z - lookat.z;
    lookat.z = z;
    eye.z += dist;
  }

  /**
   * Adds a distance in meters to the z-plane where the lookat point will be.
   * 
   * @param z
   *          plane diff.
   * @since 0.1
   * @see #setZLookAt(float)
   */
  public void addZLookAt(float z) {
    lookat.z += z;
    eye.z += z;
  }

  /**
   * Updates the eye point (camera position) and the up-left displacement
   * vectors to be consisten with the rest of the variables.
   * 
   * @since 0.1
   */
  private void updateVariables() {
    // assert pitch > 0;
    assert pitch < 90;
    assert pitch >= minPitch && pitch <= maxPitch;

    Vector3f v3CenterToEye = v3Aux1;
    v3CenterToEye.set(0, -1, 0);

    aaAux1.set(-1, 0, 0, pitch * (float) Math.PI / 180);
    m3Aux1.set(aaAux1);
    m3Aux1.transform(v3CenterToEye);

    aaAux1.set(0, 0, 1, yaw * (float) Math.PI / 180);
    m3Aux1.set(aaAux1);
    m3Aux1.transform(v3CenterToEye);

    eye.scaleAdd(distance, v3CenterToEye, lookat);

    upDisplacement.set(0, 1, 0);
    m3Aux1.transform(upDisplacement);
    rightDisplacement.set(1, 0, 0);
    m3Aux1.transform(rightDisplacement);
  }

  @Override
  public float getFovY() {
    return fovY;
  }

  @Override
  public float getZNear() {
    return zNear;
  }

  @Override
  public float getZFar() {
    return zFar;
  }

  @Override
  public void getCameraParams(Point3f eye_, Point3f lookat_, Vector3f up_) {
    eye_.set(eye);
    lookat_.set(lookat);
    up_.set(up);
  }

}
