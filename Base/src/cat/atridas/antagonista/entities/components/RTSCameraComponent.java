package cat.atridas.antagonista.entities.components;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.BaseComponent;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.GlobalComponent;
import cat.atridas.antagonista.entities.LocalComponent;
import cat.atridas.antagonista.graphics.RTSCamera;

public class RTSCameraComponent extends BaseComponent<RTSCameraComponent> {

  private boolean isActive = true;
  private RTSCamera camera = null;

  public RTSCameraComponent(Entity _entity) {
    super(_entity);
  }

  public void init(RTSCamera _camera) {
    setCamera(_camera);
    setInitialized();
  }

  public void setCamera(RTSCamera _camera) {
    camera = _camera;
  }

  public RTSCamera getCamera() {
    return camera;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  @Override
  public void copy(RTSCameraComponent _other) {
    super.copy(_other);
    camera = _other.camera;
  }

  @Override
  public String toString() {
    return "RTSCameraComponent: " + camera;
  }

  // ////////////////////////////////////////////////////////////////////////////////////////

  public final static class Global extends RTSCameraComponent implements
      GlobalComponent<RTSCameraComponent> {

    public Global(Entity _entity) {
      super(_entity);
    }

    @Override
    public Local createLocalCopy() {
      return new Local();
    }

  }

  public final class Local extends RTSCameraComponent implements
      LocalComponent<RTSCameraComponent> {

    private Local() {
      super(RTSCameraComponent.this.getEntity());
      pullChanges();
    }

    @Override
    public void pushChanges() {
      synchronized (RTSCameraComponent.this) {
        RTSCameraComponent.this.copy(this);
      }
    }

    @Override
    public void pullChanges() {
      synchronized (RTSCameraComponent.this) {
        this.copy(RTSCameraComponent.this);
      }
    }
  }

  // ////////////////////////////////////////////////////////////////////////////////////////

  private final static HashedString componentType = new HashedString(
      "RTSCameraComponent");

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
