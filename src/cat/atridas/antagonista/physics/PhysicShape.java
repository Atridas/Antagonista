package cat.atridas.antagonista.physics;

import com.bulletphysics.collision.shapes.CollisionShape;

public interface PhysicShape {
  CollisionShape getBulletShape();
}
