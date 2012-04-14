package cat.atridas.antagonista.entities;

/**
 * Local Copy of a component.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 *
 * @param <T> Final Component Class.
 */
public interface LocalComponent <T extends Component<?>> extends Component<T> {
  /**
   * Pushes the state of this component into the Global copy.
   * @since 0.2
   */
  void pushChanges();
  /**
   * Pulls the changes from the global component into this local copy.
   * @since 0.2
   */
  void pullChanges();
}
