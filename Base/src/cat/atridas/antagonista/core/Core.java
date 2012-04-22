package cat.atridas.antagonista.core;

import java.awt.Canvas;
import java.util.ArrayList;

import cat.atridas.antagonista.Clock;
import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.entities.EntityManager;
import cat.atridas.antagonista.entities.SystemManager;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.EffectManager;
import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.animation.AnimationManager;
import cat.atridas.antagonista.graphics.animation.ArmatureManager;
import cat.atridas.antagonista.graphics.MaterialManager;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.input.InputManager;
import cat.atridas.antagonista.physics.PhysicsWorld;
import cat.atridas.antagonista.scripting.ScriptManager;

/**
 * Core singleton class. Contains all Engine managers.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public final class Core {

  private RenderManager renderManager;
  private InputManager inputManager;
  private TextureManager textureManager;
  private FontManager fontManager;
  private EffectManager effectManager = new EffectManager(); // TODO
  private MaterialManager materialManager;
  private DebugRender debugRender;
  private ArmatureManager armatureManager = new ArmatureManager();
  private AnimationManager animationManager = new AnimationManager();
  private MeshManager meshManager;
  private RenderableObjectManager renderableObjectManager;

  private PhysicsFactory physicsFactory;
  private PhysicsWorld physicsWorld;

  private SystemManager systemManager = new SystemManager();
  private EntityManager entityManager = new EntityManager();

  private ScriptManager scriptManager;

  private Clock clock;

  private boolean physicsDebugRender = false;

  /**
   * Gets the RenderManager.
   * 
   * @return the RenderManager.
   * @since 0.1
   */
  public RenderManager getRenderManager() {
    return renderManager;
  }

  /**
   * Gets the InputManager.
   * 
   * @return the InputManager.
   * @since 0.1
   */
  public InputManager getInputManager() {
    return inputManager;
  }

  /**
   * Gets the TextureManager.
   * 
   * @return the TextureManager.
   * @since 0.1
   */
  public TextureManager getTextureManager() {
    return textureManager;
  }

  /**
   * Gets the FontManager.
   * 
   * @return the FontManager.
   * @since 0.1
   */
  public FontManager getFontManager() {
    return fontManager;
  }

  /**
   * Gets the EffectManager.
   * 
   * @return the EffectManager.
   * @since 0.1
   */
  public EffectManager getEffectManager() {
    return effectManager;
  }

  /**
   * Gets the MaterialManager.
   * 
   * @return the MaterialManager.
   * @since 0.1
   */
  public MaterialManager getMaterialManager() {
    return materialManager;
  }

  /**
   * Gets the DebugRender.
   * 
   * @return the DebugRender.
   * @since 0.1
   */
  public DebugRender getDebugRender() {
    return debugRender;
  }

  /**
   * Gets the ArmatureManager.
   * 
   * @return the ArmatureManager.
   * @since 0.3
   */
  public ArmatureManager getArmatureManager() {
    return armatureManager;
  }

  /**
   * Gets the AnimationManager.
   * 
   * @return the AnimationManager.
   * @since 0.3
   */
  public AnimationManager getAnimationManager() {
    return animationManager;
  }

  /**
   * Gets the MeshManager.
   * 
   * @return the MeshManager.
   * @since 0.1
   */
  public MeshManager getMeshManager() {
    return meshManager;
  }

  /**
   * Gets the RenderableObjectManager.
   * 
   * @return the RenderableObjectManager.
   * @since 0.1
   */
  public RenderableObjectManager getRenderableObjectManager() {
    return renderableObjectManager;
  }

  /**
   * Gets the PhysicsWorld.
   * 
   * @return the PhysicsWorld.
   * @since 0.2
   */
  public PhysicsWorld getPhysicsWorld() {
    return physicsWorld;
  }

  public PhysicsFactory getPhysicsFactory() {
    return physicsFactory;
  }

  /**
   * Gets the SystemManager.
   * 
   * @return the EntityManager.
   * @since 0.2
   */
  public SystemManager getSystemManager() {
    return systemManager;
  }

  /**
   * Gets the EntityManager.
   * 
   * @return the EntityManager.
   * @since 0.2
   */
  public EntityManager getEntityManager() {
    return entityManager;
  }

  /**
   * Gets the ScriptManager.
   * 
   * @return the ScriptManager.
   * @since 0.3
   */
  public ScriptManager getScriptManager() {
    return scriptManager;
  }

  /**
   * Gets the global clock.
   * 
   * @return the global clock.
   * @since 0.2
   */
  public Clock getClock() {
    return clock;
  }

  /**
   * Initializes the engine.
   * 
   * @param w
   *          width of the screen.
   * @param h
   *          height of the screen.
   * @param title
   *          title of the screen.
   * @param _forwardCompatible
   *          if a forward compatible context must be created.
   * @param displayParent
   *          Use in Applets. Null on stand-alone applications.
   * @since 0.1
   */
  public void init(int w, int h, String title, ManagerFactory factory,
      PhysicsFactory _physicsFactory, boolean forwardCompatible,
      Canvas displayParent) {
    Utils.loadNativeLibs(); // TODO nomes si no estem en un applet, potser.
                            // Provar-ho

    physicsFactory = _physicsFactory;

    renderManager = factory.createRenderManager();
    renderManager.initDisplay(w, h, title, forwardCompatible, displayParent);
    inputManager = factory.createInputManager();
    inputManager.init();

    renderManager.initGL();

    effectManager.init("data/xml/effects.xml", renderManager);
    ArrayList<HashedString> al = new ArrayList<>();
    al.add(new HashedString("dds"));
    al.add(new HashedString("png"));
    textureManager = factory.createTextureManager();
    textureManager.init(al, "data/textures/");

    al.clear();
    al.add(new HashedString("mat"));
    materialManager = factory.createMaterialManager();
    materialManager.init(al, "data/materials/");

    fontManager = factory.createFontManager();

    al.clear();
    al.add(new HashedString("fnt"));
    fontManager.init(al, "data/fonts/");

    debugRender = factory.createDebugRender();

    al.clear();
    al.add(new HashedString("arm"));
    armatureManager.init(al, "data/armatures/");

    al.clear();
    al.add(new HashedString("ani"));
    animationManager.init(al, "data/animations/");

    al.clear();
    al.add(new HashedString("mesh"));
    meshManager = factory.createMeshManager();
    meshManager.init(al, "data/meshes/");

    renderableObjectManager = factory.createRenderableObjectManager();

    renderableObjectManager.init();

    // ////////
    if (physicsFactory != null) {
      physicsWorld = physicsFactory.createPhysicsWorld();
    } else {
      physicsWorld = null;
    }
    clock = factory.createClock();

    // //////////////

    scriptManager = new ScriptManager("data/xml/scriptManager.xml");
  }

  /**
   * Sets if the physic objects should be rendered.
   * 
   * @param active
   * @since 0.5
   */
  public void setPhysicsDebugRender(boolean active) {
    physicsDebugRender = active;
  }

  /**
   * Checks if the rendering of physic objects is activated.
   * 
   * @return <code>true</code> if the engine now renders physic objects.
   * @since 0.5
   */
  public boolean getPhysicsDebugRender() {
    return physicsDebugRender;
  }

  /**
   * Performs a single iteration of the engine.
   * 
   * @since 0.5
   */
  public void performSimpleTick() {
    DeltaTime dt = clock.update();
    inputManager.update(dt);

    if (physicsWorld != null) {
      physicsWorld.update(dt);
    }
    systemManager.updateSimple(dt);

    // -----------------------------------------

    if (physicsDebugRender && physicsWorld != null) {
      physicsWorld.debugDraw();
    }

    renderManager.initFrame();

    renderableObjectManager.renderAll(renderManager);
    debugRender.render(renderManager, dt);

    renderManager.present();

    fontManager.cleanTextCache();

  }

  /**
   * Cleans all unused resources.
   * 
   * @param weakify
   *          if the managers should be weakified.
   * @since 0.1
   */
  public void cleanUnusedResources(boolean weakify) {
    if (weakify) {
      textureManager.weakify();
      effectManager.weakify();
      materialManager.weakify();
      meshManager.weakify();
      animationManager.weakify();
      armatureManager.weakify();// TODO més managers.
    }

    System.gc();

    textureManager.cleanUnusedReferences();
    effectManager.cleanUnusedReferences();
    materialManager.cleanUnusedReferences();
    meshManager.cleanUnusedReferences();
    animationManager.cleanUnusedReferences();
    armatureManager.cleanUnusedReferences();

    System.runFinalization();
  }

  /**
   * Closes all managers.
   * @since 0.1
   */
  public void close() {

    // sm.cleanUp();
    // TODO tm.cleanUp();

    renderableObjectManager = null;
    meshManager = null;
    materialManager = null;
    effectManager = null;
    fontManager = null;
    textureManager = null;
    animationManager = null;
    armatureManager = null;
    systemManager = null;
    entityManager = null;

    System.gc();
    System.runFinalization();

    // TODO
    // im.close();
    // rm.closeDisplay();
    inputManager = null;
    renderManager = null;

    System.gc();
    System.runFinalization();
  }

  private static Core instance = new Core();

  private Core() {
  }

  /**
   * Gets the singleton instance.
   * 
   * @return the singleton object.
   * @since 0.5
   */
  public static Core getCore() {
    // if (instance == null) {
    // synchronized(Core.class) {
    // if (instance == null) {
    // instance = new Core();
    // }
    // }
    // }
    return instance;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }
}
