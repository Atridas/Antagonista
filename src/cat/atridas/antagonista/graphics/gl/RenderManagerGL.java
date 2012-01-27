package cat.atridas.antagonista.graphics.gl;

import java.util.logging.Logger;

import org.lwjgl.opengl.ARBImaging;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GLContext;

import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.graphics.TechniquePass;

import cat.atridas.antagonista.graphics.gl2.SceneDataGL2;
import cat.atridas.antagonista.graphics.gl3.SceneDataGL3;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Desktop OpenGL implementation of the RenderManager class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public final class RenderManagerGL extends RenderManager {
  private static Logger LOGGER = Logger.getLogger(RenderManagerGL.class.getCanonicalName());

  /**
   * OpenGL profile.
   * @since 0.1
   */
  private Profile profile;
  
  /**
   * Scene data information.
   * @since 0.1
   */
  private SceneData sceneData;
  
  /**
   * Cached maximum number of instances given each memory requirement.
   * @since 0.1
   */
  private int maxInstancesBasic, maxInstancesColors;//, maxInstancesBones;
  
  /**
   * Caches the maximum number of instances given each memory requirement.
   * @since 0.1
   */
  private void initInstances() {
    int maxUniformSize = 0;
    if (profile.supports(Profile.GL3)) {
      maxUniformSize = glGetInteger(GL31.GL_MAX_UNIFORM_BLOCK_SIZE);
    }

    if(maxUniformSize > 0) {
      maxInstancesBasic  = maxUniformSize / TechniquePass.BASIC_INSTANCE_UNIFORMS_BLOCK_SIZE;
      maxInstancesColors = maxUniformSize / TechniquePass.SPECIAL_COLORS_UNIFORMS_BLOCK_SIZE;
      //maxInstancesBasic = maxUniformSize / TechniquePass.BASIC_INSTANCE_UNIFORMS_BLOCK_SIZE;
    } else {
      maxInstancesBasic  = 1;
      maxInstancesColors = 1;
    }
  }
  
  @Override
  public void initGL() {
    ContextCapabilities cc = GLContext.getCapabilities();
    if(cc.OpenGL42) {
      profile = Profile.GL4;
      sceneData = new SceneDataGL3(this);
    } else if(cc.OpenGL33) {
      profile = Profile.GL3;
      sceneData = new SceneDataGL3(this);
    } else if(cc.OpenGL21) {
      profile = Profile.GL2;
      sceneData = new SceneDataGL2(this);
    } else {
      throw new IllegalStateException("Can not load an opengl 2.1 or greater context.");
    }
    
    glViewport(0, 0, getWidth(), getHeight());
    
    glClearColor(1,0,1,0);
    
    glEnable(GL_BLEND);
    
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    glEnable(GL_DEPTH_TEST);
    glClearDepth(1);
    
    initInstances();
    
    
    assert !hasGLErrors();
  }

  @Override
  public void initFrame() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  @Override
  public Profile getProfile() {
    return profile;
  }


  @Override
  public void activateShader(int shader) {
    glUseProgram(shader);
  }


  @Override
  public void setDepthTest(boolean enable) {
    if(enable)
      glEnable(GL_DEPTH_TEST);
    else
      glDisable(GL_DEPTH_TEST);
  }


  @Override
  public void setDepthTest(DepthFunction function) {
    switch(function) {
    case ALWAYS:
      glDepthFunc(GL_ALWAYS);
      break;
    case EQUAL:
      glDepthFunc(GL_EQUAL);
      break;
    case GEQUAL:
      glDepthFunc(GL_GEQUAL);
      break;
    case GREATER:
      glDepthFunc(GL_GREATER);
      break;
    case LEQUAL:
      glDepthFunc(GL_LEQUAL);
      break;
    case LESS:
      glDepthFunc(GL_LESS);
      break;
    case NEVER:
      glDepthFunc(GL_NEVER);
      break;
    case NOTEQUAL:
      glDepthFunc(GL_NOTEQUAL);
      break;
    default:
      throw new IllegalArgumentException();
    }
  }


  @Override
  public void setZWrite(boolean enable) {
    glDepthMask(enable);
  }

  @Override
  public void setAlphaBlend(boolean enable) {
    if(enable)
      glEnable(GL_BLEND);
    else
      glDisable(GL_BLEND);
  }


  @Override
  public void setAlphaBlend(BlendOperation operation) {
    glBlendFunc(getAlphaOperator(operation.src_operator), getAlphaOperator(operation.dst_operator));
  }


  @Override
  public void setAlphaBlend(BlendOperationSeparate operation) {
    glBlendFuncSeparate(
        getAlphaOperator(operation.color.src_operator), getAlphaOperator(operation.color.dst_operator),
        getAlphaOperator(operation.alpha.src_operator), getAlphaOperator(operation.alpha.dst_operator)
        );
  }


  @Override
  public void setAlphaBlend(boolean enable, int renderTarget) {
    if(!profile.supports(Profile.GL3)) {
      //TODO mirar si hi ha alguna extensió que s'ho tragui
      LOGGER.severe("To enable or disable blending at specific render targets, you should have a" +
      		" profile compatible with OpenGL 3.0");
      throw new IllegalStateException();
    }
    if(enable)
      GL30.glEnablei(GL_BLEND, renderTarget);
    else
      GL30.glDisablei(GL_BLEND, renderTarget);
  }


  @Override
  public void setAlphaBlend(BlendOperation operation, int renderTarget) {
    if(!profile.supports(Profile.GL4)) {
      //TODO mirar si hi ha alguna extensió que s'ho tragui
      LOGGER.severe("To configure blending at specific render targets, you should have a" +
          " profile compatible with OpenGL 4.0");
      throw new IllegalStateException();
    }
    GL40.glBlendFunci(getAlphaOperator(operation.src_operator), getAlphaOperator(operation.dst_operator), renderTarget);
  }


  @Override
  public void setAlphaBlend(BlendOperationSeparate operation, int renderTarget) {
    if(!profile.supports(Profile.GL4)) {
      //TODO mirar si hi ha alguna extensió que s'ho tragui
      LOGGER.severe("To configure blending at specific render targets, you should have a" +
          " profile compatible with OpenGL 4.0");
      throw new IllegalStateException();
    }
    GL40.glBlendFuncSeparatei(
        getAlphaOperator(operation.color.src_operator), getAlphaOperator(operation.color.dst_operator),
        getAlphaOperator(operation.alpha.src_operator), getAlphaOperator(operation.alpha.dst_operator),
        renderTarget
        );
  }
  
  /**
   * Translates the blend operator enumeration into OpenGL identifiers.
   * 
   * @param op BendOperator enumeration.
   * @return an OpenGL identifier.
   */
  private int getAlphaOperator(BlendOperator op) {
    switch(op) {
    case CONSTANT_ALPHA:
      return GL_CONSTANT_ALPHA;
    case CONSTANT_COLOR:
      return GL_CONSTANT_COLOR;
    case DST_ALPHA:
      return GL_DST_ALPHA;
    case DST_COLOR:
      return GL_DST_COLOR;
    case ONE:
      return GL_ONE;
    case ONE_MINUS_CONSTANT_ALPHA:
      return GL_ONE_MINUS_CONSTANT_ALPHA;
    case ONE_MINUS_CONSTANT_COLOR:
      return GL_ONE_MINUS_CONSTANT_COLOR;
    case ONE_MINUS_DST_ALPHA:
      return GL_ONE_MINUS_DST_ALPHA;
    case ONE_MINUS_DST_COLOR:
      return GL_ONE_MINUS_DST_COLOR;
    case ONE_MINUS_SRC_ALPHA:
      return GL_ONE_MINUS_SRC_ALPHA;
    case ONE_MINUS_SRC_COLOR:
      return GL_ONE_MINUS_SRC_COLOR;
    case SRC_ALPHA:
      return GL_SRC_ALPHA;
    case SRC_ALPHA_SATURATE:
      return GL_SRC_ALPHA_SATURATE;
    case SRC_COLOR:
      return GL_SRC_COLOR;
    case ZERO:
      return GL_ZERO;
    case ONE_MINUS_SRC1_ALPHA:
      if(!profile.supports(Profile.GL3)) {
        //TODO mirar si hi ha alguna extensió que s'ho tragui
        LOGGER.severe("To access ONE_MINUS_SRC1_ALPHA, you should have a" +
            " profile compatible with OpenGL 3.3");
        throw new IllegalStateException();
      }
      return GL33.GL_ONE_MINUS_SRC1_ALPHA;
    case SRC1_ALPHA:
      if(!profile.supports(Profile.GL3)) {
        //TODO mirar si hi ha alguna extensió que s'ho tragui
        LOGGER.severe("To access ONE_MINUS_SRC1_ALPHA, you should have a" +
            " profile compatible with OpenGL 3.3");
        throw new IllegalStateException();
      }
      return GL33.GL_SRC1_ALPHA;
    default:
      throw new IllegalArgumentException();  
    }
  }

  @Override
  public void noVertexArray() {
    if(profile.supports(Profile.GL3)) {
      GL30.glBindVertexArray(0);
    }
  }

  @Override
  public SceneData getSceneData() {
    return sceneData;
  }

  @Override
  public boolean hasGLErrors() {
    int error = glGetError();
    boolean errorEncountered = false;
    while(error != GL_NO_ERROR) {
      String errorStr = "OpenGL error: ";
      switch(error) {
      case GL_INVALID_ENUM:
        errorStr += "GL_INVALID_ENUM";
        break;
      case GL_INVALID_VALUE:
        errorStr += "GL_INVALID_VALUE";
        break;
      case GL_INVALID_OPERATION:
        errorStr += "GL_INVALID_OPERATION";
        break;
        
      case GL_STACK_OVERFLOW:
        errorStr += "GL_STACK_OVERFLOW"; //shouldn't happen ever, in compability mode
        break;
      case GL_STACK_UNDERFLOW:
        errorStr += "GL_STACK_UNDERFLOW"; //shouldn't happen ever, in compability mode
        break;
        
      case GL_OUT_OF_MEMORY:
        errorStr += "GL_OUT_OF_MEMORY";
        break;
      case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
        errorStr += "GL_INVALID_FRAMEBUFFER_OPERATION";
        break;
        
      case ARBImaging.GL_TABLE_TOO_LARGE:
        errorStr += "ARBImaging - GL_TABLE_TOO_LARGE"; //aquest error també ha de ser extremadament xungo
        break;
      default:
        throw new IllegalStateException("Unrecognized error code: " + error);
      }
      
      try {
        throw new Exception();
      } catch(Exception e) {
        StringBuilder stackTrace = new StringBuilder(errorStr);
        for(StackTraceElement ste : e.getStackTrace()) {
          stackTrace.append("\n  ");
          stackTrace.append(ste.toString());
        }
        
        LOGGER.severe(stackTrace.toString());
      }
      errorEncountered = true;
      error = glGetError();
    }
    return errorEncountered;
  }

  @Override
  public void clearSilentlyGLErrors() {
    int error = glGetError();
    while(error != GL_NO_ERROR) {
      error = glGetError();
    }
  }

  @Override
  public int getMaxInstancesBasic() {
    return maxInstancesBasic;
  }

  @Override
  public int getMaxInstancesWithBones() {
    throw new RuntimeException("Not yet implemented");
  }

  @Override
  public int getMaxInstancesWithColors() {
    return maxInstancesColors;
  }
}
