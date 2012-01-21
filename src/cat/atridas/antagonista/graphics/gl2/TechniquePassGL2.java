package cat.atridas.antagonista.graphics.gl2;

import static org.lwjgl.opengl.GL20.*;

import java.util.logging.Logger;

import org.w3c.dom.Element;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.Shader.ShaderType;
import cat.atridas.antagonista.graphics.gl.TechniquePassGL;

public class TechniquePassGL2 extends TechniquePassGL {
  private static Logger LOGGER = Logger.getLogger(TechniquePassGL2.class.getCanonicalName());

  private int modelViewProjectionUniform, modelViewUniform, modelViewITUniform;//, bonesUniform;
  private int specialColor0, specialColor1, specialColor2, specialColor3;
  private int ambientUniform, directionalDirUniform, directionalColorUniform;
  private int specularFactorUniform, specularGlossinessUniform, heightUniform;
  
  public TechniquePassGL2(Element pass) throws AntagonistException {
    super(pass);
  }

  public TechniquePassGL2() {}
  
  public TechniquePassGL2(boolean fontPass) {
    super(fontPass);
  }

  @Override
  protected int generateShaderObject(ShaderType st, RenderManager rm) {
    switch(st) {
    case VERTEX:
      return glCreateShader(GL_VERTEX_SHADER);
    case FRAGMENT:
      return glCreateShader(GL_FRAGMENT_SHADER);
    case GEOMETRY:
      LOGGER.severe("Geometry shaders need OpenGL 3.2 or greater.");
      throw new IllegalStateException();
    case TESS_CONTROL:
      LOGGER.severe("Tesselation shaders need OpenGL 4.1 or greater.");
      throw new IllegalStateException();
    case TESS_EVALUATION:
      LOGGER.severe("Tesselation shaders need OpenGL 4.1 or greater.");
      throw new IllegalStateException();
    default:
      throw new IllegalArgumentException(st.toString());
    }
  }
  
  @Override
  protected void bindAttributes(int program) {
    if(position) {
      glBindAttribLocation(program, POSITION_ATTRIBUTE, POSITION_ATTRIBUTE_NAME);
    }
    if(normal) {
      glBindAttribLocation(program, NORMAL_ATTRIBUTE, NORMAL_ATTRIBUTE_NAME);
    }
    if(tangents) {
      glBindAttribLocation(program, TANGENT_ATTRIBUTE, TANGENT_ATTRIBUTE_NAME);
      glBindAttribLocation(program, BITANGENT_ATTRIBUTE, BITANGENT_ATTRIBUTE_NAME);
    }
    if(uv) {
      glBindAttribLocation(program, UV_ATTRIBUTE, UV_ATTRIBUTE_NAME);
    }
    if(bones) {
      glBindAttribLocation(program, BLEND_INDEX_ATTRIBUTE, BLEND_INDEX_ATTRIBUTE_NAME);
      glBindAttribLocation(program, BLEND_WEIGHT_ATTRIBUTE, BLEND_WEIGHT_ATTRIBUTE_NAME);
    }
    if(fontTechnique) {
      glBindAttribLocation(program, FONT_POSITION_ATTRIBUTE, FONT_POSITION_ATTRIBUTE_NAME);
      glBindAttribLocation(program, FONT_TEX_ATTRIBUTE, FONT_TEX_ATTRIBUTE_NAME);
      glBindAttribLocation(program, FONT_CHANNEL_ATTRIBUTE, FONT_CHANNEL_ATTRIBUTE_NAME);
      glBindAttribLocation(program, FONT_PAGE_ATTRIBUTE, FONT_PAGE_ATTRIBUTE_NAME);
    }
    if(colorAttr) {
      glBindAttribLocation(program, COLOR_ATTRIBUTE, COLOR_ATTRIBUTE_NAME);
    }
    assert !Utils.hasGLErrors();
  }

  @Override
  protected void loadBasicInstanceUniforms(int program)
      throws AntagonistException {
    modelViewProjectionUniform = glGetUniformLocation(program, MODEL_VIEW_PROJECTION_UNIFORM);
    modelViewUniform = glGetUniformLocation(program, MODEL_VIEW_UNIFORM);
    modelViewITUniform = glGetUniformLocation(program, MODEL_VIEW_IT_UNIFORM);
    if(modelViewProjectionUniform < 0) {
      LOGGER.severe("Basic instance uniforms requested but ModelViewProjection matrix not active!");
      //throw new AntagonistException();
    }
    if(modelViewUniform < 0) {
      LOGGER.severe("Basic instance uniforms requested but ModelView matrix not active!");
      //throw new AntagonistException();
    }
    assert !Utils.hasGLErrors();
  }
  
  @Override
  protected void loadSpecialColorsUniforms(int program)
      throws AntagonistException {
    specialColor0 = glGetUniformLocation(program, SPECIAL_COLOR_0_UNIFORM);
    specialColor1 = glGetUniformLocation(program, SPECIAL_COLOR_1_UNIFORM);
    specialColor2 = glGetUniformLocation(program, SPECIAL_COLOR_2_UNIFORM);
    specialColor3 = glGetUniformLocation(program, SPECIAL_COLOR_3_UNIFORM);

    if(specialColor0 < 0) {
      LOGGER.severe("Special colors uniforms requested but SpecialColor0 is not active!");
      throw new AntagonistException();
    }
    
  }

  @Override
  protected void loadBasicLightUniforms(int program) throws AntagonistException {
    ambientUniform = glGetUniformLocation(program, AMBIENT_LIGHT_UNIFORM);
    directionalDirUniform = glGetUniformLocation(program, DIRECTIONAL_LIGHT_DIR_UNIFORM);
    directionalColorUniform = glGetUniformLocation(program, DIRECTIONAL_LIGHT_COLOR_UNIFORMS);
    if(ambientUniform < 0) {
      LOGGER.severe("Basic light uniforms requested but ambient uniform not active!");
      throw new AntagonistException();
    }
    if(directionalDirUniform < 0) {
      LOGGER.severe("Basic light uniforms requested but directional dir uniform not active!");
      throw new AntagonistException();
    }
    if(directionalColorUniform < 0) {
      LOGGER.severe("Basic light uniforms requested but directional color uniform not active!");
      throw new AntagonistException();
    }
    assert !Utils.hasGLErrors();
  }

  @Override
  protected void loadBasicMaterialUniforms(int program)
      throws AntagonistException {
    specularFactorUniform     = glGetUniformLocation(program, SPECULAR_FACTOR_UNIFORM);
    specularGlossinessUniform = glGetUniformLocation(program, SPECULAR_GLOSS_UNIFORM);
    heightUniform             = glGetUniformLocation(program, HEIGHT_UNIFORM);
    if(specularFactorUniform < 0) {
      LOGGER.severe("Basic material uniforms requested but specular factor not active!");
      throw new AntagonistException();
    }
    if(specularGlossinessUniform < 0) {
      LOGGER.severe("Basic material uniforms requested but specular glossiness uniform not active!");
      throw new AntagonistException();
    }
  }

  @Override
  protected void loadFontUniforms(int program)
      throws AntagonistException {
    modelViewProjectionUniform   = glGetUniformLocation(program, FONT_WVP_MATRIX_UNIFORM);
    if(modelViewProjectionUniform < 0) {
      LOGGER.severe("Font uniforms requested but worldviewprojection uniform not active!");
      throw new AntagonistException();
    }
  }
  
  @Override
  public int getSpecularFactorUniform() {
    return specularFactorUniform;
  }
  @Override
  public int getSpecularGlossinessUniform() {
    return specularGlossinessUniform;
  }
  @Override
  public int getHeightUniform() {
    return heightUniform;
  }

  @Override
  public int getAmbientLightColorUniform() {
    return ambientUniform;
  }
  @Override
  public int getDirectionalLightDirectionUniform() {
    return directionalDirUniform;
  }
  @Override
  public int getDirectionalLightColorUniform() {
    return directionalColorUniform;
  }
  

  @Override
  public int getModelViewProjectionUniform() {
    return modelViewProjectionUniform;
  }
  @Override
  public int getModelViewUniform() {
    return modelViewUniform;
  }
  @Override
  public int getModelViewITUniform() {
    return modelViewITUniform;
  }
  @Override
  public int getSpecialColor0Uniform() {
    return specialColor0;
  }

  @Override
  public int getSpecialColor1Uniform() {
    return specialColor1;
  }
  @Override
  public int getSpecialColor2Uniform() {
    return specialColor2;
  }
  @Override
  public int getSpecialColor3Uniform() {
    return specialColor3;
  }

  @Override
  protected long getMaxUniformBufferSize() {
    return 0; //throw new IllegalStateException("Uniform Buffers not supported with " + Utils.getProfile());
  }
  
  private static int textVertexShaderID = -1,
                     textFragmentShaderID = -1;
  
  @Override
  protected int getFontShader(ShaderType shaderType) {
    switch(shaderType) {
    case VERTEX:
      if(textVertexShaderID < 0) {
        textVertexShaderID = generateShaderObject(shaderType, Core.getCore().getRenderManager());
        compileShader(textVertexShaderID, textVertexShader);
      }
      return textVertexShaderID;
    case FRAGMENT:
      if(textFragmentShaderID < 0) {
        textFragmentShaderID = generateShaderObject(shaderType, Core.getCore().getRenderManager());
        compileShader(textFragmentShaderID, textFragmentShader);
      }
      return textFragmentShaderID;
    default:
      return 0;
    }
  }
  
  public static final String textVertexShader = 
      "attribute vec2   a_position;\n" +
      "attribute vec2   a_texCoord;\n" +
      "attribute vec4   a_channel;\n" +
      "attribute float  a_page;\n" +
      "attribute vec3   a_color;\n" +
    
    
      "varying vec4  v_channel;\n" +
      "varying vec4  v_page;\n" +
      "varying vec3  v_color;\n" +
      "varying vec2  v_texCoord;\n" +
    
      "uniform mat4 u_WorldViewProj;\n" +
    
      "void main() {  gl_Position = u_WorldViewProj * vec4(a_position, 0, 1);\n" +
        "v_texCoord  = a_texCoord;\n" +
        "v_channel   = a_channel;\n" +
        "if(a_page < 0.5)\n" +
        "  v_page = vec4(1,0,0,0);\n" +
        "else if(a_page < 0.5)\n" +
        "  v_page = vec4(0,1,0,0);\n" +
        "else if(a_page < 0.5)\n" +
        "  v_page = vec4(0,0,1,0);\n" +
        "else\n" +
        "  v_page = vec4(0,0,0,1);\n" +
        "v_color     = a_color;\n" +
      "}\n";
  
  public static final String textFragmentShader = 
      "varying vec2  v_texCoord;\n" +
      "varying vec4  v_channel;\n" +
      "varying vec3  v_color;\n" +
      "varying vec4  v_page;\n" +

      "uniform sampler2D u_page0;\n" +
      "uniform sampler2D u_page1;\n" +
      "uniform sampler2D u_page2;\n" +
      "uniform sampler2D u_page3;\n" +
    
      "void main() {  \n" +
        "vec4 pixel0 = texture2D(u_page0, v_texCoord);\n" +
        "vec4 pixel1 = texture2D(u_page1, v_texCoord);\n" +
        "vec4 pixel2 = texture2D(u_page2, v_texCoord);\n" +
        "vec4 pixel3 = texture2D(u_page3, v_texCoord);\n" +
        "vec4 pixel = pixel0 * v_page.x + pixel1 * v_page.y + pixel2 * v_page.z + pixel3 * v_page.w;\n" +
        "float divisor = dot(v_channel, vec4(1.0,1.0,1.0,1.0));\n" +
        "float val;\n" +
        "if(divisor < 0.1)\n" +
        "{\n" +
        "  val = pixel.x;\n" +
        "} else {\n" +
        "  val = dot(v_channel, pixel) / divisor;\n" +
        "}\n" +
        "gl_FragColor = vec4(v_color,val);\n" +
      "}\n";
}
