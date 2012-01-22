package cat.atridas.antagonista.graphics;

import java.io.InputStream;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.RenderManager.Profile;

public final class Shader extends Resource {

  private String source = "";
  private final ShaderType type;

  public Shader(HashedString _resourceName, ShaderType _type) {
    super(_resourceName);
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
  public boolean load(InputStream is, HashedString extension) {
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
      source =  "attribute vec3 a_v3Position;\n" +
                
                "void main()\n" +
                "{\n" +
                "  gl_Position = vec4(a_v3Position,1.0);\n" +
                "}\n";
      break;
    case FRAGMENT:
      if(Utils.supports(Profile.GL3)) {
        source =  "#version 150\n" +
      
                  "out vec4 f_v4Color;\n" +
            
                  "void main(void)\n" +
                  "{\n" +
                  "  f_v4Color = vec4(1,1,1,1);\n" +
                  "}\n";
      } else {
        source =  "void main(void)\n" +
                  "{\n" +
                  "  gl_FragColor = vec4(1,1,1,1);\n" +
                  "}\n";
      }
      break;
    case GEOMETRY: //TODO
      source =  "#version 150\n" +
          
          "layout(triangles) in;\n" +
          "layout(triangle_strip, max_vertices = 3) out;\n" +
           
          "void main() {\n" +
          "  for(int i = 0; i < gl_in.length(); i++) {\n" +
          "    gl_Position = gl_in[i].gl_Position;\n" +
          "    EmitVertex();\n" +
          "  }\n" +
          "  EndPrimitive();\n" +
          "};\n";
      break;
    case TESS_EVALUATION: //TODO
      source =  "void main(void)\n" +
                "{\n" +
                "  gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;\n" +
                "}\n";
      break;
    case TESS_CONTROL: //TODO
      source =  "void main(void)\n" +
                "{\n" +
                "  gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;\n" +
                "}\n";
      break;
    default:
      throw new IllegalStateException();
    }
    
  }
}
