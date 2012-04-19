package cat.atridas.antagonista.entities.components;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.BaseComponent;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.GlobalComponent;
import cat.atridas.antagonista.entities.LocalComponent;

public class NavigableTerrainComponent extends
    BaseComponent<NavigableTerrainComponent> {

  public NavigableTerrainComponent(Entity _entity) {
    super(_entity);
  }

  public void init() {
    setInitialized();
  }

  @Override
  public void copy(NavigableTerrainComponent _other) {
    super.copy(_other);
  }

  @Override
  public String toString() {
    return "NavigableTerrainComponent";
  }

  // ////////////////////////////////////////////////////////////////////////////////////////

  public final static class Global extends NavigableTerrainComponent implements
      GlobalComponent<NavigableTerrainComponent> {

    public Global(Entity _entity) {
      super(_entity);
    }

    @Override
    public Local createLocalCopy() {
      return new Local();
    }

  }

  public final class Local extends NavigableTerrainComponent implements
      LocalComponent<NavigableTerrainComponent> {

    private Local() {
      super(NavigableTerrainComponent.this.getEntity());
      pullChanges();
    }

    @Override
    public void pushChanges() {
      synchronized (NavigableTerrainComponent.this) {
        NavigableTerrainComponent.this.copy(this);
      }
    }

    @Override
    public void pullChanges() {
      synchronized (NavigableTerrainComponent.this) {
        this.copy(NavigableTerrainComponent.this);
      }
    }
  }

  // ////////////////////////////////////////////////////////////////////////////////////////

  private final static HashedString componentType = new HashedString(
      "NavigableTerrainComponent");

  @Override
  public HashedString getComponentType() {
    return componentType;
  }

  public static HashedString getComponentStaticType() {
    return componentType;
  }

  static {
    Core.getCore().getEntityManager().registerComponentType(Global.class);
  }
}
