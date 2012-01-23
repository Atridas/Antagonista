package cat.atridas.antagonista.graphics;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
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
 * <code>
 * &lt;technique type="" quality=""&gt;</br>
 *  types:</br>
 *  -&gt; "forward"</br>
 *  -&gt; "shadow"</br>
 *  -&gt; "particle"</br>
 *  -&gt; "deferred"</br>
 *  </br>
 *  qualities. Si es demana una inexistent, es cau cap avall. Per defecte és "none"</br>
 *  -&gt; "none"</br>
 *  -&gt; "low"</br>
 *  -&gt; "mid"</br>
 *  -&gt; "hight"</br>
 *  -&gt; "ultra"</br>
 *      </br>
 *  &lt;min_version&gt;GL2&lt;/min_version&gt;       -&gt; GL2, GL3, GL4, GLSL2</br>
 *  &lt;pass&gt;</br>
 *    &lt;render_states&gt;</br>
 *      &lt;depth_test&gt;true&lt;/depth_test&gt;</br>
 *      &lt;depth_function&gt;LESS&lt;/depth_function&gt; -&gt; LESS, GREATER, EQUAL, NOTEQUAL, LEQUAL, GEQUAL, ALWAYS, NEVER</br>
 *      &lt;z_write&gt;true&lt;/z_write&gt;</br>
 *      </br>
 *      &lt;alpha_blending render_target=""&gt;false&lt;/alpha_blending&gt; -&gt; render_target o bé res, o bé 0, 1, 2, ...</br>
 *      &lt;blend_func render_target=""&gt;</br>
 *        &lt;source&gt;SRC_ALPHA&lt;/source&gt;</br>
 *        &lt;destination&gt;ONE_MINUS_SRC_ALPHA&lt;/destination&gt;</br>
 *        </br>
 *        - -</br>
 *        &lt;source_color&gt;SRC_ALPHA&lt;/source_color&gt;</br>
 *        &lt;destination_color&gt;ONE_MINUS_SRC_ALPHA&lt;/destination_color&gt;</br>
 *        &lt;source_alpha&gt;ONE&lt;/source_alpha&gt;</br>
 *        &lt;destination_alpha&gt;ZERO&lt;/destination_alpha&gt;</br>
 *        </br>
 *        </br>
 *      &lt;/blend_func&gt;  -&gt; ZERO, ONE, </br>
 *                        SRC_COLOR, SRC_ALPHA, DST_ALPHA, DST_COLOR, </br>
 *                        SRC_ALPHA_SATURATE, CONSTANT_COLOR, CONSTANT_ALPHA,</br>
 *                        ONE_MINUS_SRC_COLOR, ONE_MINUS_SRC_ALPHA,</br>
 *                        ONE_MINUS_DST_COLOR, ONE_MINUS_DST_ALPHA, </br>
 *                        //ONE_MINUS_CONSTANT_COLOR, ONE_MINUS_CONSTANT_ALPHA,</br>
 *                        SRC1_ALPHA, ONE_MINUS_SRC1_ALPHA;</br>
 *                        </br>
 *        color_final.rgb = color_fragment.rgb * source_color + color_anterior.rgb * destination_color;</br>
 *        color_final.a   = color_fragment.a   * source_alpha + color_anterior.a   * destination_alpha;     </br>           
 *        </br>
 *    &lt;/render_states&gt;</br>
 *    </br>
 *    &lt;attributes&gt;</br>
 *      &lt;position/&gt; -&gt; vec3 a_v3Position               (0)</br>
 *      &lt;normal/&gt;   -&gt; vec3 a_v3Normal                 (1)</br>
 *      &lt;tangents/&gt; -&gt; vec3 a_v3Tangent, a_v3Bitangent (2/3)</br>
 *      &lt;uv/&gt;       -&gt; vec2 a_v2UV                     (4)</br>
 *      &lt;bones/&gt;    -&gt; ivec4 a_i4BlendIndexs           (5)</br>
 *                      vec4 a_v4BlendWeights          (6)</br>
 *      &lt;color/&gt;    -&gt; vec4 a_v4Color                  (7) </br>
 *    &lt;/attributes&gt;</br>
 *    </br>
 *    &lt;uniforms&gt;</br>
 *      &lt;albedo_texture/&gt;          -&gt; sampler2D u_s2Albedo </br>
 *      &lt;normal_texture/&gt;          -&gt; sampler2D u_s2Normalmap </br>
 *      &lt;height_texture/&gt;          -&gt; sampler2D u_s2Heightmap </br>
 *      &lt;basic_instance_uniforms/&gt; -&gt; UniformInstances { m44ModelViewProjection, m44ModelView  } u_InstanceInfo[instances] </br>
 *      &lt;special_colors/&gt;          -&gt; SpecialColors { u_v4SpecialColor0, u_v4SpecialColor1, u_v4SpecialColor2, u_v4SpecialColor3 } u_ColorInfo[instances] </br>
 *      </br>
 *      &lt;basic_light/&gt;             -&gt; UniformLight { u_v3AmbientLight, u_v3DirectionalLightDirection, u_v3DirectionalLightColor } </br>
 *      &lt;basic_material/&gt;          -&gt; UniformMaterials { u_fSpecularFactor, u_fGlossiness, u_fHeight } </br>
 *    &lt;/uniforms&gt;</br>
 *    </br>
 *    &lt;results&gt;</br>
 *      &lt;color/&gt; &lt;!- - vec4 f_v4Color - -&gt;</br>
 *      &lt;depth/&gt; &lt;!- - vec4 f_v4Depth - -&gt;</br>
 *    &lt;/results&gt;</br>
 *    </br>
 *        </br>
 *    &lt;vertex_shader&gt;</br>
 *      &lt;resource&gt;vertex_shader&lt;/resource&gt; -&gt; ./data/shaders/vertex_shader.vs</br>
 *      &lt;define&gt;ANIMATED&lt;/define&gt; -&gt; afegeix "#define ANIMATED" al principi del shader</br>
 *      &lt;define&gt;TANGENTS&lt;/define&gt;</br>
 *      &lt;define&gt;PARALLAX&lt;/define&gt;</br>
 *    &lt;/vertex_shader&gt;</br>
 *    &lt;fragment_shader&gt;</br>
 *      &lt;resource&gt;fragment_shader&lt;/resource&gt; -&gt; ./data/shaders/fragment_shader.vs</br>
 *    &lt;/fragment_shader&gt;</br>
 *  &lt;/pass&gt;</br>
 *&lt;/technique&gt;</br>
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
    
    for(Entry<TechniqueType, HashMap<Quality, Technique>> qToTech : techniques.entrySet()) {
      if(!qToTech.getValue().containsKey(Quality.NONE)) {
        LOGGER.warning("Technique type " + qToTech.getKey() + " has no NONE quality technique.");
        return false;
      }
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
     * Technique used in a deferred renderer.
     * @since 0.1
     */
    DEFERRED, 
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
