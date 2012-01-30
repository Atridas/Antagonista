package cat.atridas.antagonista.entities.components;

import cat.atridas.antagonista.Clock;
import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.Entity;

@SuppressWarnings("rawtypes")
public abstract class BaseComponent <T extends BaseComponent> {
  
  private final Entity entity;
  
  protected static final Clock globalClock = Core.getCore().getClock();
  
  protected BaseComponent(Entity _entity) {
    entity = _entity;
  }
  
  public Entity getEntity() {
    return entity;
  }
  
  public abstract HashedString getComponentType();
  
  public abstract void copy(T _other);
}
