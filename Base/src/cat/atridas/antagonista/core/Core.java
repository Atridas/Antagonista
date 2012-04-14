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
import cat.atridas.antagonista.graphics.MaterialManager;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.graphics.animation.AnimationManager;
import cat.atridas.antagonista.graphics.animation.ArmatureManager;
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
	
	private RenderManager           rm;
	private InputManager            im;
	private TextureManager          tm  = new TextureManager();
	private FontManager             fm;
  private EffectManager           em  = new EffectManager(); //TODO
  private MaterialManager         mm  = new MaterialManager();
  private DebugRender             dr;
  private ArmatureManager         am  = new ArmatureManager();
  private AnimationManager        animm  = new AnimationManager();
  private MeshManager             mem = new MeshManager();
  private RenderableObjectManager rom;
  
  private PhysicsWorld pw;
  
  private SystemManager           systemManager = new SystemManager();
  private EntityManager           entityManager = new EntityManager();
  
  private ScriptManager           scriptManager;
  
  private Clock clock;

  
  private boolean physicsDebugRender = false;
  /**
   * Gets the RenderManager.
   * 
   * @return the RenderManager.
   * @since 0.1
   */
	public RenderManager getRenderManager()
	{
		return rm;
	}

  /**
   * Gets the InputManager.
   * 
   * @return the InputManager.
   * @since 0.1
   */
	public InputManager getInputManager()
	{
		return im;
	}

  /**
   * Gets the TextureManager.
   * 
   * @return the TextureManager.
   * @since 0.1
   */
	public TextureManager getTextureManager()
	{
		return tm;
	}

  /**
   * Gets the FontManager.
   * 
   * @return the FontManager.
   * @since 0.1
   */
  public FontManager getFontManager()
  {
    return fm;
  }

  /**
   * Gets the EffectManager.
   * 
   * @return the EffectManager.
   * @since 0.1
   */
  public EffectManager getEffectManager()
  {
    return em;
  }

  /**
   * Gets the MaterialManager.
   * 
   * @return the MaterialManager.
   * @since 0.1
   */
  public MaterialManager getMaterialManager()
  {
    return mm;
  }

  /**
   * Gets the DebugRender.
   * 
   * @return the DebugRender.
   * @since 0.1
   */
  public DebugRender getDebugRender()
  {
    return dr;
  }

  /**
   * Gets the ArmatureManager.
   * 
   * @return the ArmatureManager.
   * @since 0.3
   */
  public ArmatureManager getArmatureManager()
  {
    return am;
  }

  /**
   * Gets the AnimationManager.
   * 
   * @return the AnimationManager.
   * @since 0.3
   */
  public AnimationManager getAnimationManager()
  {
    return animm;
  }

  /**
   * Gets the MeshManager.
   * 
   * @return the MeshManager.
   * @since 0.1
   */
  public MeshManager getMeshManager()
  {
    return mem;
  }

  /**
   * Gets the RenderableObjectManager.
   * 
   * @return the RenderableObjectManager.
   * @since 0.1
   */
  public RenderableObjectManager getRenderableObjectManager()
  {
    return rom;
  }
	
  /**
   * Gets the PhysicsWorld.
   * 
   * @return the PhysicsWorld.
   * @since 0.2
   */
  public PhysicsWorld getPhysicsWorld() {
    return pw;
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
   * @param w width of the screen.
   * @param h height of the screen.
   * @param title title of the screen.
   * @param _forwardCompatible if a forward compatible context must be created.
   * @param displayParent Use in Applets. Null on stand-alone applications.
   * @since 0.1
   */
	public void init(int w, int h, String title, ManagerFactory factory, boolean forwardCompatible, Canvas displayParent) {
	  Utils.loadNativeLibs(); //TODO nomes si no estem en un applet, potser. Provar-ho
	  
		rm.initDisplay(w, h, title, forwardCompatible, displayParent);
		im = factory.createInputManager();
		im.init();
		
		rm = factory.createRenderManager();
		rm.initGL();
		
		em.init("data/xml/effects.xml", rm);
		ArrayList<HashedString> al = new ArrayList<>();
		al.add(new HashedString("dds"));
		al.add(new HashedString("png"));
		tm.init(al, "data/textures/");

		al.clear();
    al.add(new HashedString("mat"));
    mm.init(al, "data/materials/");
    
    
    fm = factory.createFontManager();

    al.clear();
    al.add(new HashedString("fnt"));
    fm.init(al, "data/fonts/");
    
    dr = factory.createDebugRender();
    
    al.clear();
    al.add(new HashedString("arm"));
    am.init(al, "data/armatures/");
    
    al.clear();
    al.add(new HashedString("ani"));
    animm.init(al, "data/animations/");
    
    al.clear();
    al.add(new HashedString("mesh"));
    mem.init(al, "data/meshes/");
    

    rom = factory.createRenderableObjectManager();
    
    rom.init();
    
    
    
    //////////
    pw = new PhysicsWorld();
    
    clock = new Clock();
    
    ////////////////
    
    scriptManager = new ScriptManager("data/xml/scriptManager.xml");
	}
	
	public void setPhysicsDebugRender(boolean active) {
	  physicsDebugRender = active;
	}
	
	public boolean getPhysicsDebugRender() {
	  return physicsDebugRender;
	}
	
	public void performSimpleTick() {
	  DeltaTime dt = clock.update();
    im.update(dt);
    
    pw.update(dt);
    
    systemManager.updateSimple(dt);

    
    //-----------------------------------------
    
    if(physicsDebugRender)
      pw.debugDraw();
    
    rm.initFrame();
    
    rom.renderAll(rm); 
    dr.render(rm, dt);
    
    rm.present();
    
    fm.cleanTextCache();
    
	}
	
	/**
	 * Cleans all unused resources.
	 * 
	 * @param weakify if the managers should be weakified.
	 * @since 0.1
	 */
	public void cleanUnusedResources(boolean weakify) {
	  if(weakify) {
	    tm.weakify();
	    em.weakify();
	    mm.weakify();
	    mem.weakify();
	    animm.weakify();
	    am.weakify();//TODO més managers.
	  }

    System.gc();

    tm.cleanUnusedReferences();
    em.cleanUnusedReferences();
    mm.cleanUnusedReferences();
    mem.cleanUnusedReferences();
    animm.cleanUnusedReferences();
    am.cleanUnusedReferences();

    System.runFinalization();
	}
	
	/**
	 * Closes all managers.
	 */
	public void close() {
		
		//sm.cleanUp();
		//TODO tm.cleanUp();
		

	  rom = null;
    mem = null;
    mm  = null;
    em  = null;
    fm  = null;
    tm  = null;
    animm = null;
    am = null;
    systemManager = null;
    entityManager = null;
    
    System.gc();
    System.runFinalization();
    
    //TODO
    //im.close();
    //rm.closeDisplay();
    im  = null;
    rm  = null;

    System.gc();
    System.runFinalization();
	}
	
	static Core instance;
	
	private Core() {}
	
	private static synchronized void createInstance()
	{
		if(instance == null) {
			instance = new Core();
		}
	}
	
	/**
	 * Gets the singleton instance.
	 * 
	 * @return the singleton object.
	 */
	public static Core getCore()
	{
		if(instance == null) {
			createInstance();
		}
		return instance;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException(); 
	}
}
