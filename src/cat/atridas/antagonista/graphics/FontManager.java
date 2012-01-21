package cat.atridas.antagonista.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;

//TODO optimitzar tot lo relacionat amb això.
public abstract class FontManager extends ResourceManager<Font> {
  //private static Logger logger = Logger.getLogger(FontManager.class.getCanonicalName());
  private Font defaultFont = new Font.NullFont();
  

  
  public void init(ArrayList<String> _extensionsPriorized, String _basePath) {
    setExtensions(_extensionsPriorized);
    setBasePath(_basePath);
  }
  
  public final void printString(HashedString font, String text, Tuple3f color, Matrix4f WVPmatrix, RenderManager rm) {
    printString(getResource(font), text, color, WVPmatrix, TextAligment.TOP_LEFT, rm);
  }
  
  public final void printString(HashedString font, String text, Tuple3f color, Matrix4f WVPmatrix, TextAligment textAligment, RenderManager rm) {
    printString(getResource(font), text, color, WVPmatrix, textAligment, rm);
  }
  
  public abstract void printString(
      Font font,
      String text,
      Tuple3f color,
      Matrix4f WVPmatrix,
      TextAligment textAligment,
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

  @Override
  protected Font createNewResource(HashedString name) {
    return new Font(name);
  }

  @Override
  public Font getDefaultResource() {
    return defaultFont;
  }
  

  
  public static enum TextAligment {
    TOP_LEFT, TOP_RIGHT, TOP_CENTER,
    MID_LEFT, MID_RIGHT, MID_CENTER,
    BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_CENTER
  }
}
