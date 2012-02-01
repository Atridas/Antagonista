package cat.atridas.antagonista.test;

import java.util.logging.Level;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.Clock;
import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.EntityManager;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.components.MeshComponent;
import cat.atridas.antagonista.entities.components.RTSCameraComponent;
import cat.atridas.antagonista.entities.components.TransformComponent;
import cat.atridas.antagonista.entities.systems.RTSCameraSystem;
import cat.atridas.antagonista.entities.systems.RenderingCameraSystem;
import cat.atridas.antagonista.entities.systems.RenderingSystem;
import cat.atridas.antagonista.graphics.RTSCamera;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.input.InputManager;

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

    
    SystemManager sm = core.getSystemManager();
    sm.registerSystem(new RTSCameraSystem());
    sm.registerSystem(new RenderingCameraSystem());
    sm.registerSystem(new RenderingSystem());
    
    Entity entityRoom   = em.createEntity(new HashedString("Room"));
    Entity entityCamera = em.createEntity(new HashedString("Camera"));
    
    
    TransformComponent tc = em.createComponent(entityRoom.getId(), TransformComponent.getComponentStaticType());
    Transformation transform = new Transformation();
    tc.getTransform(transform);
    
    MeshComponent mc = em.createComponent(entityRoom.getId(), MeshComponent.getComponentStaticType());
    mc.setMesh(new HashedString("Habitacio"));

    
    RTSCameraComponent cc = em.createComponent(entityCamera.getId(), RTSCameraComponent.getComponentStaticType());
    RTSCamera camera = new RTSCamera();
    camera.setMaxDistance(20);
    cc.setCamera(camera);
    
    
    
    InputManager im = core.getInputManager();
    RenderManager rm = core.getRenderManager();
    
    
    

    SceneData sceneData = rm.getSceneData();
    //RTSCamera camera = new RTSCamera();
    
    //camera.setMaxDistance(20);

    //sceneData.setPerspective(45, 1, 100);
    //sceneData.setCamera(new Point3f(20, -15, 10), new Point3f(0, 0, 0), new Vector3f(0, 0, 1));
    sceneData.setAmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
    sceneData.setDirectionalLight(new Vector3f(0,1,1), new Color3f(0.3f, 0.3f, 0.3f));
    

    im.loadActions("data/xml/inputManager.xml");
    
    im.activateMode(Utils.MAIN_GAME);
    
    Clock clock = core.getClock();
    while(!im.isCloseRequested() && !im.isActionActive(Utils.CLOSE)) {

      DeltaTime dt = clock.update();
      
      core.getPhysicsWorld().update(dt);
      
      sm.updateSimple(dt);
      
      
      core.getPhysicsWorld().debugDraw();
      
      rm.initFrame();
      
      core.getRenderableObjectManager().renderAll(rm);
      core.getDebugRender().render(rm,dt);
      
      rm.present();
      
      Core.getCore().getFontManager().cleanTextCache();
      
      Core.getCore().getInputManager().update(dt);
      
      synchronized (TestEntities.class) {
        try {
          TestEntities.class.wait(1);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    
    core.cleanUnusedResources(false);
    core.cleanUnusedResources(true);
    
    core.close();
    
  }
}
