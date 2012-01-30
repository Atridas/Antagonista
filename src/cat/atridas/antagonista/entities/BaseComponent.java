package cat.atridas.antagonista.entities;

//import java.util.logging.Logger;

import cat.atridas.antagonista.Clock;
import cat.atridas.antagonista.core.Core;

/**
 * <p>
 * Basic Component implementation. When building your new components, extend them from here.
 * </p>
 * <p>
 * To correctly implement a Component Class, you must extend this class and implement 2 inner classes,
 * a "Local" and a "Global".
 * <code><pre>
 * public final static class Global extends <strong>NewComponent</strong> 
 *                                  implements GlobalComponent&lt;<strong>NewComponent</strong>&gt; {
 *
 *   public Global(Entity _entity) {
 *     super(_entity);
 *   }
 *
 *   &#64;Override
 *   public Local createLocalCopy() {
 *     return new Local();
 *   } 
 * }
 * 
 * public final class Local extends <strong>NewComponent</strong> 
 *                          implements LocalComponent&lt;<strong>NewComponent</strong>&gt; {
 *
 *   private Local() {
 *     super(<strong>NewComponent</strong>.this.getEntity());
 *   }
 *
 *   &#64;Override
 *   public void pushChanges() {
 *     synchronized (<strong>NewComponent</strong>.this) {
 *       <strong>NewComponent</strong>.this.copy(this);
 *     }
 *   }
 *
 *   &#64;Override
 *   public void pullChanges() {
 *     synchronized (<strong>NewComponent</strong>.this) {
 *       this.copy(<strong>NewComponent</strong>.this);
 *     }
 *   }
 * }
 * </pre></code>
 * </p>
 * <p>
 * Then you must create a static method 
 * <strong><code>public static HashedString getComponentStaticType()</code></strong>
 * for the EntityManager to use statically, and register the component inside the entity manager:
 * <code><pre>
 * static {
 *   Core.getCore().getEntityManager().registerComponentType(Global.class);
 * }
 * </pre></code>
 * </p>
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.2
 *
 * @param <T> Final component's class.
 */
public abstract class BaseComponent <T extends BaseComponent<?>> implements Component<T> {
  //private static Logger LOGGER = Logger.getLogger(BaseComponent.class.getCanonicalName());
  
  /**
   * Entity this component is attached to.
   * @since 0.2
   */
  private Entity entity;
  
  /**
   * Access to the global clock, to register <strong>when</strong> a state has changed.
   * @since 0.2
   */
  protected static final Clock globalClock = Core.getCore().getClock();
  
  /**
   * Builds a Component.
   * 
   * @param _entity to attach this component to.
   * @since 0.2
   */
  protected BaseComponent(Entity _entity) {
    entity = _entity;
  }

  @Override
  public void copy(T _other) {
    entity = _other.getEntity();
  }

  @Override
  public Entity getEntity() {
    return entity;
  }
}
