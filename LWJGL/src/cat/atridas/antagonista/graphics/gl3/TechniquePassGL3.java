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

/**
 * OpenGL 3.3 implementation of the TechniquePass class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public final class TechniquePassGL3 extends TechniquePassGL {
  private static Logger LOGGER = Logger.getLogger(TechniquePassGL3.class.getCanonicalName());

  /**
   * Builds a new program pass, from an xml configuration element.
   * 
   * @param techniquePassXML xml configuration element.
   * @throws AntagonistException if there was an error building the program.
   * @since 0.1
   * @see TechniquePassGL#TechniquePassGL(Element)
   */
  public TechniquePassGL3(Element pass) throws AntagonistException {
    super(pass);
  }

  /**
   * Uninitialized constructor.
   * @since 0.1
   */
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
  protected void loadBasicInstanceUniforms(int program) {
    int basicInstanceBlock = glGetUniformBlockIndex(program, BASIC_INSTANCE_UNIFORMS_BLOCK);
    if(basicInstanceBlock == GL_INVALID_INDEX) {
      LOGGER.severe("Basic instance uniforms requested but not active!");
      //throw new AntagonistException();
    } else {
      glUniformBlockBinding(program, basicInstanceBlock, BASIC_INSTANCE_UNIFORMS_BINDING);
    }
    assert !Utils.hasGLErrors();
  }

  @Override
  protected void loadArmatureUniforms(int program) {
    int armatureBlock = glGetUniformBlockIndex(program, ARMATURE_UNIFORMS_BLOCK);
    if(armatureBlock == GL_INVALID_INDEX) {
      LOGGER.severe("Armature uniforms requested but not active!");
      //throw new AntagonistException();
    } else {
      glUniformBlockBinding(program, armatureBlock, ARMATURE_UNIFORMS_BINDING);
    }
    assert !Utils.hasGLErrors();
  }

  @Override
  protected void loadSpecialColorsUniforms(int program) {
    int specialColorsBlock = glGetUniformBlockIndex(program, SPECIAL_COLORS_UNIFORMS_BLOCK);
    if(specialColorsBlock == GL_INVALID_INDEX) {
      LOGGER.severe("Special Colors requested but not active!");
      //throw new AntagonistException();
    } else {
      glUniformBlockBinding(program, specialColorsBlock, SPECIAL_COLORS_UNIFORMS_BINDING);
    }
    assert !Utils.hasGLErrors();
  }


  @Override
  protected void loadBasicLightUniforms(int program) {
    int basicLightBlock = glGetUniformBlockIndex(program, BASIC_LIGHT_UNIFORMS_BLOCK);
    if(basicLightBlock == GL_INVALID_INDEX) {
      LOGGER.severe("Basic light uniforms requested but not active!");
      //throw new AntagonistException();
    } else {
      glUniformBlockBinding(program, basicLightBlock, BASIC_LIGHT_UNIFORMS_BINDING);
    }
    assert !Utils.hasGLErrors();
  }


  @Override
  protected void loadBasicMaterialUniforms(int program) {
    int basicMaterialBlock = glGetUniformBlockIndex(program, BASIC_MATERIAL_UNIFORMS_BLOCK);
    if(basicMaterialBlock == GL_INVALID_INDEX) {
      LOGGER.severe("Basic material uniforms requested but not active!");
      //throw new AntagonistException();
    } else {
      glUniformBlockBinding(program, basicMaterialBlock, BASIC_MATERIAL_UNIFORMS_BINDING);
    }
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
  public int getBoneMatrixPalete() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  @Override
  public int getBoneMatrixPaleteIT() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }

  @Override
  public int getSpecialColor0Uniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  @Override
  public int getSpecialColor1Uniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  @Override
  public int getSpecialColor2Uniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }
  @Override
  public int getSpecialColor3Uniform() {
    throw new IllegalStateException("Trying to fetch a uniform. Use uniform blocks instead.");
  }

  @Override
  protected void loadFontUniforms(int program) {
    // TODO Auto-generated method stub
    throw new RuntimeException("not implemented");
  }

  @Override
  protected int getFontShader(ShaderType shaderType) {
    // TODO Auto-generated method stub
    throw new RuntimeException("not implemented");
  }
}
