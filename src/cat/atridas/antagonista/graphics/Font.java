package cat.atridas.antagonista.graphics;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;

/**
 * Classe que representa una font creada amb el <b>Bitmap Font Generator</b>
 * d'AngelCode.
 * 
 * 'http://www.angelcode.com/products/bmfont/'
 * 
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 *
 */
public class Font extends Resource {
  private static Logger logger = Logger.getLogger(Font.class.getCanonicalName());
  

  public static final int VERTEX_STRIDE = 3 * Utils.INTEGER_SIZE  // x, y .. textureId
                                        + 2 * Utils.FLOAT_SIZE    // s, t
                                        + 4 * Utils.BYTE_SIZE;    // channel

  public static final int POSITION_OFFSET  = 0;
  public static final int PAGE_OFFSET      = POSITION_OFFSET  + 2 * Utils.INTEGER_SIZE;
  public static final int TEXCOORDS_OFFSET = PAGE_OFFSET      + 1 * Utils.INTEGER_SIZE;
  public static final int CHANNEL_OFFSET   = TEXCOORDS_OFFSET + 2 * Utils.FLOAT_SIZE;
  
  
  public static int getVertexSize() {
    return 3 * Integer.SIZE / 8  // x, y .. textureId
         + 2 * Float.SIZE   / 8  // s, t
         + 4 * Byte.SIZE    / 8; // channel
  }
  
  
  private int width, height, lineHeight, highestChar;
  
  private final Map<Integer, Texture> pages = new HashMap<Integer, Texture>();
  private final Map<Character, Char> chars = new HashMap<Character, Char>();
  private final Map<Kerning, Integer> kernings = new HashMap<Kerning, Integer>();

  
  public Font(HashedString name) {
    super(name);
  }

  @Override
  public boolean load(InputStream is, HashedString extension) {
    DocumentBuilder db;
    try {
      db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      //Document doc = db.parse(f);
      Document doc = db.parse(is);
      doc.getDocumentElement().normalize();
      
      Element materialXML = doc.getDocumentElement();
      if("font".compareTo(materialXML.getTagName()) != 0) {
        throw new Exception("Root element is not \"font\".");
      }

      Element infoXML = (Element)materialXML.getElementsByTagName("info").item(0);
      Element commonXML = (Element)materialXML.getElementsByTagName("common").item(0);
      Element pagesXML = (Element)materialXML.getElementsByTagName("pages").item(0);
      Element charsXML = (Element)materialXML.getElementsByTagName("chars").item(0);
      Element kerningsXML = (Element)materialXML.getElementsByTagName("kernings").item(0);
      if(infoXML == null) {
        throw new Exception("\"info\" not found.");
      }
      if(commonXML == null) {
        throw new Exception("\"common\" not found.");
      }
      if(pagesXML == null) {
        throw new Exception("\"pages\" not found.");
      }
      if(charsXML == null) {
        throw new Exception("\"chars\" not found.");
      }
      /*if(kerningsXML == null) {
        throw new Exception("\"kernings\" not found.");
      }*/

      width = Integer.parseInt( commonXML.getAttribute("scaleW") );
      height = Integer.parseInt( commonXML.getAttribute("scaleH") );
      lineHeight = Integer.parseInt( commonXML.getAttribute("lineHeight") ) + 1;

      NodeList nl = pagesXML.getElementsByTagName("page");
      for(int i = 0; i < nl.getLength(); ++i) {
        Element page = ((Element)nl.item(i));
        int id = Integer.parseInt( page.getAttribute("id") );
        String file = page.getAttribute("file");
        
        Texture tex = Core.getCore().getTextureManager().getResource(new HashedString(file));
        pages.put(id, tex);
      }
      
      int charMax = 0;
      nl = charsXML.getElementsByTagName("char");
      for(int i = 0; i < nl.getLength(); ++i) {
        Element charInfo = ((Element)nl.item(i));
        int id = Integer.parseInt( charInfo.getAttribute("id") );
        char idC = (char) id;
        charMax = (charMax > id)? charMax : id;
        
        Char charObj = new Char();

        charObj.x = (float)Integer.parseInt( charInfo.getAttribute("x") ) / width;
        charObj.y = (float)(height - Integer.parseInt( charInfo.getAttribute("y") )) / height;
        
        charObj.width = Integer.parseInt( charInfo.getAttribute("width") );
        charObj.height = Integer.parseInt( charInfo.getAttribute("height") );
        charObj.xoffset = Integer.parseInt( charInfo.getAttribute("xoffset") );
        charObj.yoffset = Integer.parseInt( charInfo.getAttribute("yoffset") );
        charObj.xadvance = Integer.parseInt( charInfo.getAttribute("xadvance") );
        charObj.page = Integer.parseInt( charInfo.getAttribute("page") );
        charObj.chanel = Integer.parseInt( charInfo.getAttribute("chnl") );
        if(charObj.chanel == 15) charObj.chanel = 0;

        charObj.fwidth  = (float) charObj.width / width;
        charObj.fheight = (float) charObj.height / height;
        
        chars.put(idC, charObj);
      }
      highestChar = charMax;
      
      if(kerningsXML != null) {
        nl = kerningsXML.getElementsByTagName("kerning");
        for(int i = 0; i < nl.getLength(); ++i) {
          Element kerningInfo = ((Element)nl.item(i));
          Kerning kerning = new Kerning();
          kerning.first = (char)Integer.parseInt( kerningInfo.getAttribute("first") );
          kerning.second = (char)Integer.parseInt( kerningInfo.getAttribute("second") );
          
          int amount = Integer.parseInt( kerningInfo.getAttribute("amount") );
          
          kernings.put(kerning, amount);
        }
      }
    } catch (Exception e) {
      
      logger.severe("Problem loading font ");
      logger.severe(Utils.logExceptionStringAndStack(e));
      
      return false;
    }
    return true;
  }
  
  public final int numTextures() {
    return pages.size();
  }
  
  public int fillBuffers(CharSequence characters, ByteBuffer vertexBuffer, IntBuffer indexBuffer, Texture[] textures) {
    assert !cleaned;
    
    for(Entry<Integer, Texture> page : pages.entrySet()) {
      textures[page.getKey()] = page.getValue();
    }
    
    int maxX = 0;
    int x = 0, y = 0;
    char lastChar = 0;
    for(int i = 0; i < characters.length(); ++i) {
      char c = characters.charAt(i);
      Char charObj = chars.get(c);
      
      if(c == '\n') {
        x = 0;
        y += lineHeight;
        charObj = (charObj == null)? new Char() : charObj;
      }
      
      if(charObj == null) {
        logger.warning("Trying to print missing character '" + c + "' - " + (int)c);
        charObj = new Char();
      }

      Integer kerning = kernings.get(new Kerning(lastChar,c));
      if(kerning != null) x += kerning;

      int x0Coord = x                  + charObj.xoffset;
      int y0Coord = y                  + charObj.yoffset;
      int x1Coord = x + charObj.width  + charObj.xoffset;
      int y1Coord = y + charObj.height + charObj.yoffset;

      float s0 = charObj.x;
      float t0 = charObj.y;
      float s1 = charObj.x + charObj.fwidth;
      float t1 = charObj.y - charObj.fheight;

      byte channel0 = ((charObj.chanel & 8) != 0) ? Byte.MAX_VALUE : 0;
      byte channel1 = ((charObj.chanel & 4) != 0) ? Byte.MAX_VALUE : 0;
      byte channel2 = ((charObj.chanel & 2) != 0) ? Byte.MAX_VALUE : 0;
      byte channel3 = ((charObj.chanel & 1) != 0) ? Byte.MAX_VALUE : 0;
      
      //vertex 00
      vertexBuffer.putInt(x0Coord); //x
      vertexBuffer.putInt(y0Coord); //y
      
      vertexBuffer.putInt(charObj.page);   //page

      vertexBuffer.putFloat(s0);
      vertexBuffer.putFloat(t0);

      vertexBuffer.put(channel0); //channel
      vertexBuffer.put(channel1); //channel
      vertexBuffer.put(channel2); //channel
      vertexBuffer.put(channel3); //channel
      
      //vertex 10
      vertexBuffer.putInt(x1Coord); //x
      vertexBuffer.putInt(y0Coord); //y

      vertexBuffer.putInt(charObj.page);   //page

      vertexBuffer.putFloat(s1);
      vertexBuffer.putFloat(t0);

      vertexBuffer.put(channel0); //channel
      vertexBuffer.put(channel1); //channel
      vertexBuffer.put(channel2); //channel
      vertexBuffer.put(channel3); //channel
      
      //vertex 11
      vertexBuffer.putInt(x1Coord); //x
      vertexBuffer.putInt(y1Coord); //y

      vertexBuffer.putInt(charObj.page);   //page

      vertexBuffer.putFloat(s1);
      vertexBuffer.putFloat(t1);

      vertexBuffer.put(channel0); //channel
      vertexBuffer.put(channel1); //channel
      vertexBuffer.put(channel2); //channel
      vertexBuffer.put(channel3); //channel
      
      //vertex 01
      vertexBuffer.putInt(x0Coord); //x
      vertexBuffer.putInt(y1Coord); //y

      vertexBuffer.putInt(charObj.page);   //page

      vertexBuffer.putFloat(s0);
      vertexBuffer.putFloat(t1);

      vertexBuffer.put(channel0); //channel
      vertexBuffer.put(channel1); //channel
      vertexBuffer.put(channel2); //channel
      vertexBuffer.put(channel3); //channel
      
      
      //triangles
      indexBuffer.put(i*4 + 0);
      indexBuffer.put(i*4 + 3);
      indexBuffer.put(i*4 + 1);

      indexBuffer.put(i*4 + 1);
      indexBuffer.put(i*4 + 3);
      indexBuffer.put(i*4 + 2);
      
      x += charObj.xadvance;
      lastChar = c;
      
      if(x1Coord > maxX) {
    	  maxX = x1Coord;
      }
    }
    return maxX;
  }
  
  private static class Char {
    float x, y, fwidth, fheight; 
    int width, height, xoffset, yoffset, xadvance, page, chanel;
  }
  
  private class Kerning {
    char first, second;

    Kerning(){}
    Kerning(char f, char s){first = f; second = s;}
    
    @Override
    public int hashCode() {
      return first * highestChar + second;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Kerning other = (Kerning) obj;
      if (first != other.first)
        return false;
      if (second != other.second)
        return false;
      return true;
    }
  }
  
  static final class NullFont extends Font {

    public NullFont() {super(Utils.NULL_FONT);}
    public int fillBuffers(CharSequence characters, ByteBuffer vertexBuffer, IntBuffer indexBuffer, Texture[] textures) {return 0;}
    
  }

  @Override
  public int getRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getVRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getLineHeight() {
    return lineHeight;
  }

  public int getHighestChar() {
    return highestChar;
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    cleaned = true;
  }
}
