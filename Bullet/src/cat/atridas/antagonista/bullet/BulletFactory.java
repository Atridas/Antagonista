package cat.atridas.antagonista.bullet;

import java.nio.ByteBuffer;

import javax.vecmath.Tuple3f;

import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.ScalarType;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;

import cat.atridas.antagonista.core.PhysicsFactory;
import cat.atridas.antagonista.physics.PhysicShape;
import cat.atridas.antagonista.physics.PhysicsWorld;
import cat.atridas.antagonista.physics.bullet.BoundingBoxShape;
import cat.atridas.antagonista.physics.bullet.PhysicsStaticMeshCore;
import cat.atridas.antagonista.physics.bullet.PhysicsWorldBullet;

public class BulletFactory implements PhysicsFactory {

	@Override
	public PhysicsWorld createPhysicsWorld() {
		return new PhysicsWorldBullet();
	}

	@Override
	public PhysicShape createIndexedMesh(int numTriangles, int indexStride,
			ByteBuffer indexs, IndexType indexType, int numVertices,
			int vertexStride, ByteBuffer vertexs) {

      IndexedMesh indexedMesh = new IndexedMesh();
      
      indexedMesh.numTriangles = numTriangles;
      indexedMesh.triangleIndexBase = indexs;
      indexedMesh.triangleIndexStride = indexStride;
      
      indexedMesh.numVertices = numVertices;
      indexedMesh.vertexBase = vertexs;
      indexedMesh.vertexStride = vertexStride;
      
      TriangleIndexVertexArray tiva = new TriangleIndexVertexArray();
      ScalarType scalarType;
      switch(indexType) {
      default:
      case SHORT:
    	  scalarType = ScalarType.SHORT;
    	  break;
      }
      tiva.addIndexedMesh(indexedMesh, scalarType);
      
      return new PhysicsStaticMeshCore(tiva);
	}

	@Override
	public PhysicShape createBoundingBox(Tuple3f minBB, Tuple3f maxBB) {
		return new BoundingBoxShape(minBB, maxBB);
	}

}
