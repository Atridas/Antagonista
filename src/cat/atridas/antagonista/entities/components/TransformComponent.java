package cat.atridas.antagonista.entities.components;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.BaseComponent;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.GlobalComponent;
import cat.atridas.antagonista.entities.LocalComponent;

public abstract class TransformComponent extends BaseComponent<TransformComponent> {
  
  private Transformation transformation = new Transformation();
  private DeltaTime lastTransformationChange = null;
  
  
  public TransformComponent(Entity entity) {
    super(entity);
    lastTransformationChange = globalClock.getCurrentFrameDeltaTime();
  }

  
  public void setTransform(Transformation _transformation) {
    transformation.setTransform(_transformation);
    lastTransformationChange = globalClock.getCurrentFrameDeltaTime();
  }
  
  public void getTransform(Transformation transformation_) {
    transformation_.setTransform(transformation);
  }
  
  public DeltaTime getTransformLastTime() {
    return lastTransformationChange;
  }

  @Override
  public void copy(TransformComponent _other) {
    super.copy(_other);
    transformation.setTransform(_other.transformation);
    lastTransformationChange = _other.lastTransformationChange;
  }
  
  @Override
  public String toString() {
    return "TransformComponent\n" + transformation.toString();
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  
  public final static class Global extends TransformComponent implements GlobalComponent<TransformComponent> {

    public Global(Entity _entity) {
      super(_entity);
    }

    @Override
    public Local createLocalCopy() {
      return new Local();
    }
    
  }
  
  public final class Local extends TransformComponent implements LocalComponent<TransformComponent> {

    private Local() {
      super(TransformComponent.this.getEntity());
      pullChanges();
    }

    @Override
    public void pushChanges() {
      synchronized (TransformComponent.this) {
        TransformComponent.this.copy(this);
      }
    }

    @Override
    public void pullChanges() {
      synchronized (TransformComponent.this) {
        this.copy(TransformComponent.this);
      }
    }
    
    
  }

  //////////////////////////////////////////////////////////////////////////////////////////

  private final static HashedString componentType = new HashedString("TransformComponent");
  
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
