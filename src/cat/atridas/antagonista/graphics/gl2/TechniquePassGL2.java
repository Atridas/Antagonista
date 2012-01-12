package cat.atridas.antagonista.graphics.gl2;

import static org.lwjgl.opengl.GL20.*;

import java.util.logging.Logger;

import org.w3c.dom.Element;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.Shader.ShaderType;
import cat.atridas.antagonista.graphics.gl.TechniquePassGL;

public class TechniquePassGL2 extends TechniquePassGL {
  private static Logger LOGGER = Logger.getLogger(TechniquePassGL2.class.getCanonicalName());

  
  private int modelViewProjectionUniform, modelViewUniform, modelViewITUniform;//, bonesUniform;
  private int ambientUniform, directionalDirUniform, directionalColorUniform;
  private int specularFactorUniform, specularGlossinessUniform, heightUniform;
  
  public TechniquePassGL2(Element pass) throws AntagonistException {
    super(pass);
  }
  
  public TechniquePassGL2() {}

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
    assert !Utils.hasGLErrors();
  }

  @Override
  protected void loadBasicInstanceUniforms(int program)
      throws AntagonistException {
    modelViewProjectionUniform = glGetUniformLocation(program, MODEL_VIEW_PROJECTION_UNIFORMS);
    modelViewUniform = glGetUniformLocation(program, MODEL_VIEW_UNIFORMS);
    modelViewITUniform = glGetUniformLocation(program, MODEL_VIEW_IT_UNIFORMS);
    if(modelViewProjectionUniform < 0) {
      LOGGER.severe("Basic instance uniforms requested but ModelViewProjection matrix not active!");
      throw new AntagonistException();
    }
    if(modelViewUniform < 0) {
      LOGGER.severe("Basic instance uniforms requested but ModelView matrix not active!");
      throw new AntagonistException();
    }
    assert !Utils.hasGLErrors();
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
  protected long getMaxUniformBufferSize() {
    return 0; //throw new IllegalStateException("Uniform Buffers not supported with " + Utils.getProfile());
  }

}
