package cat.atridas.antagonista.graphics.gl3;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.gl.MeshGL;

/**
 * OpenGL 3.3 implementation of the Mesh class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public final class MeshGL3 extends MeshGL {
  /**
   * Vertex Array Object of this mesh.
   * @since 0.1
   */
  private int vertexArrayObject;

  /**
   * Builds a blank, uninitialized mesh.
   * @param _resourceName name of the material.
   * @since 0.1
   * @see MeshGL#MeshGL(HashedString)
   */
  public MeshGL3(HashedString name) {
    super(name);
  }
  
  @Override
  protected void createVertexArrayObject() {
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
      glVertexAttribIPointer(TechniquePass.BLEND_INDEX_ATTRIBUTE, 4, GL_SHORT, stride, 14 * Utils.FLOAT_SIZE);
      
      glEnableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.BLEND_WEIGHT_ATTRIBUTE, 4, GL_FLOAT, false, stride, 14 * Utils.FLOAT_SIZE + 4 * Utils.SHORT_SIZE);
    }
    
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

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
  protected void deleteVertexArrayObject() {
    assert !cleaned;
    glDeleteVertexArrays(vertexArrayObject);
    assert !Utils.hasGLErrors();
  }

  @Override
  public void preRender() {
    assert !cleaned;
    glBindVertexArray(vertexArrayObject);
    assert !Utils.hasGLErrors();
  }

  @Override
  public void render(int _submesh, int _instances, RenderManager rm) {
    assert !cleaned;
    assert _submesh < numSubMeshes;
    int stride = 0;
    for(int i = 0; i < _submesh; ++i) {
      stride += numFaces[i] * 3 * Utils.SHORT_SIZE;
    }
    
    glDrawElementsInstanced(GL_TRIANGLES, numFaces[_submesh], GL_SHORT, stride, _instances);

    assert !Utils.hasGLErrors();
  }

}
