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
import com.bulletphysics.collision.broadphase.CollisionFilterGroups;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.dispatch.GhostPairCallback;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CapsuleShapeZ;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.collision.shapes.CylinderShapeZ;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StridingMeshInterface;
import com.bulletphysics.collision.shapes.VertexData;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.character.KinematicCharacterController;
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
  
  private DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld(
                                                          collisionDispatcher,
                                                          broadphase,
                                                          constraintSolver,
                                                          collisionConfiguration);
  
  {
    Vector3f gravity = new Vector3f(Conventions.DOWN_VECTOR);
    gravity.scale(9.8f);

    broadphase.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
    
    dynamicsWorld.setGravity( gravity );
    
    dynamicsWorld.setDebugDrawer(new PhysicsDebugDrawer());
  }
  
  /**
   * Draws the physics scene to the debug drawer.
   * @since 0.2
   */
  @SuppressWarnings("unused")
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
      Point3f point3 = new Point3f();

      Vector3f vector1 = new Vector3f();
      Vector3f vector2 = new Vector3f();

      Matrix4f matrix1 = new Matrix4f();
      
      Transform transform1 = new Transform();
      
      float[] faux1 = new float[1];
      
      for(CollisionObject co : dynamicsWorld.getCollisionObjectArray()) {
        co.getWorldTransform(transform);
        CollisionShape cs = co.getCollisionShape();
        
        PhysicsUserInfo userInfo = (PhysicsUserInfo)co.getUserPointer();
        
        if(cs instanceof SphereShape) {
          SphereShape ss = (SphereShape) cs;
          float r = ss.getRadius();
          
          point1.set(transform.origin);
          
          dr.addSphere(point1, r, userInfo.color, userInfo.zTest);
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
              point3.set(triangle[2]);
              dr.addTriangle(point1, point2, point3, userInfo.color, userInfo.zTest);
            }
            
          }
        } else if(cs instanceof CapsuleShapeZ) {
          //TODO
          CapsuleShapeZ cShape = (CapsuleShapeZ) cs;
          
          
          float height = cShape.getHalfHeight();
          float radius = cShape.getRadius();

          transform.getMatrix(matrix1);
          
          point1.set(radius*2, radius*2, (height + radius)*2);

          dr.addOBB(matrix1, point1, userInfo.color, userInfo.zTest);

          
          //point1.set(.5f,.5f,2);
          //transform.getMatrix(matrix1);
          //dr.addOBB(matrix1, point1, new Color3f(0,0,1), false);
          
          
          /*
          transform1.origin.set(0,0,0);
          transform1.basis.setIdentity();
          
          cShape.getAabb(transform, vector1, vector2);

          //transform.transform(vector1);
          //transform.transform(vector2);
          
          point1.set(vector1);
          point2.set(vector2);
          
          dr.addAABB(point1, point2, userInfo.color, userInfo.zTest);
          
          point1.set(.5f,.5f,2);
          transform.getMatrix(matrix1);
          dr.addOBB(matrix1, point1, new Color3f(0,1,1), userInfo.zTest);
          */
          
          /*
          cShape.getBoundingSphere(vector1, faux1);
          transform.transform(vector1);
          point1.set(vector1);
          dr.addSphere(point1, faux1[0], userInfo.color, userInfo.zTest);
          */
        } else if(cs instanceof CylinderShapeZ) {
          //TODO
          CylinderShapeZ cShape = (CylinderShapeZ) cs;
          
          cShape.getHalfExtentsWithoutMargin(vector1);

          transform.getMatrix(matrix1);
          
          point1.set(vector1);
          point1.x = point1.y = point1.x * 2;
          

          dr.addOBB(matrix1, point1, userInfo.color, userInfo.zTest);
          
          //point2.set(.5f,.5f,2);
          //dr.addOBB(matrix1, point2, new Color3f(1, 1, 1), false);
      } else if(cs instanceof BoxShape) {
        //TODO
        BoxShape box = (BoxShape) cs;
        
        box.getHalfExtentsWithoutMargin(vector1);

        transform.getMatrix(matrix1);
        
        point1.set(vector1);
        point1.scale(2);
        

        dr.addOBB(matrix1, point1, userInfo.color, userInfo.zTest);
        
        //point2.set(.5f,.5f,2);
        //dr.addOBB(matrix1, point2, new Color3f(1, 1, 1), false);
    } else {
          throw new RuntimeException("Debug draw of shape " + cs.getClass().getCanonicalName() + " is not yet implemented!");
        }
      }
    }
  }
  
  public void update(DeltaTime dt) {
    dynamicsWorld.stepSimulation(dt.dt);
  }
  
  public StaticRigidBody createStaticRigidBody(PhysicShape _physicShape, Vector3f fromGameToBulletOffset, PhysicsUserInfo _userInfo, Transformation _bodyTransform) {
    
    Matrix4f mat = new Matrix4f();
    _bodyTransform.getMatrix(mat);
    Transform trans = new Transform(mat);
    

    DefaultMotionState dms = new DefaultMotionState(trans);
    dms.centerOfMassOffset.origin.set(fromGameToBulletOffset);
    dms.centerOfMassOffset.origin.scale(-1);

    RigidBodyConstructionInfo rbci = new RigidBodyConstructionInfo(0, dms, _physicShape.getBulletShape());
    //RigidBodyConstructionInfo rbci = new RigidBodyConstructionInfo(0, dms, new SphereShape(5));
    RigidBody rb = new RigidBody(rbci);
    
    int collisionFlags = rb.getCollisionFlags();
    rb.setCollisionFlags(collisionFlags & CollisionFlags.STATIC_OBJECT);
    rb.setUserPointer(_userInfo);
    
    //dynamicsWorld.addRigidBody(rb);
    dynamicsWorld.addRigidBody(rb, (short)CollisionFilterGroups.STATIC_FILTER, (short)(CollisionFilterGroups.ALL_FILTER ^ CollisionFilterGroups.STATIC_FILTER));
    
    return new StaticRigidBody(rb);
  }
  
  public KinematicCharacter createKinematicCharacter(
      float characterWidth,
      float characterHeight,
      Transformation _bodyTransform,
      float stepHeight,
      PhysicsUserInfo _userInfo
      ) {

    Matrix4f mat = new Matrix4f();
    _bodyTransform.getMatrix(mat);
    Transform trans = new Transform(mat);

    PairCachingGhostObject ghostObject = new PairCachingGhostObject();
    ghostObject.setWorldTransform(trans);
    //TODO sweepBP.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
    //float characterHeight = 1.75f * characterScale;
    //float characterWidth = 1.75f * characterScale;
    
    ConvexShape capsule = new CapsuleShapeZ(characterWidth * .5f, characterHeight * .5f + characterWidth);
    //ConvexShape capsule = new CylinderShapeZ(new Vector3f(characterWidth/2, 0, characterHeight));
    ghostObject.setCollisionShape(capsule);
    ghostObject.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
    ghostObject.setUserPointer(_userInfo);

    //float stepHeight = 0.35f * characterScale;
    KinematicCharacterController character = new KinematicCharacterController(ghostObject, capsule, stepHeight);

    character.setUpAxis(2);
    //TODO new BspToBulletConverter().convertBsp(getClass().getResourceAsStream("/com/bulletphysics/demos/bsp/exported.bsp.txt"));

    dynamicsWorld.addCollisionObject(ghostObject, CollisionFilterGroups.CHARACTER_FILTER, (short)(CollisionFilterGroups.STATIC_FILTER | CollisionFilterGroups.DEFAULT_FILTER | CollisionFilterGroups.CHARACTER_FILTER));
    //dynamicsWorld.addCollisionObject(ghostObject);

    dynamicsWorld.addAction(character);
    
    return new KinematicCharacter(character, ghostObject);
  }
  
  public void deleteKinematicCharacter(KinematicCharacter kinematicCharacter) {
    dynamicsWorld.removeAction(kinematicCharacter.getBulletObject());
    dynamicsWorld.removeCollisionObject(kinematicCharacter.getGhostObject());
  }
  
  public void deleteRigidBody(BulletRigidBody rigidBody) {

    dynamicsWorld.removeRigidBody(rigidBody.getBulletObject());
    
  }
  

  private Vector3f raycastOrigin  = new Vector3f();
  private Vector3f raycastDestiny = new Vector3f();
  //ClosestRayResultCallback raycastCRRC = new ClosestRayResultCallback(raycastOrigin, raycastDestiny);
  public PhysicsUserInfo raycast(Point3f origin, Point3f destiny, Point3f point_, Vector3f normal_) {

    raycastOrigin.set(origin);
    raycastDestiny.set(destiny);

    ClosestRayResultCallback raycastCRRC = new ClosestRayResultCallback(raycastOrigin, raycastDestiny);
    //raycastCRRC.rayFromWorld.set(origin);
    //raycastCRRC.rayToWorld.set(destiny);
    
    dynamicsWorld.rayTest(raycastOrigin, raycastDestiny, raycastCRRC);
    
    point_.set(raycastCRRC.hitPointWorld);
    normal_.normalize(raycastCRRC.hitNormalWorld);
    
    if(raycastCRRC.collisionObject != null)
      return (PhysicsUserInfo) raycastCRRC.collisionObject.getUserPointer();
    else
      return null;
  }
}
