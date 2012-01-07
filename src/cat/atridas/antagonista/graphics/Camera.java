package cat.atridas.antagonista.graphics;

import javax.vecmath.Vector3f;

public interface Camera {
  float getFovY(); 
  float getZNear(); 
  float getZFar();
  
  void getCameraParams(Vector3f eye_, Vector3f lookat_, Vector3f up_);
}
