package cat.atridas.antagonista.core;

import java.nio.ByteBuffer;

import javax.vecmath.Tuple3f;

import cat.atridas.antagonista.physics.PhysicShape;
import cat.atridas.antagonista.physics.PhysicsWorld;

public interface PhysicsFactory {
	PhysicsWorld createPhysicsWorld();
	
	
	PhysicShape createIndexedMesh(int numTriangles, int indexStride, ByteBuffer indexs, IndexType indexType, int numVertices, int vertexStride, ByteBuffer vertexs);
	PhysicShape createBoundingBox(Tuple3f minBB, Tuple3f maxBB);
	
	
	public enum IndexType {
		SHORT;
	}
}
