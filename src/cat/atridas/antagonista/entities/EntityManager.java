package cat.atridas.antagonista.entities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
  private final HashMap<HashedString, HashMap<HashedString, GlobalComponent<?>>> components = new HashMap<>();
  
  /**
   * Map from entityIDs -> ComponentIDs indicating what components a given entity has.
   * @since 0.2
   */
  private final HashMap<HashedString, HashSet<HashedString>> entityComponentCache = new HashMap<>();
  
  /**
   * Maps each component id with its Class.
   * @since 0.2
   */
  private final HashMap<HashedString, Class<? extends Component<?>>> componentTypes = new HashMap<>();
  
  /**
   * Contains all entities that had been updated (created, deleted or with new/erased components)
   * @since 0.2
   */
  private final HashSet<HashedString> updatedEntities = new HashSet<>();
  
  /**
   * Creates a new entity, with the specified name identifier.
   * 
   * @param name of the new entity.
   * @return the created entity.
   * @throws RuntimeException if the name is in use.
   * @since 0.2
   */
  public synchronized Entity createEntity(HashedString name) {
    //TODO fer un read/write lock
    if(entities.containsKey(name)) {
      LOGGER.severe("Creating a entity with a used identifier! " + name);
      throw new RuntimeException();
    }
    
    Entity entity = new Entity(name);
    entities.put(name, entity);
    
    entityComponentCache.put(name, new HashSet<HashedString>());
    
    updatedEntities.add(name);
    
    return entity;
  }
  
  /**
   * Generates a new entity, with an automatically generated name.
   * 
   * @return a new entity.
   * @since 0.2
   */
  public synchronized Entity createEntity() {
    //TODO fer un read/write lock
    HashedString name = new HashedString("Auto-" + autoName++);
    return createEntity(name);
  }
  
  /**
   * Fetches a entity.
   * 
   * @param name id of the entity.
   * @return a entity. <code>null</code> if no entity with this name was found.
   * @since 0.2
   */
  public synchronized Entity getEntity(HashedString name) {
    //TODO fer un read/write lock
    return entities.get(name);
  }
  
  /**
   * Creates a new Component.
   * 
   * @param entity id of the entity that will have the component.
   * @param component id of the component to create.
   * @return a new component.
   * @since 0.2
   */
  public synchronized <T extends GlobalComponent<?>> T createComponent(Entity entity, HashedString component) {
    //TODO fer un read/write lock
    assert entities.containsKey(entity.getId());
    assert components.containsKey(component);
    
    HashMap<HashedString, GlobalComponent<?>> componentMap = components.get(component);
    HashSet<HashedString> componentSet = entityComponentCache.get(entity.getId());
    assert !componentMap.containsKey(entity.getId());
    assert !componentSet.contains(component);
    
    @SuppressWarnings("unchecked")
    Class<T> componentClass = (Class<T>) componentTypes.get(component);
    
    try {
      Constructor<T> constructor = componentClass.getConstructor(Entity.class);
      T createdComponent = constructor.newInstance(entity);
      componentMap.put(entity.getId(), createdComponent);
      componentSet.add(component);
      updatedEntities.add(entity.getId());
      
      return createdComponent;
    } catch (Exception e) {
      LOGGER.severe(Utils.logExceptionStringAndStack(e));
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Fetches a component.
   * 
   * @param entity id of the entity.
   * @param component id of the component to fetch.
   * @return a component or <code>null</code> if this entity does not have this component.
   * @since 0.2
   */
  @SuppressWarnings("unchecked")
  public synchronized <T extends GlobalComponent<?>> T getComponent(Entity entity, HashedString component) {
    //TODO fer un read/write lock

    HashMap<HashedString, GlobalComponent<?>> componentMap = components.get(component);
    T componentToReturn = (T) componentMap.get(entity.getId());
    assert 
        (componentToReturn != null && entityComponentCache.get(entity.getId()).contains(component))
        ||
        (componentToReturn == null && !entityComponentCache.get(entity.getId()).contains(component));

    assert componentToReturn == null || componentToReturn.getComponentType().equals(component);
    assert componentToReturn == null || componentToReturn.getEntityId().equals(entity.getId());
    
    return componentToReturn;
  }
  
  /**
   * Used by Component classes to register its existence.
   * 
   * @param component to be registered.
   * @since 0.2
   */
  public<T extends GlobalComponent<?>> void registerComponentType(Class<T> component) {
    try {
      Method m = component.getMethod("getComponentStaticType");
      HashedString identifier = (HashedString)m.invoke(component);
      
      if(componentTypes.containsKey(identifier)) {
        LOGGER.severe("Registering a component with a duplicated identifier: " + identifier);
        throw new RuntimeException();
      }
      
      componentTypes.put(identifier, component);
      components.put(identifier, new HashMap<HashedString, GlobalComponent<?>>());
    } catch (Exception e) {
      LOGGER.severe(Utils.logExceptionStringAndStack(e));
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Checks what entites had been updated  (created, deleted or with new/erased components)
   * since the last call on this method and returns a set of them.
   * 
   * @param updatedEntities_ output parameter with all entities that had been updated.
   * @since 0.2
   */
  public void update(Set<HashedString> updatedEntities_) {
    updatedEntities_.addAll(updatedEntities);
    updatedEntities.clear();
  }
  
  /**
   * Gets a set of all components a entity currently has attached.
   * 
   * @param entity to check.
   * @return a set of all components a entity currently has attached.
   * @since 0.2
   */
  public Set<HashedString> getAllComponents(HashedString entity) {
    assert entities.containsKey(entity);
    return Collections.unmodifiableSet(entityComponentCache.get(entity));
  }
}
