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
  private boolean rotationUpdated = true;
  
  //auxiliar
  private final Matrix4f transformMatrix = new Matrix4f();
  private boolean matrixUpdated = false;
  
  private Tuple3f yawPitchRoll = new Point3f();
  private boolean yawPithRollUpdated = false;
  
  public void setTransform(Transformation _other) {
    translation    .set(_other.translation);
    rotation       .set(_other.rotation);
    transformMatrix.set(_other.transformMatrix);
    yawPitchRoll   .set(_other.yawPitchRoll);
    
    rotationUpdated    = _other.rotationUpdated;
    matrixUpdated      = _other.matrixUpdated;
    yawPithRollUpdated = _other.yawPithRollUpdated;
  }
  
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
  
  private static final Vector3f g_v3aux1 = new Vector3f();
  public static void getClosestRotation(Vector3f _originalDir, Vector3f _finalDir, Quat4f rotation_) {
    assert Math.abs(_originalDir.lengthSquared() - 1.0) < Utils.EPSILON;
    assert Math.abs(_finalDir.lengthSquared() - 1.0) < Utils.EPSILON;
    

    float angle = _originalDir.angle(_finalDir);
    if(angle > Math.PI - Utils.EPSILON) { //mitja volta
      rotation_.x = 1;
      rotation_.y = 0;
      rotation_.z = 0;
      rotation_.w = 0;
      return;
    } else if(angle < Utils.EPSILON) { //no rotem
      rotation_.x = 0;
      rotation_.y = 0;
      rotation_.z = 0;
      rotation_.w = 1;
      return;
    }
    
    g_v3aux1.cross(_originalDir, _finalDir);
    g_v3aux1.normalize();

    float sin = (float)Math.sin(angle / 2);
    float cos = (float)Math.cos(angle / 2);

    rotation_.x = g_v3aux1.x * sin;
    rotation_.y = g_v3aux1.y * sin;
    rotation_.z = g_v3aux1.z * sin;
    rotation_.w = cos;
  }

  private static final Vector3f g_v3aux2 = new Vector3f();
  private static final Vector3f g_v3aux3 = new Vector3f();
  private static final Vector3f g_v3aux4 = new Vector3f();
  private static final Quat4f   g_qaux  = new Quat4f();
  private static final Quat4f   g_qaux2 = new Quat4f();
  public static void getClosestRotation(
      Vector3f _originalDir, Vector3f _originalUp, 
      Vector3f _finalDir, Vector3f _finalUp, Quat4f rotation_) {
    assert Math.abs(_originalUp.lengthSquared() - 1.0) < Utils.EPSILON;
    assert Math.abs(_finalUp.lengthSquared() - 1.0) < Utils.EPSILON;
    
    getClosestRotation(_originalDir, _finalDir, rotation_);

    Vector3f upRotat     = g_v3aux2;
    Vector3f left        = g_v3aux3;
    Vector3f realFinalUp = g_v3aux4;
    
    //rotem el vector original up segon la rotació donada.
    g_qaux.set(_originalUp.x, _originalUp.y, _originalUp.z, 0);
    g_qaux2.mul(rotation_, g_qaux);
    g_qaux.mulInverse(g_qaux2, rotation_);
    upRotat.set(g_qaux.x, g_qaux.y, g_qaux.z); // vector up original rotat
    
    left.cross(upRotat, _finalDir);
    upRotat.cross(_finalDir, left);
    upRotat.normalize();
    
    left.cross(_finalUp, _finalDir);
    realFinalUp.cross(_finalDir, left);
    realFinalUp.normalize();

    /*
    Vector3f aux        = g_v3aux3; //això abans era el left, que ja no fem servir més
    
    aux.sub(upRotat, realFinalUp);
    if(aux.lengthSquared() < Utils.EPSILON)
      return; //el vector up ja està més o menys on el volem.
    */
    
    getClosestRotation(upRotat, realFinalUp, g_qaux);
    
    /*
    double angle = Math.acos( upRotat.dot(realFinalUp) );
    float cos = (float) Math.cos(angle / 2.);
    float sin = (float) Math.sin(angle / 2.);

    g_qaux.x = _finalDir.x * sin;
    g_qaux.y = _finalDir.y * sin;
    g_qaux.z = _finalDir.z * sin;
    g_qaux.w = -cos;
    */
    
    rotation_.mul(g_qaux, rotation_);
  }
}
