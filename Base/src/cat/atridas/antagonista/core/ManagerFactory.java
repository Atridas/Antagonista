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

/**
 * Interface that must be implemented to create a manager factory of all
 * managers related to basic engine operations.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.5
 * 
 */
public interface ManagerFactory {
  /**
   * Creates a InputManager.
   * 
   * @return a new InputManager.
   * @since 0.5
   */
  InputManager createInputManager();

  /**
   * Creates a RenderManager.
   * 
   * @return a new RenderManager.
   * @since 0.5
   */
  RenderManager createRenderManager();

  /**
   * Creates a FontManager.
   * 
   * @return a new FontManager.
   * @since 0.5
   */
  FontManager createFontManager();

  /**
   * Creates a DebugRender.
   * 
   * @return a new DebugRender.
   * @since 0.5
   */
  DebugRender createDebugRender();

  /**
   * Creates a TextureManager.
   * 
   * @return a new TextureManager.
   * @since 0.5
   */
  TextureManager createTextureManager();

  /**
   * Creates a MaterialManager.
   * 
   * @return a new MaterialManager.
   * @since 0.5
   */
  MaterialManager createMaterialManager();

  /**
   * Creates a MeshManager.
   * 
   * @return a new MeshManager.
   * @since 0.5
   */
  MeshManager createMeshManager();

  /**
   * Creates a RenderableObjectManager.
   * 
   * @return a new RenderableObjectManager.
   * @since 0.5
   */
  RenderableObjectManager createRenderableObjectManager();

  /**
   * Creates a Clock.
   * 
   * @return a new Clock.
   * @since 0.5
   */
  Clock createClock();
}
