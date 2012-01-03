package cat.atridas.antagonista.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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

  
  private int numVerts, numSubMeshes;
  private ArrayList<Material> materials;
  
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
    
    numVerts = Integer.parseInt(lines[2]);
    ByteBuffer vertexBuffer = BufferUtils.createByteBuffer(numVerts * NUM_ELEMENTS_PER_VERTEX_STATIC_MESH * Float.SIZE / 8);
    
    assert lines.length >= firstVertexLine + numVerts;
    
    for(int i = 0; i < numVerts; ++i) {
      String elements[] = lines[i + firstVertexLine].split(" ");
      assert elements.length == NUM_ELEMENTS_PER_VERTEX_STATIC_MESH;
      
      for(int j = 0; j < NUM_ELEMENTS_PER_VERTEX_STATIC_MESH; ++j) {
        float f = Float.parseFloat(elements[j]);
        vertexBuffer.putFloat(f);
      }
      
    }
    
    final int firstMaterialsLine = firstVertexLine + numVerts + 1;
    
    numSubMeshes = Integer.parseInt(lines[firstMaterialsLine]);
    
                          materials = new ArrayList<>(numSubMeshes);
    ArrayList<ByteBuffer> submeshes = new ArrayList<>(numSubMeshes);
    
    int aux = firstMaterialsLine + 1;
    
    MaterialManager mm = Core.getCore().getMaterialManager();
    for(int i = 0; i < numSubMeshes; ++i) {
      Material material = mm.getResource( new HashedString( lines[aux] ) );
      int numFaces = Integer.parseInt(lines[aux + 1]);
      
      ByteBuffer submesh = BufferUtils.createByteBuffer(numFaces * 3 * Integer.SIZE / 8);
      
      materials.add(material);
      submeshes.add(submesh );
      
      aux += 2;
      for(int face = 0; face < numFaces; face++) {
        String[] indexes = lines[aux].split(" ");
        assert indexes.length == 3;
        for(int j = 0; j < 3; ++j) {
          int index = Integer.parseInt(indexes[j]);
          submesh.putInt(index);
        }
      }
      aux += numFaces;
    }
    
    return loadBuffers(vertexBuffer, submeshes, false); //TODO animats
  }

  private boolean loadBinary(InputStream is) {
    throw new IllegalStateException("Not yet implemented");
  }
  
  protected abstract boolean loadBuffers(ByteBuffer vertexBuffer, ArrayList<ByteBuffer> submeshes, boolean animated);
  protected abstract void loadDefault();
  
  @Override
  public int getRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getVRAMBytesEstimation() {
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
