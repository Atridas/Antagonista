package cat.atridas.antagonista.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.gl.TechniquePassGL;

public final class Technique {
  //private static Logger LOGGER = Logger.getLogger(TechniquePass.class.getCanonicalName());

  public final List<TechniquePass> passes;
  
  public Technique(Element techniqueXML, boolean gl) throws AntagonistException {
    assert techniqueXML.getTagName().equals("technique");
    
    ArrayList<TechniquePass> _passes = new ArrayList<>();

    NodeList nl = techniqueXML.getElementsByTagName("pass");
    for(int i = 0; i < nl.getLength(); ++i) {
      Element pass = ((Element)nl.item(i));
      
      Utils.supportOrException(Profile.GL2, "OpenGL ES not yet supported.");
      _passes.add(new TechniquePassGL(pass));
      assert !Utils.hasGLErrors();
    }
    
    passes = Collections.unmodifiableList(_passes);
  }
  
  protected Technique() {
    ArrayList<TechniquePass> _passes = new ArrayList<>();
    _passes.add(new TechniquePassGL());

    passes = Collections.unmodifiableList(_passes);
  }
}
