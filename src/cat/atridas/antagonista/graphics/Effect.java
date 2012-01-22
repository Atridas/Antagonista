package cat.atridas.antagonista.graphics;

import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager.Profile;

public class Effect extends Resource {
  private static Logger LOGGER = Logger.getLogger(EffectManager.class.getCanonicalName());

  private static final HashedString HS_XML = new HashedString("xml");
  
  private HashMap<TechniqueType, HashMap<Quality, Technique>> techniques = new HashMap<>();

  public Effect(HashedString _resourceName) {
    super(_resourceName);
  }
  
  @Override
  public boolean load(InputStream is, HashedString extension) {
    assert HS_XML.equals(extension);
    
    LOGGER.config("Loading effect [" + resourceName + "]");
    
    EffectManager em = Core.getCore().getEffectManager();
    
    
    try {      
      DocumentBuilder db;
      db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = db.parse(is);
      doc.getDocumentElement().normalize();
      
      
      Element effectXML = doc.getDocumentElement();
      
      String myName = effectXML.getAttribute("name");
      
      NodeList nl = effectXML.getElementsByTagName("technique");
      for(int i = 0; i < nl.getLength(); ++i) {
        Element techniqueXML = (Element)nl.item(i);
        TechniqueType techniqueType = TechniqueType.parseString(techniqueXML.getAttribute("type"));
        
        Quality q;
        if(techniqueXML.hasAttribute("quality")) {
          q = Quality.parseString(techniqueXML.getAttribute("quality"));
        } else {
          q = Quality.NONE;
        }
        
        LOGGER.config("Reading technique " + techniqueType + " with quality " + q);
        
        Technique technique;
        if(techniqueXML.getAttribute("external").toLowerCase().compareTo("true") == 0) {
          //External
          String effectName = techniqueXML.getAttribute("ref_effect");
          TechniqueType refTechniqueType = TechniqueType.parseString(techniqueXML.getAttribute("ref_type"));
          
          Quality refQ;
          if(techniqueXML.hasAttribute("ref_quality")) {
            refQ = Quality.parseString(techniqueXML.getAttribute("ref_quality"));
          } else {
            refQ = Quality.NONE;
          }
          
          Effect refEffect;
          if(effectName.compareTo(myName) == 0) {
            refEffect = this;
          } else {
            refEffect = em.getResource(new HashedString(effectName));
          }
          
          HashMap<Quality, Technique> qToTech = refEffect.techniques.get(refTechniqueType);
          if(qToTech == null) {
            throw new IllegalArgumentException("Technique " + refTechniqueType + " of effect " + effectName + " does not exist (yet).");
          }
          
          technique = qToTech.get(refQ);
          if(technique == null) {
            throw new IllegalArgumentException("Technique " + refTechniqueType + " with quality " + refQ + " of effect " + effectName + " does not exist (yet).");
          }
          
        } else {
          Profile p = Profile.getFromString(techniqueXML.getAttribute("min_version"));
          if(Utils.supports(p)) {
            technique = new Technique(techniqueXML);
            assert !Utils.hasGLErrors();
          } else {
            technique = null;
          }
        }
        
        HashMap<Quality, Technique> qToTech = techniques.get(techniqueType);
        if(qToTech == null) {
          qToTech = new HashMap<>();
          techniques.put(techniqueType, qToTech);
        }
        
        if(qToTech.containsKey(q)) {
          throw new IllegalArgumentException("Technique " + techniqueType + " with quality " + q + " defined twice.");
        }
        if(technique != null)
          qToTech.put(q, technique);
      }

      assert !Utils.hasGLErrors();
    } catch(Exception e) {
      LOGGER.warning(Utils.logExceptionStringAndStack(e));
      Utils.hasGLErrors();
      return false;
    }
    
    
    
    return true;
  }
  
  void loadDefault() {
    for(TechniqueType tt : TechniqueType.values()) {
      
      Technique technique = new Technique();
      HashMap<Quality, Technique> qToTech = new HashMap<>();
      qToTech.put(Quality.LOW, technique);
      techniques.put(tt, qToTech);
    }
  }

  
  public Technique getTechnique(TechniqueType tt, Quality q) {
    HashMap<Quality, Technique> qToTech = techniques.get(tt);
    if(qToTech != null) {
      Technique technique = qToTech.get(q);
      while(technique == null && q != Quality.NONE) {
        q = q.previousQuality();
        technique = qToTech.get(q);
      }
      return technique;
    }
    return null;
  }
  
  
  @Override
  public int getRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getVRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    /* TODO mirar com fer-ho amb les "compartides"
    for(Entry<TechniqueType, HashMap<Quality, Technique>> qToTech : techniques.entrySet()) {
      for(Entry<Quality, Technique> tech : qToTech.getValue().entrySet()) {
        tech.getValue().cleanUp();
      }
    }
    */
    techniques.clear();
  }
  
  
  public static enum TechniqueType {
    FORWARD, DEFERRED, SHADOW, PARTICLE;
    

    public static TechniqueType parseString(String str) {
      switch(str.toUpperCase()) {
      case "FORWARD":
        return FORWARD;
      case "DEFERRED":
        return DEFERRED;
      case "SHADOW":
        return SHADOW;
      case "PARTICLE":
        return PARTICLE;
      default:
        throw new IllegalStateException(str);
      }
    }
    
    @Override
    public String toString() {
      switch(this) {
      case FORWARD:
        return "FORWARD";
      case DEFERRED:
        return "DEFERRED";
      case SHADOW:
        return "SHADOW";
      case PARTICLE:
        return "PARTICLE";
      default:
        throw new IllegalStateException();
      }
    }
  }

}
