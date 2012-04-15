package cat.atridas.antagonista.physics.bullet;


import com.bulletphysics.dynamics.RigidBody;

public interface BulletRigidBody extends BulletBody {
  RigidBody getBulletObject();
}
