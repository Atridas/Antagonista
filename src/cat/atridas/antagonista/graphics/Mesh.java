package cat.atridas.antagonista.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;

/**
 * Class that encapsulates an indexed vertex array to be rendered.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public abstract class Mesh extends Resource {
  private static Logger LOGGER = Logger.getLogger(Mesh.class.getCanonicalName());

  /**
   * Number of (float) elements in a vertex not animated.
   * @since 0.1
   */
  public static final int NUM_ELEMENTS_PER_VERTEX_STATIC_MESH = 14;
  /**
   * Number of elements in a vertex animated. Short indices count as half element.
   * @since 0.1
   */
  public static final int NUM_ELEMENTS_PER_VERTEX_ANIMATED_MESH = 14 + (2 + 4); //4 indexos (shorts) + 4 pesos 

  /**
   * Stride in bytes from one vertex to the next.
   * @since 0.1
   */
  protected static final int STATIC_MESH_STRIDE   = NUM_ELEMENTS_PER_VERTEX_STATIC_MESH   * Utils.FLOAT_SIZE,
                             ANIMATED_MESH_STRIDE = NUM_ELEMENTS_PER_VERTEX_ANIMATED_MESH * Utils.FLOAT_SIZE;
  
  /**
   * "mesh"
   * @since 0.1
   */
  private static final HashedString HS_MESH = new HashedString("mesh");
  
  /**
   * Number of vertexs in the vertex array.
   * @since 0.1
   */
  protected int numVerts;
  /**
   * Number of submeshes this mesh has. Each submesh has an index buffer and a different material.
   * @since 0.1
   */
  protected int numSubMeshes;
  /**
   * Number of incides for each submesh.
   * @since 0.1
   */
  protected int numFaces[];
  /**
   * Material for each submesh.
   * @since 0.1
   */
  protected Material materials[];
  
  /**
   * Constructs an uninitialized mesh.
   * 
   * @param _resourceName name of the mesh.
   * @since 0.1
   * @see Resource#Resource(HashedString)
   */
  public Mesh(HashedString _resourceName) {
    super(_resourceName);
  }

  @Override
  public boolean load(InputStream is, HashedString extension) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading mesh " + resourceName);
    
    assert HS_MESH.equals(extension);

    try {
      MeshFileTypes mft = Utils.readHeader(is, FILE_TYPES, MeshFileTypes.ERROR);
      
      switch(mft) {
      case TEXT:
        return loadText(is);
      case BINARY:
        return loadBinary(is);
      case ERROR:
      default:
        LOGGER.warning("Unrecognized header");
        return false;
      }
      
    } catch (IOException e) {
      LOGGER.warning(Utils.logExceptionStringAndStack(e));
      return false;
    }
  }
  
  /**
   * Loads the mesh file in a text format.
   * 
   * @param is
   * @return
   * @since 0.1
   */
  private boolean loadText(InputStream is) {
    final int firstVertexLine = 3;
    String str = Utils.readInputStream(is);
    String[] lines = str.split("\n");
    
    assert lines.length >= 7;
    
    String[] vertsParams = lines[2].split(" ");
    
    numVerts = Integer.parseInt(vertsParams[0]);
    boolean animated = Boolean.parseBoolean(vertsParams[1]);
    assert !animated;//TODO animats
    
    float[] vtxs = new float[ numVerts * NUM_ELEMENTS_PER_VERTEX_STATIC_MESH ];
    
    assert lines.length >= firstVertexLine + numVerts;
    
    for(int i = 0; i < numVerts; ++i) {
      String elements[] = lines[i + firstVertexLine].split(" ");
      assert elements.length == NUM_ELEMENTS_PER_VERTEX_STATIC_MESH;
      
      for(int j = 0; j < NUM_ELEMENTS_PER_VERTEX_STATIC_MESH; ++j) {
        float f = Float.parseFloat(elements[j]);
        //vertexBuffer.putFloat(f);
        vtxs[i * NUM_ELEMENTS_PER_VERTEX_STATIC_MESH + j] = f;
      }
      
    }
    
    final int firstMaterialsLine = firstVertexLine + numVerts + 1;
    
    numSubMeshes = Integer.parseInt(lines[firstMaterialsLine]);
    
                          materials = new Material[numSubMeshes];
                          numFaces  = new int[numSubMeshes];
                          
    int aux = firstMaterialsLine + 1;
    
    MaterialManager mm = Core.getCore().getMaterialManager();
    int totalNumFaces = 0;
    for(int i = 0; i < numSubMeshes; ++i) {
      Material material = mm.getResource( new HashedString( lines[aux] ) );
      numFaces[i] = Integer.parseInt(lines[aux + 1]);
      totalNumFaces += numFaces[i];

      materials[i] = material;
      aux += 2;
    }
    
    short idxs[] = new short[totalNumFaces * 3];
    
    int faceIndex = 0;
    for(int i = 0; i < numSubMeshes; ++i) {

      for(int face = 0; face < numFaces[i]; face++) {
        String[] indexes = lines[aux + face].split(" ");
        assert indexes.length == 3;
        //for(int j = 0; j < 3; ++j) {
        //  short index = Short.parseShort(indexes[j]);
        //  faces.putShort(index);
        //}
        idxs[faceIndex * 3 + 0] = Short.parseShort(indexes[0]);
        idxs[faceIndex * 3 + 1] = Short.parseShort(indexes[1]);
        idxs[faceIndex * 3 + 2] = Short.parseShort(indexes[2]);
        faceIndex++;
      }
      aux += numFaces[i];
    }
    
    ByteBuffer vertexBuffer = BufferUtils.createByteBuffer(numVerts * NUM_ELEMENTS_PER_VERTEX_STATIC_MESH * Utils.FLOAT_SIZE);
    ByteBuffer faces = BufferUtils.createByteBuffer(totalNumFaces * 3 * Utils.SHORT_SIZE);

    vertexBuffer.asFloatBuffer().put(vtxs);
    faces.asShortBuffer().put(idxs);

    vertexBuffer.position(0);
    vertexBuffer.limit(numVerts * NUM_ELEMENTS_PER_VERTEX_STATIC_MESH * Utils.FLOAT_SIZE);
    faces.position(0);
    faces.limit(totalNumFaces * 3 * Utils.SHORT_SIZE);
    
    return loadBuffers(vertexBuffer, faces, animated); 
  }

  /**
   * Loads the mesh file in a binary format.
   * 
   * @param is
   * @return
   * @since 0.1
   */
  private boolean loadBinary(InputStream is) {
    throw new IllegalStateException("Not yet implemented");
  }
  
  /**
   * Creates the OpenGL buffers. This method must create the ARRAY_BUFFER, the ELEMENT_ARRAY_BUFFER
   * and the vertex array object if possible (OpenGL 3.0 and greater).
   * 
   * @param vertexBuffer vertex buffer.
   * @param faces index buffer.
   * @param animated indicates if the mesh is animated or not.
   * @return success of the method.
   * @since 0.1
   */
  protected abstract boolean loadBuffers(ByteBuffer vertexBuffer, ByteBuffer faces, boolean animated);
  /**
   * Loads a default mesh.
   * @since 0.1
   */
  protected abstract void loadDefault();

  /**
   * Returns the number of submeshes. Each submesh has a different material.
   * 
   * @return the number of submeshes.
   * @since 0.1
   */
  public final int getNumSubmeshes() {
    assert !cleaned;
    return numSubMeshes;
  }
  
  /**
   * Gets the material of a specific submesh.
   * 
   * @param _submesh identifier.
   * @return the material fetched.
   * @throws ArrayIndexOutOfBoundsException if the <code>_submesh</code> parameter is not a valid
   * index.
   * @since 0.1
   */
  public final Material getMaterial(int _submesh) {
    assert !cleaned;
    assert _submesh < numSubMeshes;
    return materials[_submesh];
  }
  
  /**
   * Initializes the buffer states to render the mesh.
   * @since 0.1
   */
  public abstract void preRender();
  /**
   * Renders a single instance of the mesh.
   * 
   * @param _submesh submesh to render.
   * @param rm RenderManager.
   * @since 0.1
   */
  public abstract void render(int _submesh, RenderManager rm);
  /**
   * Renders a number of instances of this mesh.
   * 
   * @param _submesh submesh to render.
   * @param instances number of instances to render.
   * @param rm RenderManager.
   * @since 0.1
   */
  public abstract void render(int _submesh, int instances, RenderManager rm); 
  
  @Override
  public final int getRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public final int getVRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * Header of a text file. "antagonist text"
   * @since 0.1
   */
  public static final byte[] TEXT_HEADER = "antagonist text".getBytes();
  /**
   * Header of a binary file. "antagonist binary"
   * @since 0.1
   */
  public static final byte[] BINARY_HEADER = "antagonist binary".getBytes();
  
  /**
   * Map header -> file type
   */
  public static final Map<byte[], MeshFileTypes> FILE_TYPES;
  static {
    Map<byte[], MeshFileTypes> fileTypes = new HashMap<>();
    fileTypes.put(TEXT_HEADER, MeshFileTypes.TEXT);
    fileTypes.put(BINARY_HEADER, MeshFileTypes.BINARY);
    
    FILE_TYPES = Collections.unmodifiableMap(fileTypes);
  }
  
  /**
   * Different file types.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   *
   */
  private static enum MeshFileTypes {
    TEXT, BINARY, ERROR
  }
}
