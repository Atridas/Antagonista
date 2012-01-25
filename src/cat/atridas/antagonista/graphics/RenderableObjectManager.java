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

/**
 * A manager of renderable objects. This manager does not manage Resources!
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 *
 */
public abstract class RenderableObjectManager {
  private static final Logger LOGGER = Logger.getLogger(RenderableObjectManager.class.getCanonicalName());
  
  /**
   * Map of renderable objects, indexed by it's identifier.
   * @since 0.1 
   */
  private final HashMap<HashedString, RenderableObject> renderableObjects = new HashMap<>();
  /**
   * List of all renderable objects, to make a fast iteration.
   * @since 0.1
   */
  private final ArrayList<RenderableObject> renderableObjectsArray = new ArrayList<>();
  
  /**
   * Initialize the manager.
   * @return success.
   * @since 0.1
   */
  public abstract boolean init();
  
  /**
   * Creates and saves a new renderable object. The objects are not automatically cleaned and
   * must be destroyed by the user.
   * 
   * @param name name of the object to be created.
   * @param mesh used by the object created.
   * @return a new renderable object.
   * @since 0.1
   * @see #destroyRenderableObject(HashedString)
   */
  public final RenderableObject addRenderableObject(HashedString name, HashedString mesh) {
    if(LOGGER.isLoggable(Level.CONFIG)) {
      LOGGER.config("Adding renderable object with name '" + name +"'");
    }

    if(renderableObjects.containsKey(name)) {
      LOGGER.warning("Renderable object with id '" + name + "' is being substituted");
      destroyRenderableObject(name);
    }
    
    RenderableObject ro = new RenderableObject(name, Core.getCore().getMeshManager().getResource(mesh));
    renderableObjects.put(name, ro);
    renderableObjectsArray.add(ro);
    return ro;
  }
  
  /**
   * Deletes from this manager a renderable object.
   * 
   * @param name of the object to be deleted.
   * @since 0.1
   */
  public final void destroyRenderableObject(HashedString name) {
    if(renderableObjects.containsKey(name)) {
      Iterator<RenderableObject> it = renderableObjectsArray.iterator();
      int i = 0;
      while(it.hasNext()) {
        RenderableObject ro = it.next();
        if(ro.getName().equals(name)) {
          renderableObjectsArray.remove(i);
          break;
        }
        ++i;
      }
    } else {
      LOGGER.warning("Renderable object with id '" + name + "' does not exist.");
    }
  }
  
  /**
   * Same as <code>destroyRenderableObject(<strong>ro.getName()</strong>)</code>
   * @param ro Renderable object to be deleted.
   * @since 0.1
   * @see #destroyRenderableObject(HashedString)
   */
  public final void destroyRenderableObject(RenderableObject ro) {
    destroyRenderableObject(ro.getName());
  }
  
  /**
   * Renders all renderable objects contained in this manager.
   * @param rm Render Manager
   * @since 0.1
   */
  public final void renderAll(RenderManager rm) {

    SceneData sceneData = rm.getSceneData();

    sceneData.setUniforms();
    
    InstanceData instanceData = new InstanceData();

    Matrix4f viewProj           = new Matrix4f();
    Matrix4f view               = new Matrix4f();
    Matrix4f model              = new Matrix4f();
    //Matrix4f modelViewProj      = new Matrix4f();
    //Matrix4f modelView          = new Matrix4f();
    //Matrix4f modelViewInvTransp = new Matrix4f();
    viewProj.setIdentity();
    view .setIdentity();
    
    sceneData.getViewMatrix(view);
    sceneData.getViewProjectionMatrix(viewProj);

    for(RenderableObject renderableObject : renderableObjectsArray) {
      if(renderableObject.isVisible() && !renderableObject.isCulled()) {
        Mesh mesh = renderableObject.getMesh();
        mesh.preRender();
        
        renderableObject.getTransformation(model);
  
        instanceData.modelView.mul(view, model);
        instanceData.modelViewProj.mul(viewProj, model);
        
        instanceData.modelViewInvTransp.invert(instanceData.modelView);
        instanceData.modelViewInvTransp.transpose();
        
        
        setInstanceUniforms( instanceData );
        
        
        int numSubmeshes = mesh.getNumSubmeshes();
        for(int submesh = 0; submesh < numSubmeshes; ++submesh) {
          Material material = mesh.getMaterial(submesh);
          material.setUpUniforms(rm);
          
          Technique technique = material.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
          for(TechniquePass pass: technique.passes) {
            pass.activate(rm);
            material.setUpUniforms(pass, rm);
            sceneData.setUniforms(pass);
  
            setInstanceUniforms( pass, instanceData );
            
            mesh.render(submesh, rm);
          }
        }
      }
    }
    
    resetGLState();
  }

  /**
   * Sets the uniforms that don't need the shader to be binded to be passed to the OpenGL 
   * (used usually in OpenGL 3.0 and later).
   * 
   * @param instanceData data of one instance.
   * @since 0.1
   */
  protected abstract void setInstanceUniforms(InstanceData instanceData);
  /**
   * Sets the uniforms that need the shader to be binded to be passed to the OpenGL (usually only
   * needed in OpenGL 2.0).
   * 
   * @param pass shader program currently used.
   * @param instanceData data of one instance.
   * @since 0.1
   */
  protected abstract void setInstanceUniforms(
                                TechniquePass pass,
                                InstanceData instanceData);
  protected abstract void resetGLState();
  
  /**
   * Cleans the OpenGL variables.
   * @since 0.1
   */
  public abstract void cleanUp();

  /**
   * <code>true</code> if this manager had been cleaned.
   * @since 0.1
   */
  protected boolean cleaned = false;
  @Override
  public void finalize() {
    if(!cleaned) {
      cleanUp();
    }
  }
}
