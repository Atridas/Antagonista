package cat.atridas.antagonista;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Element;

import cat.atridas.antagonista.core.Core;

public abstract class Utils {
  private static Logger logger = Logger.getLogger(Utils.class.getCanonicalName());
  
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
      logger.warning(e.toString());
      return null;
    } catch (UnsupportedEncodingException e) {
      logger.warning(e.toString());
      return null;
    } catch (IOException e) {
      logger.warning(e.toString());
      return null;
    }
  }
  
  
  public static boolean hasGLErrors() {
    return Core.getCore().getRenderManager().hasGLErrors();
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
}