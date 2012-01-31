package cat.atridas.antagonista.entities.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.Component;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.components.MeshComponent;
import cat.atridas.antagonista.entities.components.TransformComponent;
import cat.atridas.antagonista.graphics.RenderableObject;
import cat.atridas.antagonista.graphics.RenderableObjectManager;

public class RenderingSystem {
  private final static List<HashedString> usedComponents;
  private final static List<HashedString> usedInterfaces;
  
  static {
    List<HashedString> components = new ArrayList<>();
    components.add(TransformComponent.getComponentStaticType());
    components.add(MeshComponent.getComponentStaticType());
    usedComponents = Collections.unmodifiableList(components);

    List<HashedString> interfaces = new ArrayList<>();
    interfaces.add(SystemManager.renderInteface);
    usedInterfaces = Collections.unmodifiableList(interfaces);
    
  }
  
  public List<HashedString> getUsedComponents() {
    return usedComponents;
  }
  
  public List<HashedString> getUsedInterfaces() {
    return usedInterfaces;
  }
  
  private RenderableObjectManager rom = Core.getCore().getRenderableObjectManager();
  
  private static ThreadLocal<Transformation> g_transAux = new ThreadLocal<>();
  
  private final HashMap<HashedString, DeltaTime> lastModifications = new HashMap<>();
  
  public void addEntity(Entity entity, Component<?>[] components, DeltaTime currentTime) {
    
    assert components.length == usedComponents.size();
    if(RenderingSystem.class.desiredAssertionStatus()) {
      for(int i = 0; i < components.length; ++i) {
        assert components[i].getComponentType().equals(usedComponents.get(i));
        assert components[i].getEntityId().equals(entity.getId());
      }
    }
    assert !lastModifications.containsKey(entity.getId());

    TransformComponent transform = (TransformComponent)components[0];
    MeshComponent      mesh      = (MeshComponent)     components[1];
    
    Transformation l_transAux = g_transAux.get();
    
    RenderableObject ro = rom.addRenderableObject(entity.getId(), mesh.getComponentType());
    transform.getTransform(l_transAux);
    ro.setTransformation(l_transAux);
    
    lastModifications.put(entity.getId(), currentTime);
    
  }
  
  public void updateEntity(Entity entity, Component<?>[] components, DeltaTime currentTime) {

    assert components.length == usedComponents.size();
    if(RenderingSystem.class.desiredAssertionStatus()) {
      for(int i = 0; i < components.length; ++i) {
        assert components[i].getComponentType().equals(usedComponents.get(i));
        assert components[i].getEntityId().equals(entity.getId());
      }
    }
    assert lastModifications.containsKey(entity.getId());


    TransformComponent transform = (TransformComponent)components[0];
    MeshComponent      mesh      = (MeshComponent)     components[1];
    
    DeltaTime lastUpdate = lastModifications.get(entity.getId());
    
    boolean modified = false;
    RenderableObject ro = null;
    
    if(mesh.getMeshLastTime().isNewerThan(lastUpdate)) {
      ro = rom.getRenderableObject(entity.getId());
      
      ro.changeMesh(mesh.getMesh());
      
      modified = true;
    }
    
    if(transform.getTransformLastTime().isNewerThan(lastUpdate)) {
      if(!modified) {
        ro = rom.getRenderableObject(entity.getId());
        modified = true;
      }

      Transformation l_transAux = g_transAux.get();
      
      transform.getTransform(l_transAux);
      ro.setTransformation(l_transAux);
    }
    
    
    if(modified) {
      lastModifications.put(entity.getId(), currentTime);
    }
  }
  
  public void deleteEntity(Entity entity, DeltaTime currentTime) {
    assert lastModifications.containsKey(entity.getId());
    
    rom.destroyRenderableObject(entity.getId());
    
    lastModifications.remove(entity.getId());
  }
}
