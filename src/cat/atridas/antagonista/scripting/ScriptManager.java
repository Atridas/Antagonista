package cat.atridas.antagonista.scripting;

import org.python.util.PythonInterpreter;

public class ScriptManager {
  private final PythonInterpreter interpreter = new PythonInterpreter();
  
  public void execute(String script) {
    interpreter.exec(script);
  }
}
