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

public class SystemManager {
  public static final HashedString renderInteface = new HashedString("Render Interface");
  
  
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
    
    List<HashedString> componentsUsed = null;
    
    if(systemAcumulatedEntityChanges.size() > 0) {
      componentsUsed = system.getUsedComponents();
    }
    
    for(HashedString entityId : systemAcumulatedEntityChanges) {
      
      boolean thisSystemHasThisEntity = systemEntities.containsKey(entityId);
      List<HashedString> systemComponents = system.getUsedComponents();
      Set<HashedString> entityComponents = em.getAllComponents(entityId);
      boolean thisEntityHasAllNeededComponents = entityComponents.containsAll( systemComponents );
      
      if(!thisSystemHasThisEntity && thisEntityHasAllNeededComponents) {
        //afegir entitat.
        
        LocalComponent<?>[] components = new LocalComponent<?>[ componentsUsed.size() ];
        
        for(int i = 0; i < components.length; ++i) {
          components[i] = (LocalComponent<?>) em.getComponent(entityId, componentsUsed.get(i)).createLocalCopy();
        }
        
        systemEntities.put(entityId, components);
        
        system.addEntity(entityId, components, dt);
        
      } else if(thisSystemHasThisEntity && !thisEntityHasAllNeededComponents) {
        //treure entitat.
        
        system.deleteEntity(entityId, dt);
        
        systemEntities.remove(entityId);
      }
    }
    
    systemAcumulatedEntityChanges.clear();
    
    for(Entry<HashedString,LocalComponent<?>[]> systemEntity : systemEntities.entrySet()) {
      HashedString entityId = systemEntity.getKey();
      LocalComponent<?>[] components = systemEntity.getValue();
      
      for(LocalComponent<?> component : components) {
        component.pullChanges();
      }
      
      system.updateEntity(entityId, components, dt);
    }
    
    
    //push changes AFTER everything...
    
    for(LocalComponent<?>[] components : systemEntities.values()) {
      for(LocalComponent<?> component : components) {
        component.pushChanges();
      }
    }
  }
}
