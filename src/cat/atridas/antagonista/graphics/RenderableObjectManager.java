package cat.atridas.antagonista.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Matrix4f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Effect.TechniqueType;

public abstract class RenderableObjectManager {
  private static final Logger LOGGER = Logger.getLogger(RenderableObjectManager.class.getCanonicalName());
  
  private final HashMap<HashedString, RenderableObject> renderableObjects = new HashMap<>();
  private final ArrayList<RenderableObject> renderableObjectsArray = new ArrayList<>();
  
  public abstract boolean init();
  
  public final RenderableObject addRenderableObject(HashedString name, HashedString mesh) {
    if(LOGGER.isLoggable(Level.CONFIG)) {
      LOGGER.config("Adding renderable object with name '" + name +"'");
    }

    if(renderableObjects.containsKey(name)) {
      LOGGER.warning("Renderable object with id '" + name + "' is being substituted");
      
      
      Iterator<RenderableObject> it = renderableObjectsArray.iterator();
      int i = 0;
      while(it.hasNext()) {
        RenderableObject ro = it.next();
        if(ro.name.equals(name)) {
          renderableObjectsArray.remove(i);
          break;
        }
        ++i;
      }
      
    }
    
    RenderableObject ro = new RenderableObject(name, Core.getCore().getMeshManager().getResource(mesh));
    renderableObjects.put(name, ro);
    renderableObjectsArray.add(ro);
    return ro;
  }
  
  public final void renderAll(RenderManager rm) {

    SceneData sceneData = rm.getSceneData();

    sceneData.setUniforms();
    
    

    Matrix4f viewProj           = new Matrix4f();
    Matrix4f view               = new Matrix4f();
    Matrix4f model              = new Matrix4f();
    Matrix4f modelViewProj      = new Matrix4f();
    Matrix4f modelView          = new Matrix4f();
    Matrix4f modelViewInvTransp = new Matrix4f();
    viewProj.setIdentity();
    view .setIdentity();
    
    sceneData.getViewMatrix(view);
    sceneData.getViewProjectionMatrix(viewProj);

    for(RenderableObject renderableObject : renderableObjectsArray) {
      Mesh mesh = renderableObject.mesh;
      mesh.preRender();
      
      renderableObject.getTransformation(model);

      modelView.mul(view, model);
      modelViewProj.mul(viewProj, model);
      
      modelViewInvTransp.invert(modelView);
      modelViewInvTransp.transpose();
      
      
      setInstanceUniforms( modelViewProj, modelView, modelViewInvTransp );
      
      
      int numSubmeshes = mesh.getNumSubmeshes();
      for(int submesh = 0; submesh < numSubmeshes; ++submesh) {
        Material material = mesh.getMaterial(submesh);
        material.setUpUniforms(rm);
        
        Technique technique = material.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
        for(TechniquePass pass: technique.passes) {
          pass.activate(rm);
          material.setUpUniforms(pass, rm);
          sceneData.setUniforms(pass);

          setInstanceUniforms( pass, modelViewProj, modelView, modelViewInvTransp );
          
          mesh.render(submesh, rm);
        }
      }
    }
  }

  protected abstract void setInstanceUniforms(
                                Matrix4f modelViewProj, 
                                Matrix4f modelView, 
                                Matrix4f modelViewInvTransp);
  protected abstract void setInstanceUniforms(
                                TechniquePass pass,
                                Matrix4f modelViewProj, 
                                Matrix4f modelView, 
                                Matrix4f modelViewInvTransp);
  
  

  public abstract void cleanUp();

  protected boolean cleaned = false;
  @Override
  public void finalize() {
    if(!cleaned) {
      cleanUp();
    }
  }
}
