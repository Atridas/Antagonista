package cat.atridas.antagonista.graphics;

import java.io.InputStream;

import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;

public final class Shader extends Resource {

  private String source = "";
  private final ShaderType type;
  
  public Shader(ShaderType _type) {
    type = _type;
  }
  
  public ShaderType getType() {
    assert !cleaned;
    return type;
  }
  
  public String getSource() {
    assert !cleaned;
    return source;
  }

  @Override
  public int getRAMBytesEstimation() {
    assert !cleaned;
    return 4 + source.getBytes().length;
  }

  @Override
  public int getVRAMBytesEstimation() {
    assert !cleaned;
    return 0;
  }

  public static enum ShaderType {
    VERTEX,
    TESS_CONTROL,
    TESS_EVALUATION,
    GEOMETRY,
    FRAGMENT
  }

  @Override
  public boolean load(InputStream is) {
    assert !cleaned;

    source = Utils.readInputStream(is);
    
    return true;
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    source = null;
    cleaned = true;
  }
  
  
  void loadDefault() {
    switch(type) {
    case VERTEX: //TODO
      source =  "attribute vec3 _Position;\n" +
                "uniform mat4 u_WorldViewProj;\n" +
                "void main()\n" +
                "{\n" +
                "  gl_Position = u_WorldViewProj * vec4(_Position,1.0);\n" +
                "}\n";
      break;
    case FRAGMENT: //TODO
      source =  "void main(void)\n" +
                "{\n" +
                "  gl_FragColor = vec4(1,1,1,1);\n" +
                "}\n";
      break;
    default:
      throw new IllegalStateException("not implemented yet");
    }
    
  }
}
