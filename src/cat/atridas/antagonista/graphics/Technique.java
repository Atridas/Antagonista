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
import cat.atridas.antagonista.graphics.RenderManager.Functionality;
import cat.atridas.antagonista.graphics.gl.TechniquePassGL2;
import cat.atridas.antagonista.graphics.gl.TechniquePassGL2_UBO;
import cat.atridas.antagonista.graphics.gl.TechniquePassGL3;

public final class Technique {
  //private static Logger LOGGER = Logger.getLogger(TechniquePass.class.getCanonicalName());

  public final List<TechniquePass> passes;
  
  public Technique(Element techniqueXML, boolean gl) throws AntagonistException {
    assert techniqueXML.getTagName().equals("technique");
    
    ArrayList<TechniquePass> _passes = new ArrayList<>();

    NodeList nl = techniqueXML.getElementsByTagName("pass");
    for(int i = 0; i < nl.getLength(); ++i) {
      Element pass = ((Element)nl.item(i));

      if(Utils.supports(Profile.GL3)) {
        _passes.add(new TechniquePassGL3(pass));
      } else if(Utils.supports(Profile.GL2) && Utils.supports(Functionality.UNIFORM_BUFFER_OBJECT)) {
        _passes.add(new TechniquePassGL2_UBO(pass));
      } else if(Utils.supports(Profile.GL2)) {
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
  
  protected Technique() {
    ArrayList<TechniquePass> _passes = new ArrayList<>();
    
    if(Utils.supports(Profile.GL3)) {
      _passes.add(new TechniquePassGL3());
    } else if(Utils.supports(Profile.GL2) && Utils.supports(Functionality.UNIFORM_BUFFER_OBJECT)) {
      _passes.add(new TechniquePassGL2_UBO());
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
}
