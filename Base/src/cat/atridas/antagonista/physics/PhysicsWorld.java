package cat.atridas.antagonista.physics;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.Clock.DeltaTime;

/**
 * Interface with the jBullet Physics engine. Manages the Rigid body simulation
 * and the collision detection.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 * 
 */
public interface PhysicsWorld {

  /**
   * Draws the physics scene to the debug drawer.
   * 
   * @since 0.2
   */
  void debugDraw();

  void update(DeltaTime dt);

  PhysicsUserInfo raycast(Point3f origin, Point3f destiny, Point3f point_,
      Vector3f normal_);
}
