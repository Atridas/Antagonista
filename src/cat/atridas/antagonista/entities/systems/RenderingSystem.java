package cat.atridas.antagonista.entities.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.Component;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.components.MeshComponent;
import cat.atridas.antagonista.entities.components.TransformComponent;
import cat.atridas.antagonista.graphics.RenderableObject;
import cat.atridas.antagonista.graphics.RenderableObjectManager;

public class RenderingSystem implements cat.atridas.antagonista.entities.System {
  private final static HashedString systemID = new HashedString("RenderingSystem");

  private final static List<HashedString> usedComponents;
  private final static List<HashedString> optionalComponents;
  private final static Set<HashedString> writeToComponents;
  private final static List<HashedString> usedInterfaces;
  private final static Set<HashedString> writeToInterfaces;
  
  static {
    List<HashedString> components = new ArrayList<>();
    components.add(TransformComponent.getComponentStaticType());
    components.add(MeshComponent.getComponentStaticType());
    usedComponents = Collections.unmodifiableList(components);
    
    optionalComponents = Collections.emptyList();
    
    writeToComponents = Collections.emptySet();

    List<HashedString> interfaces = new ArrayList<>();
    interfaces.add(SystemManager.renderInteface);
    usedInterfaces = Collections.unmodifiableList(interfaces);
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
  public List<HashedString> getUsedInterfaces() {
    return usedInterfaces;
  }

  @Override
  public Set<HashedString>  getWriteToInterfaces() {
    return writeToInterfaces;
  }
  
  private RenderableObjectManager rom = Core.getCore().getRenderableObjectManager();
  
  private static ThreadLocal<Transformation> g_transAux = new ThreadLocal<Transformation>() {
                                                                    @Override protected Transformation initialValue() {
                                                                      return new Transformation();
                                                                    }
                                                                  };
  
  private final HashMap<HashedString, DeltaTime> lastModifications = new HashMap<>();

  @Override
  public void addEntity(HashedString entity, Component<?>[] components, DeltaTime currentTime) {

    assert SystemManager.assertSystemInputParameters(entity,  components, this);
    assert !lastModifications.containsKey(entity);

    TransformComponent transform = (TransformComponent)components[0];
    MeshComponent      mesh      = (MeshComponent)     components[1];
    
    Transformation l_transAux = g_transAux.get();
    
    RenderableObject ro = rom.addRenderableObject(entity, mesh.getMesh());
    transform.getTransform(l_transAux);
    ro.setTransformation(l_transAux);
    
    lastModifications.put(entity, currentTime);
    
  }

  @Override
  public void updateEntity(HashedString entity, Component<?>[] components, DeltaTime currentTime) {

    assert SystemManager.assertSystemInputParameters(entity,  components, this);
    assert lastModifications.containsKey(entity);


    TransformComponent transform = (TransformComponent)components[0];
    MeshComponent      mesh      = (MeshComponent)     components[1];
    
    DeltaTime lastUpdate = lastModifications.get(entity);
    
    boolean modified = false;
    RenderableObject ro = null;
    
    if(mesh.getMeshLastTime().isNewerThan(lastUpdate)) {
      ro = rom.getRenderableObject(entity);
      
      ro.changeMesh(mesh.getMesh());
      
      modified = true;
    }
    
    if(transform.getTransformLastTime().isNewerThan(lastUpdate)) {
      if(!modified) {
        ro = rom.getRenderableObject(entity);
        modified = true;
      }

      Transformation l_transAux = g_transAux.get();
      
      transform.getTransform(l_transAux);
      ro.setTransformation(l_transAux);
    }
    
    
    if(modified) {
      lastModifications.put(entity, currentTime);
    }
  }

  @Override
  public void deleteEntity(HashedString entity, DeltaTime currentTime) {
    assert lastModifications.containsKey(entity);
    
    rom.destroyRenderableObject(entity);
    
    lastModifications.remove(entity);
  }
}
