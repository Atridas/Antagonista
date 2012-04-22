package cat.atridas.antagonista.core;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.vecmath.Tuple3f;

import cat.atridas.antagonista.physics.PhysicShape;
import cat.atridas.antagonista.physics.PhysicsWorld;

/**
 * Interface that must be implemented to create a factory of all physic related
 * objects.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.5
 * 
 */
public interface PhysicsFactory {
  /**
   * Instantiates a new physic world.
   * 
   * @return a new PhysicsWorld.
   * @since 0.5
   */
  PhysicsWorld createPhysicsWorld();

  /**
   * Creates an arbitrary shape made of triangles.
   * 
   * @param numTriangles
   *          number of triangles.
   * @param indexs
   *          array of indexes.
   * 
   * @param numVertices
   *          number of vertices.
   * @param vertexs
   *          vertex buffer.
   * @return a shape made of the passed triangles.
   * @since 0.5
   */
  PhysicShape createIndexedMesh(int numTriangles, ShortBuffer indexs,
      int numVertices, FloatBuffer vertexs);

  /**
   * Creates an axis-aligned bounding box. If the parameters are invalid the
   * result is not defined.
   * 
   * @param minBB
   *          minimum values of each 3 coordinates.
   * @param maxBB
   *          maximum values of each 3 coordinates.
   * @return a bounding box shape.
   * @since 0.5
   */
  PhysicShape createBoundingBox(Tuple3f minBB, Tuple3f maxBB);
}
