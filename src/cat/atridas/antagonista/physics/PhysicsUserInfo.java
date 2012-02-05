package cat.atridas.antagonista.physics;

import javax.vecmath.Color3f;

import cat.atridas.antagonista.entities.Entity;

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
  
  /**
   * Game entity this physic object is associated with.
   * @since 0.2
   */
  public final Entity entity;
  
  public PhysicsUserInfo(Entity _entity) {
    entity = _entity;
  }
}
