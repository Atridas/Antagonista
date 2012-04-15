package cat.atridas.antagonista.entities.systems.bullet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.vecmath.Vector3f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.Component;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.components.TransformComponent;
import cat.atridas.antagonista.entities.components.bullet.RigidBodyComponent;
import cat.atridas.antagonista.physics.PhysicsUserInfo;
import cat.atridas.antagonista.physics.bullet.PhysicShapeBullet;
import cat.atridas.antagonista.physics.bullet.StaticRigidBody;
import cat.atridas.antagonista.physics.bullet.PhysicsWorldBullet;

public class RigidBodySystem implements cat.atridas.antagonista.entities.System {
  private static Logger LOGGER = Logger.getLogger(RigidBodySystem.class.getCanonicalName());

  private PhysicsWorldBullet physicsWorld = (PhysicsWorldBullet)Core.getCore().getPhysicsWorld();
  
  
  private final HashMap<HashedString, StaticRigidBody> staticRigidBodies = new HashMap<>();
  
  @Override
  public void addEntity(Entity entity, Component<?>[] components, DeltaTime currentTime) {
    
    assert SystemManager.assertSystemInputParameters(entity,  components, this);

    TransformComponent    transformC    = (TransformComponent)   components[0];
    RigidBodyComponent    rigidBodyC    = (RigidBodyComponent)   components[1];
    

    Transformation transform = new Transformation();
    transformC.getTransform(transform);
    
    
    
    PhysicsUserInfo pui = new PhysicsUserInfo(entity);
    pui.color.set(Utils.RED);
    pui.zTest = true;
    
    PhysicShapeBullet shape = rigidBodyC.getShape();
    Vector3f offset = new Vector3f();
    rigidBodyC.getOffset(offset);
    
    switch(rigidBodyC.getType()) {
    case STATIC:
      
      assert !staticRigidBodies.containsKey(entity.getId());
      
      StaticRigidBody srb = physicsWorld.createStaticRigidBody(shape, offset, pui, transform);
      staticRigidBodies.put(entity.getId(), srb);
      break;
    default:
      throw new IllegalArgumentException("Not implemented! " + rigidBodyC.getType());
    }
    
    
  }

  @Override
  public void updateEntity(Entity entity, Component<?>[] components, DeltaTime currentTime) {

    assert SystemManager.assertSystemInputParameters(entity,  components, this);


  }

  @Override
  public void deleteEntity(Entity entity, DeltaTime currentTime) {
    
    StaticRigidBody srb = staticRigidBodies.get(entity.getId());
    if(srb != null) {
      physicsWorld.deleteRigidBody(srb);
      return;
    }
    
    LOGGER.warning("Deleting entity [" + entity.getId() + "] from RigidBodySystem, and it's rigid body was not found.");
  }
  

  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static HashedString systemID = new HashedString("RigidBodySystem");

  private final static List<HashedString> usedComponents;
  private final static List<HashedString> optionalComponents;
  private final static Set<HashedString> writeToComponents;
  private final static Set<HashedString> otherComponents;
  private final static Set<HashedString> usedInterfaces;
  private final static Set<HashedString> writeToInterfaces;
  
  static {
    List<HashedString> components = new ArrayList<>();
    components.add(TransformComponent.getComponentStaticType());
    components.add(RigidBodyComponent.getComponentStaticType());
    usedComponents = Collections.unmodifiableList(components);
    
    
    optionalComponents = Collections.emptyList();
    
    
    writeToComponents = Collections.unmodifiableSet(new HashSet<>(usedComponents));
    
    otherComponents = Collections.emptySet();

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

  @Override
  public Set<HashedString> getOtherReadComponents() {
    return otherComponents;
  }
}
