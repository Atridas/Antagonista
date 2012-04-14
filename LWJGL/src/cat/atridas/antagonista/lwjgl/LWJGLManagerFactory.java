package cat.atridas.antagonista.lwjgl;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Element;

import cat.atridas.antagonista.AntagonistException;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.BufferUtils.BufferUtilsInstance;
import cat.atridas.antagonista.core.ManagerFactory;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.MaterialManager;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RenderableObjectManager;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.TextureManager;
import cat.atridas.antagonista.graphics.Technique.TechniquePassFactory;
import cat.atridas.antagonista.graphics.gl.MaterialManagerGL;
import cat.atridas.antagonista.graphics.gl.MeshManagerGL;
import cat.atridas.antagonista.graphics.gl.RenderManagerGL;
import cat.atridas.antagonista.graphics.gl.TextureManagerGL;
import cat.atridas.antagonista.graphics.gl2.DebugRenderGL2;
import cat.atridas.antagonista.graphics.gl2.FontManagerGL2;
import cat.atridas.antagonista.graphics.gl2.RenderableObjectManagerGL2;
import cat.atridas.antagonista.graphics.gl2.TechniquePassGL2;

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

	static {
		new SlickResourceLoader();
		new TechniquePassFactoryGL();
		new BufferUtilsInstanceLWJGL();
	}
	
	private static class SlickResourceLoader extends Utils.ResourceLoader {

		@Override
		public InputStream getResourceAsStream(String name) {
			return ResourceLoader.getResourceAsStream(name);
		}
	}
	
	private static class TechniquePassFactoryGL extends TechniquePassFactory {

		@Override
		protected TechniquePass createTechniquePass(Element techniquePassXML) throws AntagonistException {
			return new TechniquePassGL2(techniquePassXML); // TODO
		}
		
		@Override
		protected TechniquePass createTechniquePass() {
			return new TechniquePassGL2(); // TODO
		}

		@Override
		public TechniquePass createFontTechniquePass() {
			return new TechniquePassGL2(true);
		}
		
	}
	
	private static class BufferUtilsInstanceLWJGL extends BufferUtilsInstance {

		@Override
		protected ShortBuffer createShortBuffer(int length) {
			return BufferUtils.createShortBuffer(length);
		}

		@Override
		protected FloatBuffer createFloatBuffer(int length) {
			return BufferUtils.createFloatBuffer(length);
		}

		@Override
		protected ByteBuffer createByteBuffer(int length) {
			return BufferUtils.createByteBuffer(length);
		}
		
	}
}
