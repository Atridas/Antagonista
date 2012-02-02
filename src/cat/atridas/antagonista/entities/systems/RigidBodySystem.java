package cat.atridas.antagonista.entities.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.Component;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.components.RigidBodyComponent;
import cat.atridas.antagonista.entities.components.TransformComponent;
import cat.atridas.antagonista.physics.PhysicShape;
import cat.atridas.antagonista.physics.PhysicsStaticMeshCore;
import cat.atridas.antagonista.physics.PhysicsUserInfo;
import cat.atridas.antagonista.physics.PhysicsWorld;

public class RigidBodySystem implements cat.atridas.antagonista.entities.System {
  private static Logger LOGGER = Logger.getLogger(RigidBodySystem.class.getCanonicalName());

  private PhysicsWorld physicsWorld = Core.getCore().getPhysicsWorld();
  
  @Override
  public void addEntity(HashedString entity, Component<?>[] components, DeltaTime currentTime) {
    
    assert SystemManager.assertSystemInputParameters(entity,  components, this);

    //TransformComponent    transformC    = (TransformComponent)   components[0];
    RigidBodyComponent    rigidBodyC    = (RigidBodyComponent)   components[1];
    

    PhysicsUserInfo pui = new PhysicsUserInfo();
    pui.color.set(Utils.RED);
    pui.zTest = true;
    
    PhysicShape shape = rigidBodyC.getShape();
    
    switch(rigidBodyC.getType()) {
    case STATIC:
      if(shape instanceof PhysicsStaticMeshCore) {
        physicsWorld.createStaticRigidBody((PhysicsStaticMeshCore) rigidBodyC.getShape(), pui);
        break;
      }
    default:
      LOGGER.severe("RigidBody type and Shape are incompatible!");
      throw new IllegalArgumentException("RigidBody type and Shape are incompatible!");
    }
    
    
  }

  @Override
  public void updateEntity(HashedString entity, Component<?>[] components, DeltaTime currentTime) {

    assert SystemManager.assertSystemInputParameters(entity,  components, this);


  }

  @Override
  public void deleteEntity(HashedString entity, DeltaTime currentTime) {

  }
  

  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static HashedString systemID = new HashedString("RigidBodySystem");

  private final static List<HashedString> usedComponents;
  private final static List<HashedString> optionalComponents;
  private final static Set<HashedString> writeToComponents;
  private final static Set<HashedString> usedInterfaces;
  private final static Set<HashedString> writeToInterfaces;
  
  static {
    List<HashedString> components = new ArrayList<>();
    components.add(TransformComponent.getComponentStaticType());
    components.add(RigidBodyComponent.getComponentStaticType());
    usedComponents = Collections.unmodifiableList(components);
    
    
    optionalComponents = Collections.emptyList();
    
    
    writeToComponents = Collections.unmodifiableSet(new HashSet<>(usedComponents));

    Set<HashedString> interfaces = new HashSet<>();
    interfaces.add(SystemManager.physicsInteface);
    usedInterfaces = Collections.unmodifiableSet(interfaces);
    writeToInterfaces = Collections.unmodifiableSet(new HashSet<HashedString>(usedInterfaces));
  }

  @Override
  public HashedString getSystemId() {
    return systemID;
  }
  
  @Override
  public List<HashedString> getUsedComponents() {
    return usedComponents;
  }
  
  @Override
  public List<HashedString> getOptionalComponents() {
    return optionalComponents;
  }

  @Override
  public Set<HashedString>  getWriteToComponents() {
    return writeToComponents;
  }

  @Override
  public Set<HashedString> getUsedInterfaces() {
    return usedInterfaces;
  }

  @Override
  public Set<HashedString>  getWriteToInterfaces() {
    return writeToInterfaces;
  }
}
