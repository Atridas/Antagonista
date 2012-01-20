package cat.atridas.antagonista.graphics.gl2;

import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.gl.MeshGL;

public final class MeshGL2_VAO extends MeshGL {

  private int vertexArrayObject;
  
  public MeshGL2_VAO(HashedString _resourceName) {
    super(_resourceName);
  }
  
  @Override
  protected void createArrayBuffer() {
    assert !cleaned;
    int stride;
    if(animated) {
      stride = ANIMATED_MESH_STRIDE;
    } else {
      stride = STATIC_MESH_STRIDE;
    }


    vertexArrayObject = glGenVertexArrays();
    glBindVertexArray(vertexArrayObject);
    
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

    glBindVertexArray(0);

    glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.NORMAL_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.TANGENT_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.BITANGENT_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.UV_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);

    assert !Utils.hasGLErrors();
  }

  @Override
  protected void deleteArrayBuffer() {
    assert !cleaned;
    glDeleteVertexArrays(vertexArrayObject);
    assert !Utils.hasGLErrors();
  }

  protected boolean assertReadyToRender() {
    int elementArrayBufferBinding = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
    if(elementArrayBufferBinding != indexBuffer)
      return false;
    
    int vertexArrayBinding = glGetInteger(GL_VERTEX_ARRAY_BINDING);
    if(vertexArrayBinding != vertexArrayObject)
      return false;
      
    return true;
  }

  @Override
  public void preRender() {
    assert !cleaned;
    glBindVertexArray(vertexArrayObject);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    assert !Utils.hasGLErrors();
  }

  @Override
  public void render(int _submesh, int instances, RenderManager rm) {
    throw new IllegalStateException("Calling draw instanced when hardware does not support instancing");
  }

}
