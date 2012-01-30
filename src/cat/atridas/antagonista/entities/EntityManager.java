package cat.atridas.antagonista.entities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Logger;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;

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
   * Map from componentIDs -> EntityIDs -> Component objects.
   * @since 0.2
   */
  private final HashMap<HashedString, HashMap<HashedString, ? extends BaseComponent<?>>> components = new HashMap<>();
  
  
  private final HashMap<HashedString, Class<? extends BaseComponent<?>>> componentTypes = new HashMap<>();
  
  /**
   * Creates a new entity, with the specified name identifier.
   * 
   * @param name of the new entity.
   * @return the created entity.
   * @throws RuntimeException if the name is in use.
   * @since 0.2
   */
  public synchronized Entity createEntity(HashedString name) {
    if(entities.containsKey(name)) {
      LOGGER.severe("Creating a entity with a used identifier! " + name);
      throw new RuntimeException();
    }
    
    Entity entity = new Entity(name);
    entities.put(name, entity);
    
    return entity;
  }
  
  
  public synchronized <T extends BaseComponent<?>> T createComponent(HashedString entity, HashedString component) {
    assert entities.containsKey(entity);
    assert components.containsKey(component);
    
    HashMap<HashedString, ? extends BaseComponent<?>> componentMap = components.get(component);
    assert !componentMap.containsKey(entity);
    
    @SuppressWarnings("unchecked")
    Class<T> componentClass = (Class<T>) componentTypes.get(component);
    
    try {
      Constructor<T> constructor = componentClass.getConstructor(Entity.class);
      return constructor.newInstance(entities.get(entity));
    } catch (Exception e) {
      LOGGER.severe(Utils.logExceptionStringAndStack(e));
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Generates a new entity, with an automatically generated name.
   * 
   * @return a new entity.
   * @since 0.2
   */
  public synchronized Entity createEntity() {
    HashedString name = new HashedString("Auto-" + autoName++);
    return createEntity(name);
  }
  
  public<T extends BaseComponent<?>> void registerComponentType(Class<T> component) {
    try {
      Method m = component.getMethod("getComponentStaticType");
      HashedString identifier = (HashedString)m.invoke(component);
      
      if(componentTypes.containsKey(identifier)) {
        LOGGER.severe("Registering a component with a duplicated identifier: " + identifier);
        throw new RuntimeException();
      }
      
      componentTypes.put(identifier, component);
      components.put(identifier, new HashMap<HashedString, T>());
    } catch (Exception e) {
      LOGGER.severe(Utils.logExceptionStringAndStack(e));
      throw new RuntimeException(e);
    }
  }
}
