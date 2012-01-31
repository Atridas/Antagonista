package cat.atridas.antagonista.entities;

import cat.atridas.antagonista.HashedString;

/**
 * Basic Component Interface. This interface represents a game object component.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 * @param <T> Final component's class.
 * @see BaseComponent
 */
public interface Component<T extends Component<?>> {
  /**
   * Gets the identifier of this component type.
   * 
   * @return the identifier of this component type.
   * @since 0.1
   */
  HashedString getComponentType();
  /**
   * Gets the Entity this component is attached.
   * 
   * @return the Entity this component is attached.
   * @since 0.1
   */
  Entity getEntity();
  /**
   * Gets the Entity's id this component is attached.
   * 
   * @return the Entity's id this component is attached.
   * @since 0.1
   */
  HashedString getEntityId();
  /**
   * Copies the component, used to reuse discarded components.
   * @param _other input component to copy.
   * @since 0.1
   */
  void copy(T _other);
}
