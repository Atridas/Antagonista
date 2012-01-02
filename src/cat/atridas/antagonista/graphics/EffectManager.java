package cat.atridas.antagonista.graphics;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.Shader.ShaderType;

public class EffectManager extends ResourceManager<Effect> {
  private static Logger LOGGER = Logger.getLogger(EffectManager.class.getCanonicalName());
  
  
  private String basePath;
  private ArrayList<String> extensions;
  
  private ShaderManager vertexShaderManager, 
                        fragmentShaderManager, 
                        geometryShaderManager,
                        tessControlShaderManager,
                        tessEvalShaderManager;
  
  boolean isInit;
  
  public void init(String configFile, RenderManager rm) {
    assert !isInit;
    isInit = true;
    
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Creating EffectManager from file " + configFile);
    
    try {
      InputStream is = Utils.findInputStream(configFile);
      
      DocumentBuilder db;
      db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = db.parse(is);
      doc.getDocumentElement().normalize();
      
      
      Element effectsXML = doc.getDocumentElement();
      if("effects".compareTo(effectsXML.getTagName()) != 0) {
        LOGGER.severe("Root element is not \"effects\".");
        throw new Exception();
      }
      
      basePath = effectsXML.getAttribute("path");
      
      extensions = new ArrayList<>();
      
      String[] extensionsArray = effectsXML.getAttribute("extensions").split(",");
      for(String extension : extensionsArray) {
        extensions.add(extension);
      }
      
      
      Element shadersXML = (Element)effectsXML.getElementsByTagName("shaders").item(0);
      if(shadersXML == null) {
        LOGGER.severe("Element \"shaders\" not found");
        throw new Exception();
      }
      
      NodeList nl = shadersXML.getChildNodes();
      for(int i = 0; i < nl.getLength(); ++i) {
        Node n = nl.item(i);
        if(!(n instanceof Element)) {
          continue;
        }
        Element shaderConfigXML = (Element)n;
        
        String path = shaderConfigXML.getAttribute("path");
        ArrayList<String> extensions = new ArrayList<>();
        
        String[] extensionsArray1 = shaderConfigXML.getAttribute("extensions").split(",");
        for(String extension : extensionsArray1) {
          extensions.add(extension);
        }
        
        switch(shaderConfigXML.getTagName()) {
        case "vertex_shaders":
          assert vertexShaderManager == null;
          vertexShaderManager = new ShaderManager.Vertex(path, extensions);
          break;
        case "tesselation_control_shaders":
          assert tessControlShaderManager == null;
          tessControlShaderManager = new ShaderManager.TessControl(path, extensions);
          break;
        case "tesselation_evaluation_shaders":
          assert tessEvalShaderManager == null;
          tessEvalShaderManager = new ShaderManager.TessEval(path, extensions);
          break;
        case "geometry_shaders":
          assert geometryShaderManager == null;
          geometryShaderManager = new ShaderManager.Geometry(path, extensions);
          break;
        case "fragment_shaders":
          assert fragmentShaderManager == null;
          fragmentShaderManager = new ShaderManager.Fragment(path, extensions);
          break;
        default:
          LOGGER.severe("Unrecognized tag name" + shaderConfigXML.getTagName());
          throw new Exception();
        }
      }
      
      assert vertexShaderManager      != null && 
             tessControlShaderManager != null && 
             tessEvalShaderManager    != null && 
             geometryShaderManager    != null && 
             fragmentShaderManager    != null;
      
      
      nl = effectsXML.getElementsByTagName("effect");
      for(int i = 0; i < nl.getLength(); ++i) {
        Element effectXML = (Element)nl.item(i);
        String name = effectXML.getAttribute("name");
        
        getResource(new HashedString(name));
      }
      
    } catch (FileNotFoundException e) {
      LOGGER.severe("Could not find input file");
      isInit = false;
      throw new IllegalArgumentException(e);
    } catch (Exception e) {
      LOGGER.severe("Error reading xml file");
      isInit = false;
      throw new IllegalArgumentException(e);
    }
  }
  
  
  public String getShaderSource(HashedString shader, ShaderType st) {
    assert isInit;
    switch(st) {
    case VERTEX:
      return vertexShaderManager.getResource(shader).getSource();
    case TESS_CONTROL:
      return tessControlShaderManager.getResource(shader).getSource();
    case TESS_EVALUATION:
      return tessEvalShaderManager.getResource(shader).getSource();
    case GEOMETRY:
      return geometryShaderManager.getResource(shader).getSource();
    case FRAGMENT:
      return fragmentShaderManager.getResource(shader).getSource();
    default:
      throw new IllegalArgumentException();
    }
  }
  
  
  public String getDefaultShaderSource(ShaderType st) {
    assert isInit;
    switch(st) {
    case VERTEX:
      return vertexShaderManager.getDefaultResource().getSource();
    case TESS_CONTROL:
      return tessControlShaderManager.getDefaultResource().getSource();
    case TESS_EVALUATION:
      return tessEvalShaderManager.getDefaultResource().getSource();
    case GEOMETRY:
      return geometryShaderManager.getDefaultResource().getSource();
    case FRAGMENT:
      return fragmentShaderManager.getDefaultResource().getSource();
    default:
      throw new IllegalArgumentException();
    }
  }
  
  @Override
  protected String getBasePath() {
    assert isInit;
    return basePath;
  }

  @Override
  protected ArrayList<String> getExtensionsPriorized() {
    assert isInit;
    return extensions;
  }

  @Override
  protected Effect createNewResource(HashedString resourceName) {
    assert isInit;
    return new Effect(resourceName);
  }

  @Override
  protected Effect getDefaultResource() {
    assert isInit;
    // TODO Auto-generated method stub
    return null;
  }

  public Technique getDefaultTechnique() {
    // TODO
    return null;
  }
  
  private static abstract class ShaderManager extends ResourceManager<Shader> {

    private final String basePath;
    private final ArrayList<String> extensionsPriorized;
    private final ShaderType type;
    private final Shader defaultShader;
    
    ShaderManager(String _basePath, ArrayList<String> _extensionsPriorized, ShaderType _type) {
      basePath = _basePath;
      extensionsPriorized = _extensionsPriorized;
      type = _type;
      defaultShader = new Shader(HashedString.DEFAULT, type);
      defaultShader.loadDefault();
    }
    
    @Override
    protected String getBasePath() {
      return basePath;
    }

    @Override
    protected ArrayList<String> getExtensionsPriorized() {
      return extensionsPriorized;
    }

    @Override
    protected Shader createNewResource(HashedString resourceName) {
      return new Shader(resourceName, type);
    }

    @Override
    protected Shader getDefaultResource() {
      return defaultShader;
    }
    

    static class Vertex extends ShaderManager {
      Vertex(String _basePath, ArrayList<String> _extensionsPriorized) {
        super(_basePath, _extensionsPriorized, ShaderType.VERTEX);
      }
    }
    
    static class Fragment extends ShaderManager {
      Fragment(String _basePath, ArrayList<String> _extensionsPriorized) {
        super(_basePath, _extensionsPriorized, ShaderType.FRAGMENT);
      }
    }
    
    static class Geometry extends ShaderManager {
      Geometry(String _basePath, ArrayList<String> _extensionsPriorized) {
        super(_basePath, _extensionsPriorized, ShaderType.GEOMETRY);
      }
    }
    
    static class TessEval extends ShaderManager {
      TessEval(String _basePath, ArrayList<String> _extensionsPriorized) {
        super(_basePath, _extensionsPriorized, ShaderType.TESS_EVALUATION);
      }
    }
    
    static class TessControl extends ShaderManager {
      TessControl(String _basePath, ArrayList<String> _extensionsPriorized) {
        super(_basePath, _extensionsPriorized, ShaderType.TESS_CONTROL);
      }
    }
    
  }
}
