package cat.atridas.antagonista.graphics.gl;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.Mesh;
import cat.atridas.antagonista.graphics.RenderManager;

/**
 * Desktop OpenGL implementation of the Mesh class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public abstract class MeshGL extends Mesh {
  private static Logger LOGGER = Logger.getLogger(MeshGL.class.getCanonicalName());
  
  /**
   * OpenGL Buffer identifiers.
   * @since 0.1
   */
  protected int   vertexBuffer, indexBuffer;
  
  /**
   * Saves if this mesh is animated.
   * @since 0.1
   */
  protected boolean animated;

  /**
   * 
   * Constructs an uninitialized mesh.
   * 
   * @param _resourceName name of the mesh.
   * @since 0.1
   * @see Mesh#Mesh(HashedString)
   */
  public MeshGL(HashedString _resourceName) {
    super(_resourceName);
  }

  
  @Override
  protected final boolean loadBuffers(ByteBuffer _vertexBuffer, ByteBuffer _faces, boolean _animated) {
    animated = _animated;

    _vertexBuffer.rewind();
    _faces.rewind();
    
    vertexBuffer = glGenBuffers();
    
    Core.getCore().getRenderManager().noVertexArray();

    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, _vertexBuffer, GL_STATIC_DRAW);
    
    assert !Utils.hasGLErrors();
    
    indexBuffer = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, _faces, GL_STATIC_DRAW);
    
    assert !Utils.hasGLErrors();
    
    createVertexArrayObject();
    
    return !Utils.hasGLErrors();
  }

  /**
   * Creates a new vertex array object for this mesh, if the OpenGL implementation supports this
   * capability.
   * @since 0.1 
   */
  protected abstract void createVertexArrayObject();
  /**
   * Deletes the vertex array object for this mesh, if it exist.
   * @since 0.1 
   */
  protected abstract void deleteVertexArrayObject();

  @Override
  protected final void loadDefault() {
    LOGGER.config("Loading default mesh [GL]");
    animated = false;
    
    

    float vtx[] = {
        -.5f, -.5f, +.5f,             //pos
          -.57735f, -.57735f, +.57735f, //norm
          1,0,0,     //tan
          0,0,1,     //bitan
          0.f, 0.f,                   //uv

        +.5f, -.5f, +.5f, //pos
          +.57735f, -.57735f, +.57735f, //norm
          1,0,0,     //tan
          0,0,1,     //bitan
          0.f, 1.f,        //uv
                 
        +.5f, -.5f, -.5f, //pos
          +.57735f, -.57735f, -.57735f, //norm
          1,0,0,     //tan
          0,0,1,     //bitan
          1.f, 1.f,       //uv
                  
        -.5f, -.5f, -.5f, //pos
          -.57735f, -.57735f, -.57735f, //norm
          1,0,0,     //tan
          0,0,1,     //bitan
          1.f, 0.f,       //uv
                 
        -.5f, +.5f, +.5f, //pos
          //-.57735f, +.57735f, +.57735f, //norm
          -0.f, +1.f, +0.f, //norm
          1,0,0,     //tan
          0,0,1,     //bitan
          1.f, 0.f,       //uv
                  
        +.5f, +.5f, +.5f, //pos
          //+.57735f, +.57735f, +.57735f, //norm
          -0.f, +1.f, +0.f, //norm
          1,0,0,     //tan
          0,0,1,     //bitan
          1.f, 1.f,       //uv
                  
        +.5f, +.5f, -.5f, //pos
          //+.57735f, +.57735f, -.57735f, //norm
          -0.f, +1.f, +0.f, //norm
          1,0,0,     //tan
          0,0,1,     //bitan
          0.f, 1.f,       //uv
                  
        -.5f, +.5f, -.5f, //pos
          //-.57735f, +.57735f, -.57735f, //norm
          -0.f, +1.f, +0.f, //norm
          1,0,0,     //tan
          0,0,1,     //bitan
          0.f, 0.f,       //uv
                  
    };


    
    short idx[] = {
      0,2,1,
      0,3,2,
      
      0,1,5,
      0,5,4,
      
      1,2,5,
      5,2,6,
      
      2,3,6,
      6,3,7,
      
      3,0,4,
      3,4,7,
      
      4,5,6,
      4,6,7,
    };
    

    ByteBuffer _vertexBuffer = BufferUtils.createByteBuffer(vtx.length * Utils.FLOAT_SIZE);
    ByteBuffer _faces        = BufferUtils.createByteBuffer(idx.length * Utils.SHORT_SIZE);
    
    _vertexBuffer.asFloatBuffer().put(vtx);
    _faces.asShortBuffer().put(idx);
    
    _vertexBuffer.rewind();
    _faces.rewind();
    
    numSubMeshes = 1;
    numFaces     = new int[1];
    numFaces[0]  = idx.length/3;
    materials    = new Material[1];
    materials[0] = Core.getCore().getMaterialManager().getDefaultResource();
    
    boolean result = loadBuffers(_vertexBuffer, _faces, animated);
    
    assert result;
  }
  
  @Override
  public final void render(int _submesh, RenderManager rm) {
    assert !cleaned;
    assert _submesh < numSubMeshes;
    int stride = 0;
    for(int i = 0; i < _submesh; ++i) {
      stride += numFaces[i] * 3 * Utils.SHORT_SIZE;
    }
    assert assertReadyToRender();
    glDrawElements(GL_TRIANGLES, numFaces[_submesh] * 3, GL_UNSIGNED_SHORT, stride);

    assert !Utils.hasGLErrors();
  }

  /**
   * Returns if this mesh is ready to be rendered.
   * 
   * @return if this mesh is ready to be rendered.
   * @since 0.1
   */
  protected boolean assertReadyToRender() {
    return true;
  }

  @Override
  public final void cleanUp() {
    assert !cleaned;
    
    glDeleteBuffers(vertexBuffer);
    glDeleteBuffers(indexBuffer);
    
    deleteVertexArrayObject();
    cleaned = true;
  }
}
