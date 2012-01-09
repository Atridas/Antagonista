package cat.atridas.antagonista.graphics;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;


public abstract class FontManager {
  private static Logger logger = Logger.getLogger(FontManager.class.getCanonicalName());
  
  private Map<String, SoftReference<Font>> fonts = new HashMap<String, SoftReference<Font>>();
  private Font defaultFont = new Font.NullFont();
  
  public Font getFont(String file) {
    Font font = null;
    SoftReference<Font> fo = fonts.get(file);
    if(fo != null) {
      font = fo.get();
      if(font != null) {
        return font;
      }
    }
    
    
    try {
      font = new Font(file);
    } catch (IOException e) {
      font = defaultFont;
      logger.warning(e.toString());
    } catch (Exception e) {
      font = defaultFont;
      logger.warning("Returning default font");
    }
    fonts.put(file, new SoftReference<Font>(font));
    return font;
  }
  
  public final void printString(String font, String text, Tuple3f color, Matrix4f WVPmatrix, RenderManager rm) {
    printString(getFont(font), text, color, WVPmatrix, false, rm);
  }
  
  public final void printString(String font, String text, Tuple3f color, Matrix4f WVPmatrix, boolean centered, RenderManager rm) {
    printString(getFont(font), text, color, WVPmatrix, centered, rm);
  }
  
  public abstract void printString(
      Font font,
      String text,
      Tuple3f color,
      Matrix4f WVPmatrix,
      boolean centered,
      RenderManager rm);
  
  
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
  
}
