package cat.atridas.antagonista.graphics.gl;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.lwjgl.opengl.GLContext;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.ARBVertexArrayObject;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
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
  private static final boolean GL_ARB_vertex_array_object, GL3;

  static {
    GL3 = Core.getCore().getRenderManager().getProfile().supports(Profile.GL3);
    GL_ARB_vertex_array_object = GLContext.getCapabilities().GL_ARB_vertex_array_object;
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
      } else {
        glDisableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
        glDisableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
      }
      
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

      if(GL3) {
        GL30.glBindVertexArray(0);
      } else if(GL_ARB_vertex_array_object) {
        ARBVertexArrayObject.glBindVertexArray(0);
      }
    }
    
    return !Utils.hasGLErrors();
  }

  @Override
  protected void loadDefault() {
    LOGGER.warning("Not yet implemented, but I don't want to crash");
  }

  @Override
  protected void preRender() {

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
  protected void render(int _submesh, RenderManager rm) {
    int stride = 0;
    for(int i = 0; i < _submesh; ++i) {
      stride += numFaces[i] * 3 * Utils.SHORT_SIZE;
    }
    glDrawElements(GL_TRIANGLES, numFaces[_submesh], GL_SHORT, stride);
    
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
