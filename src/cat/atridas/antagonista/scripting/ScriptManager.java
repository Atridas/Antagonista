package cat.atridas.antagonista.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.python.core.PyCode;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.Utils;

public final class ScriptManager {
  private static final Logger LOGGER = Logger.getLogger(ScriptManager.class.getCanonicalName()); 
  
  private String basePath;
  
  private final PythonInterpreter interpreter;
  
  private final HashMap<String, PyCode> cachedMiniScripts = new HashMap<>();
  
  private String configFile;
  
  public ScriptManager(String _configFile) {
    interpreter = new PythonInterpreter();
    interpreter.setOut(new PythonWriter());
    
    load(_configFile);
  }
  
  public void load(String _configFile) {
    configFile = _configFile;
    
    try {
      InputStream is = Utils.findInputStream(configFile);
      
      
      DocumentBuilder db;
      db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = db.parse(is);
      doc.getDocumentElement().normalize();
      
      
      Element smXML = doc.getDocumentElement();
      
      assert "script_manager".compareTo(smXML.getNodeName()) == 0;
      
      basePath = smXML.getAttribute("base_path");
      
      NodeList nl = smXML.getChildNodes();
      for(int i = 0; i < nl.getLength(); ++i) {
        if(!(nl.item(i) instanceof Element))
          continue;
        
        Element node = (Element)nl.item(i);

        String content = node.getTextContent();
        
        switch(node.getNodeName()) {
        case "file":
          executeFile(content);
          break;
        case "script":
          execute(content);
          break;
        default:
          LOGGER.warning("Unrecognized element: " + node.getNodeName());
        }
        
        
      }
      
    } catch(Exception e) {
      LOGGER.warning(Utils.logExceptionStringAndStack(e));
    }
    if(basePath == null) {
      basePath = "data/scripts/";
    }
  }
  
  public void reload() {
    cachedMiniScripts.clear();
    load(configFile);
  }
  
  public void execute(String script) {
    try {
      PyCode code = cachedMiniScripts.get(script);
      if(code == null) {
        code = interpreter.compile(script);
        cachedMiniScripts.put(script, code);
      }
      interpreter.exec(code);
      
      
    } catch(PyException e) {
      LOGGER.warning(Utils.logExceptionStringAndStack(e));
    }
  }
  
  public void executeFile(String file) {
    try {
      String script = Utils.readFile(basePath + file);
      
      interpreter.exec(script);
      
    } catch (Exception e) {
      LOGGER.warning(Utils.logExceptionStringAndStack(e));
    }
  }
  
  
  private class PythonWriter extends Writer {
    //TODO fer-ho millor
    
    StringBuilder lastLine = new StringBuilder();

    @Override
    public void close() throws IOException {
      // --
    }

    @Override
    public void flush() throws IOException {
      if(lastLine.length() > 0) {
        LOGGER.fine(lastLine.toString());
        lastLine = new StringBuilder();
      }
    }
    
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
      
      for(int i = off; i < off + len; i++) {
        char c = cbuf[i];
        if(c == '\n') {
          LOGGER.fine(lastLine.toString());
          lastLine = new StringBuilder();
        } else {
          lastLine.append(c);
        }
      }
    }
    
  }
}
