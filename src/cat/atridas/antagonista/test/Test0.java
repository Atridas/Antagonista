package cat.atridas.antagonista.test;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.Effect.TechniqueType;
import cat.atridas.antagonista.graphics.Material;
import cat.atridas.antagonista.graphics.Mesh;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.Technique;
import cat.atridas.antagonista.graphics.TechniquePass;

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
    
    
    assert !Utils.hasGLErrors();
    
    //glDisable(GL_DEPTH_TEST);

    assert !Utils.hasGLErrors();

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    String l_szVertexShader = "#version 330\n" +
                              "layout(location = 0) in vec3 position;\n" +
                              "layout(location = 1) in vec3 normal;\n" +
                              "layout(location = 4) in vec2 uv;\n" +

                              "out vec3 v_color;\n" +
                              
                              "layout(std140) uniform UniformInstances {\n" +
                              //"  struct {\n" +
                              "    mat4 m4ModelViewProjection;\n" +
                              "    mat4 m4ModelView;\n" +
                              //"  };\n" +
                              "};\n" +
                              
                              "void main()\n" +
                              "{" +
                              //"    v_color = color;\n" +
                              "    v_color = (normal + 1) * 0.5;\n" +
                              "    gl_Position = m4ModelViewProjection * vec4(position,1);\n" +
                              //"    gl_Position = vec4(position,1);\n" +
                              //"    z = gl_Position.z;\n" +
                              "}\n",
                              
         l_szFragmentShader = "#version 330\n" +

                              "in vec3 v_color;\n" +
                              
                              "out vec4 outputColor;\n" +
                              "void main()\n" +
                              "{" +
                              //"    outputColor = vec4(0.5f, 1.0f, 0.5f, 1.0f);\n" +
                              "    outputColor = vec4(v_color, 1.0f);\n" +
                              //"    float r = gl_FragCoord.x / 800;\n" +
                              //"    float g = gl_FragCoord.y / 600;\n" +
                              //"    float b = gl_FragDepth;\n" +
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
    //assert glGetAttribLocation(program, "uv")    == 4;

    assert !Utils.hasGLErrors();
    
    
    int uniform = glGetUniformBlockIndex(program, "UniformInstances");
    
    glUniformBlockBinding(program, uniform, 0);
    
    

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
    
    rm.getSceneData().setPerspective(30, 1.f, 100);
    rm.getSceneData().setCamera(new Point3f(20,20,10), new Point3f(0,0,0), new Vector3f(0,0,1));

    rm.getSceneData().setAmbientLight(new Point3f(.2f, .2f, .2f));
    rm.getSceneData().setDirectionalLight(new Vector3f(1,0,-1), new Point3f(.8f, .8f, .8f) );
    
    Matrix4f mvp = new Matrix4f();
    mvp.setIdentity();
    rm.getSceneData().getViewProjectionMatrix(mvp);

    
    ByteBuffer bb = BufferUtils.createByteBuffer(8*4 * 4);
    
    Utils.matrixToBuffer(mvp, bb);

    mvp.setIdentity();
    rm.getSceneData().getViewMatrix(mvp);
    Utils.matrixToBuffer(mvp, bb);
    
    bb.rewind();

    int uniformBufferObject = glGenBuffers();
    
    glBindBuffer(GL_UNIFORM_BUFFER, uniformBufferObject);
    glBufferData(GL_UNIFORM_BUFFER, bb, GL_STATIC_DRAW);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    //glDisable(GL_CULL_FACE);
    //glDisable(GL_DEPTH_TEST);

    //Mesh mesh = Core.getCore().getMeshManager().getResource(new HashedString("Cub"));
    //Mesh mesh = Core.getCore().getMeshManager().getResource(new HashedString("CubBlend"));
    Mesh mesh = Core.getCore().getMeshManager().getResource(new HashedString("Habitacio"));

    //mesh = Core.getCore().getMeshManager().getDefaultResource();
    
    
    rm.getSceneData().setUniforms();
    
    while(!Core.getCore().getInputManager().isCloseRequested()) {
      rm.initFrame();
    
      //glUseProgram(program);
      //assert !Utils.hasGLErrors();
  
      //glBindVertexArray(vertexArrayObject);
      glBindBufferRange(GL_UNIFORM_BUFFER, 0, uniformBufferObject, 0, 8*4*4);
      
      //glDrawArrays(GL_TRIANGLES, 0, 3);
      //glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_SHORT, 0);
      
      mesh.preRender();
      

      for(int i1 = 0; i1 < mesh.getNumSubmeshes(); i1++) {
        Material material = mesh.getMaterial(i1);
        material.setUpUniforms(rm);
        Technique technique = material.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
        TechniquePass pass = technique.passes.get(0);
        pass.activate(rm);
        rm.getSceneData().setUniforms(pass);
        
        mesh.render(i1, rm);
      }
      
      assert !Utils.hasGLErrors();
      
      rm.present();
      
      Core.getCore().getInputManager().update();
    }
    
    Core.getCore().close();
  }

}
