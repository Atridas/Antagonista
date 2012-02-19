package cat.atridas.antagonista.graphics;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
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

/**
 * <p>
 * This class encapsulates an effect. A effect is a set of Techniques, classified by type and
 * quality.
 * </p>
 * <p>
 * The format of the effect file is a xml file with the next structure:
 * </p>
 * <p>
 * </code>
 * <pre>
 * &lt;technique type="" quality=""&gt;
 *  types:
 *  -&gt; "forward"
 *  -&gt; "animated_forward"
 *  -&gt; "shadow"
 *  -&gt; "particle"
 *  -&gt; "deferred"
 *  -&gt; "animated_deferred"
 *  
 *  qualities. Si es demana una inexistent, es cau cap avall. Per defecte és "none"
 *  -&gt; "none"
 *  -&gt; "low"
 *  -&gt; "mid"
 *  -&gt; "hight"
 *  -&gt; "ultra"
 *      
 *  &lt;min_version&gt;GL2&lt;/min_version&gt;       -&gt; GL2, GL3, GL4, GLSL2
 *  &lt;pass&gt;
 *    &lt;render_states&gt;
 *      &lt;depth_test&gt;true&lt;/depth_test&gt;
 *      &lt;depth_function&gt;LESS&lt;/depth_function&gt; -&gt; LESS, GREATER, EQUAL, NOTEQUAL, LEQUAL, GEQUAL, ALWAYS, NEVER
 *      &lt;z_write&gt;true&lt;/z_write&gt;
 *      
 *      &lt;alpha_blending render_target=""&gt;false&lt;/alpha_blending&gt; -&gt; render_target o bé res, o bé 0, 1, 2, ...
 *      &lt;blend_func render_target=""&gt;
 *        &lt;source&gt;SRC_ALPHA&lt;/source&gt;
 *        &lt;destination&gt;ONE_MINUS_SRC_ALPHA&lt;/destination&gt;
 *        
 *        - -
 *        &lt;source_color&gt;SRC_ALPHA&lt;/source_color&gt;
 *        &lt;destination_color&gt;ONE_MINUS_SRC_ALPHA&lt;/destination_color&gt;
 *        &lt;source_alpha&gt;ONE&lt;/source_alpha&gt;
 *        &lt;destination_alpha&gt;ZERO&lt;/destination_alpha&gt;
 *        
 *        
 *      &lt;/blend_func&gt;  -&gt; ZERO, ONE,
 *                        SRC_COLOR, SRC_ALPHA, DST_ALPHA, DST_COLOR,
 *                        SRC_ALPHA_SATURATE, CONSTANT_COLOR, CONSTANT_ALPHA,
 *                        ONE_MINUS_SRC_COLOR, ONE_MINUS_SRC_ALPHA,
 *                        ONE_MINUS_DST_COLOR, ONE_MINUS_DST_ALPHA,
 *                        //ONE_MINUS_CONSTANT_COLOR, ONE_MINUS_CONSTANT_ALPHA,
 *                        SRC1_ALPHA, ONE_MINUS_SRC1_ALPHA;
 *                        
 *        color_final.rgb = color_fragment.rgb * source_color + color_anterior.rgb * destination_color;
 *        color_final.a   = color_fragment.a   * source_alpha + color_anterior.a   * destination_alpha;           
 *        
 *    &lt;/render_states&gt;
 *    
 *    &lt;attributes&gt;
 *      &lt;position/&gt; -&gt; vec3 a_v3Position               (0)
 *      &lt;normal/&gt;   -&gt; vec3 a_v3Normal                 (1)
 *      &lt;tangents/&gt; -&gt; vec3 a_v3Tangent, a_v3Bitangent (2/3)
 *      &lt;uv/&gt;       -&gt; vec2 a_v2UV                     (4)
 *      &lt;bones/&gt;    -&gt; ivec4 a_i4BlendIndexs           (5)
 *                      vec4 a_v4BlendWeights          (6)
 *      &lt;color/&gt;    -&gt; vec4 a_v4Color                  (7)
 *    &lt;/attributes&gt;
 *    
 *    &lt;uniforms&gt;
 *      &lt;albedo_texture/&gt;          -&gt; sampler2D u_s2Albedo
 *      &lt;normal_texture/&gt;          -&gt; sampler2D u_s2Normalmap
 *      &lt;height_texture/&gt;          -&gt; sampler2D u_s2Heightmap
 *      &lt;basic_instance_uniforms/&gt; -&gt; UniformInstances  { m44ModelViewProjection, m44ModelView  } u_InstanceInfo[instances]
 *      &lt;armature_uniforms/&gt;       -&gt; ArmatureInstances { u_m34BonePalete[MAX_BONES], u_m34BonePaleteIT[MAX_BONES] } u_InstanceInfo[instances];
 *      &lt;special_colors/&gt;          -&gt; SpecialColors { u_v4SpecialColor0, u_v4SpecialColor1, u_v4SpecialColor2, u_v4SpecialColor3 } u_ColorInfo[instances]
 *      
 *      &lt;basic_light/&gt;             -&gt; UniformLight { u_v3AmbientLight, u_v3DirectionalLightDirection, u_v3DirectionalLightColor }
 *      &lt;basic_material/&gt;          -&gt; UniformMaterials { u_fSpecularFactor, u_fGlossiness, u_fHeight }
 *    &lt;/uniforms&gt;
 *    
 *    &lt;results&gt;
 *      &lt;color/&gt; &lt;!- - vec4 f_v4Color - -&gt;
 *      &lt;depth/&gt; &lt;!- - vec4 f_v4Depth - -&gt;
 *    &lt;/results&gt;
 *    
 *        
 *    &lt;vertex_shader&gt;
 *      &lt;resource&gt;vertex_shader&lt;/resource&gt; -&gt; ./data/shaders/vertex_shader.vs
 *      &lt;define&gt;ANIMATED&lt;/define&gt; -&gt; afegeix "#define ANIMATED" al principi del shader
 *      &lt;define&gt;TANGENTS&lt;/define&gt;
 *      &lt;define&gt;PARALLAX&lt;/define&gt;
 *    &lt;/vertex_shader&gt;
 *    &lt;fragment_shader&gt;
 *      &lt;resource&gt;fragment_shader&lt;/resource&gt; -&gt; ./data/shaders/fragment_shader.vs
 *    &lt;/fragment_shader&gt;
 *  &lt;/pass&gt;
 * &lt;/technique&gt;
 * </pre>
 * </code>
 * </p>
 * 
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * @see EffectManager
 */
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
    
    for(Entry<TechniqueType, HashMap<Quality, Technique>> qToTechEntry : techniques.entrySet()) {
      HashMap<Quality, Technique> qToTech = qToTechEntry.getValue();
      if(!qToTech.containsKey(Quality.NONE)) {
        LOGGER.warning("Technique type " + qToTechEntry.getKey() + " has no NONE quality technique.");

        assert qToTech.size() > 0;
        Set<Quality> qToTechSet = qToTech.keySet();
        Quality lowestQuality = qToTechSet.iterator().next();
        Quality qualityCont = lowestQuality;
        
        while(qualityCont != Quality.NONE) {
          qualityCont = qualityCont.previousQuality();
          if(qToTechSet.contains(qualityCont)) {
            lowestQuality = qualityCont;
          }
        }
        
        qToTech.put(Quality.NONE, qToTech.get(lowestQuality));
      }
      
      assert qToTech.containsKey(Quality.NONE);
    }
    
    return true;
  }
  
  /**
   * Creates the default Effect.
   * 
   * @since 0.1
   */
  void loadDefault() {
    for(TechniqueType tt : TechniqueType.values()) {
      
      Technique technique = new Technique();
      HashMap<Quality, Technique> qToTech = new HashMap<>();
      qToTech.put(Quality.LOW, technique);
      techniques.put(tt, qToTech);
    }
  }

  /**
   * Fetches an approbate technique. The Quality parameter is a guide, if no technique of the desired
   * quality is found, this method searches the lower qualities. In the Effect definition it is an
   * error to define a technique of a certain type and not define a NONE quality variant.
   * 
   * @param tt type of the technique.
   * @param q desired quality of the technique.
   * @return the desired technique.
   * @throws IllegalArgumentException if no technique of the type passed exists.
   * 
   * @since 0.1
   */
  public Technique getTechnique(TechniqueType tt, Quality q) {
    HashMap<Quality, Technique> qToTech = techniques.get(tt);
    if(qToTech != null) {
      Technique technique = qToTech.get(q);
      while(technique == null && q != Quality.NONE) {
        q = q.previousQuality();
        technique = qToTech.get(q);
      }
      if(technique == null)
        throw new IllegalStateException("No technique of type " + tt + " was found.");
      return technique;
    }
    throw new IllegalArgumentException("No technique of type " + tt + " was found.");
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
  
  /**
   * Enumeration that defines the different technique types.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  public static enum TechniqueType {
    /**
     * Technique used in a forward renderer.
     * @since 0.1
     */
    FORWARD, 
    /**
     * Technique used in a forward renderer.
     * @since 0.3
     */
    ANIMATED_FORWARD, 
    /**
     * Technique used in a deferred renderer.
     * @since 0.1
     */
    DEFERRED, 
    /**
     * Technique used in a deferred renderer.
     * @since 0.3
     */
    ANIMATED_DEFERRED, 
    /**
     * Technique used to render a shadowmap.
     * @since 0.1
     */
    SHADOW, 
    /**
     * Technique used to render a particle.
     * @since 0.1
     */
    PARTICLE;
    
    /**
     * Parses a string and creates an Enum Value.
     * 
     * @param str string to parse.
     * @return a valid enum value.
     * @throws IllegalArgumentException if the string is an invalid value.
     * 
     * @since 0.1
     */
    public static TechniqueType parseString(String str) {
      switch(str.toUpperCase()) {
      case "FORWARD":
        return FORWARD;
      case "DEFERRED":
        return DEFERRED;
      case "ANIMATED_FORWARD":
        return ANIMATED_FORWARD;
      case "ANIMATED_DEFERRED":
        return ANIMATED_DEFERRED;
      case "SHADOW":
        return SHADOW;
      case "PARTICLE":
        return PARTICLE;
      default:
        throw new IllegalArgumentException(str);
      }
    }
    
    @Override
    public String toString() {
      switch(this) {
      case FORWARD:
        return "FORWARD";
      case DEFERRED:
        return "DEFERRED";
      case ANIMATED_FORWARD:
        return "ANIMATED_FORWARD";
      case ANIMATED_DEFERRED:
        return "ANIMATED_DEFERRED";
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
