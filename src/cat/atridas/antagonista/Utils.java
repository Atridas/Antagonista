package cat.atridas.antagonista;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.vecmath.Matrix4f;

import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Element;

import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager.Profile;

public abstract class Utils {
  private static Logger LOGGER = Logger.getLogger(Utils.class.getCanonicalName());
  

  public static final HashedString DEFAULT = new HashedString("default");
  public static final HashedString WHITE_EFFECT = new HashedString("WhiteEffect");
  public static final HashedString CLOSE = new HashedString("close");
  public static final HashedString MAIN_GAME = new HashedString("main_game");
  public static final HashedString DEBUG_MATERIAL_NAME = new HashedString("Debug");
  
  
  public static final float EPSILON = 0.0001f;

  public static final int FLOAT_SIZE   = Float  .SIZE / 8;
  public static final int INTEGER_SIZE = Integer.SIZE / 8;
  public static final int SHORT_SIZE   = Short  .SIZE / 8;
  /*
  public static File findFile(String name) {
    try {
      //URL url = Utils.class.getResource(name);
    	URL url = ResourceLoader.getResource(name);
      return new File(url.toURI());
    } catch (Exception e) {
      return new File(name);
    }
  }
  */
  
  public static InputStream findInputStream(String name) throws FileNotFoundException {
	  /*
	  try {
      URL url = Utils.class.getResource(name);
      return url.openStream();
    } catch (Exception e) {
      return new FileInputStream(name);
    }
    */
	  InputStream is = ResourceLoader.getResourceAsStream(name);
	  if(is == null)
		  throw new FileNotFoundException(name);
	  return is;
  }
  
  /*
  public static String readFile(File f) {
    try {
      return readInputStream(new FileInputStream(f));
    } catch (FileNotFoundException e) {
      logger.warning(e.toString());
      return null;
    }
  }
  */
  
  public static String readFile(String name) throws FileNotFoundException {
	  return readInputStream(findInputStream(name));
  }
  
  public static String readInputStream(InputStream is) {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
      
      StringBuilder sb = new StringBuilder();
      String aux = reader.readLine();
      while(aux != null) {
        sb.append(aux);
        sb.append('\n');
        aux = reader.readLine();
      }
      
      reader.close();
      return sb.toString();
    } catch (FileNotFoundException e) {
      LOGGER.warning(e.toString());
      return null;
    } catch (UnsupportedEncodingException e) {
      LOGGER.warning(e.toString());
      return null;
    } catch (IOException e) {
      LOGGER.warning(e.toString());
      return null;
    }
  }
  
  /**
   * Mira quina capçalera hi ha iretorna el tipus. Deixa el stream DESPRÉS de la capçalera.
   * 
   * @param is
   * @param headers
   * @param onMissing
   * @return
   * @throws IOException
   */
  public static <T> T readHeader(InputStream is, Map<byte[],T> headers, T onMissing) throws IOException {
    HashSet<byte[]> possibleHeaders = new HashSet<>(headers.keySet());
    int i = 0;
    byte[] confirmedHeader = null;
    while(possibleHeaders.size() > 0) {
      int nextByte = is.read();
      
      Iterator<byte[]> it = possibleHeaders.iterator();
      while( it.hasNext() ) {
        byte[] header = it.next();
        if(i == header.length) {
          confirmedHeader = header;
          possibleHeaders.remove(header);
          it = possibleHeaders.iterator();
          continue;
        } else if(header[i] != (byte) nextByte) {
          possibleHeaders.remove(header);
          it = possibleHeaders.iterator();
          continue;
        }
      }
      i++;
    }
    
    if(confirmedHeader == null) {
      return onMissing;
    } else {
      return headers.get(confirmedHeader);
    }
    
  }
  

  public static boolean hasGLErrors() {
    return Core.getCore().getRenderManager().hasGLErrors();
  }
  public static void clearSilentlyGLErrors() {
    Core.getCore().getRenderManager().clearSilentlyGLErrors();
  }
  
  public static Profile getProfile() {
    return Core.getCore().getRenderManager().getProfile();
  }
  
  public static boolean supports(Profile profile) {
    return Core.getCore().getRenderManager().getProfile().supports(profile);
  }
  
  public static void supportOrException(Profile profile, String functionality) {
    Core.getCore().getRenderManager().getProfile().supportOrException(profile, functionality);
  }
  
  
  public static String getStringContentFromXMLSubElement(Element element, String name) {
    return ((Element)element.getElementsByTagName("resource").item(0)).getTextContent();
  }
  
  public static String logExceptionStringAndStack(Exception e) {
    
    StringBuilder stackTrace = new StringBuilder("Exception encountered: ");
    stackTrace.append(e.toString());
    for(StackTraceElement ste : e.getStackTrace()) {
      stackTrace.append("\n  ");
      stackTrace.append(ste.toString());
    }
    
    return stackTrace.toString();
  }
  
  
  private static StreamHandler sh = null;
  public static void setConsoleLogLevel(Level level) {

    String packname = Utils.class.getPackage().getName();
    Logger log = Logger.getLogger(packname);
    log.setLevel(level);
    
    if(sh == null) {
      sh = new StreamHandler(System.out, new SimpleFormatter());
      log.addHandler(sh);
    }
    sh.setLevel(level);
  }
  
  public static void matrixToBuffer(Matrix4f in, FloatBuffer out) {
    float f[] = new float[4];
    for(int i = 0; i < 4; ++i) {
      in.getColumn(i, f);
      out.put(f);
    }
  }

  public static void matrixToBuffer(Matrix4f in, ByteBuffer out) {
    int pos = out.position();
    FloatBuffer fb = out.asFloatBuffer();
    //fb.position(pos / FLOAT_SIZE);
    matrixToBuffer(in, fb);
    out.position(pos + (16 * FLOAT_SIZE));
  }
}