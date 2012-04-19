package cat.atridas.antagonista.graphics.gl2;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.gl.MeshGL;

/**
 * OpenGL 2.1 implementation of the Mesh class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 * 
 */
public class MeshGL2 extends MeshGL {

  /**
   * Builds a blank, uninitialized mesh.
   * 
   * @param _resourceName
   *          name of the material.
   * @since 0.1
   * @see MeshGL#MeshGL(HashedString)
   */
  public MeshGL2(HashedString _resourceName) {
    super(_resourceName);
  }

  @Override
  protected void createVertexArrayObject() {
    // --

    if (animated) {
      assert glGetInteger(GL_MAX_VERTEX_ATTRIBS) >= 6;
    } else {
      assert glGetInteger(GL_MAX_VERTEX_ATTRIBS) >= 4;
    }
  }

  @Override
  protected void deleteVertexArrayObject() {
    // --
  }

  @Override
  public void preRender() {
    int stride;
    if (animated) {
      stride = ANIMATED_MESH_STRIDE;
    } else {
      stride = STATIC_MESH_STRIDE;
    }

    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);

    glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false,
        stride, 0);

    glEnableVertexAttribArray(TechniquePass.NORMAL_ATTRIBUTE);
    glVertexAttribPointer(TechniquePass.NORMAL_ATTRIBUTE, 3, GL_FLOAT, false,
        stride, 3 * Utils.FLOAT_SIZE);

    glEnableVertexAttribArray(TechniquePass.TANGENT_ATTRIBUTE);
    glVertexAttribPointer(TechniquePass.TANGENT_ATTRIBUTE, 3, GL_FLOAT, false,
        stride, 6 * Utils.FLOAT_SIZE);

    glEnableVertexAttribArray(TechniquePass.BITANGENT_ATTRIBUTE);
    glVertexAttribPointer(TechniquePass.BITANGENT_ATTRIBUTE, 3, GL_FLOAT,
        false, stride, 9 * Utils.FLOAT_SIZE);

    glEnableVertexAttribArray(TechniquePass.UV_ATTRIBUTE);
    glVertexAttribPointer(TechniquePass.UV_ATTRIBUTE, 2, GL_FLOAT, false,
        stride, 12 * Utils.FLOAT_SIZE);

    if (animated) {
      glEnableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.BLEND_INDEX_ATTRIBUTE, 4, GL_SHORT,
          false, stride, 14 * Utils.FLOAT_SIZE);

      glEnableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
      glVertexAttribPointer(TechniquePass.BLEND_WEIGHT_ATTRIBUTE, 4, GL_FLOAT,
          false, stride, 14 * Utils.FLOAT_SIZE + 4 * Utils.SHORT_SIZE);
    } else {
      glDisableVertexAttribArray(TechniquePass.BLEND_INDEX_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.BLEND_WEIGHT_ATTRIBUTE);
    }

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
  }

  @Override
  public void render(int _submesh, int instances, RenderManager rm) {
    throw new IllegalStateException(
        "Calling draw instanced when hardware does not support instancing");
  }

}
