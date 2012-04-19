package cat.atridas.antagonista.graphics.gl3;

import static org.lwjgl.opengl.GL15.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.gl.RenderManagerGL;

/**
 * OpenGL 3.3 implementation of the SceneData class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 * 
 */
public class SceneDataGL3 extends SceneData {

  /**
   * Auxiliar buffer used to pass global scene data information to the OpenGL
   * driver.
   * 
   * @since 0.1
   */
  private ByteBuffer bb = BufferUtils.createByteBuffer(4 * 3 * Float.SIZE);
  /**
   * Global scene data information buffer OpenGL identifier.
   * 
   * @since 0.1
   */
  private int bufferId = -1;

  /**
   * Default constructor.
   * 
   * @param _rm
   *          Render Manager reference.
   * @since 0.1
   * @see SceneData#SceneData(RenderManager)
   */
  public SceneDataGL3(RenderManagerGL _rm) {
    super(_rm);
  }

  @Override
  public void setUniforms() {
    if (bufferId < 0) {
      bufferId = glGenBuffers();
      glBindBuffer(GL_UNIFORM_BUFFER, bufferId);
      glBufferData(GL_UNIFORM_BUFFER, 12 * Utils.FLOAT_SIZE, GL_DYNAMIC_DRAW);
    }

    bb.clear();
    bb.putFloat(ambientLightColor.x);
    bb.putFloat(ambientLightColor.y);
    bb.putFloat(ambientLightColor.z);
    bb.putFloat(1); // fill

    bb.putFloat(directionalLightDirection.x);
    bb.putFloat(directionalLightDirection.y);
    bb.putFloat(directionalLightDirection.z);
    bb.putFloat(1); // fill

    bb.putFloat(directionalLightColor.x);
    bb.putFloat(directionalLightColor.y);
    bb.putFloat(directionalLightColor.z);
    bb.putFloat(1); // fill
    bb.flip();

    glBindBuffer(GL_UNIFORM_BUFFER, bufferId);
    glBufferSubData(GL_UNIFORM_BUFFER, 0, bb);

    glBindBufferRange(GL_UNIFORM_BUFFER,
        TechniquePass.BASIC_LIGHT_UNIFORMS_BINDING, bufferId, 0,
        4 * 3 * Float.SIZE);
    assert !Utils.hasGLErrors();
  }

  @Override
  public void setUniforms(TechniquePass pass) {
    // --
  }

}
