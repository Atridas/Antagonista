package cat.atridas.antagonista.physics;

import com.bulletphysics.dynamics.RigidBody;

public interface BulletRigidBody extends BulletBody {
  RigidBody getBulletObject();
}
