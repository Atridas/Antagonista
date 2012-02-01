package cat.atridas.antagonista.entities;

import java.util.List;
import java.util.Set;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Clock.DeltaTime;

public interface System {

  
  HashedString getSystemId();

  List<HashedString> getUsedComponents();
  List<HashedString> getOptionalComponents();
  Set<HashedString>  getWriteToComponents();
  
  List<HashedString> getUsedInterfaces();
  Set<HashedString>  getWriteToInterfaces();
  
  
  void addEntity(HashedString entity, Component<?>[] components, DeltaTime currentTime);
  
  void updateEntity(HashedString entity, Component<?>[] components, DeltaTime currentTime);
  
  void deleteEntity(HashedString entity, DeltaTime currentTime);
}
