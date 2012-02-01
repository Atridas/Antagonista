package cat.atridas.antagonista.entities.components;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.BaseComponent;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.GlobalComponent;
import cat.atridas.antagonista.entities.LocalComponent;
import cat.atridas.antagonista.graphics.Camera;

public class CameraComponent extends BaseComponent<CameraComponent> {
  
  private boolean isActive = true;
  private Camera camera = null;

  public CameraComponent(Entity _entity) {
    super(_entity);
  }
  
  public void setCamera(Camera _camera) {
    camera = _camera;
  }
  
  public Camera getCamera() {
    return camera;
  }
  
  public boolean isActive() {
    return isActive;
  }
  
  public void setActive(boolean active) {
    isActive = active;
  }

  @Override
  public void copy(CameraComponent _other) {
    super.copy(_other);
    camera = _other.camera;
  }
  
  @Override
  public String toString() {
    return "CameraComponent: " + camera;
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  
  public final static class Global extends CameraComponent implements GlobalComponent<CameraComponent> {

    public Global(Entity _entity) {
      super(_entity);
    }

    @Override
    public Local createLocalCopy() {
      return new Local();
    }
    
  }
  
  public final class Local extends CameraComponent implements LocalComponent<CameraComponent> {

    private Local() {
      super(CameraComponent.this.getEntity());
      pullChanges();
    }

    @Override
    public void pushChanges() {
      synchronized (CameraComponent.this) {
        CameraComponent.this.copy(this);
      }
    }

    @Override
    public void pullChanges() {
      synchronized (CameraComponent.this) {
        this.copy(CameraComponent.this);
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////

  private final static HashedString componentType = new HashedString("CameraComponent");
  
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
