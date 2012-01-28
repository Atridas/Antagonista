package cat.atridas.antagonista.core;

import java.awt.Canvas;
import java.util.ArrayList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.EffectManager;
import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.MaterialManager;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.gl.RenderManagerGL;
import cat.atridas.antagonista.graphics.gl2.DebugRenderGL2;
import cat.atridas.antagonista.graphics.gl2.FontManagerGL2;
import cat.atridas.antagonista.graphics.gl2.RenderableObjectManagerGL2;
import cat.atridas.antagonista.graphics.gl3.DebugRenderGL3;
import cat.atridas.antagonista.graphics.gl3.FontManagerGL3;
import cat.atridas.antagonista.graphics.gl3.RenderableObjectManagerGL3;
import cat.atridas.antagonista.input.InputManager;
import cat.atridas.antagonista.physics.PhysicsWorld;

/**
 * Core singleton class. Contains all Engine managers.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 *
 */
public final class Core {
	
	private RenderManager           rm  = new RenderManagerGL();//TODO
	private InputManager            im  = new InputManager();
	private TextureManager          tm  = new TextureManager();
	private FontManager             fm;
  private EffectManager           em  = new EffectManager(); //TODO
  private MaterialManager         mm  = new MaterialManager();
  private DebugRender             dr;
  private MeshManager             mem = new MeshManager();
  private RenderableObjectManager rom;
  
  private PhysicsWorld pw;

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
   * Initializes the engine.
   * 
   * @param w width of the screen.
   * @param h height of the screen.
   * @param title title of the screen.
   * @param _forwardCompatible if a forward compatible context must be created.
   * @param displayParent Use in Applets. Null on stand-alone applications.
   * @since 0.1
   */
	public void init(int w, int h, String title, boolean forwardCompatible, Canvas displayParent) {
	  Utils.loadNativeLibs(); //TODO nomes si no estem en un applet, potser. Provar-ho
	  
		rm.initDisplay(w, h, title, forwardCompatible, displayParent);
		im.init();
		
		rm.initGL();
		
		em.init("data/xml/effects.xml", rm);
		ArrayList<HashedString> al = new ArrayList<>();
		al.add(new HashedString("dds"));
		al.add(new HashedString("png"));
		tm.init(al, "data/textures/");

		al.clear();
    al.add(new HashedString("mat"));
    mm.init(al, "data/materials/");
    
    
    if(Utils.supports(Profile.GL3))
      fm = new FontManagerGL3();
    else if(Utils.supports(Profile.GL2))
      fm = new FontManagerGL2();
    else
      throw new RuntimeException("Not implemented"); //TODO

    al.clear();
    al.add(new HashedString("fnt"));
    fm.init(al, "data/fonts/");
    
    if(Utils.supports(Profile.GL3)) {
      dr = new DebugRenderGL3();
    } else if(Utils.supports(Profile.GL2)) {
      dr = new DebugRenderGL2();
    } else {
      //TODO
      throw new RuntimeException("Not implemented");
    }
    
    al.clear();
    al.add(new HashedString("mesh"));
    mem.init(al, "data/meshes/");
    

    if(Utils.supports(Profile.GL3))
      rom = new RenderableObjectManagerGL3();
    else if(Utils.supports(Profile.GL2))
      rom = new RenderableObjectManagerGL2();
    else
      throw new RuntimeException("Not implemented!");
    
    rom.init();
    
    
    
    //////////
    pw = new PhysicsWorld();
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
	    mem.weakify();//TODO més managers.
	  }

    System.gc();

    tm.cleanUnusedReferences();
    em.cleanUnusedReferences();
    mm.cleanUnusedReferences();
    mem.cleanUnusedReferences();

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
}
