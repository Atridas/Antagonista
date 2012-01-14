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
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RTSCamera;
import cat.atridas.antagonista.graphics.RenderableObject;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.graphics.TextureManager;
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

    core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(25,25,25), new Color3f(1,1,1), 30);
    
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
      

      core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(0,0,25), new Color3f(0,0,1));
      core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(0,25,0), new Color3f(0,1,0));
      core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(25,0,0), new Color3f(1,0,0));
      //core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(0,-25,0), new Color3f(1,0,0));
      //core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(0,25,25), new Color3f(1,0,0));
      //core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(0,-25,25), new Color3f(1,0,0));
      //core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(25,0,25), new Color3f(1,0,0));
      //core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(-25,0,25), new Color3f(1,0,0));

      core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(0,0,-25), new Color3f(0,0,0.5f));
      core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(0,-25,0), new Color3f(0,0.5f,0));
      core.getDebugRender().addLine(new Point3f(0,0,0), new Point3f(-25,0,0), new Color3f(0.5f,0,0));
      
      
      //sceneData.setCamera(camera);
      
      rm.initFrame();
      
      core.getRenderableObjectManager().renderAll(rm);
      core.getDebugRender().render(rm,dt);
      
      rm.present();
      
      //render(rm);
      Core.getCore().getInputManager().update(dt);
    }
    
    Core.getCore().cleanUnusedResources(false);
    Core.getCore().cleanUnusedResources(true);
    
    Core.getCore().close();
  }

  /*
  static void render(RenderManager rm) {
    
    rm.initFrame();

    HashedString hs7 = new HashedString("Habitacio");
    MeshManager mem  = Core.getCore().getMeshManager();
    //Mesh mesh = mem.getResource(hs7);
    Mesh mesh = mem.getResource(hs7);

    SceneData sceneData = rm.getSceneData();

    mesh.preRender();
    sceneData.setUniforms();

    assert !Utils.hasGLErrors();
    
    FloatBuffer fb = BufferUtils.createFloatBuffer(8*4);
    Matrix4f mvp = new Matrix4f();
    Matrix4f mv  = new Matrix4f();
    mvp.setIdentity();
    mv .setIdentity();
    
    sceneData.setPerspective(45, 1, 100);
    sceneData.setCamera(new Point3f(30, 30, 30), new Point3f(0, 0, 0), new Vector3f(0, 0, 1));
    
    sceneData.getViewMatrix(mv);
    sceneData.getViewProjectionMatrix(mvp);
    
    int matrixBuffer = -1;
    if(Utils.supports(Profile.GL3) || GLContext.getCapabilities().GL_ARB_uniform_buffer_object) {
      fb.rewind();
      Utils.matrixToBuffer(mvp, fb);
      Utils.matrixToBuffer(mv, fb);
      fb.rewind();
      
      matrixBuffer = GL15.glGenBuffers();
      GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, matrixBuffer);
      GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, fb, GL15.GL_STATIC_DRAW);
      
      GL30.glBindBufferRange(
          GL31.GL_UNIFORM_BUFFER, 
          TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
          matrixBuffer, 
          0, 
          4 * 8 * Utils.FLOAT_SIZE);
      
    }
    
    int numSubmeshes = mesh.getNumSubmeshes();
    for(int submesh = 0; submesh < numSubmeshes; ++submesh) {
      Material material = mesh.getMaterial(submesh);
      material.setUpUniforms(rm);
      
      Technique technique = material.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
      for(TechniquePass pass: technique.passes) {
        pass.activate(rm);
        material.setUpUniforms(pass, rm);
        sceneData.setUniforms(pass);

        if(matrixBuffer < 0) {
          fb.rewind();
          Utils.matrixToBuffer(mv, fb);
          fb.rewind();
          GL20.glUniformMatrix4(pass.getModelViewUniform(), false, fb);
          fb.rewind();
          Utils.matrixToBuffer(mvp, fb);
          fb.rewind();
          GL20.glUniformMatrix4(pass.getModelViewProjectionUniform(), false, fb);
        }
        
        mesh.render(submesh, rm);
      }
    }
    
    rm.present();
    
    if(matrixBuffer >= 0) {
      GL15.glDeleteBuffers(matrixBuffer);
    }
  }
  */
}
