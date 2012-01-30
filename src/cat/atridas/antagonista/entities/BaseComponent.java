package cat.atridas.antagonista.entities;

//import java.util.logging.Logger;

import cat.atridas.antagonista.Clock;
import cat.atridas.antagonista.core.Core;

public abstract class BaseComponent <T extends BaseComponent<?>> implements Component<T> {
  //private static Logger LOGGER = Logger.getLogger(BaseComponent.class.getCanonicalName());
  
  private final Entity entity;
  
  protected static final Clock globalClock = Core.getCore().getClock();
  
  protected BaseComponent(Entity _entity) {
    entity = _entity;
  }
  
  public Entity getEntity() {
    return entity;
  }
}
