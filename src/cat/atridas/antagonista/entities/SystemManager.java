package cat.atridas.antagonista.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;

/**
 * Manages and updates all the systems.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 *
 */
public class SystemManager {
  public static final HashedString renderInteface = new HashedString("Render Interface");
  public static final HashedString inputInteface = new HashedString("Input Interface");
  public static final HashedString physicsInteface = new HashedString("Physics Interface");
  
  
  /**
   * Array with all systems.
   * @since 0.2
   */
  private final ArrayList<System> systems = new ArrayList<>();
  
  /**
   * Map with all entities belonging to a system.
   * 
   * This has for each system a set of all entities, and for each entity an array of each
   * local component.
   * 
   * @since 0.2
   */
  private final HashMap<HashedString, HashMap<HashedString,LocalComponent<?>[]>> entitiesCache = new HashMap<>();
  
  /**
   * Acumulation of all entity changes since last update, for each system.
   * @since 0.2
   */
  private final HashMap<HashedString, HashSet<HashedString>> acumulatedEntityChanges = new HashMap<>();
  
  public void registerSystem(System system) {
    systems.add(system);
    
    entitiesCache.put(system.getSystemId(), new HashMap<HashedString,LocalComponent<?>[]>());
    acumulatedEntityChanges.put(system.getSystemId(), new HashSet<HashedString>());
  }
  
  
  public void updateSimple(DeltaTime dt) {
    
    EntityManager em = Core.getCore().getEntityManager();

    
    
    for(System system : systems) {
      acumulateEntityChanges(em);
      
      updateSystem(system, em, dt);
      
      pushSystemChanges(system);
    }
  }
  
  HashSet<HashedString> g_updatedEntities = new HashSet<>();
  private void acumulateEntityChanges(EntityManager em) {
    g_updatedEntities.clear();
    em.update(g_updatedEntities);
    
    for(HashSet<HashedString> value : acumulatedEntityChanges.values()) {
      value.addAll(g_updatedEntities);
    }
  }
  
  private void updateSystem(System system, EntityManager em, DeltaTime dt) {
    HashSet<HashedString> systemAcumulatedEntityChanges = acumulatedEntityChanges.get(system.getSystemId());
    
    HashMap<HashedString,LocalComponent<?>[]> systemEntities = entitiesCache.get(system.getSystemId());

    

    List<HashedString> componentsUsed     = system.getUsedComponents();
    List<HashedString> optionalComponents = system.getOptionalComponents();
    
    if(systemAcumulatedEntityChanges.size() > 0) {
    
      for(HashedString entityId : systemAcumulatedEntityChanges) {
        
        boolean thisSystemHasThisEntity = systemEntities.containsKey(entityId);
        List<HashedString> systemComponents = system.getUsedComponents();
        Set<HashedString> entityComponents = em.getAllComponents(entityId);
        boolean thisEntityHasAllNeededComponents = entityComponents.containsAll( systemComponents );
        
        if(!thisSystemHasThisEntity && thisEntityHasAllNeededComponents) {
          //afegir entitat.
          
          LocalComponent<?>[] components = new LocalComponent<?>[ componentsUsed.size() + optionalComponents.size() ];
          
          for(int i = 0; i < componentsUsed.size(); ++i) {
            components[i] = (LocalComponent<?>) em.getComponent(entityId, componentsUsed.get(i)).createLocalCopy();
            assert components[i].isInitialized();
          }
          int dif = componentsUsed.size();
          
          for(int i = 0; i < optionalComponents.size(); ++i) {
            GlobalComponent<?> optional = em.getComponent(entityId, optionalComponents.get(i));
            if(optional != null) {
              components[i + dif] = optional.createLocalCopy();
              assert components[i].isInitialized();
            }
          }
          
          systemEntities.put(entityId, components);
          
          system.addEntity(entityId, components, dt);
          
        } else if(thisSystemHasThisEntity && !thisEntityHasAllNeededComponents) {
          //treure entitat.
          
          system.deleteEntity(entityId, dt);
          
          systemEntities.remove(entityId);
        } else if(thisSystemHasThisEntity) {
          
          LocalComponent<?>[] components = systemEntities.get(entityId);
          
          int dif = componentsUsed.size();
          
          for(int i = 0; i < optionalComponents.size(); ++i) {
            GlobalComponent<?> optional = em.getComponent(entityId, optionalComponents.get(i));
            if(optional != null) {
              components[i + dif] = optional.createLocalCopy();
              assert components[i].isInitialized();
            } else {
              components[i + dif] = null;
            }
          }
        }
      }
    }
    
    systemAcumulatedEntityChanges.clear();
    
    for(Entry<HashedString,LocalComponent<?>[]> systemEntity : systemEntities.entrySet()) {
      HashedString entityId = systemEntity.getKey();
      LocalComponent<?>[] components = systemEntity.getValue();
      
      int dif = componentsUsed.size();
      
      for(int i = 0; i < optionalComponents.size(); ++i) {
        if(components[i + dif] == null) {
          GlobalComponent<?> optional = em.getComponent(entityId, optionalComponents.get(i));
          if(optional != null) {
            components[i + dif] = optional.createLocalCopy();
          }
        }
      }
      
      for(LocalComponent<?> component : components) {
        if(component != null)
          component.pullChanges();
      }
      
      system.updateEntity(entityId, components, dt);
    }
  }
  
  private synchronized void pushSystemChanges(System system) {
    
    HashMap<HashedString,LocalComponent<?>[]> systemEntities = entitiesCache.get(system.getSystemId());
    
    Set<HashedString> writeToComponents = system.getWriteToComponents();
    
    for(LocalComponent<?>[] components : systemEntities.values()) {
      for(LocalComponent<?> component : components) {
        if(component != null && writeToComponents.contains(component.getComponentType()))
          component.pushChanges();
      }
    }
  }
  
  
  
  public static boolean assertSystemInputParameters(HashedString entity, Component<?>[] components, System system) {

    List<HashedString> usedComponents = system.getUsedComponents();
    List<HashedString> optionalComponents = system.getOptionalComponents();
    
    if( components.length != usedComponents.size() + optionalComponents.size() )
      return false;

    for(int i = 0; i < usedComponents.size(); ++i) {
      if(! components[i].getComponentType().equals(usedComponents.get(i)) )
        return false;
      if(! components[i].getEntityId().equals(entity) )
        return false;
    }
    
    for(int i = 0; i < optionalComponents.size(); ++i) {
      int j = i + usedComponents.size();
      if(components[j] != null) {
        if(! components[j].getComponentType().equals(optionalComponents.get(i)) )
          return false;
        if(! components[j].getEntityId().equals(entity) )
          return false;
      }
    }
    
    return true;
  }
}
