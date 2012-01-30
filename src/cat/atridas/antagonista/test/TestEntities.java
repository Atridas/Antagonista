package cat.atridas.antagonista.test;

import java.util.logging.Level;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.EntityManager;
import cat.atridas.antagonista.entities.components.MeshComponent;
import cat.atridas.antagonista.entities.components.TransformComponent;

public class TestEntities {
  public static void main(String[] args) {
    //comprovem que els asserts estiguin actius
    boolean assertsActives = false;
    assert (assertsActives = true) == true;
    if(!assertsActives)
      throw new RuntimeException("Falta activar els asserts");
    
    Utils.setConsoleLogLevel(Level.CONFIG);

    Core core = Core.getCore();
    core.init(800, 600, Test.class.getName(), true, null);
    
    EntityManager em = core.getEntityManager();

    Entity entity1 = em.createEntity();
    Entity entity2 = em.createEntity();
    Entity entity3 = em.createEntity();
    Entity entity4 = em.createEntity();
    Entity entity5 = em.createEntity(new HashedString("aaa"));

    System.out.println(entity1.getId());
    System.out.println(entity2.getId());
    System.out.println(entity3.getId());
    System.out.println(entity4.getId());
    System.out.println(entity5.getId());
    
    
    TransformComponent tc = em.createComponent(entity1.getId(), TransformComponent.getComponentStaticType());
    Transformation transform = new Transformation();
    tc.getTransform(transform);
    
    MeshComponent.Global mc = em.createComponent(entity1.getId(), MeshComponent.getComponentStaticType());

    MeshComponent.Local localMC1 =  mc.createLocalCopy();
    MeshComponent.Local localMC2 =  mc.createLocalCopy();
    
    localMC1.setMesh(new HashedString("Habitacio"));
    
    localMC1.pushChanges();
    
    localMC2.pullChanges();
    
    System.out.println(transform.toString());
  }
}
