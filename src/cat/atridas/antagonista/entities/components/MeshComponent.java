package cat.atridas.antagonista.entities.components;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.BaseComponent;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.GlobalComponent;
import cat.atridas.antagonista.entities.LocalComponent;

public abstract class MeshComponent extends BaseComponent<MeshComponent> {
  
  private HashedString meshId = null;
  private DeltaTime lastMeshIdChange = null;

  public MeshComponent(Entity _entity) {
    super(_entity);
    lastMeshIdChange = globalClock.getCurrentFrameDeltaTime();
  }
  
  public void setMesh(HashedString _meshId) {
    if(meshId == null || !meshId.equals(_meshId)) {
      meshId = _meshId;
      lastMeshIdChange = globalClock.getCurrentFrameDeltaTime();
    }
  }
  
  public HashedString getMesh() {
    return meshId;
  }
  
  public DeltaTime getMeshLastTime() {
    return lastMeshIdChange;
  }

  @Override
  public void copy(MeshComponent _other) {
    super.copy(_other);
    meshId = _other.meshId;
    lastMeshIdChange = _other.lastMeshIdChange;
  }
  
  @Override
  public String toString() {
    return "MeshComponent: " + meshId;
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  
  public final static class Global extends MeshComponent implements GlobalComponent<MeshComponent> {

    public Global(Entity _entity) {
      super(_entity);
    }

    @Override
    public Local createLocalCopy() {
      return new Local();
    }
    
  }
  
  public final class Local extends MeshComponent implements LocalComponent<MeshComponent> {

    private Local() {
      super(MeshComponent.this.getEntity());
      pullChanges();
    }

    @Override
    public void pushChanges() {
      synchronized (MeshComponent.this) {
        MeshComponent.this.copy(this);
      }
    }

    @Override
    public void pullChanges() {
      synchronized (MeshComponent.this) {
        this.copy(MeshComponent.this);
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////

  private final static HashedString componentType = new HashedString("MeshComponent");
  
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
