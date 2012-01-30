package cat.atridas.antagonista.entities;

public interface GlobalComponent <T extends Component<?>> extends Component<T> {
  T createLocalCopy();
}
