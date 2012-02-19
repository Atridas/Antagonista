package cat.atridas.antagonista.graphics.gl;

import static org.lwjgl.opengl.GL20.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.Shader.ShaderType;

/**
 * Desktop OpenGL implementation of the TechniquePass class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public abstract class TechniquePassGL extends TechniquePass {
  private static Logger LOGGER = Logger.getLogger(TechniquePassGL.class.getCanonicalName());

  /**
   * Texture uniform binding points.
   * @since 0.1
   */
  private int albedoTextureUniform, normalTextureUniform, heightTextureUniform,
  
              fontTexture0Uniform, fontTexture1Uniform, fontTexture2Uniform, fontTexture3Uniform;
  
  /**
   * Default precompiled shaders.
   * @since 0.1
   */
  private static int defaultVertexShader   = -1,
                     defaultFragmentShader = -1,
                     defaultGeometryShader = -1,
                     defaultTessControl   = -1,
                     defaultTessEval       = -1;
  
  /**
   * Builds a new program pass, from an xml configuration element.
   * 
   * @param techniquePassXML xml configuration element.
   * @throws AntagonistException if there was an error building the program.
   * @since 0.1
   * @see TechniquePass#TechniquePass(Element)
   */
  public TechniquePassGL(Element techniquePassXML) throws AntagonistException {
    super(techniquePassXML);
  }
  
  /**
   * Uninitialized constructor.
   * @since 0.1
   */
  public TechniquePassGL() {}

  /**
   * Builds a text technique pass.
   * @param fontPass <code>true</code>
   * @since 0.1
   */
  public TechniquePassGL(boolean fontPass) {
    super(fontPass);
  }

  @Override
  protected void deleteShader(int shaderID) {
    glDeleteShader(shaderID);
  }

  @Override
  protected String getVersionDeclaration(RenderManager rm) {
    
    if(Utils.supports(Profile.GL4)) {
      return "#version 420";
    } else if(Utils.supports(Profile.GL3)) {
      return "#version 330";
    } else if(Utils.supports(Profile.GL2)) {
      return "#version 120";
    } else {
      throw new IllegalArgumentException();
    }
    
  }

  @Override
  protected boolean compileShader(int shaderID, String source) {

    glShaderSource(shaderID, source);
    glCompileShader( shaderID );

    int result = glGetShader(shaderID, GL_COMPILE_STATUS);
    
    if(result == 0) {
      int len = glGetShader(shaderID, GL_INFO_LOG_LENGTH);
      String info = glGetShaderInfoLog(shaderID, len);
      
      StringBuilder sourceWithLines = new StringBuilder();
      int line = 0;
      for(String l : source.split("\n")) {
        sourceWithLines.append(line);
        sourceWithLines.append("  ");
        sourceWithLines.append(l);
        sourceWithLines.append('\n');
        line++;
        
        if(l.contains("#line")) {
          line = Integer.parseInt(l.split(" ")[1]);
        }
      }
      
      LOGGER.severe("Error compiling shader: " + info + "\nSource:\n" + sourceWithLines);
      Utils.hasGLErrors();
      return false;
    }

    assert !Utils.hasGLErrors();
    return true;
  }

  @Override
  protected int completeShaderProgram(int vs, int tc, int te, int gs, int fs, RenderManager rm)
      throws AntagonistException {

    int program = glCreateProgram();
    
    if(vs != 0)
      glAttachShader(program, vs);
    if(fs != 0)
      glAttachShader(program, fs);
    if(gs != 0)
    {
      //se suposa que ja està comprovat, però double checking no està de més
      assert rm.getProfile().supports(Profile.GL3);
      glAttachShader(program, fs);
    }
    if(tc != 0)
    {
      //se suposa que ja està comprovat, però double checking no està de més
      assert rm.getProfile().supports(Profile.GL4);
      glAttachShader(program, tc);
    }
    if(te != 0)
    {
      //se suposa que ja està comprovat, però double checking no està de més
      assert rm.getProfile().supports(Profile.GL4);
      glAttachShader(program, te);
    }
    
    
    bindAttributes(program);
    
    glLinkProgram(program);

    int result = glGetProgram(program, GL_LINK_STATUS);
    
    if(result == 0) {
      int len = glGetProgram(program, GL_INFO_LOG_LENGTH);
      String info = glGetProgramInfoLog(program, len);
      LOGGER.severe("Error linking shader: " + info);
      
      deleteShaderProgram(program);
      
      throw new AntagonistException();
    }
    assert !Utils.hasGLErrors();
    
    rm.activateShader(program);
    
    //uniforms
    if(albedoTexture) {
      albedoTextureUniform = glGetUniformLocation(program, ALBEDO_TEXTURE_UNIFORM);
      if(albedoTextureUniform < 0) {
        LOGGER.warning("Albedo texture requested but not active!");
        //hrow new AntagonistException();
      } else {
        glUniform1i(albedoTextureUniform, ALBEDO_TEXTURE_UNIT);
      }
    }
    if(normalTexture) {
      normalTextureUniform = glGetUniformLocation(program, NORMALMAP_TEXTURE_UNIFORM);
      if(normalTextureUniform < 0) {
        LOGGER.warning("Normal texture requested but not active!");
        //throw new AntagonistException();
      } else {
        glUniform1i(normalTextureUniform, NORMALMAP_TEXTURE_UNIT);
      }
    }
    if(heightTexture) {
      heightTextureUniform = glGetUniformLocation(program, HEIGHTMAP_TEXTURE_UNIFORM);
      if(heightTextureUniform < 0) {
        LOGGER.warning("Height texture requested but not active!");
        //throw new AntagonistException();
      } else {
        glUniform1i(heightTextureUniform, HEIGHTMAP_TEXTURE_UNIT);
      }
    }
    if(fontTechnique) {
      fontTexture0Uniform = glGetUniformLocation(program, FONT_TEXTURE_0_UNIFORM);
      fontTexture1Uniform = glGetUniformLocation(program, FONT_TEXTURE_1_UNIFORM);
      fontTexture2Uniform = glGetUniformLocation(program, FONT_TEXTURE_2_UNIFORM);
      fontTexture3Uniform = glGetUniformLocation(program, FONT_TEXTURE_3_UNIFORM);
      
      if(fontTexture0Uniform < 0) {
        LOGGER.warning("Font texture 0 requested but not active!");
        //throw new AntagonistException();
      } else {
        glUniform1i(fontTexture0Uniform, FONT_TEXTURE_0_UNIT);
      }
      
      if(fontTexture1Uniform < 0) {
        LOGGER.warning("Font texture 1 requested but not active!");
        //throw new AntagonistException();
      } else {
        glUniform1i(fontTexture1Uniform, FONT_TEXTURE_1_UNIT);
      }
      
      if(fontTexture2Uniform < 0) {
        LOGGER.warning("Font texture 2 requested but not active!");
        //throw new AntagonistException();
      } else {
        glUniform1i(fontTexture2Uniform, FONT_TEXTURE_2_UNIT);
      }
      
      if(fontTexture3Uniform < 0) {
        LOGGER.warning("Font texture 3 requested but not active!");
        //throw new AntagonistException();
      } else {
        glUniform1i(fontTexture3Uniform, FONT_TEXTURE_3_UNIT);
      }
    }
    assert !Utils.hasGLErrors();

    if(basicInstanceUniforms) {
      loadBasicInstanceUniforms(program);
    }
    if(armatureUniforms) {
      loadArmatureUniforms(program);
    }
    if(basicLight) {
      loadBasicLightUniforms(program);
    }
    if(basicMaterial) {
      loadBasicMaterialUniforms(program);
    }
    if(fontTechnique) {
      loadFontUniforms(program);
    }
    
    assert !Utils.hasGLErrors();

    if(LOGGER.isLoggable(Level.WARNING)) {

      int positionLocation = glGetAttribLocation(program, POSITION_ATTRIBUTE_NAME);
      int normalLocation = glGetAttribLocation(program, NORMAL_ATTRIBUTE_NAME);
      int tangentLocation = glGetAttribLocation(program, TANGENT_ATTRIBUTE_NAME);
      int bitangentLocation = glGetAttribLocation(program, BITANGENT_ATTRIBUTE_NAME);
      int uvLocation = glGetAttribLocation(program, UV_ATTRIBUTE_NAME);
      int bonesILocation = glGetAttribLocation(program, BLEND_INDEX_ATTRIBUTE_NAME);
      int bonesWLocation = glGetAttribLocation(program, BLEND_WEIGHT_ATTRIBUTE_NAME);
      int colorLocation = glGetAttribLocation(program, COLOR_ATTRIBUTE_NAME);

      if(position && positionLocation != POSITION_ATTRIBUTE) {
        LOGGER.warning("Expected position attribute at " + POSITION_ATTRIBUTE + " but found in " + positionLocation);
      }
      if(normal && normalLocation != NORMAL_ATTRIBUTE) {
        LOGGER.warning("Expected normal attribute at " + NORMAL_ATTRIBUTE + " but found in " + normalLocation);
      }
      if(tangents) {
        if(tangentLocation != TANGENT_ATTRIBUTE)
          LOGGER.warning("Expected tangent attribute at " + TANGENT_ATTRIBUTE + " but found in " + tangentLocation);
        if(bitangentLocation != BITANGENT_ATTRIBUTE)
          LOGGER.warning("Expected bitangent attribute at " + BITANGENT_ATTRIBUTE + " but found in " + bitangentLocation);
      }
      if(uv && uvLocation != UV_ATTRIBUTE) {
        LOGGER.warning("Expected uv attribute at " + UV_ATTRIBUTE + " but found in " + uvLocation);
      }
      if(bones) {
        if(bonesILocation != BLEND_INDEX_ATTRIBUTE) 
          LOGGER.warning("Expected bone indexes attribute at " + BLEND_INDEX_ATTRIBUTE + " but found in " + bonesILocation);
        if(bonesWLocation != BLEND_WEIGHT_ATTRIBUTE)
          LOGGER.warning("Expected bone weights attribute at " + BLEND_WEIGHT_ATTRIBUTE + " but found in " + bonesWLocation);
      }
      //TODO comprovar atributs de les fonts (mandra)
    
      if(colorAttr) {
        if(colorLocation != COLOR_ATTRIBUTE) {
          LOGGER.warning("Expected color attribute at " + COLOR_ATTRIBUTE + " but found in " + colorLocation);
        }
      }
    }
    //int fragDataLoc = glGetFragDataLocation(program, COLOR_FRAGMENT_DATA_NAME);
    //assert !(color && GL3) || fragDataLoc == COLOR_FRAGMENT_DATA_LOCATION;
    
    
    rm.activateShader(0);
    
    return program;
  }
  
  /**
   * Binds the attributes to the correct positions.
   * 
   * @param program OpenGL program shader identifier.
   * @since 0.1
   */
  protected abstract void bindAttributes(int program);
  /**
   * Loads the basic instance uniforms binding points or sets the correct uniform buffer
   * binding point, depending on the current OpenGL profile.
   * 
   * @param program OpenGL program shader identifier.
   * @since 0.1
   */
  protected abstract void loadBasicInstanceUniforms(int program);
  /**
   * Loads the armature uniforms binding points or sets the correct uniform buffer
   * binding point, depending on the current OpenGL profile.
   * 
   * @param program OpenGL program shader identifier.
   * @since 0.3
   */
  protected abstract void loadArmatureUniforms(int program);
  /**
   * Loads the special colors uniforms binding points or sets the correct uniform buffer
   * binding point, depending on the current OpenGL profile.
   * 
   * @param program OpenGL program shader identifier.
   * @since 0.1
   */
  protected abstract void loadSpecialColorsUniforms(int program);
  /**
   * Loads the basic light uniforms binding points or sets the correct uniform buffer
   * binding point, depending on the current OpenGL profile.
   * 
   * @param program OpenGL program shader identifier.
   * @since 0.1
   */
  protected abstract void loadBasicLightUniforms(int program);
  /**
   * Loads the basic material uniforms binding points or sets the correct uniform buffer
   * binding point, depending on the current OpenGL profile.
   * 
   * @param program OpenGL program shader identifier.
   * @since 0.1
   */
  protected abstract void loadBasicMaterialUniforms(int program);
  /**
   * Loads the font uniforms binding points or sets the correct uniform buffer
   * binding point, depending on the current OpenGL profile.
   * 
   * @param program OpenGL program shader identifier.
   * @since 0.1
   */
  protected abstract void loadFontUniforms(int program);
  
  @Override
  protected void deleteShaderProgram(int shaderProgramID) {
    glDeleteProgram(shaderProgramID);
  }

  @Override
  protected int getDefaultShader(ShaderType st) {
    switch(st) {
    case VERTEX:
      if(defaultVertexShader < 0) {
        defaultVertexShader = generateShaderObject(st, Core.getCore().getRenderManager());
        compileShader(defaultVertexShader, Core.getCore().getEffectManager().getDefaultShaderSource(st));
      }
      return defaultVertexShader;
    case FRAGMENT:
      if(defaultFragmentShader < 0) {
        defaultFragmentShader = generateShaderObject(st, Core.getCore().getRenderManager());
        compileShader(defaultFragmentShader, Core.getCore().getEffectManager().getDefaultShaderSource(st));
      }
      return defaultFragmentShader;
    case GEOMETRY:
      if(defaultGeometryShader < 0) {
        defaultGeometryShader = generateShaderObject(st, Core.getCore().getRenderManager());
        compileShader(defaultGeometryShader, Core.getCore().getEffectManager().getDefaultShaderSource(st));
      }
      return defaultGeometryShader;
    case TESS_CONTROL:
      if(defaultTessControl < 0) {
        defaultTessControl = generateShaderObject(st, Core.getCore().getRenderManager());
        compileShader(defaultTessControl, Core.getCore().getEffectManager().getDefaultShaderSource(st));
      }
      return defaultTessControl;
    case TESS_EVALUATION:
      if(defaultTessEval < 0) {
        defaultTessEval = generateShaderObject(st, Core.getCore().getRenderManager());
        compileShader(defaultTessEval, Core.getCore().getEffectManager().getDefaultShaderSource(st));
      }
      return defaultTessEval;
    default:
      throw new IllegalStateException("Oops " + st);
    }
  }

}
