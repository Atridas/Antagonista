package cat.atridas.antagonista.deprecated;

import static org.lwjgl.opengl.GL20.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
//import java.util.logging.Logger;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;

import org.lwjgl.BufferUtils;

import cat.atridas.antagonista.Utils;

public final class ShaderObject {
  //private static Logger logger = Logger.getLogger(ShaderObject.class.getCanonicalName());
  
  private int program=-1;
  private Map<String, Integer> uniforms = new HashMap<String, Integer>();
  private Map<String, Integer> attributes = new HashMap<String, Integer>();
  private Map<Integer, FloatBuffer> floatBuffers = new HashMap<Integer, FloatBuffer>();
  
  private boolean cleaned = false;
  
  public ShaderObject(int vs, int fs) {
    program = glCreateProgram();

    glAttachShader(program, vs);
    glAttachShader(program, fs);
    
    glLinkProgram(program);

    int result = glGetProgram(program, GL_LINK_STATUS);
    
    if(result == 0) {
      int len = glGetProgram(program, GL_INFO_LOG_LENGTH);
      String info = glGetProgramInfoLog(program, len);
      throw new IllegalArgumentException("Error linking shader: " + info);
    }

    
    int numUniforms = glGetProgram(program, GL_ACTIVE_UNIFORMS);
    int uniformLen  = glGetProgram(program, GL_ACTIVE_UNIFORM_MAX_LENGTH);
    
    for(int i = 0; i < numUniforms; ++i) {
      String uniform = glGetActiveUniform(program, i, uniformLen);
      int id = glGetUniformLocation(program, uniform);
      this.uniforms.put(uniform, id);
    }

    int numAttributes = glGetProgram(program, GL_ACTIVE_ATTRIBUTES);
    int attributeLen  = glGetProgram(program, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);
    
    for(int i = 0; i < numAttributes; ++i) {
      String attribute = glGetActiveAttrib(program, i, attributeLen);
      int id = glGetAttribLocation(program, attribute);
      this.attributes.put(attribute, id);
    }
  }
  
  private FloatBuffer getFloatBuffer(int size) {
    FloatBuffer fb = floatBuffers.get(size);
    if(fb == null) {
      fb = BufferUtils.createFloatBuffer(size);
      floatBuffers.put(size, fb);
    }
    fb.rewind();
    return fb;
  }
  
  
  /*private IntBuffer getIntBuffer(int size) {
    IntBuffer ib = intBuffers.get(size);
    if(ib == null) {
      ib = BufferUtils.createIntBuffer(size);
      intBuffers.put(size, ib);
    }
    ib.rewind();
    return ib;
  }*/

  private volatile Set<String> testedNullUniforms = new HashSet<String>();
  private volatile Set<String> testedNullAttributes = new HashSet<String>();
  
  public int getUniform(String name) {
    assert !cleaned;
    Integer uniformID = uniforms.get(name);
    if(uniformID == null) {
      if(!testedNullUniforms.contains(name)) {
        testedNullUniforms.add(name);
        //logger.warning("Uniform " + name + " does not exist.");
        System.out.println("Uniform " + name + " does not exist.");
      }
      return -1;
    }
    return uniformID;
  }
  
  public int getAttrib(String name) {
    assert !cleaned;
    Integer attribID = attributes.get(name);
    if(attribID == null) {
      if(!testedNullAttributes.contains(name)) {
        testedNullAttributes.add(name);
        System.out.println("Atribute " + name + " does not exist.");
      }
      return -1;
    }
    return attribID;
  }
  
  public void setUniform(String name, Matrix4f matrix) {
    assert !cleaned;
    Integer uniformID = uniforms.get(name);
    if(uniformID == null) {
      if(!testedNullUniforms.contains(name)) {
        testedNullUniforms.add(name);
        System.out.println("Uniform " + name + " does not exist.");
      }
    } else {
      setUniform(uniformID, matrix);
    }
  }
  
  public void setUniform(int uniformID, Matrix4f matrix) {
    assert !cleaned;
    FloatBuffer fb = getFloatBuffer(16);
    Utils.matrixToBuffer(matrix, fb);
    glUniformMatrix4(uniformID, false, fb);
  }
  
  public void setUniform(String name, int integer) {
    assert !cleaned;
    Integer uniformID = uniforms.get(name);
    if(uniformID == null) {
      if(!testedNullUniforms.contains(name)) {
        testedNullUniforms.add(name);
        System.out.println("Uniform " + name + " does not exist.");
      }
    } else {
      setUniform(uniformID, integer);
    }
  }
  
  public void setUniform(int uniformID, float f) {
    assert !cleaned;
    glUniform1f(uniformID, f);
  }
  
  public void setUniform(String name, float f) {
    assert !cleaned;
    Integer uniformID = uniforms.get(name);
    if(uniformID == null) {
      if(!testedNullUniforms.contains(name)) {
        testedNullUniforms.add(name);
        System.out.println("Uniform " + name + " does not exist.");
      }
    } else {
      setUniform(uniformID, f);
    }
  }
  
  public void setUniform(int uniformID, int integer) {
    assert !cleaned;
    glUniform1i(uniformID, integer);
  }
  
  public void setUniform(String name, Tuple3f tuple) {
    assert !cleaned;
    Integer uniformID = uniforms.get(name);
    if(uniformID == null) {
      if(!testedNullUniforms.contains(name)) {
        testedNullUniforms.add(name);
        System.out.println("Uniform " + name + " does not exist.");
      }
    } else {
      setUniform(uniformID, tuple);
    }
  }
  
  public void setUniform(int uniformID, Tuple3f tuple) {
    assert !cleaned;
    glUniform3f(uniformID, tuple.x, tuple.y, tuple.z);
  }
  
  public void setTextureUniform(String name, int unit ) {
    assert !cleaned;
    Integer uniformID = uniforms.get(name);
    if(uniformID == null) {
      if(!testedNullUniforms.contains(name)) {
        testedNullUniforms.add(name);
        System.out.println("Uniform " + name + " does not exist.");
      }
    } else {
      glUniform1i(uniformID, unit);
    }
  }
  
  public void setTextureUniform(int uniformID, int unit ) {
    assert !cleaned;
    glUniform1i(uniformID, unit);
  }
  
  public void setAttribBufferedPointer(
      String name, 
      int elementsPerVertex, 
      int type, 
      boolean normalized, 
      int vertexSize, 
      int offset) 
  {
    assert !cleaned;
    int attribID = attributes.get(name);
    
    glEnableVertexAttribArray(attribID);
    glVertexAttribPointer(attribID, 
                                elementsPerVertex, 
                                type ,
                                normalized, 
                                vertexSize, 
                                offset);
  }
  
  public void setAttribBufferedPointer(
      int attribID, 
      int elementsPerVertex, 
      int type, 
      boolean normalized, 
      int vertexSize, 
      int offset) 
  {
    assert !cleaned;
    
    glEnableVertexAttribArray(attribID);
    glVertexAttribPointer(attribID, 
                                elementsPerVertex, 
                                type ,
                                normalized, 
                                vertexSize, 
                                offset);
  }
  
  public void activate() {
    assert !cleaned;
    glUseProgram(program);
  }
  
  public void cleanUp() {
    if(!cleaned) {
      if(program >= 0)
        glDeleteProgram(program);
      
      program = -1;
      cleaned = false;
    }
  }
  
  @Override
  public void finalize() {
    cleanUp();
  }
  
  public ShaderObject self() { return this; }
  
  public abstract class ShaderWrapper {

	  public final ShaderObject getShader() { return self(); }
	  public final void activateShader() { activate(); }
  }

  public class TexturedShaderWrapper extends ShaderWrapper {
	  public int a_Pos;
	  public int a_Tex;
	  public int u_WorldViewProj;
	  public int u_Tex;
	  
	  public TexturedShaderWrapper() {
		a_Pos = getAttrib("a_Position");
		a_Tex = getAttrib("a_ST");
		u_WorldViewProj = getUniform("u_WorldViewProj");
		u_Tex           = getUniform("u_Tex");
	  }
	  
	  public void setUniforms(Matrix4f worldViewProMatrix) {
		  
		  setTextureUniform(u_Tex, 0);
		  setUniform(u_WorldViewProj, worldViewProMatrix);
		  
	  }
  }
  
  public class TexturedMaskedShaderWrapper extends TexturedShaderWrapper {
	  public int u_TexMask;
	  
	  public TexturedMaskedShaderWrapper() {
		  u_TexMask = getUniform("u_TexMask");
	  }
	  
	  public final void setUniforms(Matrix4f worldViewProMatrix) {

		  setTextureUniform(u_TexMask, 1);
		  super.setUniforms(worldViewProMatrix);
		  
	  }
  }
  
  public class ProtaShaderWrapper extends TexturedShaderWrapper {
		private int u_Diapo;
		private int u_DiaposX;
		private int u_DiaposY;
		
		public ProtaShaderWrapper() {

			u_Diapo         = getUniform("u_Diapo");
			u_DiaposX       = getUniform("u_DiaposX");
			u_DiaposY       = getUniform("u_DiaposY");
		}

		public final void setUniforms(Matrix4f worldViewProMatrix, int diapo, int tilesX, int tilesY) {
			setUniform(u_Diapo, diapo);
			setUniform(u_DiaposX, tilesX);
			setUniform(u_DiaposY, tilesY);
			
			super.setUniforms(worldViewProMatrix);
		}
  }
  
  public class GUIShaderWrapper extends TexturedMaskedShaderWrapper {
		private int u_Amount;
		private int u_MaxAmount;
		
		public GUIShaderWrapper() {

			u_Amount    = getUniform("u_Amount");
			u_MaxAmount = getUniform("u_MaxAmount");
		}

		public final void setUniforms(Matrix4f worldViewProMatrix, float amount, float maxAmount) {
			setUniform(u_Amount, amount);
			setUniform(u_MaxAmount, maxAmount);
			
			super.setUniforms(worldViewProMatrix);
		}
  }
}
