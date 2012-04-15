package cat.atridas.antagonista.entities.systems.bullet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Transform;

import cat.atridas.antagonista.Conventions;
import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.Component;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.components.TransformComponent;
import cat.atridas.antagonista.entities.components.bullet.CharacterControllerComponent;
import cat.atridas.antagonista.physics.bullet.KinematicCharacter;
import cat.atridas.antagonista.physics.bullet.PhysicsWorldBullet;
import cat.atridas.antagonista.physics.PhysicsUserInfo;

public class PhysicsCharacterControllerSystem implements cat.atridas.antagonista.entities.System {
  private static Logger LOGGER = Logger.getLogger(PhysicsCharacterControllerSystem.class.getCanonicalName());

  private PhysicsWorldBullet physicsWorld = (PhysicsWorldBullet)Core.getCore().getPhysicsWorld();
  
  
  private final HashMap<HashedString, KinematicCharacter> kinematicCharacters = new HashMap<>();

  private Transform      g_transform      = new Transform();
  
  private Transformation g_transformation = new Transformation();
  private Vector3f       g_vector1        = new Vector3f();
  private Vector3f       g_vector2        = new Vector3f();
  private Vector3f       g_vector3        = new Vector3f();

  private Point3f        g_point1         = new Point3f();
  private Point3f        g_point2         = new Point3f();
  
  private Quat4f         g_quat1          = new Quat4f();
  
  @Override
  public void addEntity(Entity entity, Component<?>[] components, DeltaTime currentTime) {
    
    assert SystemManager.assertSystemInputParameters(entity,  components, this);
    
    assert !kinematicCharacters.containsKey(entity.getId());

    TransformComponent           transformC = (TransformComponent)           components[0];
    CharacterControllerComponent ccC        = (CharacterControllerComponent) components[1];
    

    Transformation transform = new Transformation();
    transformC.getTransform(transform);

    Vector3f vectorAux     = g_vector1;
    Vector3f upAdjustment  = g_vector2;
    float zAdjustment = ccC.getCharacterHeight() * .5f;
    upAdjustment.set(Conventions.UP_VECTOR);
    upAdjustment.scale(zAdjustment);
    
    PhysicsUserInfo pui = new PhysicsUserInfo(entity);
    pui.color.set(Utils.SKY_BLUE);
    pui.zTest = true;
    
    transformC.getTransform(g_transformation);
    
    g_transformation.getTranslation(vectorAux);
    vectorAux.add(upAdjustment);
    g_transformation.setTranslation(vectorAux);
    
    KinematicCharacter kc = physicsWorld.createKinematicCharacter(
        ccC.getCharacterWidth(),
        ccC.getCharacterHeight(),
        g_transformation,
        ccC.getStepHeight(),
        pui);
    
    kinematicCharacters.put(entity.getId(), kc);
  }

  @Override
  public void updateEntity(Entity entity, Component<?>[] components, DeltaTime currentTime) {

    assert SystemManager.assertSystemInputParameters(entity,  components, this);

    assert kinematicCharacters.containsKey(entity.getId());

    TransformComponent           transformC = (TransformComponent)           components[0];
    CharacterControllerComponent ccC        = (CharacterControllerComponent) components[1];
    
    // Auxiliars
    Point3f currentPoint = g_point1;
    Point3f desiredPoint = g_point2;

    Vector3f vectorAux     = g_vector1;
    Vector3f walkDirection = g_vector2;
    Vector3f upAdjustment  = g_vector3;
    //
    
    float zAdjustment = ccC.getCharacterHeight() * .5f;
    upAdjustment.set(Conventions.UP_VECTOR);
    upAdjustment.scale(zAdjustment);
    
    KinematicCharacter kc = kinematicCharacters.get(entity.getId());
    
    transformC.getTransform(g_transformation);
    
    g_transformation.getTranslation(vectorAux);
    
    ccC.getDesiredPosition(desiredPoint);
    currentPoint.set(vectorAux);
    
    if(currentPoint.distanceSquared(desiredPoint) > Utils.EPSILON) {
      walkDirection.sub(desiredPoint, currentPoint);
      float dist = walkDirection.length();
      float maxSpeed = ccC.getMaxSpeed();
      if(dist > maxSpeed * currentTime.dt) {
        walkDirection.scale(maxSpeed * currentTime.dt / dist);
      }
    } else {
      walkDirection.set(0,0,0);
    }
    
    kc.getBulletObject().setWalkDirection(walkDirection);
    kc.getGhostObject().getWorldTransform(g_transform);
    
    /////////////////////////////////////////////////////////
    walkDirection.z = 0;
    if(walkDirection.lengthSquared() > Utils.EPSILON) {
      Vector3f originalDir = Conventions.FRONT_VECTOR;
    
      
      walkDirection.normalize();
      
      Utils.getClosestRotation(originalDir, walkDirection, g_quat1);
      
      g_transformation.setRotation(g_quat1);
    }
    /////////////////////////////////////////////////////////
    
    g_transform.origin.sub(upAdjustment);
    
    g_transformation.setTranslation(g_transform.origin);
    transformC.setTransform(g_transformation);
  }

  @Override
  public void deleteEntity(Entity entity, DeltaTime currentTime) {

    assert kinematicCharacters.containsKey(entity.getId());
    
    KinematicCharacter kc = kinematicCharacters.get(entity.getId());
    if(kc != null) {
      physicsWorld.deleteKinematicCharacter(kc);
      return;
    }
    
    LOGGER.warning("Deleting entity [" + entity.getId() + "] from PhysicsCharacterControllerSystem, and it's kinematic character was not found.");
  }
  

  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static HashedString systemID = new HashedString("PhysicsCharacterControllerSystem");

  private final static List<HashedString> usedComponents;
  private final static List<HashedString> optionalComponents;
  private final static Set<HashedString> writeToComponents;
  private final static Set<HashedString> otherComponents;
  private final static Set<HashedString> usedInterfaces;
  private final static Set<HashedString> writeToInterfaces;
  
  static {
    List<HashedString> components = new ArrayList<>();
    components.add(TransformComponent.getComponentStaticType());
    components.add(CharacterControllerComponent.getComponentStaticType());
    usedComponents = Collections.unmodifiableList(components);
    
    
    optionalComponents = Collections.emptyList();
    
    Set<HashedString> writeTo = new HashSet<>();
    writeTo.add(TransformComponent.getComponentStaticType());
    writeToComponents = Collections.unmodifiableSet(writeTo);
    
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
