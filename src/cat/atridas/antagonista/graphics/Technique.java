package cat.atridas.antagonista.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.gl2.TechniquePassGL2;
import cat.atridas.antagonista.graphics.gl3.TechniquePassGL3;

/**
 * Rendering technique, a.k.a a compilation of shading phases.
 * 
 * @author Isaac 'Atridas' Serrano Guash
 * @since 0.1
 *
 */
public final class Technique {
  //private static Logger LOGGER = Logger.getLogger(TechniquePass.class.getCanonicalName());

  /**
   * Phases that this technique needs to draw.
   * @since 0.1
   */
  private final List<TechniquePass> passes;
  
  /**
   * Builds a technique.
   * 
   * @param techniqueXML xml configuration element.
   * @throws AntagonistException if there was an error building the technique.
   * @since 0.1
   */
  public Technique(Element techniqueXML) throws AntagonistException {
    assert techniqueXML.getTagName().equals("technique");
    
    ArrayList<TechniquePass> _passes = new ArrayList<>();


    Profile p = Profile.getFromString(techniqueXML.getAttribute("min_version"));
    
    NodeList nl = techniqueXML.getElementsByTagName("pass");
    for(int i = 0; i < nl.getLength(); ++i) {
      Element pass = ((Element)nl.item(i));

      if(p.supports(Profile.GL3)) {
        _passes.add(new TechniquePassGL3(pass));
      } else if(p.supports(Profile.GL2)) {
        _passes.add(new TechniquePassGL2(pass));
      } else {
        throw new IllegalStateException(
            "Current Profile [" + 
                Core.getCore().getRenderManager().getProfile() + 
                                 "] not implemented.");
      //Utils.supportOrException(Profile.GL2, "OpenGL ES not yet supported.");
      }
      assert !Utils.hasGLErrors();
    }
    
    passes = Collections.unmodifiableList(_passes);
  }
  
  /**
   * Builds the default technique.
   * @since 0.1
   */
  protected Technique() {
    ArrayList<TechniquePass> _passes = new ArrayList<>();
    
    if(Utils.supports(Profile.GL3)) {
      _passes.add(new TechniquePassGL3());
    } else if(Utils.supports(Profile.GL2)) {
      _passes.add(new TechniquePassGL2());
    } else {
      throw new IllegalStateException(
          "Current Profile [" + 
              Core.getCore().getRenderManager().getProfile() + 
                              "] not implemented.");
    }

    passes = Collections.unmodifiableList(_passes);
  }
  
  /**
   * Returns the phases that this technique needs to draw.
   * @return the phases that this technique needs to draw.
   */
  public List<TechniquePass> getPasses() {
    return passes;
  }
}
