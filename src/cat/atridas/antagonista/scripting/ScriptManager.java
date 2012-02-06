package cat.atridas.antagonista.scripting;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import cat.atridas.antagonista.Utils;

public class ScriptManager {
  private static final Logger LOGGER = Logger.getLogger(ScriptManager.class.getCanonicalName()); 
  
  private final PythonInterpreter interpreter = new PythonInterpreter();
  
  {
    interpreter.setOut(new PythonWriter());
  }
  
  public void execute(String script) {
    try {
      interpreter.exec(script);
    } catch(PyException e) {
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
