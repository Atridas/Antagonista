package cat.atridas.antagonista.lwjgl;

import cat.atridas.antagonista.core.ManagerFactory;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.MaterialManager;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.graphics.gl.MaterialManagerGL;
import cat.atridas.antagonista.graphics.gl.MeshManagerGL;
import cat.atridas.antagonista.graphics.gl.RenderManagerGL;
import cat.atridas.antagonista.graphics.gl.TextureManagerGL;
import cat.atridas.antagonista.graphics.gl2.DebugRenderGL2;
import cat.atridas.antagonista.graphics.gl2.FontManagerGL2;
import cat.atridas.antagonista.graphics.gl2.RenderableObjectManagerGL2;
import cat.atridas.antagonista.input.InputManager;

public class LWJGLManagerFactory implements ManagerFactory {

	@Override
	public InputManagerLWJGL createInputManager() {
		return new InputManagerLWJGL();
	}

	@Override
	public RenderManagerGL createRenderManager() {
		return new RenderManagerGL();
	}

	@Override
	public FontManager createFontManager() {
		return new FontManagerGL2(); //TODO!!!!
	}

	@Override
	public DebugRender createDebugRender() {
		return new DebugRenderGL2(); //TODO!!!!
	}

	@Override
	public TextureManager createTextureManager() {
		return new TextureManagerGL();
	}

	@Override
	public MaterialManager createMaterialManager() {
		return new MaterialManagerGL();
	}

	@Override
	public MeshManager createMeshManager() {
		return new MeshManagerGL();
	}

	@Override
	public RenderableObjectManager createRenderableObjectManager() {
		return new RenderableObjectManagerGL2(); // TODO!!!!!!
	}

	@Override
	public Clock createClock() {
		return new Clock();
	}

}
