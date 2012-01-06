package cat.atridas.antagonista;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;


public final class Transformation {

  public static final Vector3f FRONT_VECTOR = new Vector3f(0, -1, 0); 
  public static final Vector3f UP_VECTOR = new Vector3f(0, 0, 1); 
  public static final Vector3f RIGHT_VECTOR = new Vector3f(-1, 0, 0); 
  public static final Vector3f LEFT_VECTOR = new Vector3f(1, 0, 0); 
  
  
  // hard
  private final Vector3f translation = new Vector3f();
  
  private final Quat4f   rotation    = new Quat4f(0,0,0,1);
  private boolean rotationUpdated;
  
  //auxiliar
  private final Matrix4f transformMatrix = new Matrix4f();
  private boolean matrixUpdated = false;
  
  private Tuple3f yawPitchRoll = new Point3f();
  private boolean yawPithRollUpdated = false;
  
  public void setTransform(Matrix4f _transform) {
    translation.set(_transform.m03, _transform.m13, _transform.m23);
    rotation.set(_transform);
    transformMatrix.set(_transform);
    matrixUpdated = true;
  }
  
  public void setTranslation(Vector3f _translation) {
    translation.set(_translation);
    matrixUpdated = false;
  }
  
  public void setRotation(Quat4f _rotation) {
    rotation.set(_rotation);
    matrixUpdated = false;
    yawPithRollUpdated = false;
    rotationUpdated = true;
  }
  
  public void setRotation(Matrix3f _rotation) {
    rotation.set(_rotation);
    matrixUpdated = false;
    yawPithRollUpdated = false;
    rotationUpdated = true;
  }
  
  public void setRotation(float _yaw, float _pitch, float _roll) {
    yawPitchRoll.x = _yaw;
    yawPitchRoll.y = _pitch;
    yawPitchRoll.z = _roll;
    matrixUpdated = false;
    yawPithRollUpdated = true;
    rotationUpdated = false;
  }
  
  public void setYaw(float _yaw) {
    if(!yawPithRollUpdated) {
      updateRotations();
    }
    yawPitchRoll.x = _yaw;
    matrixUpdated = false;
    assert yawPithRollUpdated;
    rotationUpdated = false;
  }
  
  public void setPitch(float _pitch) {
    if(!yawPithRollUpdated) {
      updateRotations();
    }
    yawPitchRoll.y = _pitch;
    matrixUpdated = false;
    assert yawPithRollUpdated;
    rotationUpdated = false;
  }
  
  public void setRoll(float _roll) {
    if(!yawPithRollUpdated) {
      updateRotations();
    }
    yawPitchRoll.z = _roll;
    matrixUpdated = false;
    assert yawPithRollUpdated;
    rotationUpdated = false;
  }
  
  public float getYaw() {
    if(!yawPithRollUpdated) {
      updateRotations();
    }
    return yawPitchRoll.x;
  }
  
  public float getPitch() {
    if(!yawPithRollUpdated) {
      updateRotations();
    }
    return yawPitchRoll.y;
  }
  
  public float getRoll() {
    if(!yawPithRollUpdated) {
      updateRotations();
    }
    return yawPitchRoll.z;
  }

  public void getTranslation(Vector3f translation_) {
    translation_.set(translation);
  }
  
  public void getRotation(Quat4f rotation_) {
    if(!rotationUpdated) {
      updateRotations();
    }
    rotation_.set(rotation);
  }
  
  public void getRotation(Matrix3f rotationMatrix_) {
    if(!rotationUpdated) {
      updateRotations();
    }
    rotationMatrix_.setIdentity();
    rotationMatrix_.set(rotation);
    
  }
  
  public void getMatrix(Matrix4f transformMatrix_) {
    if(!matrixUpdated) {
      if(!rotationUpdated) updateRotations();
      transformMatrix.setIdentity();
      transformMatrix.setRotation(rotation);
      transformMatrix.setTranslation(translation);
    }
    transformMatrix_.set(transformMatrix);
  }
  
  private void updateRotations() {
    assert rotationUpdated || yawPithRollUpdated;
    if(rotationUpdated) {
      Conventions.quaternionToEulerAngles(rotation, yawPitchRoll);
      yawPithRollUpdated = true;
    } else {
      Conventions.eulerAnglesToQuaternion(yawPitchRoll, rotation);
      rotationUpdated = true;
    }
  }
}
