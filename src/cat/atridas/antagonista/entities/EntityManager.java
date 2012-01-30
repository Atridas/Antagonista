package cat.atridas.antagonista.entities;

import java.util.HashMap;
import java.util.logging.Logger;

import cat.atridas.antagonista.HashedString;

/**
 * Manages all game entities.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 *
 */
public final class EntityManager {
  private static Logger LOGGER = Logger.getLogger(EntityManager.class.getCanonicalName());
  
  /**
   * Map containing all created active entities.
   * @since 0.2
   */
  private final HashMap<HashedString, Entity> entities = new HashMap<>();
  /**
   * Counter indicating the number of entities created with automatic name generation.
   * @since 0.2 
   */
  private long autoName = 0;
  
  /**
   * Creates a new entity, with the specified name identifier.
   * 
   * @param name of the new entity.
   * @return the created entity.
   * @throws RuntimeException if the name is in use.
   * @since 0.2
   */
  public final synchronized Entity createEntity(HashedString name) {
    if(entities.containsKey(name)) {
      LOGGER.severe("Creating a entity with a used identifier! " + name);
      throw new RuntimeException();
    }
    
    Entity entity = new Entity(name);
    entities.put(name, entity);
    
    return entity;
  }
  
  /**
   * Generates a new entity, with an automatically generated name.
   * 
   * @return a new entity.
   * @since 0.2
   */
  public final synchronized Entity createEntity() {
    HashedString name = new HashedString("Auto-" + autoName++);
    return createEntity(name);
  }
}
