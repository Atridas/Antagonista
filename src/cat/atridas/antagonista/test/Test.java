package cat.atridas.antagonista.test;

import cat.atridas.antagonista.core.Core;

public class Test {

  /**
   * @param args
   */
  public static void main(String[] args) {
    boolean assertsActives = false;
    assert (assertsActives = true) == true;
    
    if(!assertsActives)
      throw new RuntimeException("Falta activar els asserts");
    
    Core.getCore().init(800, 600, Test.class.getName(), null);
  }

}
