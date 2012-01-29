package cat.atridas.antagonista.physics;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.Conventions;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

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
  
  /**
   * Draws the physics scene to the debug drawer.
   * @since 0.2
   */
  public void debugDraw() {
    dynamicsWorld.debugDrawWorld();
    Transform transform = new Transform();
    DebugRender dr = Core.getCore().getDebugRender();
    
    for(CollisionObject co : dynamicsWorld.getCollisionObjectArray()) {
      co.getWorldTransform(transform);
      CollisionShape cs = co.getCollisionShape();
      if(cs instanceof SphereShape) {
        SphereShape ss = (SphereShape) cs;
        float r = ss.getRadius();
        
        dr.addSphere(new Point3f(transform.origin), r, new Color3f(1,0,0), false);
      } else if(cs instanceof BvhTriangleMeshShape) {
        BvhTriangleMeshShape triangleMeshShape = (BvhTriangleMeshShape)cs;

        Vector3f min = new Vector3f();
        Vector3f max = new Vector3f();
        triangleMeshShape.getLocalAabbMin(min);
        triangleMeshShape.getLocalAabbMax(max);
        
        //dr.addAABB(new Point3f(min), new Point3f(max), new Color3f(1,0,0), false);
      }
    }
  }
  
  public void update(DeltaTime dt) {
    dynamicsWorld.stepSimulation(dt.dt);
  }
  
  public StaticRigidBody createStaticRigidBody(PhysicsStaticMeshCore meshCore) {
    
    DefaultMotionState dms = new DefaultMotionState();

    RigidBodyConstructionInfo rbci = new RigidBodyConstructionInfo(0, dms, meshCore.meshShape);
    //RigidBodyConstructionInfo rbci = new RigidBodyConstructionInfo(0, dms, new SphereShape(5));
    RigidBody rb = new RigidBody(rbci);
    
    int collisionFlags = rb.getCollisionFlags();
    rb.setCollisionFlags(collisionFlags & CollisionFlags.STATIC_OBJECT);
    
    dynamicsWorld.addRigidBody(rb);
    
    return new StaticRigidBody(rb);
  }
}
