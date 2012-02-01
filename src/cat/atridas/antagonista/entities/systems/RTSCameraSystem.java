package cat.atridas.antagonista.entities.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.Component;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.components.CameraComponent;
import cat.atridas.antagonista.entities.components.RTSCameraComponent;
import cat.atridas.antagonista.graphics.RTSCamera;
import cat.atridas.antagonista.input.InputManager;

public class RTSCameraSystem implements cat.atridas.antagonista.entities.System {
  
  //TODO
  HashedString camUp    = new HashedString("move_camera_up");
  HashedString camDown  = new HashedString("move_camera_down");
  HashedString camLeft  = new HashedString("move_camera_left");
  HashedString camRight = new HashedString("move_camera_right");
  HashedString camDist  = new HashedString("move_camera_distance");
  
  

  @Override
  public void addEntity(HashedString entity, Component<?>[] components, DeltaTime currentTime) {
    
    assert SystemManager.assertSystemInputParameters(entity,  components, this);

    RTSCameraComponent    camera    = (RTSCameraComponent)   components[0];
    
    CameraComponent cc = Core.getCore().getEntityManager().createComponent(entity, CameraComponent.getComponentStaticType());
    
    cc.setCamera(camera.getCamera());
    
  }

  @Override
  public void updateEntity(HashedString entity, Component<?>[] components, DeltaTime currentTime) {

    assert SystemManager.assertSystemInputParameters(entity,  components, this);

    RTSCameraComponent rtsCameraComponent    = (RTSCameraComponent) components[0];
    CameraComponent    cameraComponent       = (CameraComponent)    components[1];
    
    assert cameraComponent != null;
    
    if(rtsCameraComponent.isActive()) {
      InputManager im = Core.getCore().getInputManager();
      RTSCamera rtsCamera = rtsCameraComponent.getCamera();
      
      if(im.isActionActive(camUp)) {
        rtsCamera.moveUp(2f * currentTime.dt);
      }
      if(im.isActionActive(camDown)) {
        rtsCamera.moveUp(-2f * currentTime.dt);
      }
      if(im.isActionActive(camRight)) {
        rtsCamera.moveRight(2f * currentTime.dt);
      }
      if(im.isActionActive(camLeft)) {
        rtsCamera.moveRight(-2f * currentTime.dt);
      }
      if(im.isActionActive(camDist)) {
        rtsCamera.addDistance( -.01f * im.getActionValue(camDist) );
      }
      
      cameraComponent.setActive(true);
    } else {
      cameraComponent.setActive(false);
    }

  }

  @Override
  public void deleteEntity(HashedString entity, DeltaTime currentTime) {
    //TODO Core.getCore().getEntityManager().deleteComponent
  }
  

  private final static HashedString systemID = new HashedString("RTSCameraSystem");

  private final static List<HashedString> usedComponents;
  private final static List<HashedString> optionalComponents;
  private final static Set<HashedString> writeToComponents;
  private final static Set<HashedString> usedInterfaces;
  private final static Set<HashedString> writeToInterfaces;
  
  static {
    List<HashedString> components = new ArrayList<>();
    components.add(RTSCameraComponent.getComponentStaticType());
    usedComponents = Collections.unmodifiableList(components);
    
    components = new ArrayList<>();
    components.add(CameraComponent.getComponentStaticType());
    optionalComponents = Collections.unmodifiableList(components);
    
    Set<HashedString> writes = new HashSet<>();
    writes.add(RTSCameraComponent.getComponentStaticType());
    writes.add(CameraComponent.getComponentStaticType());
    
    writeToComponents = Collections.unmodifiableSet(writes);

    Set<HashedString> interfaces = new HashSet<>();
    interfaces.add(SystemManager.renderInteface);
    interfaces.add(SystemManager.inputInteface);
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