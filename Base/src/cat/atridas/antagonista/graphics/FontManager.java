package cat.atridas.antagonista.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;
import cat.atridas.antagonista.core.BufferUtils;

/**
 * This class manages all fonts and is capable of rendering them during the
 * render phase.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public abstract class FontManager extends ResourceManager<Font> {
  // private static Logger logger =
  // Logger.getLogger(FontManager.class.getCanonicalName());
  private Font defaultFont = new Font.NullFont();

  public static final HashedString FONT_14 = new HashedString("font14");

  /**
   * Cache of texts recently used.
   * 
   * @since 0.1
   * @see #cleanTextCache()
   */
  private final HashMap<CachedTextIndex, CachedTextInfo> cachedTexts = new HashMap<>();
  /**
   * Texts that hadn't been used since the last call to
   * <code>cleanTextCache()</code>.
   * 
   * @since 0.1
   * @see #cleanTextCache()
   */
  private final HashSet<CachedTextIndex> unusedTexts = new HashSet<>();

  /**
   * Initializes the manager.
   * 
   * @param _extensions
   *          Extensions of the resources to be loaded.
   * @param _basePath
   *          Path where the resources will be searched.
   * @since 0.1
   * @see ResourceManager#ResourceManager(String, ArrayList)
   */
  public void init(ArrayList<HashedString> _extensionsPriorized,
      String _basePath) {
    setExtensions(_extensionsPriorized);
    setBasePath(_basePath);
  }

  /**
   * Same as
   * <code>printString(<strong>getResource(font)</strong>, text, color, WVPmatrix, <strong>TOP_LEFT</strong>, rm);</code>
   * 
   * @param font
   *          used to render the text.
   * @param text
   *          to write.
   * @param color
   *          used in the text.
   * @param WVPmatrix
   *          3D (or 2D orthogonal) transformation.
   * @param rm
   *          RenderManager
   * @since 0.1
   * @see #printString(Font, String, Color3f, Matrix4f, TextAlignment,
   *      RenderManager)
   */
  public final void printString(HashedString font, String text, Color3f color,
      Matrix4f WVPmatrix, RenderManager rm) {
    printString(getResource(font), text, color, WVPmatrix,
        TextAlignment.TOP_LEFT, rm);
  }

  /**
   * Same as
   * <code>printString(<strong>getResource(font)</strong>, text, color, WVPmatrix, textAlignment, rm);</code>
   * 
   * @param font
   *          used to render the text.
   * @param text
   *          to write.
   * @param color
   *          used in the text.
   * @param WVPmatrix
   *          3D (or 2D orthogonal) transformation.
   * @param textAlignment
   *          alignment of the text respect the transformation.
   * @param rm
   *          RenderManager
   * @since 0.1
   * @see #printString(Font, String, Color3f, Matrix4f, TextAlignment,
   *      RenderManager)
   */
  public final void printString(HashedString font, String text, Color3f color,
      Matrix4f WVPmatrix, TextAlignment textAlignment, RenderManager rm) {
    printString(getResource(font), text, color, WVPmatrix, textAlignment, rm);
  }

  private Matrix4f aligmentMatrix = new Matrix4f();
  private Matrix4f finalWVP = new Matrix4f();
  private Vector3f translation = new Vector3f();

  /**
   * Prints a text using the transformation and color specified.
   * 
   * @param font
   *          used to render the text.
   * @param text
   *          to write.
   * @param color
   *          used in the text.
   * @param WVPmatrix
   *          3D (or 2D orthogonal) transformation.
   * @param textAlignment
   *          alignment of the text respect the transformation.
   * @param rm
   *          RenderManager
   * @since 0.1
   */
  public final void printString(Font font, String text, Color3f color,
      Matrix4f WVPmatrix, TextAlignment textAlignment, RenderManager rm) {

    CachedTextIndex cti = new CachedTextIndex(text, font);
    CachedTextInfo cachedText = cachedTexts.get(cti);

    ByteBuffer buffer1 = null;
    ShortBuffer buffer2 = null;
    int textlen = 0;
    Texture tex[] = null;
    if (cachedText == null) {
      cachedText = new CachedTextInfo();

      textlen = text.length();
      int buffer1Size = Font.VERTEX_STRIDE * textlen * 4;
      buffer1 = BufferUtils.createByteBuffer(buffer1Size);
      buffer2 = BufferUtils.createShortBuffer(textlen * 6);

      tex = new Texture[font.numTextures()];

      cachedText.width = font.fillBuffers(text, buffer1, buffer2, tex);

      buffer1.flip();
      buffer2.flip();
    } else {
      unusedTexts.remove(cti);
    }

    translation.z = 0;

    switch (textAlignment) {
    case TOP_LEFT:
    case TOP_RIGHT:
    case TOP_CENTER:
      translation.y = 0;
      break;
    case BOTTOM_LEFT:
    case BOTTOM_RIGHT:
    case BOTTOM_CENTER:
      translation.y = -font.getLineHeight();
      break;
    case MID_LEFT:
    case MID_RIGHT:
    case MID_CENTER:
      translation.y = -font.getLineHeight() / 2.f;
      break;
    }

    switch (textAlignment) {
    case TOP_LEFT:
    case MID_LEFT:
    case BOTTOM_LEFT:
      translation.x = 0;
      break;
    case TOP_RIGHT:
    case MID_RIGHT:
    case BOTTOM_RIGHT:
      translation.x = -cachedText.width;
      break;
    case TOP_CENTER:
    case BOTTOM_CENTER:
    case MID_CENTER:
      translation.x = -cachedText.width / 2.f;
      break;
    }

    aligmentMatrix.setIdentity();
    aligmentMatrix.setTranslation(translation);

    finalWVP.mul(WVPmatrix, aligmentMatrix);

    if (cachedText.id < 0) {

      cachedText.id = printString(buffer1, buffer2, tex, textlen * 6, finalWVP,
          color, rm);
      cachedTexts.put(cti, cachedText);
    } else {
      printString(cachedText.id, finalWVP, color, rm);
    }
  }

  /**
   * Clears all cached text information not used since the last call to this
   * method.
   * 
   * @since 0.1
   */
  public final void cleanTextCache() {
    for (CachedTextIndex ctIndex : unusedTexts) {
      CachedTextInfo ctInfo = cachedTexts.remove(ctIndex);
      freeText(ctInfo.id);
    }
    unusedTexts.clear();
    unusedTexts.addAll(cachedTexts.keySet());
  }

  /**
   * Prints a String.
   * 
   * @param _vertexBuffer
   *          vertex buffer to use.
   * @param _indexBuffer
   *          index buffer to use.
   * @param _tex
   *          list of textures to activate.
   * @param indexLen
   *          number of indices.
   * @param WVPMatrix
   *          transformation to use.
   * @param color
   *          color to use.
   * @param rm
   *          RenderManager.
   * @return an index to the cache this text-font combination has been saved.
   * 
   * @since 0.1
   * @see #printString(int, Matrix4f, Color3f, RenderManager)
   */
  protected abstract int printString(ByteBuffer _vertexBuffer,
      ShortBuffer _indexBuffer, Texture[] _tex, int indexLen,
      Matrix4f WVPMatrix, Color3f color, RenderManager rm);

  /**
   * Prints a text saved in the cache.
   * 
   * @param textID
   *          cache index.
   * @param WVPMatrix
   *          transformation to use.
   * @param color
   *          color to use.
   * @param rm
   *          RenderManager
   * 
   * @since 0.1
   * @see #printString(ByteBuffer, ShortBuffer, Texture[], int, Matrix4f,
   *      Color3f, RenderManager)
   */
  protected abstract void printString(int textID, Matrix4f WVPMatrix,
      Color3f color, RenderManager rm);

  /**
   * Marks a index of the cache where text-font combinations are saved to be
   * freed.
   * 
   * @param textID
   *          index to be freed.
   * 
   * @since 0.1
   * @see #cleanTextCache()
   */
  protected abstract void freeText(int textID);

  /**
   * Debug method.
   * 
   * @param bb
   * @param ib
   */
  static void printBuffers(ByteBuffer bb, IntBuffer ib) {
    bb.rewind();
    ib.rewind();
    while (bb.hasRemaining()) {
      System.out.println(bb.position());
      System.out.println("p: " + bb.getInt() + ", " + bb.getInt());
      System.out.println("page: " + bb.getInt());
      System.out.println("t: " + bb.getFloat() + ", " + bb.getFloat());
      System.out.println("channel: " + bb.get() + ", " + bb.get() + ", "
          + bb.get() + ", " + bb.get());
    }

    System.out.println("\n");

    while (ib.hasRemaining()) {
      System.out.println(ib.get() + ", " + ib.get() + ", " + ib.get() + " : "
          + ib.get() + ", " + ib.get() + ", " + ib.get());
    }
    bb.rewind();
    ib.rewind();
  }

  @Override
  protected Font createNewResource(HashedString name) {
    return new Font(name);
  }

  @Override
  public Font getDefaultResource() {
    return defaultFont;
  }

  /**
   * Information saved in the local cache.
   * 
   * @author Isaac 'Atridas' Serrano Guasch.
   * @since 0.1
   * 
   */
  private static final class CachedTextInfo {
    /**
     * Width of the text.
     * 
     * @since 0.1
     */
    int width;
    /**
     * Id in the cache.
     * 
     * @since 0.1
     */
    int id = -1;

    @Override
    public String toString() {
      return "id: " + id + ", width: " + width;
    }
  }

  /**
   * Information used to index the cache.
   * 
   * @author Isaac 'Atridas' Serrano Guasch.
   * @since 0.1
   * 
   */
  private static final class CachedTextIndex {
    /**
     * Text rendered.
     * 
     * @since 0.1
     */
    final String text;
    /**
     * Font used.
     * 
     * @since 0.1
     */
    final HashedString font;

    /**
     * Creates the index.
     * 
     * @param _text
     * @param _font
     */
    public CachedTextIndex(String _text, Font _font) {
      text = _text;
      font = _font.resourceName;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((font == null) ? 0 : font.hashCode());
      result = prime * result + ((text == null) ? 0 : text.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CachedTextIndex other = (CachedTextIndex) obj;
      if (font == null) {
        if (other.font != null)
          return false;
      } else if (!font.equals(other.font))
        return false;
      if (text == null) {
        if (other.text != null)
          return false;
      } else if (!text.equals(other.text))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "'" + text + "' [" + font + "]";
    }
  }

  /**
   * Enumeration used to indicate witch point, of 9 possibles, is the origin of
   * a text.
   * 
   * @author Isaac 'Atridas' Serrano Guasch.
   * @since 0.1
   * 
   */
  public static enum TextAlignment {
    TOP_LEFT, TOP_RIGHT, TOP_CENTER, MID_LEFT, MID_RIGHT, MID_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_CENTER
  }
}
