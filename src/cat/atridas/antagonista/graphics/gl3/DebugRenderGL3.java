package cat.atridas.antagonista.graphics.gl3;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL33.*;

import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;

import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.graphics.Technique;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.Effect.TechniqueType;

public class DebugRenderGL3 extends DebugRender {
  
  public final static int SPHERE_STACKS = 10;
  public final static int SPHERE_SUBDIV = 10;
  

  private FloatBuffer globalDataBuffer = BufferUtils.createFloatBuffer(InstanceBufferUtils.BUFFER_SIZE);
  private FloatBuffer buffer1, buffer2;
  private int glBuffer = -1, glVAO = -1, glGlobalDataBuffer = -1;
  
  //esferes
  private int sphereIndexBuffer = -1, sphereVertexBuffer = -1, sphereVAO = -1, sphereColorBuffer = -1;
  
  private static final int POS_COL_VERTEX_SIZE = (3 + 3); //Floats
  
  {
    buffer1 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
    buffer2 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
  }
  
  private void initSphere() {
    assert !cleaned;
    ArrayList<Float> vertices = new ArrayList<>();

    vertices.add( 0.f);
    vertices.add( 0.f);
    vertices.add( 1.f); //top
    
    vertices.add( 0.f);
    vertices.add( 0.f);
    vertices.add(-1.f); //botom
    
    for(int i = 1; i < SPHERE_STACKS; i++) {
      float z = i / (SPHERE_STACKS/2.f) - 1;
      for(int j = 0; j < SPHERE_SUBDIV; ++j) {
        float len = (float)Math.sqrt(1 - z*z);
        float x = (float) Math.sin( j * Math.PI * 2 ) * len;
        float y = (float) Math.cos( j * Math.PI * 2 ) * len;
        

        vertices.add(x);
        vertices.add(y);
        vertices.add(z);
      }
    }
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////
    
    ArrayList<Short> indexes = new ArrayList<>();

    for(short i = 1; i < SPHERE_STACKS; i++) {
      short baseStack = (short)( (i-1) * SPHERE_SUBDIV + 2 );
      for(short j = 0; j < SPHERE_SUBDIV; ++j) {
        indexes.add( (short)( baseStack + j    ) );
      }
      indexes.add( (short)( baseStack ) );
    }
    //////////////////////////////////////////////////////////////
    Short saux1[] = indexes.toArray(new Short[indexes.size()]);
    short saux2[] = new short[saux1.length];
    for(int i = 0; i < saux1.length; i++) {
      saux2[i] = saux1[i];
    }
    saux1 = null;
    indexes = null;
    //////////////////////////////////////////////////////////////

    sphereVAO = glGenVertexArrays();
    glBindVertexArray(sphereVAO);
    
    sphereVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, sphereVertexBuffer);
    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.rewind();
    glBufferData(GL_ARRAY_BUFFER,vertexBuffer, GL_STATIC_DRAW);
    
    sphereIndexBuffer = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, sphereIndexBuffer);
    ShortBuffer indexBuffer = BufferUtils.createShortBuffer(saux2.length);
    indexBuffer.put(saux2);
    indexBuffer.rewind();
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
    


    glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);

    glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
    
    
    sphereColorBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, sphereColorBuffer);
    glBufferData(GL_ARRAY_BUFFER, POS_COL_VERTEX_SIZE * 50, GL_DYNAMIC_DRAW);
    
    glVertexAttribPointer(TechniquePass.COLOR_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
    glVertexAttribDivisor(TechniquePass.COLOR_ATTRIBUTE, 1);

    
    
    glBindVertexArray(0);
    glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    
    assert !Utils.hasGLErrors();
  }
  
  private void initBuffers() {
    assert !cleaned;
    if(glBuffer < 0) {
      glBuffer = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, glBuffer);
      glBufferData(GL_ARRAY_BUFFER, POS_COL_VERTEX_SIZE * 50, GL_DYNAMIC_DRAW);
      
      
      glGlobalDataBuffer = glGenBuffers();
      glBindBuffer(GL_UNIFORM_BUFFER, glGlobalDataBuffer);
      glBufferData(GL_UNIFORM_BUFFER, InstanceBufferUtils.BUFFER_SIZE, GL_DYNAMIC_DRAW);
      
      
      glVAO = glGenVertexArrays();
      glBindVertexArray(glVAO);

      glBindBuffer(GL_ARRAY_BUFFER, glBuffer);

      glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glEnableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);

      glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 6*Utils.FLOAT_SIZE, 0);
      glVertexAttribPointer(TechniquePass.COLOR_ATTRIBUTE, 3, GL_FLOAT, false, 6*Utils.FLOAT_SIZE, 3*Utils.FLOAT_SIZE);
      

      glBindVertexArray(0);
      glBindBuffer(GL_ARRAY_BUFFER, 0);
      glBindBuffer(GL_UNIFORM_BUFFER, 0);
      glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);

      assert !Utils.hasGLErrors();
      
      initSphere();
    }
  }
  
  private void render(int mode, int elements, RenderManager rm) {
    debugMaterial.setUpUniforms(rm);

    Technique technique = debugMaterial.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
    for(TechniquePass pass: technique.passes) {
      pass.activate(rm);
      debugMaterial.setUpUniforms(pass, rm);

      glDrawArrays(mode, 0, elements);
    }
  }
  
  private void growBuffers() {
    buffer1 = BufferUtils.createFloatBuffer( buffer1.capacity() / Utils.FLOAT_SIZE + POS_COL_VERTEX_SIZE * 50 );
    buffer2 = BufferUtils.createFloatBuffer( buffer2.capacity() / Utils.FLOAT_SIZE + POS_COL_VERTEX_SIZE * 50 );
  }
  
  @Override
  protected void renderLines(RenderManager rm) {
    assert !cleaned;

    if(lines.size() == 0)
      return;
    
    try {      
      float[] vertex = new float[POS_COL_VERTEX_SIZE * 2];

      int linesToDraw1 = 0;
      int linesToDraw2 = 0;
      buffer1.rewind();
      buffer2.rewind();

      
      for(Line line: lines) {
        
        vertex[0] = line.origin.x;
        vertex[1] = line.origin.y;
        vertex[2] = line.origin.z;

        vertex[3] = line.color.x;
        vertex[4] = line.color.y;
        vertex[5] = line.color.z;
        
        vertex[6] = line.destination.x;
        vertex[7] = line.destination.y;
        vertex[8] = line.destination.z;

        vertex[9]  = line.color.x;
        vertex[10] = line.color.y;
        vertex[11] = line.color.z;
        
        if(line.depthEnabled) {
          buffer1.put(vertex);
          linesToDraw1++;
        } else {
          buffer2.put(vertex);
          linesToDraw2++;
        }
      }
      
      if(linesToDraw1>0) {
        buffer1.rewind();
      
        glEnable(GL_DEPTH_TEST);
      
        glBufferData(GL_ARRAY_BUFFER, buffer1, GL_DYNAMIC_DRAW);
      
        render(GL_LINES, linesToDraw1 * 2, rm);

        assert !Utils.hasGLErrors();
      }
      
      if(linesToDraw2>0) {
        buffer2.rewind();
      
        glDisable(GL_DEPTH_TEST);
      
        glBufferData(GL_ARRAY_BUFFER, buffer2, GL_DYNAMIC_DRAW);
      
        render(GL_LINES, linesToDraw2 * 2, rm);

        assert !Utils.hasGLErrors();
      }
      
    } catch(BufferOverflowException e) {
      //fem creixer el buffer i rellancem el métode.
      //brut pq és una classe per fer debug.
      growBuffers();
      renderLines(rm);
    }
    
  }
  
  @Override
  protected void renderCrosses() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderSpheres() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderCircles() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderAxes() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderTriangles() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderAABBs() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderOBBs() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderStrings() {
    assert !cleaned;
    //TODO
  }

  private boolean prevDepthMask;
  
  @Override
  protected void beginRender(RenderManager rm) {
    assert !cleaned;
    initBuffers();
    prevDepthMask = glGetBoolean(GL_DEPTH_WRITEMASK);
    glDepthMask(false);
    
    glBindVertexArray(glVAO);
    glBindBuffer(GL_ARRAY_BUFFER, glBuffer);
    


    SceneData sd = rm.getSceneData();
    
    Matrix4f mat = new Matrix4f();
    mat.setIdentity();

    globalDataBuffer.rewind();
    sd.getViewProjectionMatrix(mat);
    Utils.matrixToBuffer(mat, globalDataBuffer);
 // a partir d'aqui no se si realment cal
    mat.setIdentity();
    sd.getViewMatrix(mat);
    Utils.matrixToBuffer(mat, globalDataBuffer);
    mat.invert();
    mat.transpose();
    Utils.matrixToBuffer(mat, globalDataBuffer);
 // fins aqui
    globalDataBuffer.rewind();
    glBindBuffer(GL_UNIFORM_BUFFER, glGlobalDataBuffer);
    glBufferData(GL_UNIFORM_BUFFER, globalDataBuffer, GL_DYNAMIC_DRAW);
    glBindBufferBase( GL_UNIFORM_BUFFER, 
                      TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
                      glGlobalDataBuffer);

    assert !Utils.hasGLErrors();
  }

  @Override
  protected void endRender() {
    assert !cleaned;
    glDepthMask(prevDepthMask);

    glBindVertexArray(0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
    
    assert !Utils.hasGLErrors();
  }


  protected boolean cleaned = false;
  
  @Override
  public void finalize() {
    if(!cleaned) {
      if(glBuffer > 0) {
        glDeleteBuffers(glBuffer);
        glDeleteBuffers(glGlobalDataBuffer);
        glDeleteBuffers(sphereIndexBuffer);
        glDeleteBuffers(sphereVertexBuffer);
        glDeleteBuffers(sphereColorBuffer);
        glDeleteVertexArrays(glVAO);
        glDeleteVertexArrays(sphereVAO);
      }
      
      cleaned = true;
    }
  }
}
