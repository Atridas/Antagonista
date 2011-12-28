package cat.atridas.antagonista.test;

import java.util.logging.Level;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;

public class Test {
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    //comprovem que els asserts estiguin actius
    boolean assertsActives = false;
    assert (assertsActives = true) == true;
    if(!assertsActives)
      throw new RuntimeException("Falta activar els asserts");
    
    Utils.setConsoleLogLevel(Level.CONFIG);
    
    Core.getCore().init(800, 600, Test.class.getName(), null);
    
    assert !Utils.hasGLErrors();
  }

}
