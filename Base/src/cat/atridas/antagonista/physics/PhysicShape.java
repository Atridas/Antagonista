package cat.atridas.antagonista.physics;

import javax.vecmath.Vector3f;

public interface PhysicShape {
  Object getBulletShape();
  void getFromGameToBulletVector(Vector3f out_);
}
