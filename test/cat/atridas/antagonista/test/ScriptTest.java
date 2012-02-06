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
import cat.atridas.antagonista.defensa.EntityFactory;
import cat.atridas.antagonista.entities.EntityManager;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.systems.PhysicsCharacterControllerSystem;
import cat.atridas.antagonista.entities.systems.RTSCameraSystem;
import cat.atridas.antagonista.entities.systems.RenderingCameraSystem;
import cat.atridas.antagonista.entities.systems.RenderingSystem;
import cat.atridas.antagonista.entities.systems.RigidBodySystem;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.input.InputManager;
import cat.atridas.antagonista.scripting.ScriptManager;

public class ScriptTest {
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
  public static void main(String[] args) {
    //comprovem que els asserts estiguin actius
    boolean assertsActives = false;
    assert (assertsActives = true) == true;
    if(!assertsActives)
      throw new RuntimeException("Falta activar els asserts");
    
    Utils.setConsoleLogLevel(Level.CONFIG);
    
    
    
    //ScriptManager scriptManager = new ScriptManager();

    Core core = Core.getCore();
    core.init(800, 600, TestEntities.class.getName(), true, null);

    ///////////////////////////////////////////////////////////////////////////////////////
    
    SystemManager sm = core.getSystemManager();
    sm.registerSystem(new RTSCameraSystem());
    
    sm.registerSystem(new PhysicsCharacterControllerSystem());
    sm.registerSystem(new RigidBodySystem());
    
    sm.registerSystem(new RenderingCameraSystem());
    sm.registerSystem(new RenderingSystem());
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////
    
    InputManager im = core.getInputManager();
    Clock clock = core.getClock();
    RenderManager rm = core.getRenderManager();
    
    EntityManager em = core.getEntityManager();
    MeshManager mm = core.getMeshManager();
    
    

    SceneData sceneData = rm.getSceneData();
    sceneData.setAmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
    sceneData.setDirectionalLight(new Vector3f(0,1,1), new Color3f(0.3f, 0.3f, 0.3f));
    
    
    
    Transformation position = new Transformation();
    
    EntityFactory.createRajola(em, mm, position, new HashedString("TerraBasic"));
    
    EntityFactory.createCamera(em, new HashedString("Camera"));
    
    while(!im.isCloseRequested() && !im.isActionActive(Utils.CLOSE)) {

      DeltaTime dt = clock.update();
      
      
      core.getPhysicsWorld().update(dt);
      
      sm.updateSimple(dt);
      
      core.getPhysicsWorld().debugDraw();
      
      rm.initFrame();
      
      core.getRenderableObjectManager().renderAll(rm);
      //dr.render(rm,dt);
      
      rm.present();
      
      Core.getCore().getFontManager().cleanTextCache();
      
      Core.getCore().getInputManager().update(dt);
    }
    
    core.cleanUnusedResources(false);
    core.cleanUnusedResources(true);
    
    core.close();
  }
}
