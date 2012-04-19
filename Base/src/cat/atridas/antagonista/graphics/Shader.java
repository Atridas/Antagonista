package cat.atridas.antagonista.graphics;

import java.io.InputStream;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager.Profile;

/**
 * Encapsulates a shader source.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public final class Shader extends Resource {
  /**
   * Source.
   * 
   * @since 0.1
   */
  private String source = "";
  /**
   * Shader phase.
   * 
   * @since 0.1
   */
  private final ShaderType type;

  /**
   * Builds the uninitialized shader.
   * 
   * @param _resourceName
   * @param _type
   * @since 0.1
   */
  public Shader(HashedString _resourceName, ShaderType _type) {
    super(_resourceName);
    type = _type;
  }

  /**
   * Gets the shading phase of this shader.
   * 
   * @return the shading phase.
   * @since 0.1
   */
  public ShaderType getType() {
    assert !cleaned;
    return type;
  }

  /**
   * Gets the source of this shader.
   * 
   * @return the source of this shader.
   * @since 0.1
   */
  public String getSource() {
    assert !cleaned;
    return source;
  }

  @Override
  public int getRAMBytesEstimation() {
    assert !cleaned;
    return 4 + source.getBytes().length;
  }

  @Override
  public int getVRAMBytesEstimation() {
    assert !cleaned;
    return 0;
  }

  /**
   * Enumeration of the different shader phases.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   * 
   */
  public static enum ShaderType {
    /**
     * Vertex Shader.
     * 
     * @since 0.1
     */
    VERTEX,
    /**
     * Tesselation Control Shader. Only available at OpenGL 4.0 or later.
     * 
     * @since 0.1
     */
    TESS_CONTROL,
    /**
     * Tesselation Evaluation Shader. Only available at OpenGL 4.0 or later.
     * 
     * @since 0.1
     */
    TESS_EVALUATION,
    /**
     * Geometry Shader. Only available at OpenGL 3.0 or later.
     * 
     * @since 0.1
     */
    GEOMETRY,
    /**
     * Fragment Shader.
     * 
     * @since 0.1
     */
    FRAGMENT
  }

  @Override
  public boolean load(InputStream is, HashedString extension) {
    assert !cleaned;

    source = Utils.readInputStream(is);

    return true;
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    source = null;
    cleaned = true;
  }

  /**
   * Loads the default, hardcoded, source.
   * 
   * @since 0.1
   */
  void loadDefault() {
    switch (type) {
    case VERTEX: // TODO
      source = "attribute vec3 a_v3Position;\n" +

      "void main()\n" + "{\n" + "  gl_Position = vec4(a_v3Position,1.0);\n"
          + "}\n";
      break;
    case FRAGMENT:
      if (Utils.supports(Profile.GL3)) {
        source = "#version 150\n" +

        "out vec4 f_v4Color;\n" +

        "void main(void)\n" + "{\n" + "  f_v4Color = vec4(1,1,1,1);\n" + "}\n";
      } else {
        source = "void main(void)\n" + "{\n"
            + "  gl_FragColor = vec4(1,1,1,1);\n" + "}\n";
      }
      break;
    case GEOMETRY: // TODO
      source = "#version 150\n" +

      "layout(triangles) in;\n"
          + "layout(triangle_strip, max_vertices = 3) out;\n" +

          "void main() {\n" + "  for(int i = 0; i < gl_in.length(); i++) {\n"
          + "    gl_Position = gl_in[i].gl_Position;\n" + "    EmitVertex();\n"
          + "  }\n" + "  EndPrimitive();\n" + "};\n";
      break;
    case TESS_EVALUATION: // TODO
      source = "void main(void)\n"
          + "{\n"
          + "  gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;\n"
          + "}\n";
      break;
    case TESS_CONTROL: // TODO
      source = "void main(void)\n"
          + "{\n"
          + "  gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;\n"
          + "}\n";
      break;
    default:
      throw new IllegalStateException();
    }

  }
}
