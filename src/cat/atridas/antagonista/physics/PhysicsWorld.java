package cat.atridas.antagonista.physics;

import javax.vecmath.Vector3f;

import cat.atridas.antagonista.Conventions;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;

/**
 * Interface with the jBullet Physics engine. Manages the Rigid body simulation and the collision
 * detection.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 *
 */
public class PhysicsWorld {
  private BroadphaseInterface broadphase = new DbvtBroadphase();
  private CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
  private Dispatcher collisionDispatcher = new CollisionDispatcher(collisionConfiguration);
  
  private ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
  
  private DynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld(
                                                  collisionDispatcher,
                                                  broadphase,
                                                  constraintSolver,
                                                  collisionConfiguration);
  {
    Vector3f gravity = new Vector3f(Conventions.DOWN_VECTOR);
    gravity.scale(9.8f);
    dynamicsWorld.setGravity( gravity );
    
    dynamicsWorld.setDebugDrawer(new PhysicsDebugDrawer());
  }
  
  public void debugDraw() {
    dynamicsWorld.debugDrawWorld();
  }
}
