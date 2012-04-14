package cat.atridas.antagonista.core;

import cat.atridas.antagonista.Clock;
import cat.atridas.antagonista.input.InputManager;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.graphics.MaterialManager;
import cat.atridas.antagonista.graphics.RenderableObjectManager;

public interface ManagerFactory {
	InputManager createInputManager();
	RenderManager createRenderManager();
	FontManager createFontManager();
	DebugRender createDebugRender();
	TextureManager createTextureManager();
	MaterialManager createMaterialManager();
	MeshManager createMeshManager();
	RenderableObjectManager createRenderableObjectManager();
	
	Clock createClock();
}
