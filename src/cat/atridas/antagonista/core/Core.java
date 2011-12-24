package cat.atridas.antagonista.core;

import java.awt.Canvas;

import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.ShaderManager;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.input.InputManager;

public final class Core {
	
	private RenderManager  rm = new RenderManager();
	private InputManager   im = new InputManager();
	private TextureManager tm = new TextureManager();
	private ShaderManager  sm = new ShaderManager();
	private FontManager    fm = new FontManager();

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
	
	public void init(int w, int h, String title, Canvas displayParent) {
		rm.initDisplay(w, h, title, displayParent);
		im.init();
		
		rm.initGL();
	}
	
	public void close() {
		im.close();
		
		//sm.cleanUp();
		//TODO tm.cleanUp();
		rm.closeDisplay();
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
