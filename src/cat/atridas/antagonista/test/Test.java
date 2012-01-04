package cat.atridas.antagonista.test;

import java.nio.FloatBuffer;
import java.util.logging.Level;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.Mesh;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.graphics.Technique;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.graphics.Effect.TechniqueType;

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
    HashedString hs5 = new HashedString("nanana");
    TextureManager tm = Core.getCore().getTextureManager();
    tm.getResource(hs);
    tm.getResource(hs3);
    tm.getResource(hs2);
    tm.getResource(hs4);
    tm.getResource(hs5);
    tm.getResource(hs);
    
    
    /*
    HashedString hs6 = new HashedString("Material 2");
    MaterialManager mm = Core.getCore().getMaterialManager();
    Material m = mm.getResource(hs6);
    
    m.activate(TechniqueType.FORWARD, Quality.MID, rm);
    */
    
    HashedString hs7 = new HashedString("Habitacio");
    MeshManager mem  = Core.getCore().getMeshManager();
    mem.getResource(hs7);
    
    
    assert !Utils.hasGLErrors();
    

    SceneData sceneData = rm.getSceneData();

    sceneData.setAmbientLight(new Point3f(0.3f, 0.3f, 0.3f));
    sceneData.setDirectionalLight(new Vector3f(0,1,1), new Point3f(0.3f, 0.3f, 0.3f));
    
    render(rm);
    
    synchronized (rm) {
      try {
        rm.wait(5000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
    
    Core.getCore().close();
  }

  
  static void render(RenderManager rm) {
    
    rm.initFrame();

    HashedString hs7 = new HashedString("Habitacio");
    MeshManager mem  = Core.getCore().getMeshManager();
    Mesh mesh = mem.getResource(hs7);

    SceneData sceneData = rm.getSceneData();

    mesh.preRender();
    sceneData.setUniforms();

    assert !Utils.hasGLErrors();
    
    FloatBuffer fb = BufferUtils.createFloatBuffer(4*4);
    Matrix4f mvp = new Matrix4f();
    Matrix4f mv  = new Matrix4f();
    mvp.setIdentity();
    mv .setIdentity();
    
    sceneData.setPerspective(45, 1, 100);
    sceneData.setCamera(new Point3f(30, 30, 30), new Point3f(0, 0, 0), new Vector3f(0, 0, 1));
    
    sceneData.getViewMatrix(mv);
    sceneData.getViewProjectionMatrix(mvp);
    
    int numSubmeshes = mesh.getNumSubmeshes();
    for(int submesh = 0; submesh < numSubmeshes; ++submesh) {
      Material material = mesh.getMaterial(submesh);
      material.setUpUniforms(rm);
      
      Technique technique = material.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
      for(TechniquePass pass: technique.passes) {
        pass.activate(rm);
        material.setUpUniforms(pass, rm);
        sceneData.setUniforms(pass);

        Utils.matrixToBuffer(mv, fb);
        GL20.glUniformMatrix4(pass.getModelViewUniform(), false, fb);
        Utils.matrixToBuffer(mvp, fb);
        GL20.glUniformMatrix4(pass.getModelViewProjectionUniform(), false, fb);
        
        mesh.render(submesh, rm);
      }
    }
    
    rm.present();
  }
}
