package cat.atridas.antagonista.core;

import java.awt.Canvas;
import java.util.ArrayList;

import cat.atridas.antagonista.deprecated.ShaderManager;
import cat.atridas.antagonista.graphics.EffectManager;
import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.MaterialManager;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.graphics.gl.FontManagerGL;
import cat.atridas.antagonista.graphics.gl.RenderManagerGL;
import cat.atridas.antagonista.input.InputManager;

public final class Core {
	
	private RenderManager   rm  = new RenderManagerGL();//TODO
	private InputManager    im  = new InputManager();
	private TextureManager  tm  = new TextureManager();
	private ShaderManager   sm  = new ShaderManager();
	private FontManager     fm  = new FontManagerGL();//TODO
  private EffectManager   em  = new EffectManager(); //TODO
  private MaterialManager mm  = new MaterialManager();
  private MeshManager     mem = new MeshManager();

	public RenderManager getRenderManager()
	{
		return rm;
	}
	
	public InputManager getInputManager()
	{
		return im;
	}
	
	public TextureManager getTextureManager()
	{
		return tm;
	}

	public ShaderManager getShaderManager()
	{
		return sm;
	}
  
  public FontManager getFontManager()
  {
    return fm;
  }
  
  public EffectManager getEffectManager()
  {
    return em;
  }
  
  public MaterialManager getMaterialManager()
  {
    return mm;
  }
  
  public MeshManager getMeshManager()
  {
    return mem;
  }
	
	public void init(int w, int h, String title, Canvas displayParent) {
		rm.initDisplay(w, h, title, displayParent);
		im.init();
		
		rm.initGL();
		
		em.init("data/xml/effects.xml", rm);
		ArrayList<String> al = new ArrayList<>();
		al.add("dds");
		al.add("png");
		tm.init(al, "data/textures/");

		al.clear();
    al.add("mat");
    mm.init(al, "data/materials/");
    
    al.clear();
    al.add("mesh");
    mem.init(al, "data/meshes/");
	}
	
	public void cleanUnusedResources(boolean weakify) {
	  if(weakify) {
	    tm.weakify();
	    em.weakify();
	    mm.weakify();
	    mem.weakify();
	  }

    System.gc();

    tm.cleanUnusedReferences();
    em.cleanUnusedReferences();
    mm.cleanUnusedReferences();
    mem.cleanUnusedReferences();

    System.runFinalization();
	}
	
	public void close() {
		
		//sm.cleanUp();
		//TODO tm.cleanUp();
		

    mem = null;
    mm  = null;
    em  = null;
    fm  = null;
    sm  = null;
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
	
	public static Core getCore()
	{
		if(instance == null) {
			createInstance();
		}
		return instance;
	}
}
