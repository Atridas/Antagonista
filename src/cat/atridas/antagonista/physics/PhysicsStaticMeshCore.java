package cat.atridas.antagonista.physics;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.StridingMeshInterface;

/**
 * Class that encapsulates a Physic mesh that will be static.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 *
 */
public class PhysicsStaticMeshCore {
  final BvhTriangleMeshShape meshShape;
  
  
  public PhysicsStaticMeshCore(StridingMeshInterface mesh) {
    meshShape = new BvhTriangleMeshShape(mesh, true);
  }
  
}
