package cat.atridas.antagonista.test;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.RenderManager;

public class Test0 {

  /**
   * @param args
   */
  public static void main(String[] args) {
    boolean assertsActives = false;
    assert (assertsActives = true) == true;
    if(!assertsActives)
      throw new RuntimeException("Falta activar els asserts");
    
    //Utils.setConsoleLogLevel(Level.CONFIG);
    
    Core.getCore().init(800, 600, Test0.class.getName(), null);
    
    RenderManager rm = Core.getCore().getRenderManager();
    
    rm.initFrame();
    
    assert !Utils.hasGLErrors();
    
    glDisable(GL_DEPTH_TEST);

    assert !Utils.hasGLErrors();

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    String l_szVertexShader = "#version 330\n" +
                              "layout(location = 0) in vec3 position;\n" +
                              "layout(location = 1) in vec3 color;\n" +
                              
                              "smooth out vec3 v_color;\n" +
                              
                              "void main()\n" +
                              "{" +
                              "    v_color = color;\n" +
                              //"    v_color = position;\n" +
                              "    gl_Position = vec4(position,1);\n" +
                              "}\n",
                              
         l_szFragmentShader = "#version 330\n" +
                            
                              "smooth in vec3 v_color;\n" +
                              
                              "out vec4 outputColor;\n" +
                              "void main()\n" +
                              "{" +
                              //"    outputColor = vec4(0.5f, 1.0f, 0.5f, 1.0f);\n" +
                              "    outputColor = vec4(v_color, 1.0f);\n" +
                              //"    float r = gl_FragCoord.x / 800;\n" +
                              //"    float g = gl_FragCoord.y / 600;\n" +
                              //"    outputColor = vec4(r, g, 0, 1.0f);\n" +
                              "}\n";
    
    int vs = glCreateShader(GL_VERTEX_SHADER),
        fs = glCreateShader(GL_FRAGMENT_SHADER);

    glShaderSource(vs, l_szVertexShader);
    glShaderSource(fs, l_szFragmentShader);

    glCompileShader(vs);
    glCompileShader(fs);
    
    int maxLength = glGetShader(vs, GL_INFO_LOG_LENGTH);
    System.out.println(glGetShaderInfoLog(vs, maxLength));
    maxLength = glGetShader(fs, GL_INFO_LOG_LENGTH);
    System.out.println(glGetShaderInfoLog(fs, maxLength));
    
    assert glGetShader(vs, GL_COMPILE_STATUS) != GL_FALSE;
    assert glGetShader(fs, GL_COMPILE_STATUS) != GL_FALSE;
    
    int program = glCreateProgram();

    glAttachShader(program, vs);
    glAttachShader(program, fs);
    
    glLinkProgram(program);
    
    assert glGetProgram(program, GL_LINK_STATUS) != GL_FALSE;

    assert glGetAttribLocation(program, "position") == 0;
    //assert glGetAttribLocation(program, "color")    == 1;

    assert !Utils.hasGLErrors();

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    float v[] = {
      0.75f, 0.75f, 0.0f,// 1.0f,
      0.75f, -0.75f, 0.0f,// 1.0f,
      -0.75f, -0.75f, 0.0f,// 1.0f,
    };
    
    float c[] = {
        1.f, 0.f, 0.f,
        0.f, 1.f, 0.f,
        0.f, 0.f, 1.f
    };
    
    short i[] = {0, 1, 2};
    
    FloatBuffer vb = BufferUtils.createFloatBuffer(3*3);
    vb.put(v);
    vb.rewind();
    
    FloatBuffer cb = BufferUtils.createFloatBuffer(3*3);
    cb.put(c);
    cb.rewind();
    
    ShortBuffer ib = BufferUtils.createShortBuffer(3);
    ib.put(i);
    ib.rewind();

    int positionBufferObject = glGenBuffers();
    int colorBufferObject    = glGenBuffers();
    int indexBufferObject    = glGenBuffers();

    glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
    glBufferData(GL_ARRAY_BUFFER, vb, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, colorBufferObject);
    glBufferData(GL_ARRAY_BUFFER, cb, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferObject);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_STATIC_DRAW);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    
    assert !Utils.hasGLErrors();
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    

    int vertexArrayObject = glGenVertexArrays();
    glBindVertexArray(vertexArrayObject);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferObject);
    
    glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
    assert !Utils.hasGLErrors();
    
    glEnableVertexAttribArray(0);
    assert !Utils.hasGLErrors();

    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0,  0);
    

    glEnableVertexAttribArray(1);
    glBindBuffer(GL_ARRAY_BUFFER, colorBufferObject);
    glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
    assert !Utils.hasGLErrors();

    glBindVertexArray(0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    glUseProgram(program);
    assert !Utils.hasGLErrors();

    glBindVertexArray(vertexArrayObject);
    //glDrawArrays(GL_TRIANGLES, 0, 3);
    glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_SHORT, 0);
    assert !Utils.hasGLErrors();
    
    rm.present();
    
    synchronized (rm) {
      try {
        rm.wait(5000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
    
    Core.getCore().close();
  }

}
