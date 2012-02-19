package cat.atridas.antagonista.graphics;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager.BlendOperation;
import cat.atridas.antagonista.graphics.RenderManager.BlendOperationSeparate;
import cat.atridas.antagonista.graphics.RenderManager.BlendOperator;
import cat.atridas.antagonista.graphics.RenderManager.DepthFunction;
import cat.atridas.antagonista.graphics.Shader.ShaderType;

/**
 * A shader program technique phase.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 *
 */
public abstract class TechniquePass {
  private static Logger LOGGER = Logger.getLogger(TechniquePass.class.getCanonicalName());
  
  /**
   * Maximum number of bones. Engine constrain.
   */
  public static final int MAX_BONES = 30;
  
  //Attributes --------------------------------------------------------------------------
  /**
   * Attribute layout positions.
   * @since 0.1
   */
  public static final int POSITION_ATTRIBUTE     = 0,
                          NORMAL_ATTRIBUTE       = 1,
                          TANGENT_ATTRIBUTE      = 2,
                          BITANGENT_ATTRIBUTE    = 3,
                          UV_ATTRIBUTE           = 4,
                          BLEND_INDEX_ATTRIBUTE  = 5,
                          BLEND_WEIGHT_ATTRIBUTE = 6,
                          COLOR_ATTRIBUTE        = 7;
  
  /**
   * Name of the attribute parameters in the shader source.
   * @since 0.1
   */
  public static final String POSITION_ATTRIBUTE_NAME     = "a_v3Position",
                             NORMAL_ATTRIBUTE_NAME       = "a_v3Normal",
                             TANGENT_ATTRIBUTE_NAME      = "a_v3Tangent",
                             BITANGENT_ATTRIBUTE_NAME    = "a_v3Bitangent",
                             UV_ATTRIBUTE_NAME           = "a_v2UV",
                             BLEND_INDEX_ATTRIBUTE_NAME  = "a_i4BlendIndexs",
                             BLEND_WEIGHT_ATTRIBUTE_NAME = "a_v4BlendWeights",
                             COLOR_ATTRIBUTE_NAME        = "a_v4Color";


  //Fragment data ----------------------------------------------------------------------
  /**
   * Render target layout positions.
   * @since 0.1
   */
  public static final int COLOR_FRAGMENT_DATA_LOCATION = 0;
  
  /**
   * Name of fragment output variables.
   * @since 0.1
   */
  public static final String COLOR_FRAGMENT_DATA_NAME = "f_v4Color";
  
  //Uniforms ----------------------------------------------------------------------------
  /**
   * Texture units for each texture type.
   * @since 0.1
   */
  public static final int    ALBEDO_TEXTURE_UNIT =    0,
                             NORMALMAP_TEXTURE_UNIT =    1,
                             HEIGHTMAP_TEXTURE_UNIT =    2;
  
  /**
   * Texture uniform shader names.
   * @since 0.1
   */
  public static final String ALBEDO_TEXTURE_UNIFORM    = "u_s2Albedo",
                             NORMALMAP_TEXTURE_UNIFORM = "u_s2Normalmap",
                             HEIGHTMAP_TEXTURE_UNIFORM = "u_s2Heightmap";
  
  /**
   * Uniform block binding point of the basic instance uniforms.
   * @since 0.1
   */
  public static final int    BASIC_INSTANCE_UNIFORMS_BINDING    = 0;
  /**
   * Uniform block name of the basic instance uniforms.
   * @since 0.1
   */
  public static final String BASIC_INSTANCE_UNIFORMS_BLOCK      = "UniformInstances";
  /**
   * Uniform block size of the basic instance uniforms.
   * @since 0.1
   */
  public static final int    BASIC_INSTANCE_UNIFORMS_BLOCK_SIZE = Utils.FLOAT_SIZE * (4*4) * 3; 
  //public static final String BASIC_INSTANCE_UNIFORMS_STRUCT     = "u_InstanceInfo";
  /**
   * Uniform name of the Model View Projection Matrix.
   * @since 0.1
   */
  public static final String MODEL_VIEW_PROJECTION_UNIFORM      = "u_m4ModelViewProjection";
  /**
   * Uniform name of the Model View Matrix.
   * @since 0.1
   */
  public static final String MODEL_VIEW_UNIFORM                 = "u_m4ModelView";
  /**
   * Uniform name of the Inverted-Transposed Model View Matrix.
   * @since 0.1
   */
  public static final String MODEL_VIEW_IT_UNIFORM              = "u_m4ModelViewIT";
  
  
  /**
   * Uniform block binding point of the armature uniforms.
   * @since 0.3
   */
  public static final int    ARMATURE_UNIFORMS_BINDING    = 1;
  /**
   * Uniform block name of the armature uniforms.
   * @since 0.3
   */
  public static final String ARMATURE_UNIFORMS_BLOCK      = "ArmatureInstances";
  /**
   * Uniform block size of the basic instance uniforms.
   * @since 0.3
   */
  public static final int    ARMATURE_UNIFORMS_BLOCK_SIZE = Utils.FLOAT_SIZE * (3*4) * MAX_BONES * 2; 
  //public static final String BASIC_INSTANCE_UNIFORMS_STRUCT     = "u_InstanceInfo";
  /**
   * Uniform name of the Bone Palete.
   * @since 0.3
   */
  public static final String BONE_PALETE_UNIFORM      = "u_m43BonePalete"; 
  //public static final String BASIC_INSTANCE_UNIFORMS_STRUCT     = "u_InstanceInfo";
  /**
   * Uniform name of the Bone Palete, Inverted and transposed.
   * @since 0.3
   */
  public static final String BONE_PALETE_IT_UNIFORM      = "u_m43BonePaleteIT";


  /**
   * Uniform block binding point of the special colors uniforms.
   * @since 0.1
   */
  public static final int    SPECIAL_COLORS_UNIFORMS_BINDING    = 2;
  /**
   * Uniform block name of the special colors uniforms.
   * @since 0.1
   */
  public static final String SPECIAL_COLORS_UNIFORMS_BLOCK      = "SpecialColors";
  /**
   * Uniform block size of the special colors uniforms.
   * @since 0.1
   */
  public static final int    SPECIAL_COLORS_UNIFORMS_BLOCK_SIZE = Utils.FLOAT_SIZE * 4 * 4; 
  /**
   * Uniform name of the special colors.
   * @since 0.1
   */
  public static final String SPECIAL_COLOR_0_UNIFORM            = "u_v4SpecialColor0",
                             SPECIAL_COLOR_1_UNIFORM            = "u_v4SpecialColor1",
                             SPECIAL_COLOR_2_UNIFORM            = "u_v4SpecialColor2",
                             SPECIAL_COLOR_3_UNIFORM            = "u_v4SpecialColor3";
  

  /**
   * Uniform block binding point of the basic light uniforms.
   * @since 0.1
   */
  public static final int    BASIC_LIGHT_UNIFORMS_BINDING = 3;
  /**
   * Uniform block name of the basic light uniforms.
   * @since 0.1
   */
  public static final String BASIC_LIGHT_UNIFORMS_BLOCK = "UniformLight";
  /**
   * Uniform name of the light parameters.
   * @since 0.1
   */
  public static final String AMBIENT_LIGHT_UNIFORM = "u_v3AmbientLight",
                             DIRECTIONAL_LIGHT_DIR_UNIFORM = "u_v3DirectionalLightDirection",
                             DIRECTIONAL_LIGHT_COLOR_UNIFORMS = "u_v3DirectionalLightColor";

  /**
   * Uniform block binding point of the basic material uniforms.
   * @since 0.1
   */
  public static final int    BASIC_MATERIAL_UNIFORMS_BINDING = 4;
  /**
   * Uniform block name of the basic material uniforms.
   * @since 0.1
   */
  public static final String BASIC_MATERIAL_UNIFORMS_BLOCK = "UniformMaterials";
  /**
   * Uniform name of the basic material uniforms.
   * @since 0.1
   */
  public static final String SPECULAR_FACTOR_UNIFORM = "u_fSpecularFactor",
                             SPECULAR_GLOSS_UNIFORM = "u_fGlossiness",
                             HEIGHT_UNIFORM = "u_fHeight";
  
  /**
   * Attribute layout for text rendering.
   * @since 0.1
   */
  public static final int FONT_POSITION_ATTRIBUTE = 0,
                          FONT_TEX_ATTRIBUTE = 1,
                          FONT_CHANNEL_ATTRIBUTE = 2,
                          FONT_PAGE_ATTRIBUTE = 3,
                          FONT_COLOR_ATTRIBUTE = 4;

  /**
   * Attribute names for text rendering.
   * @since 0.1
   */
  public static final String FONT_POSITION_ATTRIBUTE_NAME = "a_position",
                             FONT_TEX_ATTRIBUTE_NAME = "a_texCoord",
                             FONT_CHANNEL_ATTRIBUTE_NAME = "a_channel",
                             FONT_PAGE_ATTRIBUTE_NAME = "a_page",
                             FONT_COLOR_ATTRIBUTE_NAME = "a_color";

  /**
   * Text rendering texture units.
   * @since 0.1
   */
  public static final int FONT_TEXTURE_0_UNIT =    0,
                          FONT_TEXTURE_1_UNIT =    1,
                          FONT_TEXTURE_2_UNIT =    2,
                          FONT_TEXTURE_3_UNIT =    3;

  /**
   * Text rendering texture names.
   * @since 0.1
   */
  public static final String FONT_TEXTURE_0_UNIFORM = "u_page0",
                             FONT_TEXTURE_1_UNIFORM = "u_page1",
                             FONT_TEXTURE_2_UNIFORM = "u_page2",
                             FONT_TEXTURE_3_UNIFORM = "u_page3";

  /**
   * Text rendering uniform names.
   * @since 0.1
   */
  public static final String FONT_WVP_MATRIX_UNIFORM = "u_WorldViewProj";
  
  /**
   * OpenGL shader program identifier.
   * @since 0.1
   */
  private int shaderProgram;
  
  /**
   * OpenGL identifier for vertex stage.
   * @since 0.1
   */
  private int vs;
  /**
   * OpenGL identifier for tesselation control stage.
   * @since 0.1
   */
  private int tc;
  /**
   * OpenGL identifier for tesselation evaluation stage.
   * @since 0.1
   */
  private int te;
  /**
   * OpenGL identifier for geometry stage.
   * @since 0.1
   */
  private int gs;
  /**
   * OpenGL identifier for fragment stage.
   * @since 0.1
   */
  private int fs;
  
  /**
   * Maximum number of instances that the current memory limitations allow to draw in a single call.
   * @since 0.1
   */
  private int maxInstances = 1;
  
  /**
   * Tells if this technique is used to render text.
   * @since 0.1
   */
  protected boolean fontTechnique;
  
  /**
   * Tells if this program uses this attributes.
   * @since 0.1
   */
  protected boolean position, normal, tangents, uv, bones, colorAttr;
  
  /**
   * Tells if this program uses this uniforms.
   * @since 0.1
   */
  protected boolean albedoTexture, normalTexture, heightTexture,
                    basicInstanceUniforms,
                    armatureUniforms,
                    specialColorsUniforms,
                    basicLight,
                    basicMaterial;

  /**
   * Tells if this program uses this outputs.
   * @since 0.1
   */
  protected boolean colorResult, depthResult;
  
  /**
   * <code>true</code> if this program should change the depth test status when activated.
   * @since 0.1
   * @see #depthTestStatus
   * @see RenderManager#setDepthTest(boolean)
   */
  private boolean changeDepthTest = false;
  /**
   * New depth test status when this program is activated.
   * @since 0.1
   * @see #changeDepthTest
   * @see RenderManager#setDepthTest(boolean)
   */
  private boolean depthTestStatus;
  /**
   * <code>true</code> if this program should change the depth writing status when activated.
   * @since 0.1
   * @see #zWrite
   * @see RenderManager#setZWrite(boolean)
   */
  private boolean changeZWrite = false;
  /**
   * New depth writing status when this program is activated.
   * @since 0.1
   * @see #changeZWrite
   * @see RenderManager#setZWrite(boolean)
   */
  private boolean zWrite;
  /**
   * New depth function to set when this program is activated. Is this field is null, this program
   * should not change the depth function.
   * @since 0.1
   * @see RenderManager#setDepthTest(DepthFunction)
   */
  private DepthFunction depthFunction = null;
  

  /**
   * <code>true</code> if this program should change alpha blending status when activated.
   * @since 0.1
   * @see #alphaBlendingActive
   * @see RenderManager#setAlphaBlend(boolean)
   */
  private boolean changeAlphaBlending = false;
  /**
   * New alpha blending status when this program is activated.
   * @since 0.1
   * @see #changeAlphaBlending
   * @see RenderManager#setAlphaBlend(boolean)
   */
  private boolean alphaBlendingActive;
  /**
   * New alpha blending operation to set when this program is activated. If this field is null, this
   * program should not change the alpha blending operation.
   * 
   * @since 0.1
   * @see RenderManager#setAlphaBlend(BlendOperation)
   */
  private BlendOperation alphaOperation = null;
  /**
   * New alpha blending operation to set when this program is activated. If this field is null, this
   * program should not change the alpha blending operation.
   * 
   * @since 0.1
   * @see RenderManager#setAlphaBlend(BlendOperationSeparate)
   */
  private BlendOperationSeparate alphaOperationSeparate = null;

  /**
   * New alpha blending status when this program is activated. This variable maps each render target
   * with each new blending status.
   * 
   * @since 0.1
   * @see RenderManager#setAlphaBlend(boolean, int)
   */
  private TreeMap<Integer, Boolean> alphaBlendingPerRenderTarget = new TreeMap<>();
  /**
   * New alpha blending operation to set when this program is activated. If this field is null, this
   * program should not change the alpha blending operation. This variable maps each render target
   * with each new blending operation.
   * 
   * @since 0.1
   * @see RenderManager#setAlphaBlend(BlendOperation, int)
   */
  private TreeMap<Integer, BlendOperation> alphaBlendingOperationPerRenderTarget = new TreeMap<>();
  /**
   * New alpha blending operation to set when this program is activated. If this field is null, this
   * program should not change the alpha blending operation. This variable maps each render target
   * with each new blending operation.
   * 
   * @since 0.1
   * @see RenderManager#setAlphaBlend(BlendOperationSeparate, int)
   */
  private TreeMap<Integer, BlendOperationSeparate> alphaBlendingOperationSeparatePerRenderTarget = new TreeMap<>();
  
  /**
   * Builds a new program pass, from an xml configuration element.
   * 
   * @param techniquePassXML xml configuration element.
   * @throws AntagonistException if there was an error building the program.
   * @since 0.1
   * @see Technique#Technique(Element)
   */
  public TechniquePass(Element techniquePassXML) throws AntagonistException {
    assert techniquePassXML.getTagName().equals("pass");
    
    //setupCapabilities();
    
    vs = tc = te = gs = fs = 0;

    EffectManager em = Core.getCore().getEffectManager();
    RenderManager rm = Core.getCore().getRenderManager();
    
    boolean uniformsDefined = false;

    NodeList nl = techniquePassXML.getChildNodes();
    for(int i = 0; i < nl.getLength(); ++i) {
      Node n = nl.item(i);
      if(!(n instanceof Element))
        continue;
      Element element = ((Element)n);
      
      switch(element.getTagName()) {        
      case "vertex_shader":
        if(!uniformsDefined) {
          LOGGER.warning("Uniforms must be defined before vertex_shader");
          throw new AntagonistException();
        }
        vs = loadShader(element, ShaderType.VERTEX, em, rm);
        break;
      case "fragment_shader":
        fs = loadShader(element, ShaderType.FRAGMENT, em, rm);
        break;
        //TODO altres shaders
      case "render_states":
        loadRenderStates(element);
        break;
      case "attributes":
        loadAttributes(element);
        break;
      case "uniforms":
        loadUniforms(element, rm);
        uniformsDefined = true;
        break;
      case "results":
        loadResults(element);
        break;
      default:
        LOGGER.warning("Unrecognized tag name" + element.getTagName());
      }
    }
    
    shaderProgram = completeShaderProgram(vs, tc, te, gs, fs, rm);
  }
  
  /**
   * Builds the default program pass.
   * @since 0.1
   */
  protected TechniquePass() {
    vs = tc = te = gs = fs = 0;

    //EffectManager em = Core.getCore().getEffectManager();
    RenderManager rm = Core.getCore().getRenderManager();

    vs = getDefaultShader(ShaderType.VERTEX);
    fs = getDefaultShader(ShaderType.FRAGMENT);
    changeDepthTest = 
    depthTestStatus = 
    changeAlphaBlending =
    position =
    colorResult = true;
    alphaBlendingActive = false;
    
    try {
      shaderProgram = completeShaderProgram(vs, tc, te, gs, fs, rm);
    } catch (AntagonistException e) {
      LOGGER.severe(Utils.logExceptionStringAndStack(e));
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Builds the technique to render texts.
   * 
   * @param fontShader <code>true</code>
   * @since 0.1
   */
  protected TechniquePass(boolean fontShader) {
    assert fontShader = true;
    
    vs = tc = te = gs = fs = 0;

    //EffectManager em = Core.getCore().getEffectManager();
    RenderManager rm = Core.getCore().getRenderManager();

    vs = getFontShader(ShaderType.VERTEX);
    fs = getFontShader(ShaderType.FRAGMENT);
    gs = getFontShader(ShaderType.GEOMETRY);
    fontTechnique =
    changeZWrite =
    changeAlphaBlending = 
    alphaBlendingActive = true;

    changeDepthTest = 
    zWrite = false;
    
    alphaOperation = new BlendOperation();
    alphaOperation.src_operator = BlendOperator.SRC_ALPHA;
    alphaOperation.dst_operator = BlendOperator.ONE_MINUS_SRC_ALPHA;
    
    try {
      shaderProgram = completeShaderProgram(vs, tc, te, gs, fs, rm);
    } catch (AntagonistException e) {
      LOGGER.severe(Utils.logExceptionStringAndStack(e));
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Gets the OpenGL identifier of the font shader stage requested.
   * 
   * @param shaderType shader stage.
   * @return an OpenGL shader identifier.
   * @since 0.1
   */
  protected abstract int getFontShader(ShaderType shaderType);
  
  /**
   * Compiles a shader source. If there are any errors, this method logs them and returns a
   * default shader.
   * 
   * @param shaderXML element containing the resource name of the desired shader.
   * @param st shader stage.
   * @param em Effect Manager reference.
   * @param rm Render Manager reference.
   * @return an OpenGL identifier of the compiled shader stage.
   * @since 0.1
   */
  private int loadShader(Element shaderXML, ShaderType st, EffectManager em, RenderManager rm) {
    int shaderID = generateShaderObject(st, rm);
    String resourceName  = Utils.getStringContentFromXMLSubElement(shaderXML, "resource");
    
    String shaderSource = em.getShaderSource(new HashedString(resourceName), st);
    
    StringBuilder sb = new StringBuilder();
    sb.append(getVersionDeclaration(rm));
    sb.append('\n');

    sb.append("#define MAX_BONES ");
    sb.append(MAX_BONES);
    sb.append('\n');

    sb.append("#define MAX_INSTANCES ");
    sb.append(maxInstances);
    sb.append('\n');
    
    NodeList nl = shaderXML.getElementsByTagName("define");
    for(int i = 0; i < nl.getLength(); ++i) {
      Element defineXML = (Element)nl.item(i);
      sb.append("#define ");
      sb.append(defineXML.getTextContent());
      sb.append('\n');
    }
    
    sb.append(shaderSource);
    
    if(compileShader(shaderID, sb.toString()))
      return shaderID;
    else {
      LOGGER.warning("Loading default shader");
      deleteShader(shaderID);
      return getDefaultShader(st);
    }
  }
  
  /**
   * Loads the render states.
   * 
   * @param renderStatesXML configuration xml element.
   * @since 0.1
   * @see #changeAlphaBlending
   * @see #changeDepthTest
   * @see #changeZWrite
   * @see #depthFunction
   * @see #depthTestStatus
   * @see #zWrite
   * @see #alphaBlendingActive
   * @see #alphaBlendingOperationPerRenderTarget
   * @see #alphaBlendingOperationSeparatePerRenderTarget
   * @see #alphaBlendingPerRenderTarget
   * @see #alphaOperation
   * @see #alphaOperationSeparate
   */
  private void loadRenderStates(Element renderStatesXML) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading render states");

    NodeList nl = renderStatesXML.getChildNodes();
    for(int i = 0; i < nl.getLength(); ++i) {
      Node n = nl.item(i);
      if(!(n instanceof Element))
        continue;
      Element element = (Element)n;
      
      FIRST_SWITCH:
      switch(element.getTagName()) {
      case "depth_test":
        if(LOGGER.isLoggable(Level.CONFIG))
          LOGGER.config("Loading depth test");
        
        assert !changeDepthTest;
        switch(element.getTextContent()) {
        case "true":
          depthTestStatus = true;
          break;
        case "false":
          depthTestStatus = false;
          break;
        default:
          LOGGER.warning("Unrecognized parameter" + element.getTextContent());
          break FIRST_SWITCH;
        }
        changeDepthTest = true;
        break;
        
      case "depth_function":
        if(LOGGER.isLoggable(Level.CONFIG))
          LOGGER.config("Loading depth_function");
        assert depthFunction == null;
        try {
          if(LOGGER.isLoggable(Level.CONFIG))
            LOGGER.config("Loading depth function");
          depthFunction = DepthFunction.getFromString(element.getTextContent());
        } catch(Exception e) {
          LOGGER.warning("exception encountered: " + e.toString());
          String log = Utils.logExceptionStringAndStack(e);
          LOGGER.warning(log);
          depthFunction = null;
        }
        break;
        
      case "z_write":
        if(LOGGER.isLoggable(Level.CONFIG))
          LOGGER.config("Loading z write");
        
        assert !changeZWrite;
        switch(element.getTextContent()) {
        case "true":
          zWrite = true;
          break;
        case "false":
          zWrite = false;
          break;
        default:
          LOGGER.warning("Unrecognized parameter" + element.getTextContent());
          break FIRST_SWITCH;
        }
        changeZWrite = true;
        break;
        
      case "alpha_blending":
        if(LOGGER.isLoggable(Level.CONFIG))
          LOGGER.config("Loading alpha_blending");
        boolean activate;
        
        switch(element.getTextContent()) {
        case "true":
          activate = true;
          break;
        case "false":
          activate = false;
          break;
        default:
          LOGGER.warning("Unrecognized parameter" + element.getTextContent());
          break FIRST_SWITCH;
        }
        
        if(element.hasAttribute("render_target")) {
          int renderTarget = Integer.parseInt(element.getAttribute("render_target"));
          assert !alphaBlendingPerRenderTarget.containsKey(renderTarget);
          assert !changeAlphaBlending;
          alphaBlendingPerRenderTarget.put(renderTarget, activate);
        } else {
          assert alphaBlendingPerRenderTarget.size() == 0;
          assert !changeAlphaBlending;
          
          changeAlphaBlending = true;
          alphaBlendingActive = activate;
        }
        break;
        
      case "blend_func":
        if(LOGGER.isLoggable(Level.CONFIG))
          LOGGER.config("Loading alpha_blending");
        BlendOperation op = null;
        BlendOperationSeparate ops = null;
        
        if(element.getElementsByTagName("source").getLength() > 1) {
          //Alpha i color junt

          String src = Utils.getStringContentFromXMLSubElement(element, "source");
          String dst = Utils.getStringContentFromXMLSubElement(element, "destination");
          
          op = new BlendOperation();
          op.src_operator = BlendOperator.getFromString(src);
          op.dst_operator = BlendOperator.getFromString(dst);
          
        } else {
          //Separem alpha i color

          String srcColor = Utils.getStringContentFromXMLSubElement(element, "source_color");
          String dstColor = Utils.getStringContentFromXMLSubElement(element, "destination_color");
          String srcAlpha = Utils.getStringContentFromXMLSubElement(element, "source_alpha");
          String dstAlpha = Utils.getStringContentFromXMLSubElement(element, "destination_alpha");

          ops = new BlendOperationSeparate();
          
          ops.color = new BlendOperation();
          ops.color.src_operator = BlendOperator.getFromString(srcColor);
          ops.color.dst_operator = BlendOperator.getFromString(dstColor);
          ops.alpha = new BlendOperation();
          ops.alpha.src_operator = BlendOperator.getFromString(srcAlpha);
          ops.alpha.dst_operator = BlendOperator.getFromString(dstAlpha);
          
        }
        
        if(element.hasAttribute("render_target")) {
          int renderTarget = Integer.parseInt(element.getAttribute("render_target"));

          if(op != null) {
            assert alphaOperation == null && alphaOperationSeparate == null;
            assert !alphaBlendingOperationSeparatePerRenderTarget.containsKey(renderTarget);
            assert !alphaBlendingOperationPerRenderTarget.containsKey(renderTarget);
            
            alphaBlendingOperationPerRenderTarget.put(renderTarget, op);
          } else {
            assert alphaOperation == null && alphaOperationSeparate == null;
            assert !alphaBlendingOperationSeparatePerRenderTarget.containsKey(renderTarget);
            assert !alphaBlendingOperationPerRenderTarget.containsKey(renderTarget);
            
            alphaBlendingOperationSeparatePerRenderTarget.put(renderTarget, ops);
          }

        } else {

          if(op != null) {
            assert alphaOperation == null && alphaOperationSeparate == null;
            assert alphaBlendingOperationSeparatePerRenderTarget.size() == 0;
            assert alphaBlendingOperationPerRenderTarget.size() == 0;
            
            alphaOperation = op;
          } else {
            assert alphaOperation == null && alphaOperationSeparate == null;
            assert alphaBlendingOperationSeparatePerRenderTarget.size() == 0;
            assert alphaBlendingOperationPerRenderTarget.size() == 0;
            
            alphaOperationSeparate = ops;
          }
        }
        break;
      default:
        LOGGER.warning("Unrecognized tag name" + element.getTagName());
      }
    }
  }
  
  /**
   * Loads what attributes are active in this program.
   * 
   * @param attributesXML configuration xml element.
   * @since 0.1
   * @see #position
   * @see #normal
   * @see #tangents
   * @see #uv
   * @see #bones
   * @see #colorAttr
   */
  private void loadAttributes(Element attributesXML) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading attributes");

    NodeList nl = attributesXML.getChildNodes();
    for(int i = 0; i < nl.getLength(); ++i) {
      Node n = nl.item(i);
      if(!(n instanceof Element))
        continue;
      Element element = (Element)n;
      
      switch(element.getTagName()) {
      case "position":
        assert !position;
        position = true;
        break;
      case "normal":
        assert !normal;
        normal = true;
        break;
      case "tangents":
        assert !tangents;
        tangents = true;
        break;
      case "uv":
        assert !uv;
        uv = true;
        break;
      case "bones":
        assert !bones;
        bones = true;
        break;
      case "color":
        assert !colorAttr;
        colorAttr = true;
        break;
      default:
        LOGGER.warning("Unrecognized tag name " + element.getTagName());
      }
    }
  }
  
  /**
   * Loads what uniforms are active in this program.
   * 
   * @param uniformsXML configuration xml element.
   * @param rm Render Manager reference.
   * @since 0.1
   * @see #albedoTexture
   * @see #normalTexture
   * @see #heightTexture
   * @see #basicInstanceUniforms
   * @see #specialColorsUniforms
   * @see #basicMaterial
   */
  private void loadUniforms(Element uniformsXML, RenderManager rm) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading uniforms");

    //long maxUniformBufferSize = getMaxUniformBufferSize();
    
    NodeList nl = uniformsXML.getChildNodes();
    for(int i = 0; i < nl.getLength(); ++i) {
      Node n = nl.item(i);
      if(!(n instanceof Element))
        continue;
      Element element = (Element)n;
      
      switch(element.getTagName()) {
      case "albedo_texture":
        assert !albedoTexture;
        albedoTexture = true;
        break;
      case "normal_texture":
        assert !normalTexture;
        normalTexture = true;
        break;
      case "height_texture":
        assert !heightTexture;
        heightTexture = true;
        break;
      case "basic_instance_uniforms":
        assert !basicInstanceUniforms;
        basicInstanceUniforms = true;
        int newMaxInstances = rm.getMaxInstancesBasic();
        if(maxInstances == 1 || newMaxInstances < maxInstances) {
          maxInstances = newMaxInstances;
        }
        break;
      case "armature_uniforms":
        assert !armatureUniforms;
        armatureUniforms = true;
        newMaxInstances = rm.getMaxInstancesWithBones();
        if(maxInstances == 1 || newMaxInstances < maxInstances) {
          maxInstances = newMaxInstances;
        }
        break;
      case "special_colors":
        assert !specialColorsUniforms;
        specialColorsUniforms = true;
        newMaxInstances = rm.getMaxInstancesWithColors();
        if(maxInstances == 1 || newMaxInstances < maxInstances) {
          maxInstances = newMaxInstances;
        }
        break;
      case "basic_light":
        assert !basicLight;
        basicLight = true;
        break;
      case "basic_material":
        assert !basicMaterial;
        basicMaterial = true;
        break;
      default:
        LOGGER.warning("Unrecognized tag name " + element.getTagName());
      }
    }
  }

  /**
   * Loads what results are active in this program.
   * 
   * @param resultsXML configuration xml element.
   * @since 0.1
   * @see #colorResult
   * @see #depthResult
   */
  private void loadResults(Element resultsXML) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading uniforms");

    NodeList nl = resultsXML.getChildNodes();
    for(int i = 0; i < nl.getLength(); ++i) {
      Node n = nl.item(i);
      if(!(n instanceof Element))
        continue;
      Element element = (Element)n;
      
      switch(element.getTagName()) {
      case "color":
        assert !colorResult;
        colorResult = true;
        break;
      case "depth":
        assert !depthResult;
        depthResult = true;
        break;
      default:
        LOGGER.warning("Unrecognized tag name" + element.getTagName());
      }
    }
  }

  //protected abstract void setupCapabilities();
  /**
   * Generates a new OpenGL shader stage identifier.
   * @param st shader stage.
   * @param rm Render Manager reference.
   * @return a new OpenGL shader stage identifier.
   * @since 0.1
   */
  protected abstract int generateShaderObject(ShaderType st, RenderManager rm);
  /**
   * Deletes a shader stage object.
   * 
   * @param shaderID OpenGL shader stage identifier.
   * @since 0.1
   */
  protected abstract void deleteShader(int shaderID);

  /**
   * Gets the shader source GLSL version declaration.
   * @param rm Render Manager reference.
   * @return "#version ???" current GLSL version.
   * @since 0.1
   */
  protected abstract String getVersionDeclaration(RenderManager rm);
  /**
   * Compiles a shader.
   * 
   * @param shaderID OpenGL shader stage identifier.
   * @param source of the shader.
   * @return if the shader was successfully compiled.
   * @since 0.1
   */
  protected abstract boolean compileShader(int shaderID, String source);
  
  /**
   * Links all shader stages.
   * 
   * @param vs vertex shader OpenGL identifier.
   * @param tc tesselation control shader OpenGL identifier.
   * @param te tesselation evaulation shader OpenGL identifier.
   * @param gs geometry shader OpenGL identifier.
   * @param fs fragment shader OpenGL identifier.
   * @param rm Render Manager reference.
   * @return the successfully linked shader program OpenGL identifier.
   * @throws AntagonistException if the linking operation fails.
   * @since 0.1
   */
  protected abstract int completeShaderProgram(int vs, int tc, int te, int gs, int fs, RenderManager rm) throws AntagonistException;
  
  /**
   * Deletes a shader program.
   * 
   * @param shaderProgramID shader program OpenGL identifier.
   * @since 0.1
   */
  protected abstract void deleteShaderProgram(int shaderProgramID);
  
  /**
   * Gets the default shader for a specified stage.
   * 
   * @param st shader stage.
   * @return a default shader program, precompiled.
   * @since 0.1
   */
  protected abstract int getDefaultShader(ShaderType st);
  
  /**
   * Fetches the specular factor uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getSpecularFactorUniform();
  /**
   * Fetches the specular glossiness uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getSpecularGlossinessUniform();
  /**
   * Fetches the height uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getHeightUniform();

  /**
   * Fetches the ambient light color uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getAmbientLightColorUniform();
  /**
   * Fetches the directional light direction uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getDirectionalLightDirectionUniform();
  /**
   * Fetches the directional light color uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getDirectionalLightColorUniform();

  /**
   * Fetches the model view projection matrix uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getModelViewProjectionUniform();
  /**
   * Fetches the model view matrix uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getModelViewUniform();
  /**
   * Fetches the model view inverse-transposed matrix uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getModelViewITUniform();

  /**
   * Fetches the special color 0 uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getSpecialColor0Uniform();
  /**
   * Fetches the special color 1 uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getSpecialColor1Uniform();
  /**
   * Fetches the special color 2 uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getSpecialColor2Uniform();
  /**
   * Fetches the special color 3 uniform binding point.
   * 
   * @return the uniform binding point.
   * @since 0.1
   */
  public abstract int getSpecialColor3Uniform();

  /**
   * Tells if this program uses basic instance uniforms. 
   * 
   * @return if this program uses this uniforms.
   * @since 0.1
   */
  public boolean hasBasicInstanceUniforms() {
    return basicInstanceUniforms;
  }
  /**
   * Tells if this program uses special colors uniforms. 
   * 
   * @return if this program uses this uniforms.
   * @since 0.1
   */
  public boolean hasSpecialColorsUniforms() {
    return specialColorsUniforms;
  }
  /**
   * Tells if this program uses basic material uniforms. 
   * 
   * @return if this program uses this uniforms.
   * @since 0.1
   */
  public boolean hasBasicMaterialUniforms() {
    return basicMaterial;
  }
  /**
   * Tells if this program uses basic light uniforms. 
   * 
   * @return if this program uses this uniforms.
   * @since 0.1
   */
  public boolean hasBasicLightUniforms() {
    return basicLight;
  }
  
  /**
   * Activates this program pass. This method sets the shader program active and changes some 
   * render states according to the configuration.
   * 
   * @param rm Render Manager reference.
   * @since 0.1
   */
  public void activate(RenderManager rm) {
    assert !cleaned;
    rm.activateShader(shaderProgram);
    
    if(changeDepthTest)
      rm.setDepthTest(depthTestStatus);
    if(depthFunction != null)
      rm.setDepthTest(depthFunction);
    if(changeZWrite)
      rm.setZWrite(zWrite);
    
    if(changeAlphaBlending)
      rm.setAlphaBlend(alphaBlendingActive);
    else {
      for(Entry<Integer, Boolean> alphaActive : alphaBlendingPerRenderTarget.entrySet()) {
        rm.setAlphaBlend(alphaActive.getValue(), alphaActive.getKey());
      }
    }
    
    if(alphaOperation != null)
      rm.setAlphaBlend(alphaOperation);
    else if(alphaOperationSeparate != null)
      rm.setAlphaBlend(alphaOperationSeparate);
    else {
      for(Entry<Integer, BlendOperation> blendOperation :  alphaBlendingOperationPerRenderTarget.entrySet()) {
        rm.setAlphaBlend(blendOperation.getValue(), blendOperation.getKey());
      }
      for(Entry<Integer, BlendOperationSeparate> blendOperation :  alphaBlendingOperationSeparatePerRenderTarget.entrySet()) {
        rm.setAlphaBlend(blendOperation.getValue(), blendOperation.getKey());
      }
    }
    
    //TODO atributs i coses d'aquestes, o no cal?
    assert !Utils.hasGLErrors();
  }
  
  
  
  
  /**
   * variable that checks if this object has been cleaned.
   * @since 0.1
   */
  protected boolean cleaned = false;
  /**
   * Method that cleans up the OpenGL states.
   * @since 0.1
   */
  public final void cleanUp() {
    assert !cleaned;
    if(vs != 0)
      deleteShader(vs);
    if(tc != 0)
      deleteShader(tc);
    if(te != 0)
      deleteShader(te);
    if(gs != 0)
      deleteShader(gs);
    if(fs != 0)
      deleteShader(fs);
    if(shaderProgram != 0)
      deleteShaderProgram(shaderProgram);
    
    cleaned = true;
  }
  
  
  @Override
  public void finalize() {
    if(!cleaned) {
      cleanUp();
    }
  }
  
}
