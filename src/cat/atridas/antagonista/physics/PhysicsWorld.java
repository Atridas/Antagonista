package cat.atridas.antagonista.physics;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.Transformation;
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
import com.bulletphysics.collision.shapes.StridingMeshInterface;
import com.bulletphysics.collision.shapes.VertexData;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DebugDrawModes;
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
    
    // wireframe drawing seems to not be implemented correctly in jBullet, so I draw it here.
    if((dynamicsWorld.getDebugDrawer().getDebugMode() & DebugDrawModes.DRAW_WIREFRAME) != 0) {
      Transform transform = new Transform();
      DebugRender dr = Core.getCore().getDebugRender();
      
      Vector3f scaling = new Vector3f();
      Vector3f[] triangle = new Vector3f[] {new Vector3f(),new Vector3f(),new Vector3f()};
  
      Point3f point1 = new Point3f();
      Point3f point2 = new Point3f();
      
      for(CollisionObject co : dynamicsWorld.getCollisionObjectArray()) {
        co.getWorldTransform(transform);
        CollisionShape cs = co.getCollisionShape();
        
        PhysicsUserInfo userInfo = (PhysicsUserInfo)co.getUserPointer();
        
        if(cs instanceof SphereShape) {
          SphereShape ss = (SphereShape) cs;
          float r = ss.getRadius();
          
          dr.addSphere(new Point3f(transform.origin), r, userInfo.color, userInfo.zTest);
        } else if(cs instanceof BvhTriangleMeshShape) {
          BvhTriangleMeshShape triangleMeshShape = (BvhTriangleMeshShape)cs;
          
          StridingMeshInterface meshInterface = triangleMeshShape.getMeshInterface();
          
          meshInterface.getScaling(scaling);
          
          for(int i = 0; i < meshInterface.getNumSubParts(); ++i) {
            VertexData vd = meshInterface.getLockedReadOnlyVertexIndexBase(i);
            for(int j = 0; j < vd.getIndexCount()/3; j++) {
              vd.getTriangle(j*3, scaling, triangle);
              point1.set(triangle[0]);
              point2.set(triangle[1]);
              dr.addLine(point1, point2, userInfo.color, userInfo.zTest);
              point1.set(triangle[0]);
              point2.set(triangle[2]);
              dr.addLine(point1, point2, userInfo.color, userInfo.zTest);
              point1.set(triangle[2]);
              point2.set(triangle[1]);
              dr.addLine(point1, point2, userInfo.color, userInfo.zTest);
            }
            
          }
        } else {
          throw new RuntimeException("Debug draw of shape " + cs.getClass().getCanonicalName() + " is not yet implemented!");
        }
      }
    }
  }
  
  public void update(DeltaTime dt) {
    dynamicsWorld.stepSimulation(dt.dt);
  }
  
  public StaticRigidBody createStaticRigidBody(PhysicShape _physicShape, PhysicsUserInfo _userInfo, Transformation _bodyTransform) {
    
    Matrix4f mat = new Matrix4f();
    _bodyTransform.getMatrix(mat);
    Transform trans = new Transform(mat);
    

    DefaultMotionState dms = new DefaultMotionState(trans);

    RigidBodyConstructionInfo rbci = new RigidBodyConstructionInfo(0, dms, _physicShape.getBulletShape());
    //RigidBodyConstructionInfo rbci = new RigidBodyConstructionInfo(0, dms, new SphereShape(5));
    RigidBody rb = new RigidBody(rbci);
    
    int collisionFlags = rb.getCollisionFlags();
    rb.setCollisionFlags(collisionFlags & CollisionFlags.STATIC_OBJECT);
    rb.setUserPointer(_userInfo);
    
    dynamicsWorld.addRigidBody(rb);
    
    return new StaticRigidBody(rb);
  }
  
  public void deleteRigidBody(AntagonistRigidBody rigidBody) {
    dynamicsWorld.removeRigidBody(rigidBody.getBulletObject());
  }
}
