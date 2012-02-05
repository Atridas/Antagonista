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
  private final HashMap<HashedString, HashMap<HashedString,CachedEntity>> entitiesCache = new HashMap<>();
  
  /**
   * Acumulation of all entity changes since last update, for each system.
   * @since 0.2
   */
  private final HashMap<HashedString, HashSet<HashedString>> acumulatedEntityChanges = new HashMap<>();
  
  public void registerSystem(System system) {
    systems.add(system);
    
    entitiesCache.put(system.getSystemId(), new HashMap<HashedString,CachedEntity>());
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
    
    HashMap<HashedString,CachedEntity> systemEntities = entitiesCache.get(system.getSystemId());

    

    List<HashedString> componentsUsed     = system.getUsedComponents();
    List<HashedString> optionalComponents = system.getOptionalComponents();
    
    if(systemAcumulatedEntityChanges.size() > 0) {
    
      for(HashedString entityId : systemAcumulatedEntityChanges) {
        Entity entity = em.getEntity(entityId);
        
        boolean thisSystemHasThisEntity = systemEntities.containsKey(entityId);
        List<HashedString> systemComponents = system.getUsedComponents();
        Set<HashedString> entityComponents = em.getAllComponents(entityId);
        boolean thisEntityHasAllNeededComponents = entityComponents.containsAll( systemComponents );
        
        if(!thisSystemHasThisEntity && thisEntityHasAllNeededComponents) {
          //afegir entitat.
          
          CachedEntity cachedEntity = new CachedEntity(entity, componentsUsed.size() + optionalComponents.size());
          
          
          for(int i = 0; i < componentsUsed.size(); ++i) {
            cachedEntity.localComponents[i] = (LocalComponent<?>) em.getComponent(entity, componentsUsed.get(i)).createLocalCopy();
            assert cachedEntity.localComponents[i].isInitialized();
          }
          int dif = componentsUsed.size();
          
          for(int i = 0; i < optionalComponents.size(); ++i) {
            GlobalComponent<?> optional = em.getComponent(entity, optionalComponents.get(i));
            if(optional != null) {
              cachedEntity.localComponents[i + dif] = optional.createLocalCopy();
              assert cachedEntity.localComponents[i].isInitialized();
            }
          }
          
          systemEntities.put(entityId, cachedEntity);
          
          system.addEntity(entity, cachedEntity.localComponents, dt);
          
        } else if(thisSystemHasThisEntity && !thisEntityHasAllNeededComponents) {
          //treure entitat.
          
          system.deleteEntity(entity, dt);
          
          systemEntities.remove(entityId);
        } else if(thisSystemHasThisEntity) {
          
          CachedEntity cachedEntity = systemEntities.get(entityId);
          
          int dif = componentsUsed.size();
          
          for(int i = 0; i < optionalComponents.size(); ++i) {
            GlobalComponent<?> optional = em.getComponent(entity, optionalComponents.get(i));
            if(optional != null) {
              cachedEntity.localComponents[i + dif] = optional.createLocalCopy();
              assert cachedEntity.localComponents[i].isInitialized();
            } else {
              cachedEntity.localComponents[i + dif] = null;
            }
          }
        }
      }
    }
    
    systemAcumulatedEntityChanges.clear();
    
    for(Entry<HashedString,CachedEntity> systemEntity : systemEntities.entrySet()) {
      //HashedString entityId = systemEntity.getKey();
      CachedEntity cachedEntity = systemEntity.getValue();
      
      int dif = componentsUsed.size();
      
      for(int i = 0; i < optionalComponents.size(); ++i) {
        if(cachedEntity.localComponents[i + dif] == null) {
          GlobalComponent<?> optional = em.getComponent(cachedEntity.entity, optionalComponents.get(i));
          if(optional != null) {
            cachedEntity.localComponents[i + dif] = optional.createLocalCopy();
          }
        }
      }
      
      for(LocalComponent<?> component : cachedEntity.localComponents) {
        if(component != null)
          component.pullChanges();
      }
      
      system.updateEntity(cachedEntity.entity, cachedEntity.localComponents, dt);
    }
  }
  
  private synchronized void pushSystemChanges(System system) {
    
    HashMap<HashedString,CachedEntity> systemEntities = entitiesCache.get(system.getSystemId());
    
    Set<HashedString> writeToComponents = system.getWriteToComponents();
    
    for(CachedEntity cachedEntity : systemEntities.values()) {
      for(LocalComponent<?> component : cachedEntity.localComponents) {
        if(component != null && writeToComponents.contains(component.getComponentType()))
          component.pushChanges();
      }
    }
  }
  
  
  
  public static boolean assertSystemInputParameters(Entity entity, Component<?>[] components, System system) {

    List<HashedString> usedComponents = system.getUsedComponents();
    List<HashedString> optionalComponents = system.getOptionalComponents();
    
    if( components.length != usedComponents.size() + optionalComponents.size() )
      return false;

    for(int i = 0; i < usedComponents.size(); ++i) {
      if(! components[i].getComponentType().equals(usedComponents.get(i)) )
        return false;
      if(! components[i].getEntityId().equals(entity.getId()) )
        return false;
    }
    
    for(int i = 0; i < optionalComponents.size(); ++i) {
      int j = i + usedComponents.size();
      if(components[j] != null) {
        if(! components[j].getComponentType().equals(optionalComponents.get(i)) )
          return false;
        if(! components[j].getEntityId().equals(entity.getId()) )
          return false;
      }
    }
    
    return true;
  }
  
  private static final class CachedEntity {
    public final Entity entity;
    public final LocalComponent<?>[] localComponents;
    
    public CachedEntity(Entity _entity, int numLocalComponents) {
      entity = _entity;
      localComponents = new LocalComponent<?>[numLocalComponents];
    }
  }
}
