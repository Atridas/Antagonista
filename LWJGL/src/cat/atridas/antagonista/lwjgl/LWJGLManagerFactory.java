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
import cat.atridas.antagonista.graphics.RenderManager.Profile;
import cat.atridas.antagonista.graphics.Technique.TechniquePassFactory;
import cat.atridas.antagonista.graphics.gl.MaterialManagerGL;
import cat.atridas.antagonista.graphics.gl.MeshManagerGL;
import cat.atridas.antagonista.graphics.gl.RenderManagerGL;
import cat.atridas.antagonista.graphics.gl.TextureManagerGL;
import cat.atridas.antagonista.graphics.gl2.DebugRenderGL2;
import cat.atridas.antagonista.graphics.gl2.FontManagerGL2;
import cat.atridas.antagonista.graphics.gl2.RenderableObjectManagerGL2;
import cat.atridas.antagonista.graphics.gl2.TechniquePassGL2;
import cat.atridas.antagonista.graphics.gl3.DebugRenderGL3;
import cat.atridas.antagonista.graphics.gl3.FontManagerGL3;
import cat.atridas.antagonista.graphics.gl3.RenderableObjectManagerGL3;
import cat.atridas.antagonista.graphics.gl3.TechniquePassGL3;

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
    if (Utils.supports(Profile.GL3)) {
      return new FontManagerGL3();
    } else {
      return new FontManagerGL2();
    }
  }

  @Override
  public DebugRender createDebugRender() {
    if (Utils.supports(Profile.GL3)) {
      return new DebugRenderGL3();
    } else {
      return new DebugRenderGL2();
    }
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
    if (Utils.supports(Profile.GL3)) {
      return new RenderableObjectManagerGL3();
    } else {
      return new RenderableObjectManagerGL2();
    }
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

    @Override
    public boolean resourceExists(String name) {
      return ResourceLoader.resourceExists(name);
    }
  }

  private static class TechniquePassFactoryGL extends TechniquePassFactory {

    @Override
    protected TechniquePass createTechniquePass(Element techniquePassXML)
        throws AntagonistException {
      if (Utils.supports(Profile.GL3)) {
        return new TechniquePassGL3(techniquePassXML);
      } else {
        return new TechniquePassGL2(techniquePassXML);
      }
    }

    @Override
    protected TechniquePass createTechniquePass() {
      if (Utils.supports(Profile.GL3)) {
        return new TechniquePassGL3();
      } else {
        return new TechniquePassGL2();
      }
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
