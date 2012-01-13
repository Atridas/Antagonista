package cat.atridas.antagonista.graphics;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class RTSCamera implements Camera {
  
  private float fovY = 30, zNear = 0.5f, zFar = 40;
  private final Point3f  eye    = new Point3f(),
                         lookat = new Point3f(0,0,0);
  private final Vector3f up     = new Vector3f(0,0,1);

  private Vector3f rightDisplacement = new Vector3f();
  private Vector3f upDisplacement = new Vector3f();
  
  /**
   * Angles "esfèrics". Yaw a 0 vol dir que mirem cp a les +Y, a 90 cap a -X.
   * El pitch indica a 0 que mirem paralelament al terra i a 90 directament al terra.
   * MAI el pitch ha de ser 0 o 90 exactament
   */
  private float yaw = 0,
                pitch = 80;


  private float minPitch = 15,
                maxPitch = 85;
  
  /**
   * Distància (en metres) des del centre de la vista a la càmera.
   */
  private float distance = 5;


  private float minDistance = 2,
                maxDistance = 5;

  private final Vector3f v3Aux1 = new Vector3f();
  private final AxisAngle4f aaAux1 = new AxisAngle4f();
  private final Matrix3f m3Aux1 = new Matrix3f();
  {
    updateVariables();
  }
  
  public void setMinDistance(float _minDistance) {
    minDistance = _minDistance;
  }
  
  public void setMaxDistance(float _maxDistance) {
    maxDistance = _maxDistance;
  }
  
  public float getMinDistance() {
    return minDistance;
  }
  
  public float getMaxDistance() {
    return maxDistance;
  }
  
  public void setYaw(float _yaw) {
    yaw = _yaw;
    updateVariables();
  }
  
  public void setPitch(float _pitch) {
    pitch = _pitch;
    updateVariables();
  }
  
  public void setDistance(float _distance) {
    distance = _distance;
    updateVariables();
  }
  
  public void addYaw(float _yaw) {
    float nyaw = yaw + _yaw;
    while(nyaw > 360) {
      nyaw -= 360;
    }
    while(nyaw < 0) {
      nyaw += 360;
    }
    setYaw(nyaw);
  }
  
  public void addPitch(float _pitch) {
    float npitch = pitch + _pitch;
    if(npitch > maxPitch) {
      npitch = maxPitch;
    }
    if(npitch < minPitch) {
      npitch = minPitch;
    }
    setPitch(npitch);
  }
  
  public void addDistance(float _distance) {
    float ndistance = distance + _distance;
    if(ndistance > maxDistance) {
      ndistance = maxDistance;
    }
    if(ndistance < minDistance) {
      ndistance = minDistance;
    }
    setDistance(ndistance);
  }

  
  public void moveRight(float distance) {
    assert rightDisplacement.z == 0;
    
    v3Aux1.scale(distance, rightDisplacement);
    lookat.add(v3Aux1);
    eye.add(v3Aux1);
  }
  
  public void moveUp(float distance) {
    assert upDisplacement.z == 0;
    
    v3Aux1.scale(distance, upDisplacement);
    lookat.add(v3Aux1);
    eye.add(v3Aux1);
  }
  
  public void setZLookAt(float z) {
    float dist = z - lookat.z;
    lookat.z = z;
    eye.z += dist;
  }
  
  public void addZLookAt(float z) {
    lookat.z += z;
    eye.z += z;
  }
  
  
  private void updateVariables() {
    assert pitch > 0 && pitch < 90;
    assert pitch >= minPitch && pitch <= maxPitch;
    
    Vector3f v3CenterToEye = v3Aux1;
    v3CenterToEye.set(0, -1, 0);
    
    aaAux1.set(-1,0,0, pitch * (float) Math.PI / 180);
    m3Aux1.set(aaAux1);
    m3Aux1.transform(v3CenterToEye);
    
    aaAux1.set(0, 0, 1, yaw * (float) Math.PI / 180);
    m3Aux1.set(aaAux1);
    m3Aux1.transform(v3CenterToEye);
    
    eye.scaleAdd(distance, v3CenterToEye, lookat);
    

    upDisplacement.set(0,1,0);
    m3Aux1.transform(upDisplacement);
    rightDisplacement.set(1,0,0);
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
