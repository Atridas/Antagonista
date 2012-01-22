package cat.atridas.antagonista;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

/**
 * <p>
 * This class encapsulates information about the front, up, left and right vectors in the engine.
 * Use the vectors here created and never change them. Also, whenever you need to compute the Euler
 * angles, use the functions in this class so everything works consistently.
 * </p>
 * <p>
 * We will do a Yaw -> Pitch -> Roll rotation (in that order) and Yaw will be arround the
 * Z vector (a positive Yaw means turning to the left), Pitch arround the -X vector (a 
 * positive angle means to turn your head up) and Roll arround the -Y angle (a positive 
 * Roll means to do a barrell roll to your right).
 * </p>
 * <p>
 * In the Blender exporter we should take the ZXY rotation and negate both Pitch and Roll.
 * </p>
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @version 1.1 22/1/2012
 * @since 0.1
 *
 */
public class Conventions {

  /**
   * Front vector, taken from Blender (0,-1,0).
   * @since 0.1
   */
  public static final Vector3f FRONT_VECTOR = new Vector3f(0, -1, 0);
  /**
   * Up vector, taken from Blender (0,0,1).
   * @since 0.1
   */
  public static final Vector3f UP_VECTOR = new Vector3f(0, 0, 1);
  /**
   * Right vector, taken from Blender (-1,0,0).
   * @since 0.1
   */
  public static final Vector3f RIGHT_VECTOR = new Vector3f(-1, 0, 0); 
  /**
   * Left vector, taken from Blender (1,0,0).
   * @since 0.1
   */
  public static final Vector3f LEFT_VECTOR = new Vector3f(1, 0, 0); 
  

  private static final ThreadLocal<Quat4f> quaternion = new ThreadLocal<>();

  
  /**
   * Transforms a Quaternion into Euler Angles.
   * 
   * @since 0.1
   * @param _quaternion Rotation entered as a unit quaternion.
   * @param euler_ returns the angles in Yaw (x) Pitch (y) Roll (z) convention.
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
   * Transform a rotation matrix into Euler Angles.
   * 
   * @since 0.1
   * @param _matrix Rotation Matrix.
   * @param euler_ returns the angles in Yaw (x) Pitch (y) Roll (z) convention.
   */
  public static void matrixToEulerAngles(Matrix3f _matrix, Tuple3f euler_) {
    Quat4f q = quaternion.get();
    q.set(_matrix);
    quaternionToEulerAngles(q, euler_);
  }
  

  /**
   * Transform a rotation matrix into Euler Angles.
   * 
   * @since 0.1
   * @param _matrix Rotation Matrix.
   * @param euler_ returns the angles in Yaw (x) Pitch (y) Roll (z) convention.
   */
  public static void matrixToEulerAngles(Matrix4f _matrix, Tuple3f euler_) {
    Quat4f q = quaternion.get();
    q.set(_matrix);
    quaternionToEulerAngles(q, euler_);
  }
  

  /**
   * Transform an axis angle rotation into Euler Angles.
   * 
   * @since 0.1
   * @param _aa Axis angle rotation.
   * @param euler_ returns the angles in Yaw (x) Pitch (y) Roll (z) convention.
   */
  public static void axisAngleToEulerAngles(AxisAngle4f _aa, Tuple3f euler_) {
    Quat4f q = quaternion.get();
    q.set(_aa);
    quaternionToEulerAngles(q, euler_);
  }
  

  /**
   * Transforms an Euler rotation to Quaternion anotation.
   * 
   * @since 0.1
   * @param _euler Euler rotation to translate, in Yaw (x) Pitch (y) Roll (z) convention.
   * @param quaternion_ output.
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
   * Transforms an Euler rotation to Matrix anotation.
   * 
   * @since 0.1
   * @param _euler Euler rotation to translate, in Yaw (x) Pitch (y) Roll (z) convention.
   * @param matrix_ output.
   */
  public static void eulerAnglesToMatrix(Tuple3f _euler, Matrix3f matrix_) {
    Quat4f q = quaternion.get();
    quaternionToEulerAngles(q, _euler);
    matrix_.set(q);
  }
  

  /**
   * Transforms an Euler rotation to Matrix anotation.
   * 
   * @since 0.1
   * @param _euler Euler rotation to translate, in Yaw (x) Pitch (y) Roll (z) convention.
   * @param matrix_ output.
   */
  public static void eulerAnglesToMatrix(Tuple3f _euler, Matrix4f matrix_) {
    Quat4f q = quaternion.get();
    quaternionToEulerAngles(q, _euler);
    matrix_.setRotation(q);
  }
  

  /**
   * Transforms an Euler rotation to Axis angle anotation.
   * 
   * @since 0.1
   * @param _euler Euler rotation to translate, in Yaw (x) Pitch (y) Roll (z) convention.
   * @param aa_ output.
   */
  public static void eulerAnglesToAxisAngle(Tuple3f _euler, AxisAngle4f aa_) {
    Quat4f q = quaternion.get();
    quaternionToEulerAngles(q, _euler);
    aa_.set(q);
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
