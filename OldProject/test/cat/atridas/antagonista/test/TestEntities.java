package cat.atridas.antagonista.test;

import java.util.logging.Level;

import javax.vecmath.Color3f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.Clock;
import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.defensa.EntityFactory;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.EntityManager;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.components.CharacterControllerComponent;
import cat.atridas.antagonista.entities.components.NavigableTerrainComponent;
import cat.atridas.antagonista.entities.systems.PhysicsCharacterControllerSystem;
import cat.atridas.antagonista.entities.systems.RTSCameraSystem;
import cat.atridas.antagonista.entities.systems.RenderingCameraSystem;
import cat.atridas.antagonista.entities.systems.RenderingSystem;
import cat.atridas.antagonista.entities.systems.RigidBodySystem;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.Font;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.input.InputManager;
import cat.atridas.antagonista.lwjgl.LWJGLManagerFactory;
import cat.atridas.antagonista.physics.PhysicsUserInfo;

public class TestEntities {
  /**
   * Arguments de la VM interessants:
   * 
   * -ea -Djava.library.path="./native/windows" 
   * -Xmx1024m -XX:+UseG1GC
   * -XX:MaxGCPauseMillis=5
   * -XX:GCPauseIntervalMillis=83
   * -verbose:gc 
   * -Dcom.sun.management.jmxremote
   *  
   * @param args
   */
  @SuppressWarnings("unused")
  public static void main(String[] args) {
    //comprovem que els asserts estiguin actius
    boolean assertsActives = false;
    assert (assertsActives = true) == true;
    if(!assertsActives)
      throw new RuntimeException("Falta activar els asserts");
    
    Utils.setConsoleLogLevel(Level.CONFIG);

    Core core = Core.getCore();
    core.init(800, 600, TestEntities.class.getName(), new LWJGLManagerFactory(), true, null);
    
    EntityManager em = core.getEntityManager();

    ///////////////////////////////////////////////////////////////////////////////////////
    
    SystemManager sm = core.getSystemManager();
    sm.registerSystem(new RTSCameraSystem());
    
    sm.registerSystem(new PhysicsCharacterControllerSystem());
    sm.registerSystem(new RigidBodySystem());
    
    sm.registerSystem(new RenderingCameraSystem());
    sm.registerSystem(new RenderingSystem());
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////
    /*{
      Entity entityRoom   = em.createEntity(new HashedString("Room"));
      
      TransformComponent tc = em.createComponent(entityRoom.getId(), TransformComponent.getComponentStaticType());
      
      tc.init();
      
      MeshComponent mc = em.createComponent(entityRoom.getId(), MeshComponent.getComponentStaticType());
      mc.init(new HashedString("Habitacio"));
      
      RigidBodyComponent rbc = em.createComponent(entityRoom.getId(), RigidBodyComponent.getComponentStaticType());
      rbc.init(PhysicType.STATIC, core.getMeshManager().getResource(new HashedString("Habitacio")).getPhysicsMesh());
    }*/
    MeshManager mm = core.getMeshManager();
    Vector3f vecAux = new Vector3f();
    Quat4f quatAux = new Quat4f();
    Transformation transAux = new Transformation();
    HashedString altarMesh = new HashedString("AltarBasic");
    /*
    HashedString terraMesh = new HashedString("TerraBasic");
    HashedString murMesh = new HashedString("ParetsBasic");
    for(int i = -10; i <= 10; i+=2) {
      for(int j = -10; j <= 10; j+=2) {
        vecAux.set(i,j,0);
        transAux.setTranslation(vecAux);
        
        createRajola(em, mm, transAux, terraMesh);
      }
      
      // murs
      // top
      {
        vecAux.set(i,12,0);
        transAux.setTranslation(vecAux);
        
        createMur(em, mm, transAux, murMesh);
      }
      // right
      {
        vecAux.set(12,i,0);
        transAux.setTranslation(vecAux);
        
        createMur(em, mm, transAux, murMesh);
      }
      // bot
      {
        vecAux.set(i,-12,0);
        transAux.setTranslation(vecAux);
        
        createMur(em, mm, transAux, murMesh);
      }
      // left
      {
        vecAux.set(-12,i,0);
        transAux.setTranslation(vecAux);
        
        createMur(em, mm, transAux, murMesh);
      }
    }

    {
      vecAux.set(12,12,0);
      transAux.setTranslation(vecAux);
      
      createMur(em, mm, transAux, murMesh);
    }
    {
      vecAux.set(-12,12,0);
      transAux.setTranslation(vecAux);
      
      createMur(em, mm, transAux, murMesh);
    }
    {
      vecAux.set(12,-12,0);
      transAux.setTranslation(vecAux);
      
      createMur(em, mm, transAux, murMesh);
    }
    {
      vecAux.set(-12,-12,0);
      transAux.setTranslation(vecAux);
      
      createMur(em, mm, transAux, murMesh);
    }
    */
    EntityFactory.crearNivell(nivellDeProves, mm, em);
    
    vecAux.set(22,0,0);
    
    quatAux.x = quatAux.y = 0;
    quatAux.z = (float)Math.sqrt(.5f);
    quatAux.w = (float)Math.sqrt(.5f);
    
    transAux.setTranslation(vecAux);
    transAux.setRotation(quatAux);
    EntityFactory.createAltar(em, mm, transAux, altarMesh);
    
    /////////////////////////////////////////////////////////////////////
    Entity entityMaster = EntityFactory.createMaster(em, new HashedString("Master"));
    
    /////////////////////////////////////////////////////////////////////
    EntityFactory.createCamera(em, new HashedString("Camera"));
    
    ////////////////////////////////////////////////////////////////////////////////////
    
    Point2f point2d = new Point2f();

    Point3f point1 = new Point3f();
    Point3f point2 = new Point3f();
    Point3f point3 = new Point3f();
    Point3f point4 = new Point3f();

    Vector3f vector1 = new Vector3f();
    Vector3f vector2 = new Vector3f();
    Vector3f vector3 = new Vector3f();
    Vector3f vector4 = new Vector3f();
    
    HashedString shootAction = new HashedString("shoot");
    
    ////////////////////////////////////////////////////////////////////////////////////
    
    /*
    PhysicsUserInfo pui = new PhysicsUserInfo();
    pui.color.set(new Color3f(0,1,1));
    pui.zTest = false;
    
    transform = new Transformation();
    transform.setTranslation(new Vector3f(0,0,5));
    
    KinematicCharacter kc = core.getPhysicsWorld().createKinematicCharacter(.5f, 2f, transform, .05f, pui);
    */
    
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
    
    float runningTime = 0;
    while(!im.isCloseRequested() && !im.isActionActive(Utils.CLOSE)) {

      DeltaTime dt = clock.update();
      
      
      if(im.isActionActive(shootAction)) {
        point2d.x = im.getMouseX();
        point2d.y = im.getMouseY();
        rm.getSceneData().getFarPlanePoint(point2d, point1);
        //dr.addCross(point1, Utils.BLUE, 3, 5);
        

        Point3f origin  = point2;
        Point3f destiny = point1;
        Point3f collisionPoint = point3;
        Vector3f collisionNormal = vector1;
        rm.getSceneData().getCameraPosition(origin);
        
        PhysicsUserInfo pui = core.getPhysicsWorld().raycast(origin,destiny,collisionPoint,collisionNormal);
        if(pui != null && pui.entity.getGlobalComponent(NavigableTerrainComponent.getComponentStaticType()) != null) {
          dr.addCross(collisionPoint, pui.color, 1, .5f);
          
          
          CharacterControllerComponent ccc = em.getComponent(entityMaster, CharacterControllerComponent.getComponentStaticType());
          ccc.setDesiredPosition(collisionPoint);
        }
      }
      
      
      core.getPhysicsWorld().update(dt);
      
      sm.updateSimple(dt);
      
      /*////////////////////////////////////////
      Point3f origin  = point1;
      Point3f destiny = point2;
      Point3f collisionPoint = point3;
      Vector3f collisionNormal = vector1;
      
      origin.set(0,0,10);
      destiny.set(0,0,-10);
      if(core.getPhysicsWorld().raycast(origin,destiny,collisionPoint,collisionNormal) != null) {
        dr.addCross(collisionPoint, new Color3f(1,1,0), 3);
        point4.set(collisionNormal);
        point4.scale(5);
        point4.add(collisionPoint);
        dr.addLine(collisionPoint, point4, new Color3f(0,1,1));
      }
      ////////////////////////////////////////*/
      
      //Vector3f walk = new Vector3f(1,0,0);
      //walk.scale(dt.dt * 30);
      //kc.getBulletObject().setWalkDirection(walk);
      
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
      runningTime += dt.dt;
      dr.addString2D(new Point2f(.0f,.05f), font, "timeSec: " + runningTime, .05f, new Color3f(0,0,0));
      dr.addString2D(new Point2f(.0f,.1f), font, "timeMS: " + dt.getTimeMilisSinceStart(), .05f, new Color3f(0,0,0));
      dr.addString2D(new Point2f(.0f,.15f), font, "timeDrift: " + runningTime * 1000 / dt.getTimeMilisSinceStart(), .05f, new Color3f(0,0,0));
      
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
  
  
  private static final String nivellDeProves = 
          "30 14\n" +
          "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
          "MMRRRRRRRRRRRRMMMMMRRRRRMMMMMM\n" +
          "MMRRRRRRRRRRRRMMMMRRRRRRRMMMMM\n" +
          "MMRRRRRRRRRRRRMMMRRRRRRRRRMMMM\n" +
          "MMRRRRRRRRRRRRMMRRRRRRRRRRRMMM\n" +
          "MMRRRRRRRRRRRRMRRRRRRRRRRRRRMM\n" +
          "MRRRRRRRRRRRRRRRRRRRRRRRRRRRMM\n" +
          "MRRRRRRRRRRRRRRRRRRRRRRRRRRRMM\n" +
          "MMRRRRRRRRRRRRMRRRRRRRRRRRRRMM\n" +
          "MMRRRRRRRRRRRRMMRRRRRRRRRRRMMM\n" +
          "MMRRRRRRRRRRRRMMMRRRRRRRRRMMMM\n" +
          "MMRRRRRRRRRRRRMMMMRRRRRRRMMMMM\n" +
          "MMRRRRRRRRRRRRMMMMMRRRRRMMMMMM\n" +
          "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM";
}
