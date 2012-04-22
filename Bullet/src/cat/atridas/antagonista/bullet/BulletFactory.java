package cat.atridas.antagonista.bullet;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.vecmath.Tuple3f;

import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.ScalarType;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.BufferFactory;
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
  public PhysicShape createIndexedMesh(int numTriangles, ShortBuffer indexs,
      int numVertices, FloatBuffer vertexs) {

    IndexedMesh indexedMesh = new IndexedMesh();

    indexedMesh.numTriangles = numTriangles;

    short[] idxs = new short[indexs.limit() - indexs.position()];
    indexs.get(idxs);
    indexedMesh.triangleIndexBase = BufferFactory.createByteBuffer(idxs.length
        * Utils.SHORT_SIZE);
    indexedMesh.triangleIndexBase.asShortBuffer().put(idxs);
    indexedMesh.triangleIndexBase.position(0);
    indexedMesh.triangleIndexBase.limit(idxs.length * Utils.SHORT_SIZE);

    indexedMesh.triangleIndexStride = 3 * Utils.SHORT_SIZE;

    indexedMesh.numVertices = numVertices;
    float[] vtxs = new float[vertexs.limit() - vertexs.position()];
    vertexs.get(vtxs);
    indexedMesh.vertexBase = BufferFactory.createByteBuffer(vtxs.length
        * Utils.FLOAT_SIZE);
    indexedMesh.vertexBase.asFloatBuffer().put(vtxs);
    indexedMesh.vertexBase.position(0);
    indexedMesh.vertexBase.limit(vtxs.length * Utils.FLOAT_SIZE);
    indexedMesh.vertexStride = 3 * Utils.FLOAT_SIZE;

    TriangleIndexVertexArray tiva = new TriangleIndexVertexArray();
    tiva.addIndexedMesh(indexedMesh, ScalarType.SHORT);

    return new PhysicsStaticMeshCore(tiva);
  }

  @Override
  public PhysicShape createBoundingBox(Tuple3f minBB, Tuple3f maxBB) {
    return new BoundingBoxShape(minBB, maxBB);
  }

}
