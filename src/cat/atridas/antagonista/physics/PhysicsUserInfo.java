package cat.atridas.antagonista.physics;

import javax.vecmath.Color3f;

/**
 * Engine information for each physic entity.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 */
public class PhysicsUserInfo {
  /**
   * Debug color.
   * @since 0.2
   */
  public final Color3f color = new Color3f();
  /**
   * In debug draw, perform the zTest.
   * @since 0.2
   */
  public boolean zTest;
  
}
