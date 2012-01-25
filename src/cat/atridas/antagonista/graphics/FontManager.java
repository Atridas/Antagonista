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

import org.lwjgl.BufferUtils;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;

//TODO optimitzar tot lo relacionat amb això.
public abstract class FontManager extends ResourceManager<Font> {
  //private static Logger logger = Logger.getLogger(FontManager.class.getCanonicalName());
  private Font defaultFont = new Font.NullFont();
  
  private final HashMap<CachedTextIndex, CachedTextInfo> cachedTexts = new HashMap<>();
  private final HashSet<CachedTextIndex> unusedTexts = new HashSet<>();
  
  public void init(ArrayList<HashedString> _extensionsPriorized, String _basePath) {
    setExtensions(_extensionsPriorized);
    setBasePath(_basePath);
  }
  
  public final void printString(HashedString font, String text, Color3f color, Matrix4f WVPmatrix, RenderManager rm) {
    printString(getResource(font), text, color, WVPmatrix, TextAlignment.TOP_LEFT, rm);
  }
  
  public final void printString(HashedString font, String text, Color3f color, Matrix4f WVPmatrix, TextAlignment textAlignment, RenderManager rm) {
    printString(getResource(font), text, color, WVPmatrix, textAlignment, rm);
  }

  private Matrix4f    aligmentMatrix = new Matrix4f();
  private Matrix4f    finalWVP       = new Matrix4f();
  private Vector3f    translation    = new Vector3f();
  public final void printString(
      Font font,
      String text,
      Color3f color,
      Matrix4f WVPmatrix,
      TextAlignment textAlignment,
      RenderManager rm) {
    
    CachedTextIndex cti = new CachedTextIndex(text, font);
    CachedTextInfo cachedText = cachedTexts.get(cti);
    
    ByteBuffer  buffer1 = null;
    ShortBuffer buffer2 = null;
    int textlen = 0;
    Texture tex[] = null;
    if(cachedText == null) {
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
    
    switch(textAlignment) {
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
    
    switch(textAlignment) {
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
    
    
    if(cachedText.id < 0) {
      
      cachedText.id = printString(
                        buffer1, buffer2,
                        tex, textlen * 6,
                        finalWVP, color,
                        rm);
      cachedTexts.put(cti, cachedText);
    } else {
      printString(
          cachedText.id,
          finalWVP, color,
          rm);
    }
  }
  
  public final void cleanTextCache() {
    for(CachedTextIndex ctIndex : unusedTexts) {
      CachedTextInfo ctInfo = cachedTexts.remove(ctIndex);
      freeText(ctInfo.id);
    }
    unusedTexts.clear();
    unusedTexts.addAll( cachedTexts.keySet() );
  }
  
  protected abstract int printString(
      ByteBuffer _vertexBuffer, ShortBuffer _indexBuffer,
      Texture[] _tex, int indexLen,
      Matrix4f WVPMatrix, Color3f color,
      RenderManager rm);
  
  protected abstract void printString(
      int textID,
      Matrix4f WVPMatrix, Color3f color,
      RenderManager rm);
  
  protected abstract void freeText(int textID);
  
  static void printBuffers(ByteBuffer bb, IntBuffer ib) {
    bb.rewind();
    ib.rewind();
    while(bb.hasRemaining()) {
      System.out.println(bb.position());
      System.out.println("p: " + bb.getInt() + ", " + bb.getInt());
      System.out.println("page: " + bb.getInt());
      System.out.println("t: " + bb.getFloat() + ", " + bb.getFloat());
      System.out.println("channel: "  + bb.get() 
                                      + ", " + bb.get()
                                      + ", " + bb.get()
                                      + ", " + bb.get());
    }
    
    System.out.println("\n");
    
    while(ib.hasRemaining()) {
      System.out.println(
          ib.get() + ", " + ib.get() + ", " + ib.get() 
        + " : "
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
  
  private static final class CachedTextInfo {
    int width, id = -1;
    
    @Override
    public String toString() {
      return "id: " + id + ", width: " + width;
    }
  }
  
  private static final class CachedTextIndex {
    final String text;
    final HashedString font;
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
  
  public static enum TextAlignment {
    TOP_LEFT, TOP_RIGHT, TOP_CENTER,
    MID_LEFT, MID_RIGHT, MID_CENTER,
    BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_CENTER
  }
}
