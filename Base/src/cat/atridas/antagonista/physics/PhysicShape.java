package cat.atridas.antagonista.physics;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;

public interface PhysicShape {
  CollisionShape getBulletShape();
  void getFromGameToBulletVector(Vector3f out_);
}
