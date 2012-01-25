package cat.atridas.antagonista.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

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
    int len = text.length();
    int buffer1Size = Font.VERTEX_STRIDE * len * 4;
    ByteBuffer buffer1 = BufferUtils.createByteBuffer(buffer1Size);
    ShortBuffer  buffer2 = BufferUtils.createShortBuffer(len * 6);
    
    Texture tex[] = new Texture[font.numTextures()];
    
    int x = font.fillBuffers(text, buffer1, buffer2, tex);

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
      translation.x = -x;
      break;
    case TOP_CENTER:
    case BOTTOM_CENTER:
    case MID_CENTER:
      translation.x = -x / 2.f;
      break;
    }

    aligmentMatrix.setIdentity();
    aligmentMatrix.setTranslation(translation);
    
    
    finalWVP.mul(WVPmatrix, aligmentMatrix);
    
    buffer1.flip();
    buffer2.flip();
    
    printString(
        buffer1, buffer2,
        tex, len * 6,
        finalWVP, color,
        rm);
  }
  
  protected abstract void printString(
      ByteBuffer _vertexBuffer, ShortBuffer _indexBuffer,
      Texture[] _tex, int indexLen,
      Matrix4f WVPMatrix, Color3f color,
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
  

  
  public static enum TextAlignment {
    TOP_LEFT, TOP_RIGHT, TOP_CENTER,
    MID_LEFT, MID_RIGHT, MID_CENTER,
    BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_CENTER
  }
}
