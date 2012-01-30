package cat.atridas.antagonista.entities;

import java.util.logging.Logger;

import cat.atridas.antagonista.Clock;
import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;

public abstract class BaseComponent <T extends BaseComponent<?>> implements Cloneable {
  private static Logger LOGGER = Logger.getLogger(BaseComponent.class.getCanonicalName());
  
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

  @SuppressWarnings("unchecked")
  @Override
  public BaseComponent<T> clone() {
    try {
      return (BaseComponent<T>)super.clone();
    } catch (CloneNotSupportedException e) {
      LOGGER.severe(Utils.logExceptionStringAndStack(e));
      throw new RuntimeException(e);
    }
  }
}
