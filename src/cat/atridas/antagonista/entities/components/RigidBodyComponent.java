package cat.atridas.antagonista.entities.components;

import java.util.logging.Logger;

import javax.vecmath.Vector3f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.BaseComponent;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.GlobalComponent;
import cat.atridas.antagonista.entities.LocalComponent;
import cat.atridas.antagonista.physics.BoundingBoxShape;
import cat.atridas.antagonista.physics.PhysicShape;
import cat.atridas.antagonista.physics.PhysicsStaticMeshCore;

public class RigidBodyComponent extends BaseComponent<RigidBodyComponent> {
  private static Logger LOGGER = Logger.getLogger(RigidBodyComponent.class.getCanonicalName());
  
  private PhysicType type;
  private PhysicShape shape;

  public RigidBodyComponent(Entity _entity) {
    super(_entity);
  }
  
  public void init(PhysicType _type, PhysicShape _shape) {
    type = _type;
    shape = _shape;
    switch(type) {
    case STATIC:
      if(_shape instanceof PhysicsStaticMeshCore) {
        break;
      }else if(_shape instanceof BoundingBoxShape) {
        break;
      }
    default:
      LOGGER.severe("RigidBody type and Shape are incompatible!");
      throw new IllegalArgumentException("RigidBody type and Shape are incompatible!");
    }
    setInitialized();
  }
  
  public PhysicType getType() {
    return type;
  }
  
  public PhysicShape getShape() {
    return shape;
  }
  
  public void getOffset(Vector3f offset_) {
    shape.getFromGameToBulletVector(offset_);
  }

  @Override
  public void copy(RigidBodyComponent _other) {
    super.copy(_other);
    type = _other.type;
    shape = _other.shape;
  }
  
  @Override
  public String toString() {
    return "RigidBodyComponent: ";
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  
  public static enum PhysicType {
    STATIC, DYNAMIC, KINEMATIC
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  
  public final static class Global extends RigidBodyComponent implements GlobalComponent<RigidBodyComponent> {

    public Global(Entity _entity) {
      super(_entity);
    }

    @Override
    public Local createLocalCopy() {
      return new Local();
    }
    
  }
  
  public final class Local extends RigidBodyComponent implements LocalComponent<RigidBodyComponent> {

    private Local() {
      super(RigidBodyComponent.this.getEntity());
      pullChanges();
    }

    @Override
    public void pushChanges() {
      synchronized (RigidBodyComponent.this) {
        RigidBodyComponent.this.copy(this);
      }
    }

    @Override
    public void pullChanges() {
      synchronized (RigidBodyComponent.this) {
        this.copy(RigidBodyComponent.this);
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////

  private final static HashedString componentType = new HashedString("RigidBodyComponent");
  
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
