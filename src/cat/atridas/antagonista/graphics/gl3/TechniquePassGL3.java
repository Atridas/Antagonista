package cat.atridas.antagonista.graphics.gl3;

import java.util.logging.Logger;

import org.w3c.dom.Element;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.Shader.ShaderType;
import cat.atridas.antagonista.graphics.gl.TechniquePassGL;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;

public final class TechniquePassGL3 extends TechniquePassGL {
  private static Logger LOGGER = Logger.getLogger(TechniquePassGL3.class.getCanonicalName());

  public TechniquePassGL3(Element pass) throws AntagonistException {
    super(pass);
  }
  
  public TechniquePassGL3() {}

  @Override
  protected int generateShaderObject(ShaderType st, RenderManager rm) {
    switch(st) {
    case VERTEX:
      return glCreateShader(GL_VERTEX_SHADER);
    case FRAGMENT:
      return glCreateShader(GL_FRAGMENT_SHADER);
    case GEOMETRY:
      return glCreateShader(GL_GEOMETRY_SHADER);
    case TESS_CONTROL:
      LOGGER.severe("Tesselation shaders need a context of OpenGL 4.1 or greater.");
      throw new IllegalStateException();
    case TESS_EVALUATION:
      LOGGER.severe("Tesselation shaders need a context of OpenGL 4.1 or greater.");
      throw new IllegalStateException();
    default:
      throw new IllegalArgumentException(st.toString());
    }
  }
  

  @Override
  protected void bindAttributes(int program) {
    // -- no cal
  }


  @Override
  protected void loadBasicInstanceUniforms(int program) throws AntagonistException {
    int basicInstanceBlock = glGetUniformBlockIndex(program, BASIC_INSTANCE_UNIFORMS_BLOCK);
    if(basicInstanceBlock < 0) {
      LOGGER.severe("Basic instance uniforms requested but not active!");
      throw new AntagonistException();
    }
    
    glUniformBlockBinding(program, basicInstanceBlock, BASIC_INSTANCE_UNIFORMS_BINDING);
    assert !Utils.hasGLErrors();
  }


  @Override
  protected void loadBasicLightUniforms(int program) throws AntagonistException {
    int basicLightBlock = glGetUniformBlockIndex(program, BASIC_LIGHT_UNIFORMS_BLOCK);
    if(basicLightBlock < 0) {
      LOGGER.severe("Basic light uniforms requested but not active!");
      throw new AntagonistException();
    }
    
    glUniformBlockBinding(program, basicLightBlock, BASIC_LIGHT_UNIFORMS_BINDING);
    assert !Utils.hasGLErrors();
  }


  @Override
  protected void loadBasicMaterialUniforms(int program)
      throws AntagonistException {
    int basicMaterialBlock = glGetUniformBlockIndex(program, BASIC_MATERIAL_UNIFORMS_BLOCK);
    if(basicMaterialBlock < 0) {
      LOGGER.severe("Basic material uniforms requested but not active!");
      throw new AntagonistException();
    }

    glUniformBlockBinding(program, basicMaterialBlock, BASIC_MATERIAL_UNIFORMS_BINDING);
    assert !Utils.hasGLErrors();
  }
  
  @Override
  public int getSpecularFactorUniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  @Override
  public int getSpecularGlossinessUniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  @Override
  public int getHeightUniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }

  @Override
  public int getAmbientLightColorUniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  @Override
  public int getDirectionalLightDirectionUniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  @Override
  public int getDirectionalLightColorUniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  

  @Override
  public int getModelViewProjectionUniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  @Override
  public int getModelViewUniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  @Override
  public int getModelViewITUniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }


  @Override
  protected long getMaxUniformBufferSize() {
    return glGetInteger64(GL_MAX_UNIFORM_BLOCK_SIZE);
  }
}
