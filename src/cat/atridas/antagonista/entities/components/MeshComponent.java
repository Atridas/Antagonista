package cat.atridas.antagonista.entities.components;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.BaseComponent;
import cat.atridas.antagonista.entities.Entity;

public final class MeshComponent extends BaseComponent<MeshComponent> {
  
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
    meshId = _other.meshId;
    lastMeshIdChange = _other.lastMeshIdChange;
  }
  
  @Override
  public String toString() {
    return "MeshComponent: " + meshId;
  }

  //////////////////////////////////////////////////////////////////////////////////////////

  private final static HashedString componentType = new HashedString("MeshComponent");
  
  @Override
  public HashedString getComponentType() {
    return componentType;
  }

  @Override
  public MeshComponent clone() {
    return (MeshComponent) super.clone();
  }
 
  public static HashedString getComponentStaticType() {
    return componentType;
  }
  
  static {
    Core.getCore().getEntityManager().registerComponentType(MeshComponent.class);
  }
}
