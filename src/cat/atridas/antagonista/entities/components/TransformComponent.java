package cat.atridas.antagonista.entities.components;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.BaseComponent;
import cat.atridas.antagonista.entities.Entity;

public final class TransformComponent extends BaseComponent<TransformComponent> {
  
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
    transformation.setTransform(_other.transformation);
    lastTransformationChange = _other.lastTransformationChange;
  }
  
  @Override
  public String toString() {
    return "TransformComponent\n" + transformation.toString();
  }

  //////////////////////////////////////////////////////////////////////////////////////////

  private final static HashedString componentType = new HashedString("TransformComponent");
  
  @Override
  public HashedString getComponentType() {
    return componentType;
  }

  @Override
  public TransformComponent clone() {
    TransformComponent other = (TransformComponent) super.clone();
    other.transformation = new Transformation();
    other.transformation.setTransform(transformation);
    return other;
  }
 
  public static HashedString getComponentStaticType() {
    return componentType;
  }
  
  static {
    Core.getCore().getEntityManager().registerComponentType(TransformComponent.class);
  }
}
