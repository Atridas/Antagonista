package cat.atridas.antagonista.graphics;

import static org.lwjgl.opengl.GL20.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import cat.atridas.antagonista.Utils;

public final class ShaderManager {
  private final Map<String, Integer> vertexShaders = new HashMap<String, Integer>();
  private final Map<String, Integer> fragmentShaders = new HashMap<String, Integer>();
  
  //vertex -> fragments -> shader
  private final Map<String, Map<String, SoftReference<ShaderObject>>> shaders = new HashMap<String, Map<String,SoftReference<ShaderObject>>>();
  
  private boolean cleaned = false;

  public ShaderObject getShader(String shader) {
	  return getShader(shader, shader);
  }
  
  public ShaderObject getShader(String vertexShader, String fragmentShader) {
	  vertexShader   = "data/shaders/" + vertexShader + ".vs";
	  fragmentShader = "data/shaders/" + fragmentShader + ".fs";
	  
    Map<String, SoftReference<ShaderObject>> map = shaders.get(vertexShader);
    if(map == null) {
      map = new HashMap<String, SoftReference<ShaderObject>>();
      shaders.put(vertexShader, map);
    }
    SoftReference<ShaderObject> aux = map.get(fragmentShader);
    ShaderObject so = null;
    if(aux != null) so = aux.get();
    
    if(so == null) {
      glUseProgram(0);

      int vs = getVertexShader(vertexShader);
      int fs = getFragmentShader(fragmentShader);
      
      try {
        so = new ShaderObject(vs, fs);
      } catch (Exception e) {
        throw new IllegalArgumentException("Error linking shader: "
            + "\nVertex:\n" + vertexShader
            + "\nFragment:\n" + fragmentShader);
      }
    }
    return so;
  }
  
  private static String loadVSSource(String file) {
	  InputStream is;
	try {
		is = Utils.findInputStream(file);
	} catch (FileNotFoundException e) {
		return "attribute vec3 _Position;" +
				"uniform mat4 u_WorldViewProj;" +
				"void main()" +
				"{" +
				"  gl_Position = u_WorldViewProj * vec4(_Position,1.0);" +
				"}";  
	}
	  
	  if(is != null) {
		  return Utils.readInputStream(is);
	  } else {
		return  "attribute vec3 _Position;" +
				"uniform mat4 u_WorldViewProj;" +
				"void main()" +
				"{" +
				"  gl_Position = u_WorldViewProj * vec4(_Position,1.0);" +
				"}";  
	  }
  }
  
  private static String loadFSSource(String file) {
	  InputStream is;
	try {
		is = Utils.findInputStream(file);
	} catch (FileNotFoundException e) {
		return  "void main(void)" +
				"{" +
				"  gl_FragColor = vec4(1,1,1,1);" +
				"}";  
	}

	  if(is != null) {
		  return Utils.readInputStream(is);
	  } else {
		return  "void main(void)" +
				"{" +
				"  gl_FragColor = vec4(1,1,1,1);" +
				"}";  
	  }
  }
  
  private int getVertexShader(String vertexShader) {
    Integer i = vertexShaders.get(vertexShader);
    if(i != null) {
      return i;
    }
    
    int vs = glCreateShader(GL_VERTEX_SHADER);

    //glShaderSource(vs, vertexShader);
    String source = loadFSSource(vertexShader);
    glShaderSource(vs, source);
    glCompileShader( vs );
    
    int result;
    result = glGetShader(vs, GL_COMPILE_STATUS);
    
    if(result == 0) {
      int len = glGetShader(vs, GL_INFO_LOG_LENGTH);
      String info = glGetShaderInfoLog(vs, len);
      throw new IllegalArgumentException("Error: " + info + "\n in vertex shader: " + vertexShader + "\nSource:\n" + source);
    }
    
    vertexShaders.put(vertexShader, vs);
    return vs;
  }
  
  private int getFragmentShader(String fragmentShader) {
    Integer i = fragmentShaders.get(fragmentShader);
    if(i != null) {
      return i;
    }
    
    int fs = glCreateShader(GL_FRAGMENT_SHADER);
    
    //glShaderSource(fs, fragmentShader);
    String source = loadVSSource(fragmentShader);
    glShaderSource(fs, source);
    glCompileShader( fs );
    
    //IntBuffer result = BufferUtils.createIntBuffer(1);
    int result = glGetShader(fs, GL_COMPILE_STATUS);
    
    if(result == 0) {
      int len = glGetShader(fs, GL_INFO_LOG_LENGTH);
      String info = glGetShaderInfoLog(fs, len);
      throw new IllegalArgumentException("Error :" + info + " in fragment shader: " + fragmentShader + "\nSource:\n" + source);
    }
    
    fragmentShaders.put(fragmentShader, fs);
    return fs;
  }
  

  
  public void cleanUp() {
    if(!cleaned) {
      for(int vs: vertexShaders.values()) {
        glDeleteShader(vs);
      }
      for(int fs: fragmentShaders.values()) {
        glDeleteShader(fs);
      }
      for(Map<String, SoftReference<ShaderObject>> map : shaders.values()) {
        for(SoftReference<ShaderObject> aux : map.values()) {
          ShaderObject so = aux.get();
          if(so != null) so.cleanUp();
        }
      }
      vertexShaders.clear();
      fragmentShaders.clear();
      shaders.clear();
    }
  }
  
  @Override
  public void finalize() {
    cleanUp();
  }
}
