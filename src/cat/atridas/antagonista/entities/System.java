package cat.atridas.antagonista.entities;

import java.util.List;
import java.util.Set;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Clock.DeltaTime;

/**
 * This is a system that will control the engine's logic and updates.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 *
 */
public interface System {

  /**
   * Gets the ID of this system.
   * 
   * @return the ID of this system.
   * @since 0.2
   */
  HashedString getSystemId();

  /**
   * Gets a list of the components that an entity will need to have to be updated
   * by this system.
   *  
   * @return a list of component this system uses.
   * @since 0.2
   */
  List<HashedString> getUsedComponents();
  /**
   * Gets a list of components this system may read/update.
   * 
   * @return a list of optional components.
   * @since 0.2
   */
  List<HashedString> getOptionalComponents();
  /**
   * A set of components this system may write. This list must contain only components that apear
   * in either the used components list or the optional components list.
   * 
   * @return a set of components this system may write.
   * @since 0.2
   */
  Set<HashedString>  getWriteToComponents();
  /**
   * Set of components this system may read from other entities when updating/adding entities.
   * 
   * @return components this system may read from other entities.
   * @since 0.2
   */
  Set<HashedString>  getOtherReadComponents();
  
  /**
   * Interfaces this system uses.
   * 
   * @return Interfaces this system uses.
   * @since 0.2
   */
  Set<HashedString> getUsedInterfaces();
  /**
   * Interfaces this system may use to write to. This set must contain only interfaces from the
   * used interfaces set.
   * 
   * @return Interfaces this system may use to write to.
   * @since 0.2
   */
  Set<HashedString>  getWriteToInterfaces();
  
  /**
   * This method is called each time a new entity has all needed components to be updated by this
   * system.
   * 
   * @param entity new entity that this system will update.
   * @param components an array of components. This array contains first the components from the
   *        used components list and then the ones from the optional components (if there are not
   *        those components, those values are null) in the same order as in the corresponding list.
   * @param currentTime the current delta time.
   * @since 0.2
   */
  void addEntity(Entity entity, Component<?>[] components, DeltaTime currentTime);
  
  /**
   * This method is called each frame for each entity that meets the requirements.
   * 
   * @param entity entity updated.
   * @param components an array of components. This array contains first the components from the
   *        used components list and then the ones from the optional components (if there are not
   *        those components, those values are null) in the same order as in the corresponding list.
   * @param currentTime the current delta time.
   * @since 0.2
   */
  void updateEntity(Entity entity, Component<?>[] components, DeltaTime currentTime);
  
  /**
   * This method is called each time an entity no longer meets the requirements of this system.
   * 
   * @param entity deleted.
   * @param currentTime the current delta time.
   * @since 0.2
   */
  void deleteEntity(Entity entity, DeltaTime currentTime);
}
