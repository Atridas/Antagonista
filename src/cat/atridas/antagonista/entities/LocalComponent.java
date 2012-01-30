package cat.atridas.antagonista.entities;

public interface LocalComponent <T extends Component<?>> extends Component<T> {
  void pushChanges();
  void pullChanges();
}
