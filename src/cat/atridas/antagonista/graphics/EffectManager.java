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
import cat.atridas.antagonista.graphics.gl2.TechniquePassGL2;

public class EffectManager extends ResourceManager<Effect> {
  private static Logger LOGGER = Logger.getLogger(EffectManager.class.getCanonicalName());
  
  
  private ShaderManager vertexShaderManager, 
                        fragmentShaderManager, 
                        geometryShaderManager,
                        tessControlShaderManager,
                        tessEvalShaderManager;
  
  private Effect defaultResource;
  private TechniquePass fontPass;
  
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
      
      setBasePath( effectsXML.getAttribute("path") );
      
      ArrayList<HashedString> extensions = new ArrayList<>();
      
      String[] extensionsArray = effectsXML.getAttribute("extensions").split(",");
      for(String extension : extensionsArray) {
        extensions.add(new HashedString(extension));
      }
      
      setExtensions(extensions);
      
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
        ArrayList<HashedString> shaderExtensions = new ArrayList<>();
        
        String[] extensionsArray1 = shaderConfigXML.getAttribute("extensions").split(",");
        for(String extension : extensionsArray1) {
          shaderExtensions.add(new HashedString(extension));
        }
        
        switch(shaderConfigXML.getTagName()) {
        case "vertex_shaders":
          assert vertexShaderManager == null;
          vertexShaderManager = new ShaderManager.Vertex(path, shaderExtensions);
          break;
        case "tesselation_control_shaders":
          assert tessControlShaderManager == null;
          tessControlShaderManager = new ShaderManager.TessControl(path, shaderExtensions);
          break;
        case "tesselation_evaluation_shaders":
          assert tessEvalShaderManager == null;
          tessEvalShaderManager = new ShaderManager.TessEval(path, shaderExtensions);
          break;
        case "geometry_shaders":
          assert geometryShaderManager == null;
          geometryShaderManager = new ShaderManager.Geometry(path, shaderExtensions);
          break;
        case "fragment_shaders":
          assert fragmentShaderManager == null;
          fragmentShaderManager = new ShaderManager.Fragment(path, shaderExtensions);
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

      assert !Utils.hasGLErrors();
      
      nl = effectsXML.getElementsByTagName("effect");
      for(int i = 0; i < nl.getLength(); ++i) {
        Element effectXML = (Element)nl.item(i);
        String name = effectXML.getAttribute("name");
        
        getResource(new HashedString(name));
        assert !Utils.hasGLErrors();
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
    
    defaultResource = new Effect(Utils.DEFAULT);
    defaultResource.loadDefault();
    
    //TODO millor
    fontPass = new TechniquePassGL2(true);
    
    assert !Utils.hasGLErrors();
  }
  
  public TechniquePass getFontPass() {
    return fontPass;
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
  protected Effect createNewResource(HashedString resourceName) {
    assert isInit;
    return new Effect(resourceName);
  }

  @Override
  public Effect getDefaultResource() {
    assert isInit;
    assert defaultResource != null;
    return defaultResource;
  }

  public TechniquePass getDefaultTechnique() {
    // TODO
    return null;
  }
  
  private static abstract class ShaderManager extends ResourceManager<Shader> {

    private final ShaderType type;
    private final Shader defaultShader;
    
    ShaderManager(String _basePath, ArrayList<HashedString> _extensionsPriorized, ShaderType _type) {
      super( _basePath, _extensionsPriorized );
      type = _type;
      defaultShader = new Shader(Utils.DEFAULT, type);
      defaultShader.loadDefault();
    }

    @Override
    protected Shader createNewResource(HashedString resourceName) {
      return new Shader(resourceName, type);
    }

    @Override
    public Shader getDefaultResource() {
      return defaultShader;
    }
    

    static class Vertex extends ShaderManager {
      Vertex(String _basePath, ArrayList<HashedString> _extensionsPriorized) {
        super(_basePath, _extensionsPriorized, ShaderType.VERTEX);
      }
    }
    
    static class Fragment extends ShaderManager {
      Fragment(String _basePath, ArrayList<HashedString> _extensionsPriorized) {
        super(_basePath, _extensionsPriorized, ShaderType.FRAGMENT);
      }
    }
    
    static class Geometry extends ShaderManager {
      Geometry(String _basePath, ArrayList<HashedString> _extensionsPriorized) {
        super(_basePath, _extensionsPriorized, ShaderType.GEOMETRY);
      }
    }
    
    static class TessEval extends ShaderManager {
      TessEval(String _basePath, ArrayList<HashedString> _extensionsPriorized) {
        super(_basePath, _extensionsPriorized, ShaderType.TESS_EVALUATION);
      }
    }
    
    static class TessControl extends ShaderManager {
      TessControl(String _basePath, ArrayList<HashedString> _extensionsPriorized) {
        super(_basePath, _extensionsPriorized, ShaderType.TESS_CONTROL);
      }
    }
    
  }
}
