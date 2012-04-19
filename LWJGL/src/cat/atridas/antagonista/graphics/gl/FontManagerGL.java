package cat.atridas.antagonista.graphics.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.HashSet;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Font;
import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.Texture;

/**
 * Implementation for the desktop OpenGL of the font manager.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * 
 */
public abstract class FontManagerGL extends FontManager {

  /**
   * Map that contains the references from VAO identifiers to needed rendering
   * information.
   * 
   * @since 0.1
   */
  private final HashMap<Integer, CachedTextInfo> cachedBuffers = new HashMap<>();

  /**
   * Set of free VAO identifiers usefull to render new texts.
   * 
   * @since 0.1
   */
  private final HashSet<Integer> freeCachedBuffers = new HashSet<>();

  /**
   * Technique pass used to render text.
   * 
   * @since 0.1
   */
  private TechniquePass pass;

  /**
   * Matrix buffer to store the MVP transformation.
   * 
   * @since 0.1
   */
  private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

  @Override
  protected final int printString(ByteBuffer _vertexBuffer,
      ShortBuffer _indexBuffer, Texture[] _tex, int indexLen,
      Matrix4f WVPMatrix, Color3f color, RenderManager rm) {

    int cachedBufferIndex;
    CachedTextInfo cachedTextInfo;
    if (freeCachedBuffers.size() > 0) {
      cachedBufferIndex = freeCachedBuffers.iterator().next();
      freeCachedBuffers.remove(cachedBufferIndex);
      cachedTextInfo = cachedBuffers.get(cachedBufferIndex);
    } else {
      cachedBufferIndex = createVAO();
      assert cachedBufferIndex > 0;
      cachedTextInfo = new CachedTextInfo();
      cachedBuffers.put(cachedBufferIndex, cachedTextInfo);
    }

    rm.noVertexArray();

    // Actualitzar el buffer de vertexos
    if (cachedTextInfo.vbLen < _vertexBuffer.capacity()) {
      if (cachedTextInfo.vertexBuffer == -1) {
        cachedTextInfo.vertexBuffer = glGenBuffers();
      }
      cachedTextInfo.vbLen = _vertexBuffer.capacity();
      glBindBuffer(GL_ARRAY_BUFFER, cachedTextInfo.vertexBuffer);
      glBufferData(GL_ARRAY_BUFFER, _vertexBuffer, GL_DYNAMIC_DRAW);

    } else {
      glBindBuffer(GL_ARRAY_BUFFER, cachedTextInfo.vertexBuffer);
      glBufferSubData(GL_ARRAY_BUFFER, 0, _vertexBuffer);
    }

    // Actualitzar el buffer de indexos
    if (cachedTextInfo.ibLen < _indexBuffer.capacity()) {
      if (cachedTextInfo.indexBuffer == -1) {
        cachedTextInfo.indexBuffer = glGenBuffers();
      }
      cachedTextInfo.ibLen = _indexBuffer.capacity();
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, cachedTextInfo.indexBuffer);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, _indexBuffer, GL_DYNAMIC_DRAW);

      // printBuffers(buffer1, buffer2);
    } else {
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, cachedTextInfo.indexBuffer);
      glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, _indexBuffer);
    }

    if (pass == null) {
      // shader = new ShaderObject(Font.VERTEX_SHADER,
      // Font.FRAGMENT_SHADER_1_TEX);
      pass = Core.getCore().getEffectManager().getFontPass();
    }

    cachedTextInfo.textures = new Texture[_tex.length];
    for (int i = 0; i < _tex.length; ++i) {
      cachedTextInfo.textures[i] = _tex[i];
      _tex[i].activate(i);
    }

    activateVAO(cachedBufferIndex, cachedTextInfo);

    pass.activate(rm);

    matrixBuffer.clear();
    Utils.matrixToBuffer(WVPMatrix, matrixBuffer);
    matrixBuffer.flip();

    glUniformMatrix4(pass.getModelViewProjectionUniform(), false, matrixBuffer);
    glVertexAttrib3f(TechniquePass.FONT_COLOR_ATTRIBUTE, color.x, color.y,
        color.z);

    cachedTextInfo.indexLen = indexLen;
    glDrawElements(GL_TRIANGLES, indexLen, GL_UNSIGNED_SHORT, 0);

    return cachedBufferIndex;
  }

  @Override
  protected void printString(int textID, Matrix4f WVPMatrix, Color3f color,
      RenderManager rm) {
    assert cachedBuffers.containsKey(textID);
    assert !freeCachedBuffers.contains(textID);

    CachedTextInfo cachedTextInfo = cachedBuffers.get(textID);

    for (int i = 0; i < cachedTextInfo.textures.length; ++i) {
      cachedTextInfo.textures[i].activate(i);
    }

    activateVAO(textID, cachedTextInfo);

    pass.activate(rm);

    matrixBuffer.clear();
    Utils.matrixToBuffer(WVPMatrix, matrixBuffer);
    matrixBuffer.flip();

    glUniformMatrix4(pass.getModelViewProjectionUniform(), false, matrixBuffer);
    glVertexAttrib3f(TechniquePass.FONT_COLOR_ATTRIBUTE, color.x, color.y,
        color.z);

    glDrawElements(GL_TRIANGLES, cachedTextInfo.indexLen, GL_UNSIGNED_SHORT, 0);
  }

  @Override
  protected void freeText(int textID) {
    assert cachedBuffers.containsKey(textID);
    assert !freeCachedBuffers.contains(textID);
    freeCachedBuffers.add(textID);
  }

  /**
   * Activates a VAO. If the OpenGL implementation does not support VAOs, this
   * method emulates them.
   * 
   * @param vao
   *          identifier.
   * @param cachedBuffers
   *          rendering information.
   * @since 0.1
   */
  protected abstract void activateVAO(int vao, CachedTextInfo cachedBuffers);

  /**
   * Creates a new VAO identifier. If the OpenGL implementation does not support
   * VAOs, this method emulates them.
   * 
   * @return a new VAO identifier.
   * @since 0.1
   */
  protected abstract int createVAO();

  /**
   * Binds the vertex attributes and buffers to render the text.
   * 
   * @param cachedBuffers
   *          buffer information.
   * @since 0.1
   */
  protected final void bindVertexAttribs(CachedTextInfo cachedBuffers) {

    glBindBuffer(GL_ARRAY_BUFFER, cachedBuffers.vertexBuffer);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, cachedBuffers.indexBuffer);

    glEnableVertexAttribArray(TechniquePass.FONT_POSITION_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.FONT_TEX_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.FONT_CHANNEL_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.FONT_PAGE_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.FONT_COLOR_ATTRIBUTE);

    glVertexAttribPointer(TechniquePass.FONT_POSITION_ATTRIBUTE, 2, GL_INT,
        false, Font.VERTEX_STRIDE, Font.POSITION_OFFSET);

    glVertexAttribPointer(TechniquePass.FONT_PAGE_ATTRIBUTE, 1, GL_INT, false,
        Font.VERTEX_STRIDE, Font.PAGE_OFFSET);

    glVertexAttribPointer(TechniquePass.FONT_TEX_ATTRIBUTE, 2, GL_FLOAT, false,
        Font.VERTEX_STRIDE, Font.TEXCOORDS_OFFSET);

    glVertexAttribPointer(TechniquePass.FONT_CHANNEL_ATTRIBUTE, 4, GL_BYTE,
        true, Font.VERTEX_STRIDE, Font.CHANNEL_OFFSET);
  }

  /**
   * Cached information needed to render a given text with a given font.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   * 
   */
  protected static final class CachedTextInfo {
    /**
     * Buffer identifiers.
     * 
     * @since 0.1
     */
    private int vertexBuffer = -1, indexBuffer = -1;
    /**
     * Length of each buffer.
     * 
     * @since 0.1
     */
    private int vbLen = 0, ibLen = 0;
    /**
     * Number of indexes in the index buffer to render.
     * 
     * @since 0.1
     */
    private int indexLen;
    /**
     * Textures used to render the text.
     * 
     * @since 0.1
     */
    private Texture[] textures;
  }
}
