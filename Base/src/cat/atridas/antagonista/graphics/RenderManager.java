package cat.atridas.antagonista.graphics;

import java.awt.Canvas;
import java.util.logging.Logger;

/**
 * Encapsulation of global rendering capabilities. This class is used in all
 * phases of rendering.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public abstract class RenderManager {
  private static Logger LOGGER = Logger.getLogger(RenderManager.class
      .getCanonicalName());

  /**
   * Width of the screen.
   * 
   * @since 0.1
   */
  protected int width;
  /**
   * Height of the screen.
   * 
   * @since 0.1
   */
  protected int height;

  /**
   * Marks if the OpenGL context is created with forward compatible capabilities
   * (without deprecated functionality).
   * 
   * @since 0.1
   */
  protected boolean forwardCompatible = true;

  /**
   * Gets the screen width in pixels.
   * 
   * @return the screen width in pixels.
   * @since 0.1
   */
  public final int getWidth() {
    return width;
  }

  /**
   * Gets the screen height in pixels.
   * 
   * @return the screen height in pixels.
   * @since 0.1
   */
  public final int getHeight() {
    return height;
  }

  /**
   * Computes the aspect ratio. This method returns how many pixels are in
   * horizontal for each vertical pixel.
   * 
   * @return the aspect ratio.
   * @since 0.1
   */
  public final float getAspectRatio() {
    return (float) width / (float) height;
  }

  /**
   * Checks if the context was created with forward compatibility.
   * 
   * @return <code>true</code> if deprecated functionality has been eliminated.
   * @since 0.1
   */
  public final boolean isForwardCompatible() {
    return forwardCompatible;
  }

  /**
   * Creates a window with the specified dimentions and title.
   * 
   * @param _width
   *          width of the screen.
   * @param _height
   *          height of the screen.
   * @param title
   *          title of the screen.
   * @param _forwardCompatible
   *          if a forward compatible context must be created.
   * @param displayParent
   *          Use in Applets. Null on stand-alone applications.
   * @since 0.1
   */
  public abstract void initDisplay(final int _width, final int _height,
      final String title, final boolean _forwardCompatible, Canvas displayParent);

  /**
   * Gets the current OpenGL profile.
   * 
   * @return the current profile.
   * @since 0.1
   */
  public abstract Profile getProfile();

  /**
   * Initializes the OpenGL state.
   * 
   * @since 0.1
   */
  public abstract void initGL();

  /**
   * Closes the window.
   * 
   * @since 0.1
   */
  public abstract void closeDisplay();

  /**
   * Starts a frame.
   * 
   * @since 0.1
   */
  public abstract void initFrame();

  /**
   * Presents a frame to the screen.
   * 
   * @since 0.1
   */
  public abstract void present();

  /**
   * Activates a shader program.
   * 
   * @param shader
   *          to be activated.
   * @since 0.1
   */
  public abstract void activateShader(int shader);

  /**
   * Activates or deactivates the depth test.
   * 
   * @param enable
   *          if the test should be activated or deactivated.
   * @since 0.1
   */
  public abstract void setDepthTest(boolean enable);

  /**
   * Activates or deactivates writing to the Depth Buffer after passing the
   * depth test.
   * 
   * @param enable
   *          if z-write should be active or not.
   * @since 0.1
   */
  public abstract void setZWrite(boolean enable);

  /**
   * Sets the depth comparison operator.
   * 
   * @param function
   *          What comparison operator should be used in the depth test.
   * @since 0.1
   */
  public abstract void setDepthTest(DepthFunction function);

  /**
   * Activates or deactivates alpha blending.
   * 
   * @param enable
   *          if alpha blending should be active.
   * @since 0.1
   */
  public abstract void setAlphaBlend(boolean enable);

  /**
   * Sets the alpha blending function.
   * 
   * @param operation
   *          used in alpha blending.
   * @since 0.1
   */
  public abstract void setAlphaBlend(BlendOperation operation);

  /**
   * Sets the alpha blending function, different for the colors and alpha.
   * 
   * @param operation
   *          used in alpha blending.
   * @since 0.1
   */
  public abstract void setAlphaBlend(BlendOperationSeparate operation);

  /**
   * Activates alpha blending for a specific render target.
   * 
   * @param enable
   *          if alpha blending should be active.
   * @param renderTarget
   *          number of the render target to change alpha blending.
   * @throws IllegalStateException
   *           if this context does not implement this functionality.
   * @since 0.1
   */
  public abstract void setAlphaBlend(boolean enable, int renderTarget);

  /**
   * Sets the alpha blending function, for a specific render target.
   * 
   * @param operation
   *          used in alpha blending.
   * @param renderTarget
   *          number of the render target to change alpha blending.
   * @throws IllegalStateException
   *           if this context does not implement this functionality.
   * @since 0.1
   */
  public abstract void setAlphaBlend(BlendOperation operation, int renderTarget);

  /**
   * Sets the alpha blending function, different for the colors and alpha, for a
   * specific render target.
   * 
   * @param operation
   *          used in alpha blending.
   * @param renderTarget
   *          number of the render target to change alpha blending.
   * @throws IllegalStateException
   *           if this context does not implement this functionality.
   * @since 0.1
   */
  public abstract void setAlphaBlend(BlendOperationSeparate operation,
      int renderTarget);

  /**
   * Reset Vertex Array State.
   * 
   * @since 0.1
   */
  public abstract void noVertexArray();

  /**
   * Gets the global scene data object.
   * 
   * @return the global scene data object.
   * @since 0.1
   */
  public abstract SceneData getSceneData();

  /**
   * Checks if there are any OpenGL errors.
   * 
   * @return if there is any OpenGL Error. Those errors are logged in the
   *         RenderManager logger, with a <code>severe</code> level.
   * 
   * @since 0.1
   */
  public abstract boolean hasGLErrors();

  /**
   * Clears all OpenGL errors without logging them.
   * 
   * @since 0.1
   */
  public abstract void clearSilentlyGLErrors();

  /**
   * Gets the maximum number of instances a basic mesh can have. This method
   * computes how many instances can be successfully rendered at once with the
   * current memory constrains.
   * 
   * @return the maximum number of instances.
   * @since 0.1
   */
  public abstract int getMaxInstancesBasic();

  /**
   * Gets the maximum number of instances an animated mesh can have. This method
   * computes how many instances can be successfully rendered at once with the
   * current memory constrains.
   * 
   * @return the maximum number of instances.
   * @since 0.1
   */
  public abstract int getMaxInstancesWithBones();

  /**
   * Gets the maximum number of instances a colored material can have. This
   * method computes how many instances can be successfully rendered at once
   * with the current memory constrains.
   * 
   * @return the maximum number of instances.
   * @since 0.1
   */
  public abstract int getMaxInstancesWithColors();

  /**
   * Enumeration of possible profiles.
   * 
   * @author Isaac 'Atridas' Serrano Guash
   * @since 0.1
   * 
   */
  public static enum Profile {
    /**
     * OpenGL 2.1 context.
     * 
     * @since 0.1
     */
    GL2(2, false),
    /**
     * OpenGL 3.3 context.
     * 
     * @since 0.1
     */
    GL3(3, false),
    /**
     * OpenGL 4.2 context.
     * 
     * @since 0.1
     */
    GL4(4, false);

    /**
     * OpenGL ES 2.0 context.
     * 
     * @since 0.1
     */
    // GLES2 (2,true);

    /**
     * Current OpenGL version.
     */
    private final int glVersion;
    /**
     * Indicates if this is a ES version.
     */
    private final boolean gles;

    private Profile(int _glVersion, boolean _gles) {
      glVersion = _glVersion;
      gles = _gles;
    }

    /**
     * Gets a enumeration value from a string.
     * 
     * @param str
     *          String to be parsed.
     * @return a value of this enumeration.
     * @since 0.1
     * @throws IllegalArgumentException
     *           if the string does not have a valid value.
     */
    public static Profile getFromString(String str) {
      switch (str) {
      case "GL2":
        return GL2;
      case "GL3":
        return GL3;
      case "GL4":
        return GL4;

        // case "GLES2":
        // return GLES2;

      default:
        throw new IllegalArgumentException();
      }
    }

    /**
     * Checks if this profile supports capabilities of another profile.
     * 
     * @param other
     *          capabilities to check.
     * @return <code>true</code> if this profile supports the other.
     * @since 0.1
     */
    public boolean supports(Profile other) {
      if (glVersion < other.glVersion)
        return false;
      if (gles != other.gles)
        return false;

      return true;
    }

    /**
     * If this profile does not support another profile capabilities, throws an
     * unchecked exception.
     * 
     * @param other
     *          capabilities to check.
     * @param functionality
     *          used in the log severe message.
     * @since 0.1
     * @throws RuntimeException
     *           if the functionality is not supported.
     */
    public void supportOrException(Profile other, String functionality) {
      if (!supports(other)) {
        LOGGER.severe("Functionality " + functionality + " needs a context of "
            + other.toString() + " or greater.");
        throw new RuntimeException("Functionality " + functionality
            + " needs a context of " + other.toString() + " or greater.");
      }
    }

    @Override
    public String toString() {
      switch (this) {
      // case GLES2:
      // return "OpenGL ES 2.0";
      case GL4:
        return "OpenGL 4.2";
      case GL3:
        return "OpenGL 3.3";
      case GL2:
        return "OpenGL 2.1";
      default:
        throw new IllegalStateException();
      }
    }
  }

  /**
   * Enumeration of different depth function operators.
   * 
   * @author Isaac 'Atridas' Serrano Guasch.
   * @since 0.1
   * 
   */
  public static enum DepthFunction {
    /**
     * Depth test passes if incoming fragment's depth is less that the previous
     * fragment depth.
     * 
     * @since 0.1
     */
    LESS,
    /**
     * Depth test passes if incoming fragment's depth is greater that the
     * previous fragment depth.
     * 
     * @since 0.1
     */
    GREATER,
    /**
     * Depth test passes if incoming fragment's depth is equal that the previous
     * fragment depth.
     * 
     * @since 0.1
     */
    EQUAL,
    /**
     * Depth test passes if incoming fragment's depth is not equal that the
     * previous fragment depth.
     * 
     * @since 0.1
     */
    NOTEQUAL,
    /**
     * Depth test passes if incoming fragment's depth is less or equal that the
     * previous fragment depth.
     * 
     * @since 0.1
     */
    LEQUAL,
    /**
     * Depth test passes if incoming fragment's depth is greater or equal that
     * the previous fragment depth.
     * 
     * @since 0.1
     */
    GEQUAL,
    /**
     * Always passes depth test.
     * 
     * @since 0.1
     */
    ALWAYS,
    /**
     * Never passes depth test.
     * 
     * @since 0.1
     */
    NEVER;

    /**
     * Gets a enumeration value from a string.
     * 
     * @param str
     *          String to be parsed.
     * @return a value of this enumeration.
     * @since 0.1
     * @throws IllegalArgumentException
     *           if the string does not have a valid value.
     */
    public static DepthFunction getFromString(String str) {
      switch (str.toUpperCase()) {
      case "LESS":
        return LESS;
      case "GREATER":
        return GREATER;
      case "EQUAL":
        return EQUAL;
      case "NOTEQUAL":
        return NOTEQUAL;
      case "LEQUAL":
        return LEQUAL;
      case "GEQUAL":
        return GEQUAL;
      case "ALWAYS":
        return ALWAYS;
      case "NEVER":
        return NEVER;
      default:
        throw new IllegalArgumentException(str);
      }
    }
  }

  /**
   * <p>
   * Operator in the blending function. The blending function has the form of
   * </p>
   * <p>
   * <code>
   * result = src_operator * src + dst_operator * dst<br/>
   * result := final value.<br/>
   * src := new fragment value.<br/>
   * dst := previous fragment value.<br/>
   * xxx_operator := configurable multiplier.<br/>
   * </code>
   * </p>
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   * 
   */
  public static enum BlendOperator {
    /**
     * <code>
     * src := new fragment value.</br>
     * dst := previous fragment value.</br>
     * src1 := new fragment value destined to the first render target.</br>
     * color := (r,g,b,a)</br>
     * alpha := (a,a,a,a)</br>
     * src_saturate := i = min(src, 1 - dst) -> (i,i,i,1)</br>
     * one_minus_ := 1 - value </br>
     * constant := value specified by this aplication</br>
     * </code>
     */

    /**
     * (0,0,0,0)
     * 
     * @since 0.1
     */
    ZERO,
    /**
     * (1,1,1,1)
     * 
     * @since 0.1
     */
    ONE,
    /**
     * New fragment full color (r,g,b,a).
     * 
     * @since 0.1
     */
    SRC_COLOR,
    /**
     * New fragment alpha (a,a,a,a).
     * 
     * @since 0.1
     */
    SRC_ALPHA,
    /**
     * Previous fragment alpha (a,a,a,a).
     * 
     * @since 0.1
     */
    DST_ALPHA,
    /**
     * Previous fragment full color (r,g,b,a).
     * 
     * @since 0.1
     */
    DST_COLOR,
    /**
     * <code>i = min(src, 1 - dst) -> (i,i,i,1)</code>
     * 
     * @since 0.1
     */
    SRC_ALPHA_SATURATE,
    /**
     * Color specified by the aplication (r,g,b,a).
     * 
     * @since 0.1
     */
    CONSTANT_COLOR,
    /**
     * Alpha specified by the application (a,a,a,a).
     * 
     * @since 0.1
     */
    CONSTANT_ALPHA,
    /**
     * New fragment full color (1-r,1-g,1-b,1-a).
     * 
     * @since 0.1
     */
    ONE_MINUS_SRC_COLOR,
    /**
     * New fragment alpha (1-a,1-a,1-a,1-a).
     * 
     * @since 0.1
     */
    ONE_MINUS_SRC_ALPHA,
    /**
     * Previous fragment full color (1-r,1-g,1-b,1-a).
     * 
     * @since 0.1
     */
    ONE_MINUS_DST_COLOR,
    /**
     * Previous fragment alpha (1-a,1-a,1-a,1-a).
     * 
     * @since 0.1
     */
    ONE_MINUS_DST_ALPHA,
    /**
     * Color specified by the application (1-r,1-g,1-b,1-a).
     * 
     * @since 0.1
     */
    ONE_MINUS_CONSTANT_COLOR,
    /**
     * Alpha specified by the application (1-a,1-a,1-a,1-a).
     * 
     * @since 0.1
     */
    ONE_MINUS_CONSTANT_ALPHA,
    /**
     * New fragment full color destined to the first render target (r,g,b,a).
     * 
     * @since 0.1
     */
    SRC1_ALPHA,
    /**
     * New fragment alpha destined to the first render target (a,a,a,a).
     * 
     * @since 0.1
     */
    ONE_MINUS_SRC1_ALPHA;

    /**
     * Gets a enumeration value from a string.
     * 
     * @param str
     *          String to be parsed.
     * @return a value of this enumeration.
     * @since 0.1
     * @throws IllegalArgumentException
     *           if the string does not have a valid value.
     */
    public static BlendOperator getFromString(String str) {
      switch (str) {
      case "ZERO":
        return ZERO;
      case "ONE":
        return ONE;
      case "SRC_COLOR":
        return SRC_COLOR;
      case "SRC_ALPHA":
        return SRC_ALPHA;
      case "DST_ALPHA":
        return DST_ALPHA;
      case "DST_COLOR":
        return DST_COLOR;
      case "SRC_ALPHA_SATURATE":
        return SRC_ALPHA_SATURATE;
      case "CONSTANT_COLOR":
        return CONSTANT_COLOR;
      case "CONSTANT_ALPHA":
        return CONSTANT_ALPHA;
      case "ONE_MINUS_SRC_COLOR":
        return ONE_MINUS_SRC_COLOR;
      case "ONE_MINUS_SRC_ALPHA":
        return ONE_MINUS_SRC_ALPHA;
      case "ONE_MINUS_DST_COLOR":
        return ONE_MINUS_DST_COLOR;
      case "ONE_MINUS_DST_ALPHA":
        return ONE_MINUS_DST_ALPHA;
      case "ONE_MINUS_CONSTANT_COLOR":
        return ONE_MINUS_CONSTANT_COLOR;
      case "ONE_MINUS_CONSTANT_ALPHA":
        return ONE_MINUS_CONSTANT_ALPHA;
      case "SRC1_ALPHA":
        return SRC1_ALPHA;
      case "ONE_MINUS_SRC1_ALPHA":
        return ONE_MINUS_SRC1_ALPHA;
      default:
        throw new IllegalArgumentException();
      }
    }
  }

  /**
   * <p>
   * Operators in the blending function. The blending function has the form of
   * </p>
   * <p>
   * <code>
   * result = src_operator * src + dst_operator * dst<br/>
   * result := final value.<br/>
   * src := new fragment value.<br/>
   * dst := previous fragment value.<br/>
   * xxx_operator := configurable multiplier.<br/>
   * </code>
   * </p>
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   * @see BlendOperator
   * 
   */
  public static final class BlendOperation {
    /**
     * Operator used to multiply the new fragment value in the blending
     * function.
     * 
     * @since 0.1
     */
    public BlendOperator src_operator;
    /**
     * Operator used to multiply the previous fragment value in the blending
     * function.
     * 
     * @since 0.1
     */
    public BlendOperator dst_operator;
  }

  /**
   * Separate blending operations for the alpha channel and the other 3 colors.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   * @see BlendOperator
   * @see BlendOperation
   * 
   */
  public static final class BlendOperationSeparate {
    /**
     * Operation used in the 3 color channels.
     * 
     * @since 0.1
     */
    public BlendOperation color;
    /**
     * Operation used in the alpha channel.
     * 
     * @since 0.1
     */
    public BlendOperation alpha;
  }
}
