package cat.atridas.antagonista.graphics;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public interface Camera {
  float getFovY(); 
  float getZNear(); 
  float getZFar();
  
  void getCameraParams(Point3f eye_, Point3f lookat_, Vector3f up_);
}
