package cat.atridas.antagonista.graphics.gl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;

import java.util.logging.Logger;

import org.lwjgl.opengl.ARBUniformBufferObject;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLContext;
import org.w3c.dom.Element;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.Shader.ShaderType;

public class TechniquePassGL extends TechniquePass {
  private static Logger LOGGER = Logger.getLogger(TechniquePassGL.class.getCanonicalName());

  private int albedoTextureUniform;
  
  private int modelViewProjectionUniform, modelViewUniform;//, bonesUniform;
  
  private int ambientUniform, directionalDirUniform, directionalColorUniform;
  private int specularFactorUniform, specularGlossinessUniform, heightUniform;
  
  private static int defaultVertexShader   = -1,
                     defaultFragmentShader = -1,
                     defaultGeometryShader = -1,
                     defaultTessControl   = -1,
                     defaultTessEval       = -1;
  
  private static final boolean GL_ARB_uniform_buffer_object, GL3;

  static {
    GL3 = Core.getCore().getRenderManager().getProfile().supports(Profile.GL3);
    GL_ARB_uniform_buffer_object = GLContext.getCapabilities().GL_ARB_uniform_buffer_object;
  }
  
  public TechniquePassGL(Element techniquePassXML) throws AntagonistException {
    super(techniquePassXML);
  }
  
  public TechniquePassGL() {
  }
  
  @Override
  protected int generateShaderObject(ShaderType st, RenderManager rm) {
    switch(st) {
    case VERTEX:
      return glCreateShader(GL_VERTEX_SHADER);
    case FRAGMENT:
      return glCreateShader(GL_FRAGMENT_SHADER);
    case GEOMETRY:
      rm.getProfile().supportOrException(Profile.GL3, "geometry shaders");
      return glCreateShader(GL_GEOMETRY_SHADER);
    case TESS_CONTROL:
      rm.getProfile().supportOrException(Profile.GL4, "tesselation shaders");
      return glCreateShader(GL_TESS_CONTROL_SHADER);
    case TESS_EVALUATION:
      rm.getProfile().supportOrException(Profile.GL4, "tesselation shaders");
      return glCreateShader(GL_TESS_EVALUATION_SHADER);
    default:
      throw new IllegalArgumentException();
    }
  }

  @Override
  protected void deleteShader(int shaderID) {
    glDeleteShader(shaderID);
  }

  @Override
  protected String getVersionDeclaration(RenderManager rm) {
    switch(rm.getProfile()) {
    case GL2:
      return "#version 120";
    case GL3:
      return "#version 330";
    case GL4:
      return "#version 420";
    default:
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
    
    
    //aqui la teca!!!!!
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
    
    if(color && GL3) {
      glBindFragDataLocation(program, COLOR_FRAGMENT_DATA_LOCATION, COLOR_FRAGMENT_DATA_NAME);
    }
    
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
        LOGGER.severe("Albedo texture requested but not active!");
        throw new AntagonistException();
      }
      
      glUniform1i(albedoTextureUniform, ALBEDO_TEXTURE_UNIT);
    }
    assert !Utils.hasGLErrors();
    
    if(basicInstanceUniforms) {
      loadBasicInstanceUniforms(program);
    }
    if(basicLight) {
      loadBasicLightUniforms(program);
    }
    if(basicMaterial) {
      loadBasicMaterialUniforms(program);
    }
    
    assert !Utils.hasGLErrors();

    assert !position || glGetAttribLocation(program, POSITION_ATTRIBUTE_NAME) == POSITION_ATTRIBUTE;
    assert !normal   || glGetAttribLocation(program, NORMAL_ATTRIBUTE_NAME) == NORMAL_ATTRIBUTE;
    assert !tangents || glGetAttribLocation(program, TANGENT_ATTRIBUTE_NAME) == TANGENT_ATTRIBUTE;
    assert !tangents || glGetAttribLocation(program, BITANGENT_ATTRIBUTE_NAME) == BITANGENT_ATTRIBUTE;
    assert !uv       || glGetAttribLocation(program, UV_ATTRIBUTE_NAME) == UV_ATTRIBUTE;
    assert !bones    || glGetAttribLocation(program, BLEND_INDEX_ATTRIBUTE_NAME) == BLEND_INDEX_ATTRIBUTE;
    assert !bones    || glGetAttribLocation(program, BLEND_WEIGHT_ATTRIBUTE_NAME) == BLEND_WEIGHT_ATTRIBUTE;
    
    int fragDataLoc = glGetFragDataLocation(program, COLOR_FRAGMENT_DATA_NAME);
    assert !(color && GL3) || fragDataLoc == COLOR_FRAGMENT_DATA_LOCATION;
    
    
    rm.activateShader(0);
    
    return program;
  }
  
  private void loadBasicInstanceUniforms(int program) throws AntagonistException {
    if(GL3) {
      int basicInstanceBlock = GL31.glGetUniformBlockIndex(program, BASIC_INSTANCE_UNIFORMS_BLOCK);
      if(basicInstanceBlock < 0) {
        LOGGER.severe("Basic instance uniforms requested but not active!");
        throw new AntagonistException();
      }
      
      GL31.glUniformBlockBinding(program, basicInstanceBlock, BASIC_INSTANCE_UNIFORMS_BINDING);
    } else if(GL_ARB_uniform_buffer_object) {
      int basicInstanceBlock = ARBUniformBufferObject.glGetUniformBlockIndex(program, BASIC_INSTANCE_UNIFORMS_BLOCK);
      if(basicInstanceBlock < 0) {
        LOGGER.severe("Basic instance uniforms requested but not active!");
        throw new AntagonistException();
      }

      ARBUniformBufferObject.glUniformBlockBinding(program, basicInstanceBlock, BASIC_INSTANCE_UNIFORMS_BINDING);
    } else {
      modelViewProjectionUniform = glGetUniformLocation(program, MODEL_VIEW_PROJECTION_UNIFORMS);
      modelViewUniform = glGetUniformLocation(program, MODEL_VIEW_UNIFORMS);
      if(modelViewProjectionUniform < 0) {
        LOGGER.severe("Basic instance uniforms requested but ModelViewProjection matrix not active!");
        throw new AntagonistException();
      }
      if(modelViewUniform < 0) {
        LOGGER.severe("Basic instance uniforms requested but ModelView matrix not active!");
        throw new AntagonistException();
      }
    }
    assert !Utils.hasGLErrors();
  }


  private void loadBasicLightUniforms(int program) throws AntagonistException {
    if(GL3) {
      int basicLightBlock = GL31.glGetUniformBlockIndex(program, BASIC_LIGHT_UNIFORMS_BLOCK);
      if(basicLightBlock < 0) {
        LOGGER.severe("Basic light uniforms requested but not active!");
        throw new AntagonistException();
      }
      
      GL31.glUniformBlockBinding(program, basicLightBlock, BASIC_LIGHT_UNIFORMS_BINDING);
      
    } else if(GL_ARB_uniform_buffer_object) {
      int basicLightBlock = ARBUniformBufferObject.glGetUniformBlockIndex(program, BASIC_LIGHT_UNIFORMS_BLOCK);
      if(basicLightBlock < 0) {
        LOGGER.severe("Basic light uniforms requested but not active!");
        throw new AntagonistException();
      }
      
      ARBUniformBufferObject.glUniformBlockBinding(program, basicLightBlock, BASIC_LIGHT_UNIFORMS_BINDING);
    } else {
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
    }
    assert !Utils.hasGLErrors();
  }

  private void loadBasicMaterialUniforms(int program) throws AntagonistException {
    if(GL3) {
      int basicMaterialBlock = GL31.glGetUniformBlockIndex(program, BASIC_MATERIAL_UNIFORMS_BLOCK);
      if(basicMaterialBlock < 0) {
        LOGGER.severe("Basic material uniforms requested but not active!");
        throw new AntagonistException();
      }

      GL31.glUniformBlockBinding(program, basicMaterialBlock, BASIC_MATERIAL_UNIFORMS_BINDING);
      
    } else if(GL_ARB_uniform_buffer_object) {
      int basicMaterialBlock = ARBUniformBufferObject.glGetUniformBlockIndex(program, BASIC_MATERIAL_UNIFORMS_BLOCK);
      if(basicMaterialBlock < 0) {
        LOGGER.severe("Basic material uniforms requested but not active!");
        throw new AntagonistException();
      }

      ARBUniformBufferObject.glUniformBlockBinding(program, basicMaterialBlock, BASIC_MATERIAL_UNIFORMS_BINDING);
    } else {
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
    assert !Utils.hasGLErrors();
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

  @Override
  protected long getMaxUniformBufferSize() {
    if(GL3)
      return glGetInteger64(GL31.GL_MAX_UNIFORM_BLOCK_SIZE);
    else if(GL_ARB_uniform_buffer_object)
      return glGetInteger(ARBUniformBufferObject.GL_MAX_UNIFORM_BLOCK_SIZE);
    else
      return 0;
  }

}
