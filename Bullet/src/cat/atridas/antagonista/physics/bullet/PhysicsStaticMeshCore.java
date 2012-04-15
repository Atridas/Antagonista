package cat.atridas.antagonista.physics.bullet;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.StridingMeshInterface;

/**
 * Class that encapsulates a Physic mesh that will be static.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 *
 */
public class PhysicsStaticMeshCore implements PhysicShapeBullet {
  private final BvhTriangleMeshShape meshShape;
  
  
  public PhysicsStaticMeshCore(StridingMeshInterface mesh) {
    meshShape = new BvhTriangleMeshShape(mesh, true);
  }


  @Override
  public BvhTriangleMeshShape getBulletShape() {
    return meshShape;
  }


  @Override
  public void getFromGameToBulletVector(Vector3f out_) {
    out_.set(0,0,0);
  }
}
