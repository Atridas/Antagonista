package cat.atridas.antagonista.test;

import java.io.FileNotFoundException;
import java.util.logging.Level;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.bullet.BulletFactory;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.defensa.EntityFactory;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.EntityManager;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.entities.components.MeshComponent;
import cat.atridas.antagonista.entities.components.TransformComponent;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.graphics.animation.AnimationCore;
import cat.atridas.antagonista.graphics.animation.ArmatureInstance;
import cat.atridas.antagonista.input.InputManager;
import cat.atridas.antagonista.lwjgl.LWJGLManagerFactory;

public class TestAnimacions {

  public static void main(String[] args) throws FileNotFoundException {
    //comprovem que els asserts estiguin actius
    boolean assertsActives = false;
    assert (assertsActives = true) == true;
    if(!assertsActives)
      throw new RuntimeException("Falta activar els asserts");
    
    Utils.setConsoleLogLevel(Level.FINEST);
    

    Core core = Core.getCore();
    core.init(800, 600, TestAnimacions.class.getName(), new LWJGLManagerFactory(), new BulletFactory(), true, null);

    
    //ScriptManager scriptManager = new ScriptManager("data/xml/scriptManager.xml");
    
    //cat.atridas.antagonista.entities.System pyRTSCameraSystem = scriptManager.createNewInstance("RTSCameraSystem", cat.atridas.antagonista.entities.System.class);
    
    ///////////////////////////////////////////////////////////////////////////////////////
    
    SystemManager sm = core.getSystemManager();
    /*
    sm.registerSystem(new RTSCameraSystem());
    //sm.registerSystem(pyRTSCameraSystem);
    
    sm.registerSystem(new PhysicsCharacterControllerSystem());
    sm.registerSystem(new RigidBodySystem());
    
    sm.registerSystem(new RenderingCameraSystem());
    sm.registerSystem(new RenderingSystem());
    */
    sm.registerSystem("RTSCameraSystem");
    
    sm.registerSystem("PhysicsCharacterControllerSystem");
    sm.registerSystem("RigidBodySystem");
    
    sm.registerSystem("RenderingCameraSystem");
    sm.registerSystem("RenderingSystem");
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////
    
    InputManager im = core.getInputManager();
    RenderManager rm = core.getRenderManager();
    
    EntityManager em = core.getEntityManager();
    //MeshManager mm = core.getMeshManager();

    im.loadActions("data/xml/inputManager.xml");
    im.activateMode(Utils.MAIN_GAME);
    
    DebugRender dr = core.getDebugRender();
    dr.activate();
    core.setPhysicsDebugRender(true);
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    

    SceneData sceneData = rm.getSceneData();
    sceneData.setAmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
    sceneData.setDirectionalLight(new Vector3f(0.5f,1,-1), new Color3f(0.3f, 0.3f, 0.3f));
    
    

    ///////////////////////////////////////////////////////////////////////
    
    //ArmatureManager am = core.getArmatureManager();
    //ArmatureCore masterArmature = am.getResource(new HashedString("MasterArmature"));

    //Animation animacio = core.getAnimationManager().getResource(new HashedString("AtacarMaster"));
    //Animation animacio = core.getAnimationManager().getResource(new HashedString("CaminarMaster"));
    AnimationCore animacio = core.getAnimationManager().getResource(new HashedString("IdleMaster"));
    
    ArmatureInstance animatedArmature = null;//new ArmatureInstance(masterArmature);

    //animatedArmature.performSingleAnimation(atacar, 0);
    //animatedArmature.performSingleAnimation(atacar, atacar.getDuration() / 2f);
    //animatedArmature.performSingleAnimation(atacar, atacar.getDuration());
    
    ///////////////////////////////////////////////////////////////////////
    
    
    //Transformation position = new Transformation();
    HashedString masterID = new HashedString("Master");
    
    Entity entityMaster = em.createEntity(masterID);
    
    TransformComponent tc = em.createComponent(entityMaster, TransformComponent.getComponentStaticType());
    
    Transformation transform = new Transformation();
    transform.setTranslation(new Vector3f(0,0,1));
    tc.init(transform);
    
    Matrix4f worldMatrix = new Matrix4f();
    transform.getMatrix(worldMatrix);
    
    MeshComponent mc = em.createComponent(entityMaster, MeshComponent.getComponentStaticType());
    mc.init(new HashedString("MasterTest"));
    
    EntityFactory.createCamera(em, new HashedString("Camera"));
    
    //CharacterControllerComponent ccc = em.createComponent(entityMaster, CharacterControllerComponent.getComponentStaticType());
    //ccc.init(new Point3f(5,5,1), 1f, 2, .1f, 3f);
    
    //EntityFactory.createRajola(em, mm, position, new HashedString("TerraBasic"));
    
    //EntityFactory.createCamera(em, new HashedString("Camera"));
    
    
    
    //scriptManager.load("data/scripts/test.py");
    
    //String script = "from data.scripts.test import catacrocker\n";
    
    //scriptManager.execute(script);
    
    float anim = 0;
    
    while(!im.isCloseRequested() && !im.isActionActive(Utils.CLOSE)) {
      
      if(animatedArmature != null) {
        animatedArmature.performSingleAnimation(animacio, anim);
        animatedArmature.debugRender(dr, worldMatrix);
      }
      
      core.performSimpleTick();
      
      anim += core.getClock().getCurrentFrameDeltaTime().dt;
      if(anim > animacio.getDuration()) {
        anim = 0;
      }
      
      if(animatedArmature == null) {
        animatedArmature = core.getRenderableObjectManager().getRenderableObject(masterID).getArmature();
      }
    }
    
    core.cleanUnusedResources(false);
    core.cleanUnusedResources(true);
    
    core.close();
  }
}
