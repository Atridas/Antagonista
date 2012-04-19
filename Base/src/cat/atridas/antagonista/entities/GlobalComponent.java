package cat.atridas.antagonista.entities;

/**
 * Defines a Universal Component Copy. This will have the "real" value.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 * 
 * @param <T>
 *          Final Component Class.
 */
public interface GlobalComponent<T extends Component<?>> extends Component<T> {
  /**
   * Creates a local copy of this Component.
   * 
   * @return a local copy of this Component.
   */
  LocalComponent<T> createLocalCopy();
}
