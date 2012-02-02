package cat.atridas.antagonista.physics;

import com.bulletphysics.dynamics.RigidBody;

/**
 * This class represents a static rigid body; a static scene part that will be never moved.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 *
 */
public class StaticRigidBody implements BulletRigidBody {
  private final RigidBody rigidBody;
  
  StaticRigidBody(RigidBody _rigidBody) {
    rigidBody = _rigidBody;
  }
  
  public RigidBody getBulletObject() {
    return rigidBody;
  }
}
