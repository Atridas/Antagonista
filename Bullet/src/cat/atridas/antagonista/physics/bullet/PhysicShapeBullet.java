package cat.atridas.antagonista.physics.bullet;

import javax.vecmath.Vector3f;

import cat.atridas.antagonista.physics.PhysicShape;

import com.bulletphysics.collision.shapes.CollisionShape;

public interface PhysicShapeBullet extends PhysicShape {
  CollisionShape getBulletShape();

  void getFromGameToBulletVector(Vector3f out_);
}
