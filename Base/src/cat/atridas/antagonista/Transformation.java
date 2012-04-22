package cat.atridas.antagonista;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

/**
 * Encapsulates a transformation, composed of a translation, a rotation and a
 * scale.
 * 
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public final class Transformation {

  /**
   * Front vector, taken from Blender (0,-1,0).
   * 
   * @since 0.1
   */
  public static final Vector3f FRONT_VECTOR = Conventions.FRONT_VECTOR;
  /**
   * Up vector, taken from Blender (0,0,1).
   * 
   * @since 0.1
   */
  public static final Vector3f UP_VECTOR = Conventions.UP_VECTOR;
  /**
   * Right vector, taken from Blender (-1,0,0).
   * 
   * @since 0.1
   */
  public static final Vector3f RIGHT_VECTOR = Conventions.RIGHT_VECTOR;
  /**
   * Left vector, taken from Blender (1,0,0).
   * 
   * @since 0.1
   */
  public static final Vector3f LEFT_VECTOR = Conventions.LEFT_VECTOR;

  // hard
  /**
   * Translation part of this transformation.
   * 
   * @since 0.5
   */
  private final Vector3f translation = new Vector3f();

  /**
   * Rotation in Quaternion Form. Up to date if and only if {@link #rotationUpdated} is <code>true</code>.
   * 
   * @since 0.5
   * @see #rotationUpdated
   */
  private final Quat4f rotation = new Quat4f(0, 0, 0, 1);
  /**
   * Indicates if {@link #rotation} is up to date.
   * 
   * @since 0.5
   * @see #rotation
   */
  private boolean rotationUpdated = true;

  /**
   * Scale part of this transformation.
   * 
   * @since 0.5
   */
  private float scale = 1;

  // auxiliar
  /**
   * Matrix with all the information. Up to date if and only if {@link #matrixUpdated} is <code>true</code>.
   * 
   * @since 0.5
   * @see #matrixUpdated
   */
  
  private final Matrix4f transformMatrix = new Matrix4f();
  /**
   * Indicates if {@link #transformMatrix} is up to date.
   * 
   * @since 0.5
   * @see #transformMatrix
   */
  private boolean matrixUpdated = false;

  /**
   * Rotation in yaw / pitch / roll form. Up to date if and only if {@link #yawPitchRollUpdated} is
   * <code>true</code>.
   * 
   * @since 0.5
   * @see #yawPitchRollUpdated
   */
  private Tuple3f yawPitchRoll = new Point3f();
  /**
   * Indicates if {@link #yawPitchRoll} is up to date.
   * 
   * @since 0.5
   * @see #yawPitchRoll
   */
  private boolean yawPitchRollUpdated = false;

  /**
   * Copies the transformation from another.
   * 
   * @param _other
   *          original transformation to copy.
   * @since 0.1
   */
  public void setTransform(Transformation _other) {
    translation.set(_other.translation);
    rotation.set(_other.rotation);
    transformMatrix.set(_other.transformMatrix);
    yawPitchRoll.set(_other.yawPitchRoll);

    rotationUpdated = _other.rotationUpdated;
    matrixUpdated = _other.matrixUpdated;
    yawPitchRollUpdated = _other.yawPitchRollUpdated;

    scale = _other.scale;

    rotationUpdated = _other.rotationUpdated;
    matrixUpdated = _other.matrixUpdated;
    yawPitchRollUpdated = _other.yawPitchRollUpdated;
  }

  /**
   * Copies the transformation from a matrix.
   * 
   * @param _transform
   *          the original matrix.
   * @since 0.1
   */
  public void setTransform(Matrix4f _transform) {
    translation.set(_transform.m03, _transform.m13, _transform.m23);
    rotation.set(_transform);
    transformMatrix.set(_transform);

    scale = _transform.getScale();

    matrixUpdated = true;
    rotationUpdated = true;
    yawPitchRollUpdated = false;
  }

  /**
   * Changes the translation of this transformation.
   * 
   * @param _translation
   *          new translation.
   * @since 0.1
   */
  public void setTranslation(Vector3f _translation) {
    translation.set(_translation);
    matrixUpdated = false;
  }

  /**
   * Sets the rotation of this transformation.
   * 
   * @param _rotation
   *          new rotation.
   * @since 0.1
   */
  public void setRotation(Quat4f _rotation) {
    rotation.set(_rotation);
    matrixUpdated = false;
    yawPitchRollUpdated = false;
    rotationUpdated = true;
  }

  /**
   * Sets the rotation of this transformation.
   * 
   * @param _rotation
   *          new rotation.
   * @since 0.1
   */
  public void setRotation(Matrix3f _rotation) {
    rotation.set(_rotation);
    matrixUpdated = false;
    yawPitchRollUpdated = false;
    rotationUpdated = true;
  }

  /**
   * Sets the rotation of this transformation.
   * 
   * @param _yaw
   *          rotation about the up axis. Positive means turning to the left.
   * @param _pitch
   *          rotation about the left/right axis. Positive means looking up.
   * @param _roll
   *          rotation about the front axis. Positive rolling to the right.
   * @since 0.1
   */
  public void setRotation(float _yaw, float _pitch, float _roll) {
    yawPitchRoll.x = _yaw;
    yawPitchRoll.y = _pitch;
    yawPitchRoll.z = _roll;
    matrixUpdated = false;
    yawPitchRollUpdated = true;
    rotationUpdated = false;
  }

  /**
   * Sets the yaw rotation of this transformation.
   * 
   * @param _yaw
   *          rotation about the up axis. Positive means turning to the left.
   * @since 0.1
   */
  public void setYaw(float _yaw) {
    if (!yawPitchRollUpdated) {
      updateRotations();
    }
    yawPitchRoll.x = _yaw;
    matrixUpdated = false;
    assert yawPitchRollUpdated;
    rotationUpdated = false;
  }

  /**
   * Sets the pitch rotation of this transformation.
   * 
   * @param _pitch
   *          rotation about the left/right axis. Positive means looking up.
   * @since 0.1
   */
  public void setPitch(float _pitch) {
    if (!yawPitchRollUpdated) {
      updateRotations();
    }
    yawPitchRoll.y = _pitch;
    matrixUpdated = false;
    assert yawPitchRollUpdated;
    rotationUpdated = false;
  }

  /**
   * Sets the roll rotation of this transformation.
   * 
   * @param _roll
   *          rotation about the front axis. Positive rolling to the right.
   * @since 0.1
   */
  public void setRoll(float _roll) {
    if (!yawPitchRollUpdated) {
      updateRotations();
    }
    yawPitchRoll.z = _roll;
    matrixUpdated = false;
    assert yawPitchRollUpdated;
    rotationUpdated = false;
  }

  /**
   * Gets the yaw rotation of this transformation.
   * 
   * @return the yaw rotation of this transformation.
   * @since 0.1
   */
  public float getYaw() {
    if (!yawPitchRollUpdated) {
      updateRotations();
    }
    return yawPitchRoll.x;
  }

  /**
   * Gets the pitch rotation of this transformation.
   * 
   * @return the pitch rotation of this transformation.
   * @since 0.1
   */
  public float getPitch() {
    if (!yawPitchRollUpdated) {
      updateRotations();
    }
    return yawPitchRoll.y;
  }

  /**
   * Gets the roll rotation of this transformation.
   * 
   * @return the roll rotation of this transformation.
   * @since 0.1
   */
  public float getRoll() {
    if (!yawPitchRollUpdated) {
      updateRotations();
    }
    return yawPitchRoll.z;
  }

  /**
   * Gets the translation of this transformation.
   * 
   * @param translation_
   * @since 0.1
   */
  public void getTranslation(Vector3f translation_) {
    translation_.set(translation);
  }

  /**
   * Gets the rotation of this transformation.
   * 
   * @param rotation_
   * @since 0.1
   */
  public void getRotation(Quat4f rotation_) {
    if (!rotationUpdated) {
      updateRotations();
    }
    rotation_.set(rotation);
  }

  /**
   * Gets the rotation of this transformation.
   * 
   * @param rotationMatrix_
   * @since 0.1
   */
  public void getRotation(Matrix3f rotationMatrix_) {
    if (!rotationUpdated) {
      updateRotations();
    }
    rotationMatrix_.setIdentity();
    rotationMatrix_.set(rotation);

  }

  /**
   * Gets the transformation matrix equivalent to this transformation.
   * 
   * @param transformMatrix_
   * @since 0.5
   */
  public void getMatrix(Matrix4f transformMatrix_) {
    if (!matrixUpdated) {
      if (!rotationUpdated)
        updateRotations();
      transformMatrix.setIdentity();
      transformMatrix.setRotation(rotation);
      transformMatrix.setTranslation(translation);
      transformMatrix.setScale(scale);
    }
    transformMatrix_.set(transformMatrix);
  }

  /**
   * Makes sure both {@link #rotation} and {@link #yawPitchRoll} are up to date.
   * 
   * @since 0.5
   * @see #rotation
   * @see #rotationUpdated
   * @see #yawPitchRoll
   * @see #yawPitchRollUpdated
   */
  private void updateRotations() {
    assert rotationUpdated || yawPitchRollUpdated;
    if (rotationUpdated) {
      Conventions.quaternionToEulerAngles(rotation, yawPitchRoll);
      yawPitchRollUpdated = true;
    } else {
      Conventions.eulerAnglesToQuaternion(yawPitchRoll, rotation);
      rotationUpdated = true;
    }
  }

  @Override
  public String toString() {
    getMatrix(transformMatrix);
    return transformMatrix.toString();
  }
}
