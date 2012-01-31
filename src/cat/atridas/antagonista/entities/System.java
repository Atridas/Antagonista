package cat.atridas.antagonista.entities;

import java.util.List;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Clock.DeltaTime;

public interface System {

  
  public HashedString getSystemId();
  
  public List<HashedString> getUsedComponents();
  
  public List<HashedString> getUsedInterfaces();
  
  
  void addEntity(HashedString entity, Component<?>[] components, DeltaTime currentTime);
  
  void updateEntity(HashedString entity, Component<?>[] components, DeltaTime currentTime);
  
  void deleteEntity(HashedString entity, DeltaTime currentTime);
}
