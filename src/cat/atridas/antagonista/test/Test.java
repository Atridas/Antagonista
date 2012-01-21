package cat.atridas.antagonista.test;

import java.util.logging.Level;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.Clock;
import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.Font;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RTSCamera;
import cat.atridas.antagonista.graphics.RenderableObject;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.graphics.FontManager.TextAligment;
import cat.atridas.antagonista.input.InputManager;

public class Test {
  
  /**
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
    core.init(800, 600, Test.class.getName(), null);
    
    RenderManager rm = core.getRenderManager();
    
    
    HashedString hs = new HashedString("Textura 2");
    HashedString hs2 = new HashedString("Normalmap proves");
    HashedString hs3 = new HashedString("Heightmap proves");
    HashedString hs4 = new HashedString("Textura nivell proves");
    //HashedString hs5 = new HashedString("nanana");
    TextureManager tm = Core.getCore().getTextureManager();
    tm.getResource(hs);
    tm.getResource(hs3);
    tm.getResource(hs2);
    tm.getResource(hs4);
    //tm.getResource(hs5);
    tm.getResource(hs);
    
    
    /*
    HashedString hs6 = new HashedString("Material 2");
    MaterialManager mm = Core.getCore().getMaterialManager();
    Material m = mm.getResource(hs6);
    
    m.activate(TechniqueType.FORWARD, Quality.MID, rm);
    */

    HashedString hs7  = new HashedString("Habitacio");
    HashedString hs8  = new HashedString("Habitacio 2");
    HashedString hs9  = new HashedString("Habitacio 3");
    HashedString hs10 = new HashedString("Habitacio 4");
    MeshManager mem  = core.getMeshManager();
    mem.getResource(hs7);
    
    
    assert !Utils.hasGLErrors();
    

    SceneData sceneData = rm.getSceneData();
    RTSCamera camera = new RTSCamera();
    
    camera.setMaxDistance(20);

    sceneData.setPerspective(45, 1, 100);
    sceneData.setCamera(new Point3f(30, -30, 30), new Point3f(0, 0, 0), new Vector3f(0, 0, 1));
    sceneData.setAmbientLight(new Point3f(0.3f, 0.3f, 0.3f));
    sceneData.setDirectionalLight(new Vector3f(0,1,1), new Point3f(0.3f, 0.3f, 0.3f));

    RenderableObject ro1 = core.getRenderableObjectManager().addRenderableObject(hs7,  hs7);
    RenderableObject ro2 = core.getRenderableObjectManager().addRenderableObject(hs8,  hs7);
    RenderableObject ro3 = core.getRenderableObjectManager().addRenderableObject(hs9,  hs7);
    RenderableObject ro4 = core.getRenderableObjectManager().addRenderableObject(hs10, hs7);
    
    Matrix4f mat = new Matrix4f();
    mat.setIdentity();

    ro1.setTransformation(mat);
    mat.setTranslation(new Vector3f( 25,  0, 0));
    ro2.setTransformation(mat);
    mat.setTranslation(new Vector3f(-25,  0, 0));
    ro3.setTransformation(mat);
    mat.setTranslation(new Vector3f(  0, 25, 0));
    ro4.setTransformation(mat);
    
    
    InputManager im = Core.getCore().getInputManager();
    im.loadActions("data/xml/inputManager.xml");
    
    im.activateMode(Utils.MAIN_GAME);

    HashedString camUp    = new HashedString("move_camera_up");
    HashedString camDown  = new HashedString("move_camera_down");
    HashedString camLeft  = new HashedString("move_camera_left");
    HashedString camRight = new HashedString("move_camera_right");
    HashedString camDist  = new HashedString("move_camera_distance");

    Font font = core.getFontManager().getResource(new HashedString("font14")); 
    
    DebugRender dr = core.getDebugRender();
    
    dr.addLine(new Point3f(0,0,0), new Point3f(25,25,25), new Color3f(1,1,1), 30);
    
    dr.activate();
    
    Clock clock = new Clock();
    while(!im.isCloseRequested() && !im.isActionActive(Utils.CLOSE)) {

      float dt = clock.update();
      
      if(im.isActionActive(camUp)) {
        camera.moveUp(2f * dt);
      }
      if(im.isActionActive(camDown)) {
        camera.moveUp(-2f * dt);
      }
      if(im.isActionActive(camRight)) {
        camera.moveRight(2f * dt);
      }
      if(im.isActionActive(camLeft)) {
        camera.moveRight(-2f * dt);
      }
      if(im.isActionActive(camDist)) {
        camera.addDistance( -.01f * im.getActionValue(camDist) );
      }
      
      /*
      dr.addLine(new Point3f(0,0,0), new Point3f(0,0,25), new Color3f(0,0,1));
      dr.addLine(new Point3f(0,0,0), new Point3f(0,25,0), new Color3f(0,1,0));
      dr.addLine(new Point3f(0,0,0), new Point3f(25,0,0), new Color3f(1,0,0));

      dr.addCross(new Point3f(0,10,10), new Color3f(0,0,0), 1);
      dr.addCross(new Point3f(10,10,0), new Color3f(0,0,1), 2);
      dr.addCross(new Point3f(-10,-10,0), new Color3f(1,0,0), 1,false);

      dr.addSphere(new Point3f(0,0,10), 5, new Color3f(1,1,1));
      dr.addSphere(new Point3f(10,0,0), 5, new Color3f(1,0,0));
      dr.addSphere(new Point3f(0,-10,0), 5, new Color3f(0,1,0),false);
      
      dr.addCircle(new Point3f(0,5,7), new Vector3f(0,0,1), 1, new Color3f(0,1,0));
      dr.addCircle(new Point3f(5,5,7), new Vector3f(0,1,0), 2, new Color3f(0,0,1));
      dr.addCircle(new Point3f(-5,-5,7), new Vector3f(1,0,0), 1, new Color3f(1,0,0), false);
      

      
      dr.addTriangle(new Point3f(-10,0,0), new Point3f(0,-10,0), new Point3f(0,0,10), new Color3f(1,1,1));
      dr.addTriangle(new Point3f(20,0,0), new Point3f(0,20,0), new Point3f(0,0,20), new Color3f(.7f,.7f,.7f));
      dr.addTriangle(new Point3f(10,0,0), new Point3f(0,10,0), new Point3f(0,0,10), new Color3f(.3f,.3f,.3f),false);
      
       
      
      
      
      Matrix4f matN = new Matrix4f();
      matN.setIdentity();
      matN.setTranslation(new Vector3f(3,3,5));
      dr.addAxes(matN, 3);
      
      matN.setTranslation(new Vector3f(3,-3,5));
      matN.setRotation(new AxisAngle4f(0,0,1, (float)Math.PI / 6));
      dr.addAxes(matN, 3);
      
      
      dr.addAABB(new Point3f(-5,-5,-5), new Point3f(5,5,5), new Color3f(1,1,1));
      dr.addAABB(new Point3f(-2,-2,0), new Point3f(2,2,4), new Color3f(.5f,.5f,.5f), false);

      
      //Matrix4f matN = new Matrix4f();
      matN.setIdentity();
      matN.setTranslation(new Vector3f(3,3,5));
      dr.addOBB(matN, new Vector3f(3,3,3), new Color3f(1,1,1));
      
      matN.setTranslation(new Vector3f(3,-3,5));
      matN.setRotation(new AxisAngle4f(0,0,1, (float)Math.PI / 6));
      dr.addOBB(matN, new Vector3f(2,3,3), new Color3f(1,1,1));
      */
      
      dr.addCross(new Point3f(0,0,2), new Color3f(0,0,1), 1);
      dr.addString(new Point3f(0,0,2), font, "Hola mundu", 1, new Color3f(1,0,0));
      
      dr.addCross(new Point3f(0,0,5), new Color3f(0,0,1), 1);
      dr.addString(new Point3f(0,0,5), font, "Hola mundu", 1, TextAligment.MID_LEFT, new Color3f(1,0,0));
      
      dr.addCross(new Point3f(0,0,8), new Color3f(0,0,1), 1);
      dr.addString(new Point3f(0,0,8), font, "Hola mundu", 1, TextAligment.BOTTOM_LEFT, new Color3f(1,0,0));
      
      dr.addCross(new Point3f(0,0,11), new Color3f(0,0,1), 1);
      dr.addString(new Point3f(0,0,11), font, "Hola mundu", 1, TextAligment.BOTTOM_CENTER, new Color3f(1,0,0));
      
      dr.addCross(new Point3f(0,0,14), new Color3f(0,0,1), 1);
      dr.addString(new Point3f(0,0,14), font, "Hola mundu", 1, TextAligment.MID_CENTER, new Color3f(1,0,0));
      
      //sceneData.setCamera(camera);
      
      rm.initFrame();
      
      core.getRenderableObjectManager().renderAll(rm);
      core.getDebugRender().render(rm,dt);
      
      rm.present();
      
      Core.getCore().getInputManager().update(dt);
    }
    
    Core.getCore().cleanUnusedResources(false);
    Core.getCore().cleanUnusedResources(true);
    
    Core.getCore().close();
  }
}
