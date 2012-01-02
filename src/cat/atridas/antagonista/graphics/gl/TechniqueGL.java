package cat.atridas.antagonista.graphics.gl;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;

import java.util.logging.Logger;

import org.lwjgl.opengl.GLContext;
import org.w3c.dom.Element;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.Technique;
import cat.atridas.antagonista.graphics.Shader.ShaderType;

public class TechniqueGL extends Technique {
  private static Logger LOGGER = Logger.getLogger(TechniqueGL.class.getCanonicalName());

  private int albedoTextureUniform;
  private int basicInstanceBlock, basicInstanceStruct;
  
  private int basicLightBlock, ambientUniform, directionalDirUniform, directionalColorUniform;
  private int basicMaterialBlock, specularFactorUniform, specularGlossiness;
  
  private boolean GL_ARB_uniform_buffer_object, GL3;
  
  public TechniqueGL(Element techniqueXML) throws AntagonistException {
    super(techniqueXML);
  }

  @Override
  protected void setupCapabilities() {
    GL3 = Core.getCore().getRenderManager().getProfile().supports(Profile.GL3);
    GL_ARB_uniform_buffer_object = GLContext.getCapabilities().GL_ARB_uniform_buffer_object;
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
      return false;
    }
    
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
    
    glLinkProgram(program);

    int result = glGetProgram(program, GL_LINK_STATUS);
    
    if(result == 0) {
      int len = glGetProgram(program, GL_INFO_LOG_LENGTH);
      String info = glGetProgramInfoLog(program, len);
      LOGGER.severe("Error linking shader: " + info);
      
      deleteShaderProgram(program);
      
      throw new AntagonistException();
    }
    
    //uniforms
    if(albedoTexture) {
      albedoTextureUniform = glGetUniformLocation(program, ALBEDO_TEXTURE_UNIFORM);
      if(albedoTextureUniform < 0) {
        LOGGER.severe("Albedo texture requested but not active!");
        throw new AntagonistException();
      }
      
      glUniform1i(albedoTextureUniform, ALBEDO_TEXTURE_UNIT);
    }
    
    if(basicInstanceUniforms) {
      loadBasicInstanceUniforms(program);
    }
    if(basicLight) {
      loadBasicLightUniforms(program);
    }
    if(basicMaterial) {
      loadBasicMaterialUniforms(program);
    }
    
    return program;
  }
  
  private void loadBasicInstanceUniforms(int program) throws AntagonistException {
    if(GL3) {
      basicInstanceBlock = glGetUniformBlockIndex(program, BASIC_INSTANCE_UNIFORMS_BLOCK);
      if(basicInstanceBlock < 0) {
        LOGGER.severe("Basic instance uniforms requested but not active!");
        throw new AntagonistException();
      }
    } else if(GL_ARB_uniform_buffer_object) {
      basicInstanceBlock = glGetUniformBlockIndex(program, BASIC_INSTANCE_UNIFORMS_BLOCK);
      if(basicInstanceBlock < 0) {
        LOGGER.severe("Basic instance uniforms requested but not active!");
        throw new AntagonistException();
      }
    } else {
      basicInstanceStruct = glGetUniformLocation(program, BASIC_INSTANCE_UNIFORMS_STRUCT);
      if(basicInstanceStruct < 0) {
        LOGGER.severe("Basic instance uniforms requested but not active!");
        throw new AntagonistException();
      }
    }
  }


  private void loadBasicLightUniforms(int program) throws AntagonistException {
    if(GL3) {
      basicLightBlock = glGetUniformBlockIndex(program, BASIC_LIGHT_UNIFORMS_BLOCK);
      if(basicLightBlock < 0) {
        LOGGER.severe("Basic light uniforms requested but not active!");
        throw new AntagonistException();
      }
    } else if(GL_ARB_uniform_buffer_object) {
      basicLightBlock = glGetUniformBlockIndex(program, BASIC_LIGHT_UNIFORMS_BLOCK);
      if(basicLightBlock < 0) {
        LOGGER.severe("Basic light uniforms requested but not active!");
        throw new AntagonistException();
      }
    } else {
      ambientUniform = glGetUniformLocation(program, AMBIENT_LIGHT_UNIFORM_BLOCK);
      directionalDirUniform = glGetUniformLocation(program, DIRECTIONAL_LIGHT_POS_UNIFORM_BLOCK);
      directionalColorUniform = glGetUniformLocation(program, DIRECTIONAL_LIGHT_COLOR_UNIFORMS_BLOCK);
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
  }

  private void loadBasicMaterialUniforms(int program) throws AntagonistException {
    if(GL3) {
      basicMaterialBlock = glGetUniformBlockIndex(program, BASIC_MATERIAL_UNIFORMS_BLOCK);
      if(basicMaterialBlock < 0) {
        LOGGER.severe("Basic material uniforms requested but not active!");
        throw new AntagonistException();
      }
    } else if(GL_ARB_uniform_buffer_object) {
      basicMaterialBlock = glGetUniformBlockIndex(program, BASIC_MATERIAL_UNIFORMS_BLOCK);
      if(basicMaterialBlock < 0) {
        LOGGER.severe("Basic material uniforms requested but not active!");
        throw new AntagonistException();
      }
    } else {
      specularFactorUniform = glGetUniformLocation(program, SPECULAR_FACTOR_UNIFORMS_BLOCK);
      specularGlossiness = glGetUniformLocation(program, SPECULAR_GLOSS_UNIFORMS_BLOCK);
      if(specularFactorUniform < 0) {
        LOGGER.severe("Basic material uniforms requested but specular factor not active!");
        throw new AntagonistException();
      }
      if(specularGlossiness < 0) {
        LOGGER.severe("Basic material uniforms requested but specular glossiness uniform not active!");
        throw new AntagonistException();
      }
    }
  }
  
  @Override
  protected void deleteShaderProgram(int shaderProgramID) {
    glDeleteProgram(shaderProgramID);
  }

  @Override
  protected int getDefaultShader(ShaderType st) {
    // TODO Auto-generated method stub
    assert false;
    return 0;
  }

  @Override
  protected long getMaxUniformBufferSize() {
    if(GL3)
      return glGetInteger64(GL_MAX_UNIFORM_BLOCK_SIZE);
    else
      return 0;
  }

}
