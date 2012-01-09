package cat.atridas.antagonista.test;

import java.util.logging.Level;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

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
    
    Core.getCore().init(800, 600, Test.class.getName(), null);
    
    RenderManager rm = Core.getCore().getRenderManager();
    
    
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
    MeshManager mem  = Core.getCore().getMeshManager();
    mem.getResource(hs7);
    
    
    assert !Utils.hasGLErrors();
    

    SceneData sceneData = rm.getSceneData();
    RTSCamera camera = new RTSCamera();

    //sceneData.setPerspective(45, 1, 100);
    //sceneData.setCamera(new Point3f(30, 30, 30), new Point3f(0, 0, 0), new Vector3f(0, 0, 1));
    sceneData.setAmbientLight(new Point3f(0.3f, 0.3f, 0.3f));
    sceneData.setDirectionalLight(new Vector3f(0,1,1), new Point3f(0.3f, 0.3f, 0.3f));

    RenderableObject ro1 = Core.getCore().getRenderableObjectManager().addRenderableObject(hs7,  hs7);
    RenderableObject ro2 = Core.getCore().getRenderableObjectManager().addRenderableObject(hs8,  hs7);
    RenderableObject ro3 = Core.getCore().getRenderableObjectManager().addRenderableObject(hs9,  hs7);
    RenderableObject ro4 = Core.getCore().getRenderableObjectManager().addRenderableObject(hs10, hs7);
    
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
    
    while(!im.isCloseRequested() && !im.isActionActive(Utils.CLOSE)) {
      
      
      sceneData.setCamera(camera);
      
      rm.initFrame();
      
      Core.getCore().getRenderableObjectManager().renderAll(rm);
      
      rm.present();
      
      //render(rm);
      Core.getCore().getInputManager().update();
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
