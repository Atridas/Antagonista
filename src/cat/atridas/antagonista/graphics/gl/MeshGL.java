package cat.atridas.antagonista.graphics.gl;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBDrawInstanced;
import org.lwjgl.opengl.GL31;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.Mesh;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.TechniquePass;

public class MeshGL extends Mesh {
  private static Logger LOGGER = Logger.getLogger(MeshGL.class.getCanonicalName());
  
  private int   vertexBuffer, indexBuffer,  vertexArrayObject;
  
  private boolean animated;

  private static final int STATIC_MESH_STRIDE   = NUM_ELEMENTS_PER_VERTEX_STATIC_MESH   * Utils.FLOAT_SIZE;
  private static final int ANIMATED_MESH_STRIDE = NUM_ELEMENTS_PER_VERTEX_ANIMATED_MESH * Utils.FLOAT_SIZE;
  private static final boolean GL_ARB_draw_instanced, GL_ARB_vertex_array_object, GL3;

  static {
    GL3 = Core.getCore().getRenderManager().getProfile().supports(Profile.GL3);
    GL_ARB_vertex_array_object = GLContext.getCapabilities().GL_ARB_vertex_array_object;
    GL_ARB_draw_instanced      = GLContext.getCapabilities().GL_ARB_draw_instanced &&
                                 GLContext.getCapabilities().GL_ARB_uniform_buffer_object;
  } 
  

  public MeshGL(HashedString _resourceName) {
    super(_resourceName);
  }

  @Override
  protected boolean loadBuffers(ByteBuffer _vertexBuffer, ByteBuffer _faces, boolean _animated) {
    animated = _animated;

    _vertexBuffer.rewind();
    _faces.rewind();
    
    if(GL3) {
      GL30.glBindVertexArray(0);
    } else if(GL_ARB_vertex_array_object) {
      ARBVertexArrayObject.glBindVertexArray(0);
    }
    
    vertexBuffer = glGenBuffers();

    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, _vertexBuffer, GL_STATIC_DRAW);
    
    assert !Utils.hasGLErrors();
    
    indexBuffer = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, _faces, GL_STATIC_DRAW);
    
    assert !Utils.hasGLErrors();
    
    if(GL3 || GL_ARB_vertex_array_object) {
      int stride;
      if(animated) {
        stride = ANIMATED_MESH_STRIDE;
      } else {
        stride = STATIC_MESH_STRIDE;
      }


      if(GL3) {
        vertexArrayObject = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexArrayObject);
      } else {
        vertexArrayObject = ARBVertexArrayObject.glGenVertexArrays();
        ARBVertexArrayObject.glBindVertexArray(vertexArrayObject);
      }
      glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);

      
      glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, stride, 0);
      
      glEnableVertexAttribArray(TechniquePass.NORMAL_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.NORMAL_ATTRIBUTE, 3, GL_FLOAT, false, stride, 3 * Utils.FLOAT_SIZE);
      
      glEnableVertexAttribArray(TechniquePass.TANGENT_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.TANGENT_ATTRIBUTE, 3, GL_FLOAT, false, stride, 6 * Utils.FLOAT_SIZE);
      
      glEnableVertexAttribArray(TechniquePass.BITANGENT_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.BITANGENT_ATTRIBUTE, 3, GL_FLOAT, false, stride, 9 * Utils.FLOAT_SIZE);
      
      glEnableVertexAttribArray(TechniquePass.UV_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.UV_ATTRIBUTE, 2, GL_FLOAT, false, stride, 12 * Utils.FLOAT_SIZE);
      
      if(animated) {
        glEnableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
        glVertexAttribPointer(TechniquePass.BLEND_INDEX_ATTRIBUTE, 4, GL_SHORT, false, stride, 15 * Utils.FLOAT_SIZE);
        
        glEnableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
        glVertexAttribPointer(TechniquePass.BLEND_WEIGHT_ATTRIBUTE, 4, GL_FLOAT, false, stride, 15 * Utils.FLOAT_SIZE + 4 * Utils.SHORT_SIZE);
      }
      
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

      if(GL3) {
        GL30.glBindVertexArray(0);
      } else if(GL_ARB_vertex_array_object) {
        ARBVertexArrayObject.glBindVertexArray(0);
      }
      

      glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.NORMAL_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.TANGENT_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.BITANGENT_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.UV_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
    }
    
    return !Utils.hasGLErrors();
  }

  @Override
  protected void loadDefault() {
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
    
    if(GL3) {
      GL30.glBindVertexArray(0);
    } else if(GL_ARB_vertex_array_object) {
      ARBVertexArrayObject.glBindVertexArray(0);
    }
    
    vertexBuffer = glGenBuffers();

    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, _vertexBuffer, GL_STATIC_DRAW);
    
    assert !Utils.hasGLErrors();
    
    indexBuffer = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, _faces, GL_STATIC_DRAW);
    
    assert !Utils.hasGLErrors();
    
    if(GL3 || GL_ARB_vertex_array_object) {
      int stride = 14 * Utils.FLOAT_SIZE;
      


      if(GL3) {
        vertexArrayObject = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexArrayObject);
      } else {
        vertexArrayObject = ARBVertexArrayObject.glGenVertexArrays();
        ARBVertexArrayObject.glBindVertexArray(vertexArrayObject);
      }
      glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);

      
      glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, stride, 0);
      
      glEnableVertexAttribArray(TechniquePass.NORMAL_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.NORMAL_ATTRIBUTE, 3, GL_FLOAT, false, stride, 3 * Utils.FLOAT_SIZE);
      
      glEnableVertexAttribArray(TechniquePass.TANGENT_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.TANGENT_ATTRIBUTE, 3, GL_FLOAT, false, stride, 6 * Utils.FLOAT_SIZE);
      
      glEnableVertexAttribArray(TechniquePass.BITANGENT_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.BITANGENT_ATTRIBUTE, 3, GL_FLOAT, false, stride, 9 * Utils.FLOAT_SIZE);
      
      glEnableVertexAttribArray(TechniquePass.UV_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.UV_ATTRIBUTE, 2, GL_FLOAT, false, stride, 12 * Utils.FLOAT_SIZE);
      assert !Utils.hasGLErrors();
      
      //TODO
      /*
      if(animated) {
        glEnableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
        glVertexAttribPointer(TechniquePass.BLEND_INDEX_ATTRIBUTE, 4, GL_SHORT, false, stride, 15 * Utils.FLOAT_SIZE);
        
        glEnableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
        glVertexAttribPointer(TechniquePass.BLEND_WEIGHT_ATTRIBUTE, 4, GL_FLOAT, false, stride, 15 * Utils.FLOAT_SIZE + 4 * Utils.SHORT_SIZE);
      } else {
        glDisableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
        glDisableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
      }
      */
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

      if(GL3) {
        GL30.glBindVertexArray(0);
      } else if(GL_ARB_vertex_array_object) {
        ARBVertexArrayObject.glBindVertexArray(0);
      }
      

      glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.NORMAL_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.TANGENT_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.BITANGENT_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.UV_ATTRIBUTE);
    }
    
    assert !Utils.hasGLErrors();
  }

  @Override
  public void preRender() {
    assert !cleaned;

    if(GL3 || GL_ARB_vertex_array_object) {
      if(GL3) {
        GL30.glBindVertexArray(vertexArrayObject);
      } else {
        ARBVertexArrayObject.glBindVertexArray(vertexArrayObject);
      }
    } else {
      int stride;
      if(animated) {
        stride = ANIMATED_MESH_STRIDE;
      } else {
        stride = STATIC_MESH_STRIDE;
      }
      
      glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);

      
      glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, stride, 0);
      
      glEnableVertexAttribArray(TechniquePass.NORMAL_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.NORMAL_ATTRIBUTE, 3, GL_FLOAT, false, stride, 3 * Utils.FLOAT_SIZE);
      
      glEnableVertexAttribArray(TechniquePass.TANGENT_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.TANGENT_ATTRIBUTE, 3, GL_FLOAT, false, stride, 6 * Utils.FLOAT_SIZE);
      
      glEnableVertexAttribArray(TechniquePass.BITANGENT_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.BITANGENT_ATTRIBUTE, 3, GL_FLOAT, false, stride, 9 * Utils.FLOAT_SIZE);
      
      glEnableVertexAttribArray(TechniquePass.UV_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.UV_ATTRIBUTE, 2, GL_FLOAT, false, stride, 12 * Utils.FLOAT_SIZE);
      
      if(animated) {
        glEnableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
        glVertexAttribPointer(TechniquePass.BLEND_INDEX_ATTRIBUTE, 4, GL_SHORT, false, stride, 15 * Utils.FLOAT_SIZE);
        
        glEnableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
        glVertexAttribPointer(TechniquePass.BLEND_WEIGHT_ATTRIBUTE, 4, GL_FLOAT, false, stride, 15 * Utils.FLOAT_SIZE + 4 * Utils.SHORT_SIZE);
      } else {
        glDisableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
        glDisableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
      }
      

      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    }
  }
  
  @Override
  public void render(int _submesh, RenderManager rm) {
    assert !cleaned;
    int stride = 0;
    for(int i = 0; i < _submesh; ++i) {
      stride += numFaces[i] * 3 * Utils.SHORT_SIZE;
    }
    glDrawElements(GL_TRIANGLES, numFaces[_submesh] * 3, GL_UNSIGNED_SHORT, stride);

    assert !Utils.hasGLErrors();
  }
  
  @Override
  public void render(int _submesh, int _instances, RenderManager rm) {
    assert !cleaned;
    int stride = 0;
    for(int i = 0; i < _submesh; ++i) {
      stride += numFaces[i] * 3 * Utils.SHORT_SIZE;
    }
    
    if(GL3) {
      GL31.glDrawElementsInstanced(GL_TRIANGLES, numFaces[_submesh], GL_SHORT, stride, _instances);
    } else if(GL_ARB_draw_instanced) {
      ARBDrawInstanced.glDrawElementsInstancedARB(
                                  GL_TRIANGLES, numFaces[_submesh], GL_SHORT, stride, _instances);
    } else {
      throw new IllegalStateException("Calling draw instanced when hardware does not support instancing");
    }

    assert !Utils.hasGLErrors();
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    
    glDeleteBuffers(vertexBuffer);
    glDeleteBuffers(indexBuffer);
    if(GL3) {
      GL30.glDeleteVertexArrays(vertexArrayObject);
    } else if(GL_ARB_vertex_array_object){
      ARBVertexArrayObject.glDeleteVertexArrays(vertexArrayObject);
    }
    
    cleaned = true;
  }
}
