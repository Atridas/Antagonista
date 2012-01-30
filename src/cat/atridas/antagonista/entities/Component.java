package cat.atridas.antagonista.entities;

import cat.atridas.antagonista.HashedString;

public interface Component<T extends Component<?>> {
  HashedString getComponentType();
  void copy(T _other);
}
