package cat.atridas.antagonista.graphics;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager.BlendOperation;
import cat.atridas.antagonista.graphics.RenderManager.BlendOperationSeparate;
import cat.atridas.antagonista.graphics.RenderManager.BlendOperator;
import cat.atridas.antagonista.graphics.RenderManager.DepthFunction;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.Shader.ShaderType;

public abstract class Technique {
  private static Logger LOGGER = Logger.getLogger(Technique.class.getCanonicalName());
  
  private int shaderProgram;
  private int vs, tc, te, gs, fs;
  
  private boolean changeDepthTest = false;
  private boolean depthTestStatus;
  private DepthFunction depthFunction = null;
  
  private boolean changeAlphaBlending = false;
  private boolean alphaBlendingActive;
  private BlendOperation alphaOperation = null;
  private BlendOperationSeparate alphaOperationSeparate = null;

  private TreeMap<Integer, Boolean> alphaBlendingPerRenderTarget = new TreeMap<>();
  private TreeMap<Integer, BlendOperation> alphaBlendingOperationPerRenderTarget = new TreeMap<>();
  private TreeMap<Integer, BlendOperationSeparate> alphaBlendingOperationSeparatePerRenderTarget = new TreeMap<>();
  
  Technique(Element techniqueXML) throws AntagonistException {
    assert techniqueXML.getTagName().equals("technique");
    
    vs = tc = te = gs = fs = 0;

    EffectManager em = Core.getCore().getEffectManager();
    RenderManager rm = Core.getCore().getRenderManager();

    NodeList nl = techniqueXML.getChildNodes();
    for(int i = 0; i < nl.getLength(); ++i) {
      Element element = ((Element)nl.item(i));
      
      switch(element.getTagName()) {
      case "min_version":
        String versionString = element.getTextContent();
        
        Profile p = Profile.getFromString(versionString);
        if(!rm.getProfile().supports(p)) {
          LOGGER.warning("This technique needs a profile compatible with " + p);
          throw new AntagonistException();
        }
        
      case "vertex_shader":
        vs = loadShader(element, ShaderType.VERTEX, em);
        break;
      case "fragment_shader":
        vs = loadShader(element, ShaderType.FRAGMENT, em);
        break;
        //TODO altres shaders
      case "render_states":
        loadRenderStates(element);
        break;
      case "attributes":
        loadAttributes(element);
        break;
      case "uniforms":
        loadUniforms(element);
        break;
      case "results":
        loadResults(element);
        break;
      default:
        LOGGER.warning("Unrecognized tag name" + element.getTagName());
      }
    }
  }
  
  private int loadShader(Element shaderXML, ShaderType st, EffectManager em) {
    int shaderID = generateShaderObject(st);
    String resourceName  = Utils.getStringContentFromXMLSubElement(shaderXML, "resource");
    
    String shaderSource = em.getShaderSource(new HashedString(resourceName), st);
    
    StringBuilder sb = new StringBuilder();
    sb.append(getVersionDeclaration());
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
  
  private void loadRenderStates(Element renderStatesXML) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading render states");

    NodeList nl = renderStatesXML.getChildNodes();
    for(int i = 0; i < nl.getLength(); ++i) {
      Element element = ((Element)nl.item(i));
      
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
          depthFunction = null;
        }
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
          op.src = BlendOperator.getFromString(src);
          op.dst = BlendOperator.getFromString(dst);
          
        } else {
          //Separem alpha i color

          String srcColor = Utils.getStringContentFromXMLSubElement(element, "source_color");
          String dstColor = Utils.getStringContentFromXMLSubElement(element, "destination_color");
          String srcAlpha = Utils.getStringContentFromXMLSubElement(element, "source_alpha");
          String dstAlpha = Utils.getStringContentFromXMLSubElement(element, "destination_alpha");

          ops = new BlendOperationSeparate();
          
          ops.color = new BlendOperation();
          ops.color.src = BlendOperator.getFromString(srcColor);
          ops.color.dst = BlendOperator.getFromString(dstColor);
          ops.alpha = new BlendOperation();
          ops.alpha.src = BlendOperator.getFromString(srcAlpha);
          ops.alpha.dst = BlendOperator.getFromString(dstAlpha);
          
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
      }
    }
  }
  
  private void loadAttributes(Element attributesXML) {
    //TODO
  }
  
  private void loadUniforms(Element uniformsXML) {
    //TODO
  }
  
  private void loadResults(Element resultsXML) {
    //TODO
  }

  protected abstract int generateShaderObject(ShaderType st);
  protected abstract void deleteShader(int shaderID);

  protected abstract String getVersionDeclaration();
  protected abstract boolean compileShader(int shaderID, String source);
  
  protected abstract int completeShaderProgram(int vs, int tc, int te, int gs, int fs) throws AntagonistException;
  protected abstract void deleteShaderProgram(int shaderProgramID);
  
  protected abstract int getDefaultShader(ShaderType st);
  
  
  
  public void activate(RenderManager rm) {
    assert !cleaned;
    rm.activateShader(shaderProgram);
    
    if(changeDepthTest)
      rm.setDepthTest(depthTestStatus);
    if(depthFunction != null)
      rm.setDepthTest(depthFunction);
    
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
  }
  
  
  
  

  protected boolean cleaned = false;
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
