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
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.components.CameraComponent;
import cat.atridas.antagonista.graphics.SceneData;

public class RenderingCameraSystem implements cat.atridas.antagonista.entities.System {
  

  @Override
  public void addEntity(Entity entity, Component<?>[] components, DeltaTime currentTime) {
    // --
  }

  @Override
  public void updateEntity(Entity entity, Component<?>[] components, DeltaTime currentTime) {

    assert SystemManager.assertSystemInputParameters(entity,  components, this);

    CameraComponent    camera    = (CameraComponent)   components[0];
    
    if(camera.isActive()) {
      SceneData sd = Core.getCore().getRenderManager().getSceneData();
      
      sd.setCamera(camera.getCamera());
    }
  }

  @Override
  public void deleteEntity(Entity entity, DeltaTime currentTime) {
    // --
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static HashedString systemID = new HashedString("RenderingCameraSystem");

  private final static List<HashedString> usedComponents;
  private final static List<HashedString> optionalComponents;
  private final static Set<HashedString> writeToComponents;
  private final static Set<HashedString> otherComponents;
  private final static Set<HashedString> usedInterfaces;
  private final static Set<HashedString> writeToInterfaces;
  
  static {
    List<HashedString> components = new ArrayList<>();
    components.add(CameraComponent.getComponentStaticType());
    usedComponents = Collections.unmodifiableList(components);

    optionalComponents = Collections.emptyList();
    
    writeToComponents = Collections.emptySet();
    
    otherComponents = Collections.emptySet();

    Set<HashedString> interfaces = new HashSet<>();
    interfaces.add(SystemManager.renderInteface);
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
