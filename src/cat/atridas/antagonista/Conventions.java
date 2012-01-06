package cat.atridas.antagonista;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

public class Conventions {

  public static final Vector3f FRONT_VECTOR = new Vector3f(0, -1, 0); 
  public static final Vector3f UP_VECTOR = new Vector3f(0, 0, 1); 
  public static final Vector3f RIGHT_VECTOR = new Vector3f(-1, 0, 0); 
  public static final Vector3f LEFT_VECTOR = new Vector3f(1, 0, 0); 
  

  private static final ThreadLocal<Quat4f> quaternion = new ThreadLocal<>();

  /**
   * Suposem que fem yaw -> pitch -> roll (en aquest ordre).
   * Yaw és al voltant de Z,
   * Pitch al voltant de -X,
   * Roll al voltant de -Y.
   * 
   * La rotació per tant l'hauríem d'agafar en ZXY i el pitch i el roll en "negatiu".
   */
  public static void quaternionToEulerAngles(Quat4f _quaternion, Tuple3f euler_) {
       
    double q0 = _quaternion.w;
    double q1 = _quaternion.x;
    double q2 = _quaternion.y;
    double q3 = _quaternion.z;

    double q03 = q0 * q3;
    double q12 = q1 * q2;
    double q33 = q3 * q3;
    double q22 = q2 * q2;
    double q23 = q2 * q3;
    double q01 = q0 * q1;
    double q02 = q0 * q2;
    double q13 = q1 * q3;
    double q11 = q1 * q1;
    
    double q00 = q0 * q0;
    
    assert Math.abs(1 - q00 + q11 + q22 + q33) < 0.0001; // que estigui normalitzat per favor

    float yaw   = (float) Math.atan2( 2 * (q03 + q12), 1 - 2 * (q33 + q11));
    float pitch = (float) Math.asin ( 2 * (q23 - q01));
    float roll  = (float) Math.atan2(-2 * (q02 + q13), 1 - 2 * (q11 + q22));

    euler_.x = yaw;
    euler_.y = pitch;
    euler_.z = roll;
  }
  
  
  /**
   * Suposem que fem yaw -> pitch -> roll (en aquest ordre).
   * Yaw és al voltant de Z,
   * Pitch al voltant de -X,
   * Roll al voltant de -Y.
   * 
   * La rotació per tant l'hauríem d'agafar en ZXY i el pitch i el roll en "negatiu".
   */
  public static void matrixToEulerAngles(Matrix3f _matrix, Tuple3f euler_) {
    Quat4f q = quaternion.get();
    q.set(_matrix);
    quaternionToEulerAngles(q, euler_);
  }
  
  
  /**
   * Suposem que fem yaw -> pitch -> roll (en aquest ordre).
   * Yaw és al voltant de Z,
   * Pitch al voltant de -X,
   * Roll al voltant de -Y.
   * 
   * La rotació per tant l'hauríem d'agafar en ZXY i el pitch i el roll en "negatiu".
   */
  public static void matrixToEulerAngles(Matrix4f _matrix, Tuple3f euler_) {
    Quat4f q = quaternion.get();
    q.set(_matrix);
    quaternionToEulerAngles(q, euler_);
  }
  
  
  /**
   * Suposem que fem yaw -> pitch -> roll (en aquest ordre).
   * Yaw és al voltant de Z,
   * Pitch al voltant de -X,
   * Roll al voltant de -Y.
   * 
   * La rotació per tant l'hauríem d'agafar en ZXY i el pitch i el roll en "negatiu".
   */
  public static void axisAngleToEulerAngles(AxisAngle4f _aa, Tuple3f euler_) {
    Quat4f q = quaternion.get();
    q.set(_aa);
    quaternionToEulerAngles(q, euler_);
  }
  

  /**
   * Suposem que fem yaw -> pitch -> roll (en aquest ordre).
   * Yaw és al voltant de Z,
   * Pitch al voltant de -X,
   * Roll al voltant de -Y.
   * 
   * La rotació per tant l'hauríem d'agafar en ZXY i el pitch i el roll en "negatiu".
   */
  public static void eulerAnglesToQuaternion(Tuple3f _euler, Quat4f quaternion_) {
    
    float yaw   = _euler.x;
    float pitch = _euler.y;
    float roll  = _euler.z;
    
    float cosY = (float) Math.cos(yaw   / 2);
    float cosP = (float) Math.cos(pitch / 2);
    float cosR = (float) Math.cos(roll  / 2);

    float sinY = (float) Math.sin(yaw   / 2);
    float sinP = (float) Math.sin(pitch / 2);
    float sinR = (float) Math.sin(roll  / 2);

    quaternion_.w = + (cosY * cosP * cosR) + (sinY * sinP * sinR);
    quaternion_.x = - (cosY * sinP * cosR) - (sinY * cosP * sinR);
    quaternion_.y = - (cosY * cosP * sinR) + (sinY * sinP * cosR);
    quaternion_.z = + (sinY * cosP * cosR) - (cosY * sinP * sinR);
  }
  
  
  /**
   * Suposem que fem yaw -> pitch -> roll (en aquest ordre).
   * Yaw és al voltant de Z,
   * Pitch al voltant de -X,
   * Roll al voltant de -Y.
   * 
   * La rotació per tant l'hauríem d'agafar en ZXY i el pitch i el roll en "negatiu".
   */
  public static void eulerAnglesToMatrix(Tuple3f _euler, Matrix3f matrix_) {
    Quat4f q = quaternion.get();
    quaternionToEulerAngles(q, _euler);
    matrix_.set(q);
  }
  
  
  /**
   * Suposem que fem yaw -> pitch -> roll (en aquest ordre).
   * Yaw és al voltant de Z,
   * Pitch al voltant de -X,
   * Roll al voltant de -Y.
   * 
   * La rotació per tant l'hauríem d'agafar en ZXY i el pitch i el roll en "negatiu".
   */
  public static void eulerAnglesToMatrix(Tuple3f _euler, Matrix4f matrix_) {
    Quat4f q = quaternion.get();
    quaternionToEulerAngles(q, _euler);
    matrix_.setRotation(q);
  }
  
  
  /**
   * Suposem que fem yaw -> pitch -> roll (en aquest ordre).
   * Yaw és al voltant de Z,
   * Pitch al voltant de -X,
   * Roll al voltant de -Y.
   * 
   * La rotació per tant l'hauríem d'agafar en ZXY i el pitch i el roll en "negatiu".
   */
  public static void eulerAnglesToAxisAngle(Tuple3f _euler, AxisAngle4f aa_) {
    Quat4f q = quaternion.get();
    quaternionToEulerAngles(q, _euler);
    aa_.set(q);
  }
}
