package cat.atridas.antagonista.test;

import java.util.logging.Level;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
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
import cat.atridas.antagonista.entities.components.RigidBodyComponent;
import cat.atridas.antagonista.entities.components.TransformComponent;
import cat.atridas.antagonista.entities.components.RigidBodyComponent.PhysicType;
import cat.atridas.antagonista.entities.systems.RTSCameraSystem;
import cat.atridas.antagonista.entities.systems.RenderingCameraSystem;
import cat.atridas.antagonista.entities.systems.RenderingSystem;
import cat.atridas.antagonista.entities.systems.RigidBodySystem;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.Font;
import cat.atridas.antagonista.graphics.RTSCamera;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.input.InputManager;
import cat.atridas.antagonista.physics.KinematicCharacter;
import cat.atridas.antagonista.physics.PhysicsUserInfo;

public class TestEntities {
  /**
   * Arguments de la VM interessants:
   * 
   * -ea -Djava.library.path="./native/windows" 
   * -XX:MinHeapFreeRatio=90 -XX:MaxHeapFreeRatio=95 
   * -Xmx2048m -XX:+UseG1GC
   * -verbose:gc 
   * 
   * @param args
   */
  public static void main(String[] args) {
    //comprovem que els asserts estiguin actius
    boolean assertsActives = false;
    assert (assertsActives = true) == true;
    if(!assertsActives)
      throw new RuntimeException("Falta activar els asserts");
    
    Utils.setConsoleLogLevel(Level.CONFIG);

    Core core = Core.getCore();
    core.init(800, 600, TestEntities.class.getName(), true, null);
    
    EntityManager em = core.getEntityManager();

    ///////////////////////////////////////////////////////////////////////////////////////
    
    SystemManager sm = core.getSystemManager();
    sm.registerSystem(new RTSCameraSystem());
    
    sm.registerSystem(new RigidBodySystem());
    
    sm.registerSystem(new RenderingCameraSystem());
    sm.registerSystem(new RenderingSystem());
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////
    
    Entity entityRoom   = em.createEntity(new HashedString("Room"));
    Entity entityCamera = em.createEntity(new HashedString("Camera"));

    Entity entityMaster = em.createEntity(new HashedString("Master"));
    
    
    TransformComponent tc = em.createComponent(entityRoom.getId(), TransformComponent.getComponentStaticType());
    
    tc.init();
    
    MeshComponent mc = em.createComponent(entityRoom.getId(), MeshComponent.getComponentStaticType());
    mc.init(new HashedString("Habitacio"));
    
    RigidBodyComponent rbc = em.createComponent(entityRoom.getId(), RigidBodyComponent.getComponentStaticType());
    rbc.init(PhysicType.STATIC, core.getMeshManager().getResource(new HashedString("Habitacio")).getPhysicsMesh());

    /////////////////////////////////////////////////////////////////////
    
    
    tc = em.createComponent(entityMaster.getId(), TransformComponent.getComponentStaticType());
    
    Transformation transform = new Transformation();
    //transform.setTranslation(new Vector3f(0,0,1));
    tc.init(transform);
    
    
    mc = em.createComponent(entityMaster.getId(), MeshComponent.getComponentStaticType());
    mc.init(new HashedString("MasterTest"));
    
    
    /////////////////////////////////////////////////////////////////////
    
    RTSCameraComponent cc = em.createComponent(entityCamera.getId(), RTSCameraComponent.getComponentStaticType());
    RTSCamera camera = new RTSCamera();
    camera.setMaxDistance(30);
    camera.setDistance(20);
    camera.setPitch(0);
    cc.init(camera);
    
    
    
    
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////
    

    PhysicsUserInfo pui = new PhysicsUserInfo();
    pui.color.set(Utils.RED);
    pui.zTest = false;
    
    transform = new Transformation();
    transform.setTranslation(new Vector3f(0,0,5));
    
    KinematicCharacter kc = core.getPhysicsWorld().createKinematicCharacter(.25f, 3f, transform, .05f, pui);
    
    
    ///////////////////////////////////////////////////////////////////////////////////
    
    InputManager im = core.getInputManager();
    RenderManager rm = core.getRenderManager();
    
    
    

    SceneData sceneData = rm.getSceneData();
    //RTSCamera camera = new RTSCamera();
    
    //camera.setMaxDistance(20);

    //sceneData.setPerspective(45, 1, 100);
    //sceneData.setCamera(new Point3f(20, -15, 10), new Point3f(0, 0, 0), new Vector3f(0, 0, 1));
    sceneData.setAmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
    sceneData.setDirectionalLight(new Vector3f(0,1,1), new Color3f(0.3f, 0.3f, 0.3f));
    
    DebugRender dr = core.getDebugRender();
    dr.activate();
    Font font = core.getFontManager().getResource(new HashedString("font14"));

    im.loadActions("data/xml/inputManager.xml");
    
    im.activateMode(Utils.MAIN_GAME);
    
    Clock clock = core.getClock();
    
    clock.reset();
    sm.updateSimple(clock.update());
    clock.reset();
    while(!im.isCloseRequested() && !im.isActionActive(Utils.CLOSE)) {

      DeltaTime dt = clock.update();
      
      core.getPhysicsWorld().update(dt);
      
      sm.updateSimple(dt);
      
      Vector3f walk = new Vector3f(1,0,0);
      walk.scale(dt.dt * 30);
      kc.getBulletObject().setWalkDirection(walk);
      /*
      dr.addOBB(new Matrix4f(new float[] {
                                       1,0,0,0,
                                       0,1,0,0,
                                       0,0,1,0.5f,
                                       0,0,0,1
                                        }),
          new Point3f(.5f,.5f,.5f), new Color3f(1,0,0));
      
      dr.addAABB(new Point3f(-.5f,-.5f,0), new Point3f(.5f,.5f,1), new Color3f(1,0,0));
      */
      dr.addString2D(new Point2f(.0f,.0f), font, "FPS: " + dt.fps, .05f, new Color3f(0,0,0));
      core.getPhysicsWorld().debugDraw();
      
      rm.initFrame();
      
      core.getRenderableObjectManager().renderAll(rm);
      dr.render(rm,dt);
      
      rm.present();
      
      Core.getCore().getFontManager().cleanTextCache();
      
      Core.getCore().getInputManager().update(dt);
      
      /*
      synchronized (TestEntities.class) {
        try {
          TestEntities.class.wait(1);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      */
    }
    
    core.cleanUnusedResources(false);
    core.cleanUnusedResources(true);
    
    core.close();
    
  }
}
