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

public abstract class Mesh extends Resource {
  private static Logger LOGGER = Logger.getLogger(Mesh.class.getCanonicalName());

  public static final int NUM_ELEMENTS_PER_VERTEX_STATIC_MESH = 14;
  public static final int NUM_ELEMENTS_PER_VERTEX_ANIMATED_MESH = 14 + (2 + 4); //4 indexos (shorts) + 4 pesos 

  
  protected int numVerts, numSubMeshes, numFaces[];
  protected Material materials[];
  
  protected Mesh(HashedString _resourceName) {
    super(_resourceName);
  }

  @Override
  public boolean load(InputStream is, String extension) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading mesh " + resourceName);
    
    assert "mesh".compareToIgnoreCase(extension) == 0;

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
    
    return loadBuffers(vertexBuffer, faces, animated); 
  }

  private boolean loadBinary(InputStream is) {
    throw new IllegalStateException("Not yet implemented");
  }
  
  protected abstract boolean loadBuffers(ByteBuffer vertexBuffer, ByteBuffer faces, boolean animated);
  protected abstract void loadDefault();

  
  public final int getNumSubmeshes() {
    assert !cleaned;
    return numSubMeshes;
  }
  
  public final Material getMaterial(int _submesh) {
    assert !cleaned;
    assert _submesh < numSubMeshes;
    return materials[_submesh];
  }
  
  public abstract void preRender(); 
  public abstract void render(int _submesh, RenderManager rm); 
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

  
  public static final byte[] TEXT_HEADER = "antagonist text".getBytes();
  public static final byte[] BINARY_HEADER = "antagonist binary".getBytes();
  
  public static final Map<byte[], MeshFileTypes> FILE_TYPES;
  static {
    Map<byte[], MeshFileTypes> fileTypes = new HashMap<>();
    fileTypes.put(TEXT_HEADER, MeshFileTypes.TEXT);
    fileTypes.put(BINARY_HEADER, MeshFileTypes.BINARY);
    
    FILE_TYPES = Collections.unmodifiableMap(fileTypes);
  }
  
  private static enum MeshFileTypes {
    TEXT, BINARY, ERROR
  }
}
