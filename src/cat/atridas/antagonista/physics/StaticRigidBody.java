package cat.atridas.antagonista.physics;

/**
 * This class represents a static rigid body; a static scene part that will be never moved.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 *
 */
public class StaticRigidBody implements AntagonistRigidBody {
  private final com.bulletphysics.dynamics.RigidBody rigidBody;
  
  StaticRigidBody(com.bulletphysics.dynamics.RigidBody _rigidBody) {
    rigidBody = _rigidBody;
  }
  
  public com.bulletphysics.dynamics.RigidBody getBulletObject() {
    return rigidBody;
  }
}
